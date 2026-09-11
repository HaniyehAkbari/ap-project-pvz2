package com.pvz2.models.miniGame.vaseBreaker;

import com.pvz2.models.zombie.Zombie;

public class Vase {
    private int row;
    private int col;
    private boolean isBroken;
    private VaseType type;
    private Zombie hiddenZombie;
    private SeedPacket hiddenSeed;


    public Vase(int row, int col, VaseType type, Zombie hiddenZombie, SeedPacket hiddenSeed) {
        this.row = row;
        this.col = col;
        this.isBroken = false;
        this.type = type;
        this.hiddenZombie = hiddenZombie;
        this.hiddenSeed = hiddenSeed;
    }

    public String breakVase(VaseBreakerLevel level) {
        if (isBroken) return "This vase is already broken.";
        isBroken = true;

        float spawnX = col * 100 + 50;
        float spawnY = row * 100 + 50;

        if (hiddenZombie != null) {
            hiddenZombie.setX(spawnX);
            hiddenZombie.setY(spawnY);
            level.addZombie(hiddenZombie);
            return "A " + hiddenZombie.getName().name() + " zombie jumps out!";
        }

        if (hiddenSeed != null) {
            level.getDroppedSeeds().add(hiddenSeed);
            return "You found a " + hiddenSeed.getPlantType().name() + " seed packet!";
        }
        return "The vase was empty.";

    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isBroken() {
        return isBroken;
    }

    public VaseType getType() {
        return type;
    }

    public SeedPacket getHiddenSeed() {
        return hiddenSeed;
    }

    public Zombie getHiddenZombie() {
        return hiddenZombie;
    }
}
