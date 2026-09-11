package com.pvz2.models.projectile.strikeStrategies;

import com.pvz2.models.Damageable;
import com.pvz2.models.core.App;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.obstacles.Obstacle;
import com.pvz2.models.zombie.Zombie;
import java.util.Comparator;
import java.util.List;

public class CheckStraightStrike implements CheckStrike {
    private transient GameWorld currentWorld;

    @Override
    public Damageable strike(double x, double y, double oldX, double oldY, List<Damageable> lastTargets) {
        List<Zombie> sortedZombies = App.getCurrentGame(this).getActiveZombies().stream()
                .sorted(Comparator.comparingDouble(Zombie::getX))
                .toList();
        for (Zombie zombie : sortedZombies) {
            if (lastTargets != null && lastTargets.contains(zombie)) {
                continue;
            }
            if (isCollidingWithCircle(oldX, oldY, x, y, zombie.getX(), zombie.getY(), 40)) {
                return zombie;
            }
        }
        List<Obstacle> sortedObstacle = App.getCurrentGame(this).getActiveObstacles().stream()
            .sorted(Comparator.comparingDouble(Obstacle::getX))
            .toList();
        for (Obstacle obstacle : sortedObstacle) {
            if (isCollidingWithCircle(oldX, oldY, x, y, obstacle.getX(), obstacle.getY(), 40)) {
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

    private boolean isCollidingWithCircle(double x1, double y1, double x2, double y2,
                                          double cx, double cy, double radius) {
        double l2 = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);

        if (l2 == 0) {
            return Math.hypot(cx - x1, cy - y1) <= radius;
        }

        double t = ((cx - x1) * (x2 - x1) + (cy - y1) * (y2 - y1)) / l2;
        t = Math.max(0, Math.min(1, t));

        double closestX = x1 + t * (x2 - x1);
        double closestY = y1 + t * (y2 - y1);

        double distanceSquared = (cx - closestX) * (cx - closestX) + (cy - closestY) * (cy - closestY);
        return distanceSquared <= (radius * radius);
    }

    @Override
    public void setWorld(GameWorld world) {
        if (currentWorld == null || !currentWorld.equals(world)){
            currentWorld = world;
        }
    }

    public GameWorld getCurrentWorld() {
        return currentWorld;
    }

    @Override
    public Damageable strike(double x, double y, Damageable zombie) {
        return null;
    }
}
