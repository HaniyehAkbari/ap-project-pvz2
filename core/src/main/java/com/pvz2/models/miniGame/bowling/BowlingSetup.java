package com.pvz2.models.miniGame.bowling;

import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.card.PlantCard;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.cellTerrains.LandTerrain;
import com.pvz2.models.world.levelSetup.LevelSetup;
import com.pvz2.models.world.mechanics.ConveyorMechanic;
import com.pvz2.models.world.mechanics.NormalMechanic;
import com.pvz2.models.zombie.wave.Wave;
import com.pvz2.models.zombie.wave.WaveManager;
import com.pvz2.models.zombie.wave.WaveSpawnEntry;

import java.util.List;

public class BowlingSetup implements LevelSetup {
    private final int rows;
    private final int cols;
    private final int redLineCol;
    private final List<WaveSpawnEntry> availableZombies;
    private final int waveCount;
    private final int baseDifficulty;

    public BowlingSetup(int rows, int cols, int redLineCol,
                        List<WaveSpawnEntry> availableZombies,
                        int waveCount, int baseDifficulty) {
        this.rows = rows;
        this.cols = cols;
        this.redLineCol = redLineCol;
        this.availableZombies = availableZombies;
        this.waveCount = waveCount;
        this.baseDifficulty = baseDifficulty;
    }

    @Override
    public void groundSetup(GameWorld world) {
        world.setConveyorMode(true);
        world.setRows(rows);
        world.setCols(cols);

        Cell[][] grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell(r, c, new LandTerrain());
                if (c >= redLineCol) {
                    grid[r][c].setPlantable(false);
                }
            }
        }
        world.setGrid(grid);

        world.addMechanic(new BowlingMechanics());

        List<PlantCard> bowlingCards = List.of(
                new PlantCard(PlantType.WALL_NUT, 0, 0),
                new PlantCard(PlantType.EXPLODE_O_NUT, 0, 0),
                new PlantCard(PlantType.GIANT_WALLNUT, 0, 0)
        );

        world.addMechanic(new ConveyorMechanic(bowlingCards));

        List<Wave> waves = Wave.generateWaves(waveCount, baseDifficulty, availableZombies, 40);
        WaveManager waveManager = new WaveManager(waves);
        world.addMechanic(new NormalMechanic(waveManager));
    }

    @Override
    public boolean requirePlantSelection() {
        return false;
    }
}
