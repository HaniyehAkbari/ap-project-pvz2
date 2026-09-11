package com.pvz2.models.miniGame.IZombie;

import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.card.PlantCardFactory;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.cellTerrains.LandTerrain;
import com.pvz2.models.world.levelSetup.LevelSetup;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.view.util.LawnGrid;

import java.util.List;

public class OnlineIZombieSetup implements LevelSetup {
    private final int rows;
    private final int cols;
    private final List<Zombie> stageZombies;

    public OnlineIZombieSetup(int rows, int cols, List<Zombie> stageZombies) {
        this.rows = rows;
        this.cols = cols;
        this.stageZombies = stageZombies;
    }

    @Override
    public void groundSetup(GameWorld gameWorld) {
        OnlineIZombieLevel world = (OnlineIZombieLevel) gameWorld;
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

        world.getPlantLists().addAll(List.of(PlantCardFactory.createCard(PlantType.SUNFLOWER, 1),
            PlantCardFactory.createCard(PlantType.CABBAGE_PULT, 1),
            PlantCardFactory.createCard(PlantType.POTATO_MINE, 1),
            PlantCardFactory.createCard(PlantType.REPEATER, 1),
            PlantCardFactory.createCard(PlantType.WALL_NUT, 1),
            PlantCardFactory.createCard(PlantType.CHERRY_BOMB, 1),
            PlantCardFactory.createCard(PlantType.BONK_CHOY, 1),
            PlantCardFactory.createCard(PlantType.CITRON, 1)));

        world.setAvailableZombies(stageZombies);
        for (int r = 0; r < rows; r++) {
            world.getBrains().add(new Brain(r, LawnGrid.getCellX(0) - LawnGrid.CELL_WIDTH,
                LawnGrid.getCellY(r)));
        }
    }

    public int getBrainCost(String type){
        return switch (type){
            case "ZombieArmor1" -> 100;
            case "ZombieArmor2" -> 150;
            case "ZombieArmor4", "ZombieModernAllStar" -> 300;
            case "ZombieWizard" -> 200;
            case "ZombieLostCityJane" -> 75;
            case "ZombieGargantuar" -> 400;
            default -> 50;
        };
    }

    public List<Zombie> getStageZombies() {
        return stageZombies;
    }

    @Override
    public boolean requirePlantSelection() {
        return false;
    }
}
