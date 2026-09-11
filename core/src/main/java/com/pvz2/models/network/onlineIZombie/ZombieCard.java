package com.pvz2.models.network.onlineIZombie;

public class ZombieCard {
    private final String type;
    private int brainCost;
    private final static float MAX_COOLDOWN = 10;
    private float currentCooldown = 0;
    private boolean ready = true;

    public ZombieCard(String type, int brainCost) {
        this.type = type;
        this.brainCost = brainCost;
    }

    public void update(float delta) {
        if (ready) return;
        currentCooldown += delta;

        if (currentCooldown >= MAX_COOLDOWN) {

            ready = true;
            currentCooldown = 0;
        }
    }

    public String getType() {
        return type;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void reset() {
        ready = true;
        currentCooldown = 0;
    }

    public int getBrainCost() {
        return brainCost;
    }

    public float getMaxCooldownTicks() {
        return MAX_COOLDOWN;
    }

    public float getCurrentCooldownTicks() {
        return currentCooldown;
    }

}
