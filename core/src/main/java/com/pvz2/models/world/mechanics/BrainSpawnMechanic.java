package com.pvz2.models.world.mechanics;

import com.pvz2.models.miniGame.IZombie.OnlineIZombieLevel;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.network.onlineIZombie.BrainCurrency;

import java.util.Random;

public class BrainSpawnMechanic implements Mechanic {
    private float lastSpawnTime = -4f;
    private final float spawnInterval;

    public BrainSpawnMechanic() {
        this(8f);
    }

    public BrainSpawnMechanic(float spawnInterval) {
        this.spawnInterval = spawnInterval;
    }

    @Override
    public void applyMechanic(GameWorld world) {
        if (!(world instanceof OnlineIZombieLevel level)) return;

        float now = level.getElapsedTime();
        if (now - lastSpawnTime >= spawnInterval) {
            spawnRandomBrain(level);
            lastSpawnTime = now;
        }
    }

    private void spawnRandomBrain(OnlineIZombieLevel level) {
        int rows = level.getRows() > 0 ? level.getRows() : 5;
        int cols = level.getCols() > 0 ? level.getCols() : 9;
        int redLineCol = level.getRedLineCol();

        if (redLineCol >= cols) return;

        Random random = new Random();
        int row = random.nextInt(rows);
        int col = redLineCol + random.nextInt(cols - redLineCol);

        BrainCurrency brain = level.getBrainsPool().acquire();
        brain.setup(row, col);
        level.getActiveBrains().add(brain);
    }
}
