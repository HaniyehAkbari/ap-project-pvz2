package com.pvz2.models.greenhouse;

import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.Plant;

public class Pot {
    private final int x;
    private final int y;
    private boolean isLocked;
    private Plant plant;
    private PlantType plantType;
    private long plantedTime;
    private boolean isReady;

    public Pot(int x, int y, boolean isLocked) {
        this.x = x;
        this.y = y;
        this.isLocked = isLocked;
        this.plant = null;
        this.plantType = null;
        this.plantedTime = 0;
        this.isReady = false;
    }

    public boolean isEmpty() {
        return plant == null && plantType == null;
    }

    public void plant(Plant p, PlantType type) {
        this.plant = p;
        this.plantType = type;
        this.plantedTime = System.currentTimeMillis();
        this.isReady = false;
    }

    public void clear() {
        this.plant = null;
        this.plantType = null;
        this.plantedTime = 0;
        this.isReady = false;
    }

    public long getRemainingHours() {
        if (plant == null && plantType == null) return 0;
        long elapsedMillis = System.currentTimeMillis() - plantedTime;
        long requiredHours = (plantType == PlantType.MARIGOLD) ? 2 : 8;
        long requiredMillis = requiredHours * 3600 * 1000;
        long remainingMillis = requiredMillis - elapsedMillis;
        if (remainingMillis <= 0) {
            isReady = true;
            return 0;
        }
        return (long) Math.ceil(remainingMillis / (3600.0 * 1000));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public Plant getPlant() {
        return plant;
    }

    public PlantType getPlantType() {
        return plantType;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }
}
