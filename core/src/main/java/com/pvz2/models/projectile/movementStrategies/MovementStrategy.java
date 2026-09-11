package com.pvz2.models.projectile.movementStrategies;

import com.pvz2.models.projectile.Projectile;

public interface MovementStrategy {
    float changeOriginY();

    void move(Projectile projectile, float delta);

    boolean isDead(Projectile projectile);
}
