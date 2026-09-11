package com.pvz2.models.world.levelSetup;

import com.pvz2.models.enums.PlantLayer;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.factory.PlantFactory;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.cellTerrains.LandTerrain;
import com.pvz2.models.world.mechanics.NormalMechanic;
import com.pvz2.models.world.mechanics.SunSpawnMechanic;
import com.pvz2.models.zombie.wave.Wave;
import com.pvz2.models.zombie.wave.WaveManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SaveOurSeedsLevelSetup implements LevelSetup {
    private int rows;
    private int cols;
    private List<Wave> waves;
    private Map<Point, PlantType> protectedPlants;

    private final List<Plant> protectedPlantInstances = new ArrayList<>();

    public SaveOurSeedsLevelSetup(int rows, int cols, List<Wave> waves, Map<Point, PlantType> protectedPlants) {
        this.rows = rows;
        this.cols = cols;
        this.waves = waves;
        this.protectedPlants = protectedPlants;
    }

    @Override
    public void groundSetup(GameWorld world) {
        world.setConveyorMode(false);
        world.setRows(rows);
        world.setCols(cols);

        Cell[][] grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                grid[r][c] = new Cell(r, c, new LandTerrain());
        world.setGrid(grid);

        for (Map.Entry<Point, PlantType> entry : protectedPlants.entrySet()) {
            int row = (int) entry.getKey().getY();
            int col = (int) entry.getKey().getX();

            Plant plant = PlantFactory.createPlant(entry.getValue(), row, col, grid[row][col]);
            protectedPlantInstances.add(plant);

            grid[row][col].setPlant(plant, PlantLayer.MAIN);


        }

        WaveManager waveManager = new WaveManager(waves);
        world.addMechanic(new NormalMechanic(waveManager));
        world.addMechanic(new SunSpawnMechanic());
    }

    @Override
    public boolean requirePlantSelection() {
        return false;
    }

    public boolean isProtectedPlant(Plant plant) {
        return plant != null && protectedPlantInstances.contains(plant);
    }
}
