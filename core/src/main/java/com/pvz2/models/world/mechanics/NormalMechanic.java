package com.pvz2.models.world.mechanics;

import com.pvz2.models.world.GameWorld;
import com.pvz2.models.zombie.wave.WaveManager;

import java.util.Random;

public class NormalMechanic implements Mechanic {
    private WaveManager waveManager;
    private Random random = new Random();

    public NormalMechanic(WaveManager waveManager) {
        this.waveManager = waveManager;
    }

    @Override
    public void applyMechanic(GameWorld world) {
        if (!waveManager.update()) {
            waveManager.spawnNextZombie(random.nextInt(world.getRows()), world);
        }
    }

    public WaveManager getWaveManager() {
        return waveManager;
    }
}
