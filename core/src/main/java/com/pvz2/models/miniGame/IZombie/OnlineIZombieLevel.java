package com.pvz2.models.miniGame.IZombie;

import com.pvz2.models.plant.card.PlantCard;
import com.pvz2.models.pool.GenericObjectPool;
import com.pvz2.models.projectile.Projectile;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameState;
import com.pvz2.models.world.Sun;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.network.onlineIZombie.BrainCurrency;
import com.pvz2.models.world.levelSetup.LevelSetup;
import com.pvz2.models.world.loseCondition.LoseCondition;
import com.pvz2.models.world.mechanics.Mechanic;
import com.pvz2.models.world.winCondition.WinCondition;
import com.pvz2.models.network.onlineIZombie.ZombieCard;
import com.pvz2.view.util.LawnGrid;

import java.util.ArrayList;
import java.util.List;

public class OnlineIZombieLevel extends IZombieLevel {
    private int zombieBrains = 0;
    private List<BrainCurrency> activeBrains = new ArrayList<>();
    private List<ZombieCard> zombieCards = new ArrayList<>();
    private transient GenericObjectPool<BrainCurrency> brainsPool = new GenericObjectPool<>(BrainCurrency::new);

    public OnlineIZombieLevel(LevelSetup levelSetup,
                               ArrayList<LoseCondition> loseConditions,
                               WinCondition winCondition,
                               ArrayList<Mechanic> mechanics) {
        super(levelSetup, loseConditions, winCondition, mechanics);
        Cell.setCurrentWorld(this);
    }

    @Override
    protected void updateAll(float delta) {
        activePlants.forEach(plant -> plant.update(delta, this));
        List<Projectile> projectileSnapshot = new ArrayList<>(activeProjectiles);
        projectileSnapshot.forEach(projectile -> projectile.update(delta, this));
        List<Zombie> zombieSnapshot = new ArrayList<>(activeZombies);
        zombieSnapshot.forEach(zombie -> zombie.update(delta, this));
        for (PlantCard card : plantLists) card.update(delta);
        for (Sun sun : activeSuns) {
            sun.update(delta, this);
            if (sun.getProducer() == null && sun.isExpired()) {
                sun.collect();
            }
        }
        for (Cell[] cells : grid){
            for (Cell cell : cells){
                cell.update(delta);
            }
        }
    }

    @Override
    public void tick(float delta) {
        if (state != GameState.PLAYING) return;
        elapsedTime += delta;
        updateAll(delta);
        for (ZombieCard zombieCard : zombieCards) zombieCard.update(delta);
        for (BrainCurrency brainCurrency : activeBrains) {
            brainCurrency.update(delta);
            if (brainCurrency.isExpired()) {
                brainCurrency.collect();
            }
        }
        removeIfDead();
        activeBrains.removeIf(brain -> {
            if (brain.isCollected()) {
                brainsPool.release(brain);
                return true;
            }
            return false;
        });
        for (Zombie zombie : getActiveZombies()) {
            if (zombie.isDead()) continue;

            int row = LawnGrid.getRowFromY(zombie.getY());
            Brain brain = getBrainAtRow(row);
            if (brain != null && !brain.isEaten()) {
                if (zombie.getX() <= brain.getX() + 20) {
                    brain.eat();
                    zombie.eatBrainAndLeave();
                }
            }
        }
        for (Mechanic mechanic : mechanics) {
            mechanic.applyMechanic(this);
        }
    }

    public int getZombieBrains() {
        return zombieBrains;
    }

    public void addBrainsToPlayer(int amount) {
        this.zombieBrains += amount;
    }

    public List<BrainCurrency> getActiveBrains() {
        if (activeBrains == null) activeBrains = new ArrayList<>();
        return activeBrains;
    }

    public List<ZombieCard> getZombieCards() {
        if (zombieCards == null || zombieCards.isEmpty()) {
            zombieCards = new ArrayList<>();
            OnlineIZombieSetup setup = (OnlineIZombieSetup) getLevelSetup();
            for (Zombie zombie : setup.getStageZombies()) {
                zombieCards.add(new ZombieCard(zombie.getSpecificName(),
                    setup.getBrainCost(zombie.getSpecificName())));
            }
        }
        return zombieCards;
    }

    public GenericObjectPool<BrainCurrency> getBrainsPool() {
        if (brainsPool == null) brainsPool = new GenericObjectPool<>(BrainCurrency::new);
        return brainsPool; }

}
