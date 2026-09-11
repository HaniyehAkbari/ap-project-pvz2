package com.pvz2.models.world.ChapterWorld;

import com.pvz2.controller.GameMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantLayer;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.cellTerrains.LandTerrain;
import com.pvz2.models.world.cellTerrains.WaterTerrain;
import com.pvz2.models.world.levelSetup.BigWaveBeachLevelSetup;
import com.pvz2.models.world.levelSetup.LevelSetup;
import com.pvz2.models.world.loseCondition.LoseCondition;
import com.pvz2.models.world.mechanics.Mechanic;
import com.pvz2.models.world.winCondition.WinCondition;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.zombie.ZombieFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BigWaveBeachWorld extends GameWorld {
    private final float tideCycleTime = 30.0f;
    private final float lowLyingCoastSpawnTime = 25.0f;
    private int tideLineCol;
    private int currentTideCol;
    private float lastTideChangeTime = 0f;
    private float lastLowLyingCoastSpawnTime = 0f;
    private Random random = new Random();
    private List<Cell> lowLyingCells = new ArrayList<>();

    public BigWaveBeachWorld(LevelSetup levelSetup, ArrayList<LoseCondition> loseConditions,
                             WinCondition winCondition, ArrayList<Mechanic> mechanics) {
        super(levelSetup, loseConditions, winCondition, mechanics);
    }

    @Override
    protected void applyChapterRules() {
        Random random = new Random();
        if (getLevelSetup() instanceof BigWaveBeachLevelSetup beachSetup) {
            tideLineCol = beachSetup.getTideLineCol();
            this.currentTideCol = 9;
        }
        int lowLyingCoastsCount = random.nextInt(4) + 1;
        for (int i = 0; i < lowLyingCoastsCount; i++) {
            makeCellLowLyingCoast();
        }
    }

    private void makeCellLowLyingCoast() {
        Random random = new Random();
        int cellRow = random.nextInt(getRows());
        int cellCol = random.nextInt(3) + getCols() - 3;
        if (grid[cellRow][cellCol].isLowLyingCoast()) {
            makeCellLowLyingCoast();
        } else {
            grid[cellRow][cellCol].setLowLyingCoast(true);
            lowLyingCells.add(grid[cellRow][cellCol]);
        }
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);
        updateLowLyingCoasts();
        updateTide();
    }

    private void updateLowLyingCoasts() {
        float currentTime = getElapsedTime();

        if (currentTime - lastLowLyingCoastSpawnTime >= lowLyingCoastSpawnTime) {
            lastLowLyingCoastSpawnTime = currentTime;

            for (Cell[] cells : grid) {
                for (Cell cell : cells) {
                    if (cell.isLowLyingCoast() && cell.getCol() >= currentTideCol) {
                        if (random.nextBoolean()) {
                            Zombie zombie = random.nextBoolean() ?
                                new ZombieFactory().createZombie("ZombieDefault") :
                                new ZombieFactory().createZombie(App.getZombieId("ZombieConehead"));
                            if (zombie != null) {
                                GameMenuController.showAnnouncement("A zombie is rising from a low lying coast!");
                                zombie.setX(cell.getX());
                                zombie.setY(cell.getY());
                                this.addZombie(zombie);
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateTide() {
        float currentTime = getElapsedTime();

        if (currentTime - lastTideChangeTime >= tideCycleTime) {
            lastTideChangeTime = currentTime;

            int minCol = tideLineCol;
            int maxCol = getCols();
            int newTideCol = random.nextInt(maxCol - minCol + 1) + minCol+1;

            if (newTideCol < currentTideCol) {
                for (int c = newTideCol; c < currentTideCol; c++) {
                    changeColumnTerrain(c, true);
                }
                GameMenuController.updateState("tide rising...");
            } else if (newTideCol > currentTideCol) {
                for (int c = currentTideCol; c < newTideCol; c++) {
                    changeColumnTerrain(c, false);
                }
                GameMenuController.updateState("tide receding...");

            }

            currentTideCol = newTideCol;
        }
    }


    private void changeColumnTerrain(int col, boolean makeWater) {
        Cell[][] grid = getGrid();
        col = col - 1;
        for (int r = 0; r < getRows(); r++) {
            Cell cell = grid[r][col];
            if (makeWater) {
                cell.setTerrain(new WaterTerrain());
                if (cell.getPlant(PlantLayer.MAIN) != null) cell.getPlant(PlantLayer.MAIN).die();
                if (cell.getPlant(PlantLayer.SHIELD) != null)
                    cell.getPlant(PlantLayer.SHIELD).die();
            } else {
                cell.setTerrain(new LandTerrain());
                if (cell.getPlant(PlantLayer.BASE) != null) cell.getPlant(PlantLayer.BASE).die();

            }
        }
    }

    public int getTideLineCol() {
        return tideLineCol;
    }

    public int getCurrentTideCol() {
        return currentTideCol;
    }

    public List<Cell> getLowLyingCells() {
        return lowLyingCells;
    }
}
