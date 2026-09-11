package com.pvz2.models.world.levelSetup;

import com.pvz2.models.world.ChapterWorld.AncientEgyptWorld;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.mechanics.NormalMechanic;
import com.pvz2.models.world.mechanics.SunSpawnMechanic;
import com.pvz2.models.zombie.wave.Wave;
import com.pvz2.models.zombie.wave.WaveManager;

import java.util.List;

public class DeadLineLevelSetup implements LevelSetup {
    private int rows;
    private int cols;
    private int deadLineCol;
    private List<Wave> waves;

    public DeadLineLevelSetup(int rows, int cols, int deadLineCol, List<Wave> waves) {
        this.rows = rows;
        this.cols = cols;
        this.deadLineCol = deadLineCol;
        this.waves = waves;
    }

    @Override
    public void groundSetup(GameWorld world) {
        world.setConveyorMode(false);
        buildGrid(world, rows, cols);

        ((AncientEgyptWorld) world).setDeadLineCol(deadLineCol);

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
