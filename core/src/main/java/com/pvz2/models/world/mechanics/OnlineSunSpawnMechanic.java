package com.pvz2.models.world.mechanics;

import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.Sun;
import com.pvz2.models.world.SunType;

import java.util.Random;

public class OnlineSunSpawnMechanic implements Mechanic {
    private float lastSpawnTime = -8f;
    private float spawnInterval = 12f;

    @Override
    public void applyMechanic(GameWorld world) {
        float now = world.getElapsedTime();

        if (now - lastSpawnTime >= spawnInterval) {
            spawnRandomSun(world);
            spawnInterval = Math.max(6f, 12f - 0.02f * world.getElapsedTime());
            lastSpawnTime = now;
        }
    }

    private void spawnRandomSun(GameWorld world) {
        int rows = world.getRows() > 0 ? world.getRows() : 5;
        int cols = world.getCols() > 0 ? world.getCols() : 9;

        Random random = new Random();
        double r = random.nextDouble();
        SunType type;
        if (r < 0.75) {
            type = SunType.NORMAL;
        } else if (r < 0.90) {
            type = SunType.SPECIAL;
        } else {
            type = SunType.RADIOACTIVE;
        }

        int row = random.nextInt(rows);
        int col = random.nextInt(cols);

        Sun sun = world.getSunsPool().acquire();
        sun.setup(row, col, type);
        world.getActiveSuns().add(sun);
    }
}
