package com.pvz2.models.miniGame.IZombie;

import com.pvz2.models.enums.PlantLayer;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.enums.Zombies;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.factory.PlantFactory;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.cellTerrains.LandTerrain;
import com.pvz2.models.world.levelSetup.LevelSetup;
import com.pvz2.models.zombie.Zombie;

import java.util.List;
import java.util.Random;

public class IZombieSetup implements LevelSetup {
    private int rows;
    private int cols;
    private List<Zombie> stageZombies;


    public IZombieSetup(int rows, int cols, List<Zombie> stageZombies) {
        this.rows = rows;
        this.cols = cols;
        this.stageZombies = stageZombies;
    }

    @Override
    public void groundSetup(GameWorld world) {
        world.setConveyorMode(false);
        if (world.getLawnMowerManager() != null) {
            world.getLawnMowerManager().setEnabled(false);
        }
        world.setRows(rows);
        world.setCols(cols);
        Cell[][] grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell(r, c, new LandTerrain());
            }
        }
        world.setGrid(grid);
        if (world instanceof IZombieLevel level) {
            Random random = new Random();
            level.setAvailableZombies(stageZombies);
            for (int r = 0; r < rows; r++) {
                Brain brain = new Brain(r, 10, r * 100 + 50);
                level.getBrains().add(brain);
            }
            PlantType[] possiblePlants = {PlantType.PEASHOOTER,
                    PlantType.SNOW_PEA,
                    PlantType.WALL_NUT,
                    PlantType.SUNFLOWER};
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < 5; c++) {
                    PlantType type = possiblePlants[random.nextInt(possiblePlants.length)];

                    int x = c * 100 + 50;
                    int y = r * 100 + 50;
                    Cell cell = grid[r][c];

                    Plant plant = PlantFactory.createPlant(type, x, y, cell);
                    if (plant != null) {
                        cell.setPlant(plant, PlantLayer.MAIN);
                        level.getActivePlants().add(plant);
                    }
                }
            }
            for (int r = 0; r < rows; r++) {
                SunProducer sp = new SunProducer(Zombies.ARMORED, 1100, 0.4, 20);
                sp.setX(8 * 100 + 50);
                sp.setY(r * 100 + 50);
                sp.initSpawnTick(level.getElapsedTime());
                level.getSunProducers().add(sp);
                level.addZombie(sp);
            }
        }
    }


    @Override
    public boolean requirePlantSelection() {
        return false;
    }
}
