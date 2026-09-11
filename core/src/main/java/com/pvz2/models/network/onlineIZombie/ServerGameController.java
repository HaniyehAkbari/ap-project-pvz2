package com.pvz2.models.network.onlineIZombie;

import com.pvz2.models.core.User;
import com.pvz2.models.core.UserDataManager;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.miniGame.IZombie.OnlineIZombieLevel;
import com.pvz2.models.miniGame.IZombie.OnlineIZombieLose;
import com.pvz2.models.miniGame.IZombie.OnlineIZombieSetup;
import com.pvz2.models.miniGame.IZombie.OnlineIZombieWin;
import com.pvz2.models.network.onlineIZombie.messages.*;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.card.ImitatorCard;
import com.pvz2.models.plant.card.PlantCard;
import com.pvz2.models.plant.components.ImitatorIntroComponent;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.Sun;
import com.pvz2.models.world.SunType;
import com.pvz2.models.world.loseCondition.LoseCondition;
import com.pvz2.models.world.mechanics.BrainSpawnMechanic;
import com.pvz2.models.world.mechanics.Mechanic;
import com.pvz2.models.world.mechanics.OnlineSunSpawnMechanic;
import com.pvz2.models.world.winCondition.WinCondition;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.zombie.ZombieFactory;
import com.pvz2.models.network.ClientHandler;
import com.pvz2.view.util.LawnGrid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Owns the one real GameWorld for a live match. Every gameplay request is validated
 * and applied here — never on the client — then the result is broadcast to both players.
 * A shared scheduler ticks the world forward and checks win/lose on a timer (see start()).
 */
public class ServerGameController {
    private static final float TICK_INTERVAL_SECONDS = 0.02f;
    private static final long TICK_INTERVAL_MS = 20L;
    private static final int BROADCAST_EVERY_N_TICKS = 3;

    private static final ScheduledExecutorService TICK_SCHEDULER = Executors.newScheduledThreadPool(4);

    private final Match match;
    private final OnlineIZombieLevel world;
    private final WinCondition winCondition;
    private final List<LoseCondition> loseConditions;
    private final Runnable onMatchEnd;

    private volatile boolean matchEnded = false;
    private ScheduledFuture<?> tickTask;
    private int ticksSinceLastBroadcast = 0;

    public ServerGameController(Match match, Runnable onMatchEnd) {
        this.match = match;
        this.onMatchEnd = onMatchEnd;

        List<Zombie> stageZombies = new ArrayList<>();
        ZombieFactory zombieFactory = new ZombieFactory();
        stageZombies.add(zombieFactory.createZombie("ZombieImp"));
        stageZombies.add(zombieFactory.createZombie("ZombieArmor1"));
        stageZombies.add(zombieFactory.createZombie("ZombieArmor2"));
        stageZombies.add(zombieFactory.createZombie("ZombieArmor4"));
        stageZombies.add(zombieFactory.createZombie("ZombieModernAllStar"));
        stageZombies.add(zombieFactory.createZombie("ZombieWizard"));
        stageZombies.add(zombieFactory.createZombie("ZombieLostCityJane"));
        stageZombies.add(zombieFactory.createZombie("ZombieGargantuar"));

        ArrayList<Mechanic> mechanics = new ArrayList<>();
        mechanics.add(new OnlineSunSpawnMechanic());
        mechanics.add(new BrainSpawnMechanic());

        ArrayList<LoseCondition> loseConditions = new ArrayList<>();
        loseConditions.add(new OnlineIZombieLose());
        this.loseConditions = loseConditions;
        this.winCondition = new OnlineIZombieWin();

        this.world = new OnlineIZombieLevel(
            new OnlineIZombieSetup(5, 9, stageZombies), loseConditions, winCondition, mechanics);
    }

    public void start() {
        tickTask = TICK_SCHEDULER.scheduleAtFixedRate(this::onTick, 0, TICK_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (tickTask != null) tickTask.cancel(false);
    }

    private synchronized void onTick() {
        if (matchEnded) return;
        long startTime = System.currentTimeMillis();
        try {
            world.tick(TICK_INTERVAL_SECONDS);

            if (winCondition.checkWin(world)) {
                endMatch(Match.Side.PLANTS, "Time's up — the plants held the line!");
                return;
            }
            for (LoseCondition loseCondition : loseConditions) {
                if (loseCondition.checkLose(world)) {
                    endMatch(Match.Side.ZOMBIES, "The zombies ate every brain!");
                    return;
                }
            }

            broadcastIfDue();
        }  catch (Throwable t) {
            t.printStackTrace();
        }
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Tick took: " + duration + " ms");
    }

    private void broadcastIfDue() {
        ticksSinceLastBroadcast++;
        if (ticksSinceLastBroadcast >= BROADCAST_EVERY_N_TICKS) {
            broadcastState();
        }
    }

    private void endMatch(Match.Side winner, String message) {
        matchEnded = true;
        stop();
        MatchOver update = new MatchOver(match.getMatchId(), winner.name(), message);
        match.getPlantsPlayer().send("MATCH_OVER", null, update);
        match.getZombiesPlayer().send("MATCH_OVER", null, update);
        onMatchEnd.run();
    }

    public synchronized void handlePlantPlant(ClientHandler sender, PlantPlantRequest req) {
        if (!match.getMatchId().equals(req.matchId)) return;
        if (match.getSideOf(sender) != Match.Side.PLANTS) return;

        Cell selectedCell = Cell.findCell(req.x, req.y, world.getGrid());
        if (selectedCell == null) return;

        PlantCard selectedCard = findPlantCard(req.type);
        if (selectedCard == null || !selectedCard.isReady()) return;
        if (selectedCard.getSunCost() > world.getSun()) return;
        int col = LawnGrid.getColFromX(req.x);
        if (col > 4) {
            sendResult(sender, false, "you cant plant here");
            return;
        }

        User plantsUser = UserDataManager.loadUser(sender.getUsername());
        boolean isImitator = selectedCard instanceof ImitatorCard;
        PlantType typeToPlant;
        boolean boosted;
        if (selectedCard instanceof ImitatorCard imitatorCard) {
            typeToPlant = imitatorCard.getTargetType();
            boosted = plantsUser.getUnlockedPlantsLevels().get(PlantType.IMITATER) >= 4;
        } else {
            typeToPlant = req.type;
            boosted = plantsUser.hasBoost(typeToPlant);
        }

        Plant plant = selectedCell.handlePlanting(typeToPlant, boosted);
        if (plant == null) return;

        plant.setCell(selectedCell);
        if (isImitator) {
            plant.setImitate(true);
            plant.addComponent(new ImitatorIntroComponent());
        }
        selectedCard.setReady(false);
        world.setSun(world.getSun() - selectedCard.getSunCost());

        broadcastState();
    }

    public synchronized void handlePluckPlant(ClientHandler sender, PluckPlantRequest req) {
        if (!match.getMatchId().equals(req.matchId)) return;
        if (match.getSideOf(sender) != Match.Side.PLANTS) return;

        Cell selectedCell = Cell.findCell(req.x, req.y, world.getGrid());
        if (selectedCell == null || !selectedCell.findAndRemovePlant()) return;

        broadcastState();
    }

    public synchronized void handleCollectSun(ClientHandler sender, CollectSunRequest req) {
        if (!match.getMatchId().equals(req.matchId)) return;
        if (match.getSideOf(sender) != Match.Side.PLANTS) return;

        for (Sun sun : world.getActiveSuns()) {
            if (sun.getBounds().contains(req.x, req.y) && !sun.isCollected() && !sun.isExploded()) {
                if (sun.getType() == SunType.RADIOACTIVE) {
                    sun.explode();
                } else {
                    sun.collect();
                    world.addSunToPlayer(sun.getSize());
                }
                broadcastState();
                return;
            }
        }
    }

    public synchronized void handleCollectBrain(ClientHandler sender, CollectBrainRequest req) {
        if (!match.getMatchId().equals(req.matchId)) return;
        if (match.getSideOf(sender) != Match.Side.ZOMBIES) return;

        for (BrainCurrency brain : world.getActiveBrains()) {
            if (!brain.isCollected() && brain.contains(req.x, req.y, 100f)) {
                brain.collect();
                world.addBrainsToPlayer(50);
                broadcastState();
                return;
            }
        }
    }

    public synchronized void handlePlaceZombie(ClientHandler sender, PlaceZombieRequest req) {
        if (!match.getMatchId().equals(req.matchId)) return;
        if (match.getSideOf(sender) != Match.Side.ZOMBIES) return;

        Zombie zombie;
        try {
            zombie = new ZombieFactory().createZombie(req.zombieType);
        } catch (Exception e) {
            sendResult(sender, false, "invalid zombie");
            return;
        }

        boolean allowed = world.getAvailableZombies().stream()
            .anyMatch(z -> z.getName() == zombie.getName()
                || (z.getSpecificName() != null && z.getSpecificName().equalsIgnoreCase(req.zombieType)));
        if (!allowed) {
            sendResult(sender, false, "you dont have this zombie");
            return;
        }

        int col = LawnGrid.getColFromX(req.x);
        if (col < world.getRedLineCol()) {
            sendResult(sender, false, "you cant place zombie here");
            return;
        }

        OnlineIZombieSetup setup = (OnlineIZombieSetup) world.getLevelSetup();
        int cost = setup.getBrainCost(zombie.getSpecificName());
        if (world.getZombieBrains() < cost) {
            sendResult(sender, false, "you dont have enough brains");
            return;
        }

        world.addBrainsToPlayer(-cost);
        zombie.setX(req.x);
        zombie.setY(req.y);
        world.addZombie(zombie);
        ZombieCard zombieCard = findZombieCard(req.zombieType);
        if (zombieCard != null){
            zombieCard.setReady(false);
        }

        sendResult(sender, true, "zombie placed at " + req.x + ", " + req.y);
        broadcastState();
    }

    private void sendResult(ClientHandler sender, boolean success, String message) {
        sender.send("ACTION_RESULT", null, new ActionResult(success, message));
    }

    private PlantCard findPlantCard(PlantType type) {
        List<PlantCard> cards = world.isConveyorMode() ? world.getConveyorBelt() : world.getPlantLists();
        for (PlantCard card : cards) {
            if (card.getType().equals(type)) return card;
        }
        return null;
    }

    private ZombieCard findZombieCard(String type) {
        List<ZombieCard> cards = world.getZombieCards();
        for (ZombieCard card : cards) {
            if (card.getType().equals(type)) return card;
        }
        return null;
    }

    private void broadcastState() {
        ticksSinceLastBroadcast = 0;
        GameStateUpdate update = new GameStateUpdate(match.getMatchId(), world);
        match.getPlantsPlayer().send("GAME_STATE", null, update);
        match.getZombiesPlayer().send("GAME_STATE", null, update);
        System.out.println("done");
    }
}
