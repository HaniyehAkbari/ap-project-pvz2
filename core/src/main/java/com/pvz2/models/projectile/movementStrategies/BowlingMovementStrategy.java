package com.pvz2.models.projectile.movementStrategies;

import com.pvz2.models.Damageable;
import com.pvz2.models.core.App;
import com.pvz2.models.projectile.Projectile;

import java.util.Random;

public class BowlingMovementStrategy implements MovementStrategy {
    float speedX;
    float speedY;
    Random random = new Random();

    public BowlingMovementStrategy(float speedX, float speedY) {
        this.speedX = speedX;
        this.speedY = speedY;
    }

    @Override
    public float changeOriginY() {
        return 0;
    }

    @Override
    public void move(Projectile projectile, float delta) {
        if (projectile.getY() >= (App.getCellCenterY(4) + App.getCellHeight()/2) && speedY > 0) {
            speedY *= -1;
        } else if (projectile.getY() <= App.getFirstCellY() - App.getCellHeight()/2 && speedY < 0) {
            speedY *= -1;
        }
        projectile.setX(projectile.getX() + (speedX * delta));
        projectile.setY(projectile.getY() + (speedY * delta));
    }

    public void onHit(Damageable target, Projectile projectile) {
        if (target != null && !projectile.getLastTarget().contains(target)) {
            int sign;
            if (speedY == 0) {
                if (target.getY() == App.getCellCenterY(0)) {
                    sign = 1;
                } else if (target.getY() == App.getCellCenterY(4)) {
                    sign = -1;
                } else {
                    sign = random.nextInt(2) * 2 - 1;
                }
                speedY = (float) (speedX * Math.sqrt(2) / 2 * sign);
                speedX = (float) (speedX * Math.sqrt(2) / 2);
            } else {
                speedY *= -1;
            }
        }
    }

    @Override
    public boolean isDead(Projectile projectile) {
        return projectile.getX() > 2000 || projectile.getX() < 0 || projectile.getY() > 2000 || projectile.getY() < 0;
    }
}
