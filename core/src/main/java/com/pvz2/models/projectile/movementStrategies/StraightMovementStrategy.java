package com.pvz2.models.projectile.movementStrategies;

import com.pvz2.models.projectile.Projectile;

public class StraightMovementStrategy implements MovementStrategy {
    float speedX;
    float speedY;
    float changeYAmount = 0;

    public StraightMovementStrategy(float speedX, float speedY, float changeYAmount) {
        this.speedX = speedX;
        this.speedY = speedY;
        this.changeYAmount = changeYAmount;
    }

    @Override
    public float changeOriginY() {
        return changeYAmount;
    }

    @Override
    public void move(Projectile projectile, float delta) {
        projectile.setX(projectile.getX() + (speedX * delta));
        projectile.setY(projectile.getY() + (speedY * delta));
    }

    @Override
    public boolean isDead(Projectile projectile) {
        return projectile.getX() > 2000 || projectile.getX() < 0 || projectile.getY() > 2000 || projectile.getY() < 0;
    }


}
