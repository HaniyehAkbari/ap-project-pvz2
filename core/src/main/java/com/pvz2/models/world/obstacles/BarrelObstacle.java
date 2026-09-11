package com.pvz2.models.world.obstacles;

public class BarrelObstacle extends Obstacle {

    public BarrelObstacle(float x, float y, int health) {
        super(x, y, health);
    }

    @Override
    public boolean blocksProjectiles() {
        return true;
    }
}
