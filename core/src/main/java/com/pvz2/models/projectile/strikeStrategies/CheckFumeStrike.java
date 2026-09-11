package com.pvz2.models.projectile.strikeStrategies;

import com.pvz2.models.Damageable;
import com.pvz2.models.core.App;
import com.pvz2.models.world.obstacles.Obstacle;
import com.pvz2.models.zombie.Zombie;
import java.util.List;

public class CheckFumeStrike implements CheckStrike{
    private final float range;

    public CheckFumeStrike(float range) {
        this.range = range;
    }

    @Override
    public Damageable strike(double x, double y, double oldX, double oldY, List<Damageable> lastTargets) {
        for (Zombie zombie : App.getCurrentGame().getActiveZombies()) {
            if (lastTargets != null && lastTargets.contains(zombie)) {
                continue;
            }
            if (isBetween(x, y - App.getCellHeight()/2, x + range,
                y + App.getCellHeight()/2, zombie.getX(), zombie.getY())) {
                return zombie;
            }
        }
        for (Obstacle obstacle : App.getCurrentGame().getActiveObstacles()) {
            if (isBetween(x, y - App.getCellHeight()/2, x + range, y + App.getCellHeight()/2, obstacle.getX(),
                obstacle.getY())) {
                return obstacle;
            }
        }
        return null;
    }

    static boolean isBetween(double x, double y, double oldX, double oldY, float x2, float y2) {
        boolean xBetween =
            (x2 <= oldX && x2 >= x) || (x2 >= oldX && x2 <= x);
        boolean yBetween =
            (y2 <= oldY && y2 >= y) || (y2 >= oldY && y2 <= y);
        return xBetween && yBetween;
    }

    @Override
    public Damageable strike(double x, double y, Damageable zombie) {
        return null;
    }
}
