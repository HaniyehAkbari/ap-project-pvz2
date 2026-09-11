package com.pvz2.models.projectile.movementStrategies;

import com.pvz2.models.projectile.Projectile;

public class LobbedMovementStrategy implements MovementStrategy {
    private static final float DEFAULT_ARC_HEIGHT = 250.0f;
    private static final float DEFAULT_TRAVEL_DURATION = 1.2f;

    private final float maxArcHeight;
    private final float travelDuration;
    private float elapsed = 0f;

    public LobbedMovementStrategy() {
        this(DEFAULT_ARC_HEIGHT, DEFAULT_TRAVEL_DURATION);
    }

    public LobbedMovementStrategy(float maxArcHeight, float travelDuration) {
        this.maxArcHeight = maxArcHeight;
        this.travelDuration = travelDuration;
    }

    @Override
    public float changeOriginY() {
        return 0;
    }

    @Override
    public void move(Projectile projectile, float delta) {
        elapsed += delta;
        float t = Math.min(1f, elapsed / travelDuration);

        float linearX = projectile.getOriginX() + t * (projectile.getTargetX() - projectile.getOriginX());
        float linearY = projectile.getOriginY() + t * (projectile.getTargetY() - projectile.getOriginY());
        float arcY = (float) (4 * maxArcHeight * t * (1.0 - t));

        projectile.setX(linearX);
        projectile.setY(linearY + arcY);
    }

    @Override
    public boolean isDead(Projectile projectile) {
        return elapsed >= travelDuration;
    }
}
