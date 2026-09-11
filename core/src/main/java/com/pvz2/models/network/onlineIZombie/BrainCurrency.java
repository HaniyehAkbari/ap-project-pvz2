package com.pvz2.models.network.onlineIZombie;

import com.pvz2.models.core.App;
import com.pvz2.models.pool.Resettable;

import java.util.UUID;

public class BrainCurrency implements Resettable {
    private String id = UUID.randomUUID().toString();

    private float x, y;
    private float finalX, finalY;
    private float fallSpeed = 200f;
    private float groundedTimer = 0f;
    private static final float DESPAWN_TIME = 10f;

    private enum FallPhase { SKY_FALL, GROUNDED }
    private FallPhase fallPhase = FallPhase.SKY_FALL;

    private boolean collected = false;
    private boolean expired = false;
    private float animTime = 0f;

    public void setup(int row, int col) {
        this.id = UUID.randomUUID().toString();

        this.finalX = App.getCellCenterX(col);
        this.finalY = App.getCellCenterY(row);

        this.x = finalX;
        this.y = 1050f;

        this.collected = false;
        this.expired = false;
        this.groundedTimer = 0f;
        this.animTime = 0f;
        this.fallPhase = FallPhase.SKY_FALL;
    }

    public void update(float delta) {
        if (collected || expired) return;

        animTime += delta;

        switch (fallPhase) {
            case SKY_FALL -> {
                if (y > finalY) {
                    y -= fallSpeed * delta;
                    if (y <= finalY) {
                        y = finalY;
                        fallPhase = FallPhase.GROUNDED;
                        groundedTimer = 0f;
                    }
                } else {
                    tickDespawn(delta);
                }
            }
            case GROUNDED -> tickDespawn(delta);
        }
    }

    private void tickDespawn(float delta) {
        groundedTimer += delta;
        if (groundedTimer >= DESPAWN_TIME) {
            expired = true;
        }
    }

    public void collect() {
        this.collected = true;
        this.expired = true;
    }

    public boolean isExpired() { return expired || collected; }
    public boolean isCollected() { return collected; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getAnimTime() { return animTime; }
    public String getId() { return id; }

    public boolean contains(float px, float py, float radius) {
        float dx = px - x, dy = py - y;
        return dx * dx + dy * dy <= radius * radius;
    }

    @Override
    public void reset(float x, float y) {
        this.collected = false;
        this.expired = false;
    }

    @Override
    public void reset(float x, float y, int size, com.pvz2.models.plant.components.SunProducerComponent component) {
    }

    @Override
    public void reset(float x, float y, com.pvz2.models.projectile.hitStrategies.HitStrategy hitStrategy,
                      com.pvz2.models.projectile.movementStrategies.MovementStrategy movementStrategy,
                      com.pvz2.models.projectile.strikeStrategies.CheckStrike checkStrike,
                      com.pvz2.models.enums.ProjectileType type) {
    }
}
