package com.pvz2.models.plant.card;

import com.pvz2.models.enums.PlantType;

public class PlantCard {
    private final PlantType type;
    private int sunCost;
    private float maxCooldown;
    private float currentCooldown = 0;
    private boolean ready = true;
    private boolean activeCooldown = true;

    public PlantCard(PlantType type, int sunCost, float maxCooldownTicks) {
        this.type = type;
        this.sunCost = sunCost;
        this.maxCooldown = maxCooldownTicks;
    }

    public void update(float delta) {
        if (!activeCooldown) return;
        if (ready) return;
        currentCooldown += delta;

        if (currentCooldown >= maxCooldown) {

            ready = true;
            currentCooldown = 0;
        }
    }

    public PlantType getType() {
        return type;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        if (!ready && !activeCooldown) return;
        this.ready = ready;
    }

    public void reset() {
        ready = true;
        currentCooldown = 0;
    }

    public int getSunCost() {
        return sunCost;
    }

    public void setSunCost(int sunCost) {
        this.sunCost = sunCost;
    }

    public float getMaxCooldownTicks() {
        return maxCooldown;
    }

    public void setMaxCooldownTicks(float maxCooldown) {
        this.maxCooldown = maxCooldown;
    }

    public float getCurrentCooldownTicks() {
        return currentCooldown;
    }


}
