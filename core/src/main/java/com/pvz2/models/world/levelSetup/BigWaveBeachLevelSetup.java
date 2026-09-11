package com.pvz2.models.world.levelSetup;

import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.cellTerrains.WaterTerrain;
import com.pvz2.models.world.mechanics.NormalMechanic;
import com.pvz2.models.world.mechanics.SunSpawnMechanic;
import com.pvz2.models.zombie.wave.Wave;
import com.pvz2.models.zombie.wave.WaveManager;

import java.util.List;

public class BigWaveBeachLevelSetup implements LevelSetup {
    private final int tideLineCol;
    private int rows;
    private int cols;
    private List<Wave> waves;

    public BigWaveBeachLevelSetup(int tideLineCol, int rows, int cols, List<Wave> waves) {
        this.tideLineCol = tideLineCol;
        this.rows = rows;
        this.cols = cols;
        this.waves = waves;
    }

    @Override
    public void groundSetup(GameWorld game) {
        buildGrid(game, rows, cols);
        Cell[][] grid = game.getGrid();
        for (int r = 0; r < game.getRows(); r++) {
            grid[r][game.getCols() - 1].setTerrain(new WaterTerrain());
        }

        WaveManager waveManager = new WaveManager(waves);
        game.addMechanic(new NormalMechanic(waveManager));
        game.addMechanic(new SunSpawnMechanic());
        game.registerZombieKillListener(() -> waveManager.onZombieKilled());

    }

    @Override
    public boolean requirePlantSelection() {
        return true;
    }

    public int getTideLineCol() {
        return tideLineCol;
    }
}
