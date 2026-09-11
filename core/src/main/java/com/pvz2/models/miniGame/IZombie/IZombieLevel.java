package com.pvz2.models.miniGame.IZombie;

import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.levelSetup.LevelSetup;
import com.pvz2.models.world.loseCondition.LoseCondition;
import com.pvz2.models.world.mechanics.Mechanic;
import com.pvz2.models.world.winCondition.WinCondition;
import com.pvz2.models.zombie.Zombie;

import java.util.ArrayList;
import java.util.List;

public class IZombieLevel extends GameWorld {
    private List<Zombie> availableZombies;
    private List<Brain> brains;
    private List<SunProducer> sunProducers;
    private int redLineCol;

    public int getRedLineCol() {
        return redLineCol;
    }

    public IZombieLevel(LevelSetup levelSetup,
                        ArrayList<LoseCondition> loseConditions,
                        WinCondition winCondition,
                        ArrayList<Mechanic> mechanics) {
        super(levelSetup, loseConditions, winCondition, mechanics);
        this.redLineCol = 5;
        setSun(150);
    }

    @Override
    protected void applyChapterRules() {
        setSun(150);
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);

        for (SunProducer sp : getSunProducers()) {
            if (!sp.isDead() && getActiveZombies().contains(sp)) {
                sp.updateSunGeneration(this);
            }
        }

        for (Zombie zombie : getActiveZombies()) {
            if (zombie.isDead()) continue;

            int row = (int) (zombie.getY() / 100);
            Brain brain = getBrainAtRow(row);
            if (brain != null && !brain.isEaten()) {
                if (zombie.getX() <= brain.getX() + 20) {
                    brain.eat();
                    zombie.eatBrainAndLeave();
                }
            }
        }
    }

    public int getZombieCost(Zombie zombie) {
        String typeName = zombie.getName().name();
        return switch (typeName) {
            case "NORMAL", "ZombieDefault" -> 50;
            case "ZombieArmor1" -> 150;
            case "ZombieArmor2" -> 175;
            case "ZombieBarrelRoller" -> 300;
            case "ZombieDarkImpDragon" -> 25;
            default -> 75;
        };
    }

    public Brain getBrainAtRow(int row) {
        for (Brain brain : getBrains()) {
            if (brain.getRow() == row) return brain;
        }
        return null;
    }

    public int getMinZombieCost() {
        int minCost = Integer.MAX_VALUE;
        for (Zombie z : getAvailableZombies()) {
            int cost = getZombieCost(z);
            if (cost < minCost) minCost = cost;
        }
        return minCost == Integer.MAX_VALUE ? 50 : minCost;
    }

    @Override
    public void addSunToPlayer(int amount) {
        setSun(getSun() + amount);
    }

    public List<Brain> getBrains() {
        if (brains == null) brains = new ArrayList<>();
        return brains;
    }

    public List<SunProducer> getSunProducers() {
        if (sunProducers == null) sunProducers = new ArrayList<>();
        return sunProducers;
    }

    public List<Zombie> getAvailableZombies() {
        if (availableZombies == null) availableZombies = new ArrayList<>();
        return availableZombies;
    }

    public void setAvailableZombies(List<Zombie> availableZombies) {
        this.availableZombies = availableZombies;
    }
}
