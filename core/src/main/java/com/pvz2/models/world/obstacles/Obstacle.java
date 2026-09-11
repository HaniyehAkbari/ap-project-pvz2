package com.pvz2.models.world.obstacles;

import com.pvz2.models.Damageable;
import com.pvz2.models.zombie.Zombie;

public abstract class Obstacle implements Damageable {
    protected float x, y;
    protected int health;
    protected boolean isDestroyed = false;

    public static final long DAMAGE_FLASH_DURATION_MS = 150L;
    private long lastDamageTimestamp = -1L;

    public Obstacle(float x, float y, int health) {
        this.x = x;
        this.y = y;
        this.health = health;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void takeDamage(int damage, Zombie zombie) {

    }

    @Override
    public void takeDamage(int amount, String type) {
        if (isDestroyed) return;

        triggerDamageFlash();
        this.health -= amount;
        if (this.health <= 0) {
            this.health = 0;
            this.isDestroyed = true;
        }
    }

    protected void triggerDamageFlash() {
        this.lastDamageTimestamp = System.currentTimeMillis();
    }

    public boolean isFlashingRed() {
        if (lastDamageTimestamp < 0) return false;
        return (System.currentTimeMillis() - lastDamageTimestamp) < DAMAGE_FLASH_DURATION_MS;
    }

    public float getDamageFlashProgress() {
        if (!isFlashingRed()) return 0f;
        long elapsed = System.currentTimeMillis() - lastDamageTimestamp;
        return 1f - ((float) elapsed / (float) DAMAGE_FLASH_DURATION_MS);
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public boolean blocksProjectiles() {
        return !isDestroyed;
    }

    public void die() {
        this.isDestroyed = true;
    }

    public int getHealth() {
        return health;
    }
}
