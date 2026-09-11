package com.pvz2.models.plant.visions;

import com.pvz2.models.Damageable;
import com.pvz2.models.core.App;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.ChapterWorld.FrostbiteCavesWorld;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.obstacles.Obstacle;
import com.pvz2.models.zombie.Zombie;

public class RotatedVisionStrategy implements VisionStrategy {
    private final float angle;
    private final float width;
    private final float range;

    public RotatedVisionStrategy(float angle, float width, float range) {
        this.angle = angle;
        this.width = width;
        this.range = range;
    }


    @Override
    public Damageable findZombie(Plant owner) {
        GameWorld gameWorld = App.getCurrentGame();

        for (Zombie zombie : gameWorld.getActiveZombies()) {
            float xRel = zombie.getX() - owner.getX();
            float yRel = zombie.getY() - owner.getY();
            double xPrime = xRel * Math.cos(angle) + yRel * Math.sin(angle);
            double yPrime = -xRel * Math.sin(angle) + yRel * Math.cos(angle);

            if ((xPrime > 0 && xPrime < range) && (yPrime > -width / 2 && yPrime < width / 2)) {
                return zombie;
            }
        }
        for (Obstacle obstacle : gameWorld.getActiveObstacles()) {
            float xRel = obstacle.getX() - owner.getX();
            float yRel = obstacle.getY() - owner.getY();
            double xPrime = xRel * Math.cos(angle) + yRel * Math.sin(angle);
            double yPrime = -xRel * Math.sin(angle) + yRel * Math.cos(angle);

            if ((xPrime > 0 && xPrime < range) && (yPrime > -width / 2 && yPrime < width / 2)) {
                return obstacle;
            }
        }
        if (gameWorld instanceof FrostbiteCavesWorld){
            for (Plant plant : gameWorld.getActivePlants()) {
                if (!plant.isFreeze()) continue;
                float xRel = plant.getX() - owner.getX();
                float yRel = plant.getY() - owner.getY();
                double xPrime = xRel * Math.cos(angle) + yRel * Math.sin(angle);
                double yPrime = -xRel * Math.sin(angle) + yRel * Math.cos(angle);

                if ((xPrime > 0 && xPrime < range) && (yPrime > -width / 2 && yPrime < width / 2)) {
                    return plant;
                }
            }
        }
        return null;
    }

}
