package com.pvz2.models.miniGame.vaseBreaker;

import com.pvz2.models.enums.PlantType;

public class SeedPacket {
    private static final int EXPIRE_AFTER_TICKS = 50;
    private float x;
    private float y;
    private PlantType plantType;
    private boolean collected;
    private int ticksAlive = 0;

    public SeedPacket(float x, float y, PlantType plantType) {
        this.x = x;
        this.y = y;
        this.plantType = plantType;
        this.collected = false;
    }

    public void tick() {
        if (!collected) ticksAlive++;
    }

    public boolean isExpired() {
        return !collected && ticksAlive >= EXPIRE_AFTER_TICKS;
    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public PlantType getPlantType() {
        return plantType;
    }


    public void collect() {
        this.collected = true;
    }
}
