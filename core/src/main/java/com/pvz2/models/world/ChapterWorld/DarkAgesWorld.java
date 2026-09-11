package com.pvz2.models.world.ChapterWorld;

import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.cellTerrains.LandTerrain;
import com.pvz2.models.world.levelSetup.LevelSetup;
import com.pvz2.models.world.loseCondition.LoseCondition;
import com.pvz2.models.world.mechanics.Mechanic;
import com.pvz2.models.world.mechanics.SunSpawnMechanic;
import com.pvz2.models.world.obstacles.Grave;
import com.pvz2.models.world.winCondition.WinCondition;
import com.pvz2.view.util.LawnGrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DarkAgesWorld extends GameWorld {

    private static final int MIN_GRAVES = 3;
    private static final int MAX_GRAVES = 6;
    private Random random;
    private List<Cell> necromancyCells = new ArrayList<>();

    public DarkAgesWorld(LevelSetup levelSetup,
                         ArrayList<LoseCondition> loseConditions,
                         WinCondition winCondition,
                         ArrayList<Mechanic> mechanics) {
        super(levelSetup, loseConditions, winCondition, mechanics);
    }

    @Override
    protected void applyChapterRules() {
        this.random = new Random();

        removeSunSpawnMechanic();
        spawnInitialGraves();
        markNecromancyCells();

        Cell[][] grid = getGrid();
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                cell.setTerrain(new LandTerrain());
            }
        }
    }

    private void removeSunSpawnMechanic() {
        getMechanics().removeIf(m -> m instanceof SunSpawnMechanic);
    }

    private void spawnInitialGraves() {
        int graveCount = MIN_GRAVES + random.nextInt(MAX_GRAVES - MIN_GRAVES + 1);

        Cell[][] grid = getGrid();
        int spawned = 0;
        int attempts = 0;
        int maxAttempts = graveCount * 10;

        while (spawned < graveCount && attempts < maxAttempts) {
            attempts++;
            int row = random.nextInt(getRows());
            int col = random.nextInt(getCols());
            Cell cell = grid[row][col];

            if (cell.hasObstacle() || !cell.isEmpty()) continue;

            Grave grave = createRandomGrave(col, row);
            cell.setObstacle(grave);
            cell.setPlantable(false);
            addGrave(grave);
            spawned++;
        }
    }

    private Grave createRandomGrave(int col, int row) {
        float x = LawnGrid.getCellX(col);
        float y = LawnGrid.getCellY(row);

        int rand = random.nextInt(100);
        if (rand < 20) {
            return new Grave(x, y, row, col, Grave.GraveType.SUN);
        } else if (rand < 30) {
            return new Grave(x, y, row, col, Grave.GraveType.PLANT_FOOD);
        } else {
            return new Grave(x, y, row, col, Grave.GraveType.NORMAL);
        }
    }

    private void markNecromancyCells() {
        Cell[][] grid = getGrid();
        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < getCols(); c++) {
                if (random.nextDouble() < 0.2) {
                    grid[r][c].setNecromancyPotential(true);
                    necromancyCells.add(grid[r][c]);
                }
            }
        }
    }

    public void spawnWaveGraves() {
        Cell[][] grid = getGrid();
        int newGraves = 1 + random.nextInt(3);

        for (int i = 0; i < newGraves; i++) {
            int row = random.nextInt(getRows());
            int col = random.nextInt(getCols());
            Cell cell = grid[row][col];

            if (!cell.hasObstacle() && cell.isEmpty()) {
                Grave grave = createRandomGrave(col, row);
                cell.setObstacle(grave);
                cell.setPlantable(false);
                addGrave(grave);
            }
        }
    }

    public void triggerNecromancy() {
        Cell[][] grid = getGrid();
        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < getCols(); c++) {
                Cell cell = grid[r][c];
                if (cell.isNecromancyPotential() && cell.hasObstacle() && cell.getObstacle() instanceof Grave) {
                    cell.setNecromancyTriggered(true);
                    cell.setNecromancyPotential(false);
                }
            }
        }
    }

    public List<Cell> getNecromancyCells() {
        return necromancyCells;
    }

    private final java.util.Set<com.pvz2.models.zombie.Zombie> necromancyZombies = new java.util.HashSet<>();

    public void registerNecromancyZombie(com.pvz2.models.zombie.Zombie zombie) {
        necromancyZombies.add(zombie);
    }

    public java.util.Set<com.pvz2.models.zombie.Zombie> getNecromancyZombies() {
        return necromancyZombies;
    }
}
