package com.pvz2.models.world.levelSetup;

import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.mechanics.NormalMechanic;
import com.pvz2.models.world.mechanics.SunSpawnMechanic;
import com.pvz2.models.zombie.wave.Wave;
import com.pvz2.models.zombie.wave.WaveManager;

import java.util.List;

public class NormalLevelSetup implements LevelSetup {
    private int rows;
    private int cols;
    private List<Wave> waves;

    public NormalLevelSetup(int rows, int cols, List<Wave> waves) {
        this.rows = rows;
        this.cols = cols;
        this.waves = waves;
    }

    @Override
    public void groundSetup(GameWorld world) {
        world.setConveyorMode(false);
        buildGrid(world, rows, cols);

        WaveManager waveManager = new WaveManager(waves);
        world.addMechanic(new NormalMechanic(waveManager));
        world.addMechanic(new SunSpawnMechanic());

        world.registerZombieKillListener(() -> waveManager.onZombieKilled());
    }

    @Override
    public boolean requirePlantSelection() {
        return true;
    }
}
