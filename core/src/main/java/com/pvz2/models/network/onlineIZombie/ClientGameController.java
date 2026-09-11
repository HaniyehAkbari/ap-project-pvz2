package com.pvz2.models.network.onlineIZombie;

import com.badlogic.gdx.Gdx;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.miniGame.IZombie.OnlineIZombieLevel;
import com.pvz2.models.network.onlineIZombie.messages.*;
import com.pvz2.models.plant.card.PlantCard;
import com.pvz2.models.network.NetworkClient;
import com.pvz2.models.network.NetworkMessage;
import com.pvz2.view.screen.MenuScreen;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ClientGameController {
    public enum Side { PLANTS, ZOMBIES }

    private final String matchId;
    private final Side side;
    private final String opponentUsername;

    private OnlineIZombieLevel world;
    private boolean shovelSelected = false;
    private boolean plantSelected = false;
    private PlantType selectedPlantType = null;
    private boolean zombieSelected = false;
    private String selectedZombieType = null;
    private BiConsumer<Boolean, String> actionResultListener;
    private BiConsumer<Side, String> matchOverListener;
    private Consumer<ReactionReceived> reactionListener;
    private Runnable onWorldReadyListener;
    private boolean worldReadyFired = false;



    public ClientGameController(MatchFound info) {
        this.matchId = info.matchId;
        this.side = Side.valueOf(info.side);
        this.opponentUsername = info.opponentUsername;
        NetworkClient.get().onPush("GAME_STATE", this::onGameStatePush);
        NetworkClient.get().onPush("ACTION_RESULT", this::onActionResult);
        NetworkClient.get().onPush("MATCH_OVER", this::onMatchOver);
        NetworkClient.get().onPush("REACTION", this::onReaction);
    }

    public void setActionResultListener(BiConsumer<Boolean, String> listener) {
        this.actionResultListener = listener;
    }

    public void setMatchOverListener(BiConsumer<Side, String> listener) {
        this.matchOverListener = listener;
    }

    public void setReactionListener(Consumer<ReactionReceived> listener) {
        this.reactionListener = listener;
    }

    public Side getSide() { return side; }
    public String getOpponentUsername() { return opponentUsername; }
    public OnlineIZombieLevel getWorld() { return world; }

    private void onGameStatePush(NetworkMessage msg) {
        GameStateUpdate update = NetworkClient.get().parsePayload(msg, GameStateUpdate.class);
        if (!matchId.equals(update.matchId)) return;
        boolean wasNull = (world == null);
        world = update.world;
        if (wasNull && onWorldReadyListener != null && !worldReadyFired) {
            worldReadyFired = true;
            onWorldReadyListener.run();
        }

    }

    private void onActionResult(NetworkMessage msg) {
        ActionResult result = NetworkClient.get().parsePayload(msg, ActionResult.class);
        Gdx.app.postRunnable(() -> {
            if (actionResultListener != null) actionResultListener.accept(result.success, result.message);
        });
    }

    private void onMatchOver(NetworkMessage msg) {
        MatchOver update = NetworkClient.get().parsePayload(msg, MatchOver.class);
        if (!matchId.equals(update.matchId)) return;
        Gdx.app.postRunnable(() -> {
            if (matchOverListener != null) matchOverListener.accept(Side.valueOf(update.winnerSide), update.message);
        });

    }

    private void onReaction(NetworkMessage msg) {
        ReactionReceived received = NetworkClient.get().parsePayload(msg, ReactionReceived.class);
        if (!matchId.equals(received.matchId)) return;
        Gdx.app.postRunnable(() -> {
            if (reactionListener != null) reactionListener.accept(received);
        });

    }

    public void unselectShovel() {
        shovelSelected = false;
    }

    public boolean selectAndUnselectShovel() {
        if (side == Side.ZOMBIES) return false;
        unselectPlant();
        shovelSelected = !shovelSelected;
        return true;
    }

    public boolean selectAndUnselectPlant(PlantType type, MenuScreen screen) {
        if (world == null) return false;
        if (side == Side.ZOMBIES) return false;
        if (plantSelected) {
            unselectShovel();
            if (selectedPlantType == type) {
                unselectPlant();
                return false;
            }
            unselectPlant();
        }
        PlantCard selectedCard = null;
        for (PlantCard card : world.getPlantLists()) {
            if (card.getType().equals(type)) { selectedCard = card; break; }
        }
        if (selectedCard == null) return false;
        if (!selectedCard.isReady()) {
            screen.addToast("Error", "This plant isn't ready!");
            return false;
        }
        if (selectedCard.getSunCost() > world.getSun()) {
            screen.addToast("Error", "You don't have enough suns!");
            return false;
        }
        plantSelected = true;
        selectedPlantType = type;
        return true;
    }

    public void unselectPlant() {
        plantSelected = false;
        selectedPlantType = null;
    }

    public boolean selectAndUnselectZombie(String type, MenuScreen screen) {
        if (world == null) return false;
        if (side == Side.PLANTS) return false;
        if (zombieSelected) {
            if (selectedZombieType.equals(type)) {
                unselectZombie();
                return false;
            }
            unselectZombie();
        }
        ZombieCard selectedCard = null;
        for (ZombieCard card : world.getZombieCards()) {
            if (card.getType().equals(type)) { selectedCard = card; break; }
        }
        if (selectedCard == null) return false;
        if (!selectedCard.isReady()) {
            screen.addToast("Error", "This zombie isn't ready!");
            return false;
        }
        if (selectedCard.getBrainCost() > world.getZombieBrains()) {
            screen.addToast("Error", "You don't have enough brains!");
            return false;
        }
        zombieSelected = true;
        selectedZombieType = type;
        return true;
    }

    public void unselectZombie() {
        zombieSelected = false;
        selectedZombieType = null;
    }

    public void setOnWorldReadyListener(Runnable listener) {
        this.onWorldReadyListener = listener;
        if (world != null && !worldReadyFired) {
            worldReadyFired = true;
            listener.run();
        }
    }

    public void plantPlant(float x, float y) {
        if (side != Side.PLANTS || !plantSelected) return;
        NetworkClient.get().sendMessage("PLANT_PLANT", new PlantPlantRequest(matchId, selectedPlantType, x, y));
        unselectPlant();
    }

    public void pluckPlant(float x, float y) {
        if (side != Side.PLANTS || !shovelSelected) return;
        NetworkClient.get().sendMessage("PLUCK_PLANT", new PluckPlantRequest(matchId, x, y));
    }

    public void collectSun(float touchX, float touchY) {
        if (side != Side.PLANTS) return;
        NetworkClient.get().sendMessage("COLLECT_SUN", new CollectSunRequest(matchId, touchX, touchY));
    }

    public void collectBrain(float touchX, float touchY) {
        if (side != Side.ZOMBIES) return;
        NetworkClient.get().sendMessage("COLLECT_BRAIN", new CollectBrainRequest(matchId, touchX, touchY));
    }

    public void placeZombie(String zombieType, float x, float y) {
        if (side != Side.ZOMBIES) return;
        NetworkClient.get().sendMessage("PLACE_ZOMBIE", new PlaceZombieRequest(matchId, zombieType, x, y));
    }

    public void sendReaction(ReactionCategory category, int index){
        NetworkClient.get().sendMessage("SEND_REACTION", new SendReactionRequest(matchId, category, index));
    }

}
