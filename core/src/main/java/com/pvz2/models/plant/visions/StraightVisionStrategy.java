package com.pvz2.models.plant.visions;

import com.pvz2.models.Damageable;
import com.pvz2.models.core.App;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.ChapterWorld.FrostbiteCavesWorld;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.obstacles.Obstacle;
import com.pvz2.models.zombie.Zombie;

public class StraightVisionStrategy implements VisionStrategy {
    private final float range;
    private final float width;
    private final boolean needZombie;

    public StraightVisionStrategy(float range, float width, boolean needZombie) {
        this.range = range;
        this.width = width;
        this.needZombie = needZombie;
    }

    @Override
    public Damageable findZombie(Plant owner) {
        return needZombie ? findClosest(owner) : findAny(owner);
    }

    private Damageable findAny(Plant owner) {
        GameWorld world = App.getCurrentGame(owner);

        for (Zombie zombie : world.getActiveZombies()) {
            if (inRange(owner, zombie.getX(), zombie.getY())) return zombie;
        }
        for (Obstacle obstacle : world.getActiveObstacles()) {
            if (inRange(owner, obstacle.getX(), obstacle.getY())) return obstacle;
        }
        if (world instanceof FrostbiteCavesWorld) {
            for (Plant plant : world.getActivePlants()) {
                if (plant.isFreeze() && inRange(owner, plant.getX(), plant.getY())) {
                    return plant;
                }
            }
        }
        return null;
    }


    private Damageable findClosest(Plant owner) {
        GameWorld world = App.getCurrentGame(owner);
        float minX = Float.MAX_VALUE;
        Damageable closest = null;

        for (Zombie zombie : world.getActiveZombies()) {
            if (inRange(owner, zombie.getX(), zombie.getY()) && zombie.getX() < minX) {
                minX = zombie.getX();
                closest = zombie;
            }
        }

        if (closest == null && world instanceof FrostbiteCavesWorld) {
            for (Plant plant : world.getActivePlants()) {
                if (plant.isFreeze() && inRange(owner, plant.getX(), plant.getY()) && plant.getX() < minX) {
                    minX = plant.getX();
                    closest = plant;
                }
            }
        }

        if (closest == null) {
            for (Obstacle obstacle : world.getActiveObstacles()) {
                if (inRange(owner, obstacle.getX(), obstacle.getY()) && obstacle.getX() < minX) {
                    minX = obstacle.getX();
                    closest = obstacle;
                }
            }
        }

        return closest;
    }


    private boolean inRange(Plant owner, float x, float y) {
        return VisionStrategy.isBetween(x, owner.getX(), owner.getX() + range) &&
            VisionStrategy.isBetween(y, owner.getY() - width / 2, owner.getY() + width / 2);
    }
}
