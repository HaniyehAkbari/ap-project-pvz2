package com.pvz2.models.world.ChapterWorld;

import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameState;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.Sandstorm;
import com.pvz2.models.world.levelSetup.LevelSetup;
import com.pvz2.models.world.loseCondition.LoseCondition;
import com.pvz2.models.world.mechanics.Mechanic;
import com.pvz2.models.world.obstacles.Grave;
import com.pvz2.models.world.winCondition.WinCondition;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.view.util.LawnGrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AncientEgyptWorld extends GameWorld {
    private static final int MIN_GRAVES = 2;
    private static final int MAX_GRAVES = 5;
    private int deadLineCol;

    private final List<Sandstorm> activeSandstorms = new ArrayList<>();

    public AncientEgyptWorld(LevelSetup levelSetup, ArrayList<LoseCondition> loseConditions,
                             WinCondition winCondition, ArrayList<Mechanic> mechanics) {
        super(levelSetup, loseConditions, winCondition, mechanics);
    }

    @Override
    protected void applyChapterRules() {
        spawnInitialGraves();
        setSandstormActive(true);
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);

        for (int i = activeSandstorms.size() - 1; i >= 0; i--) {
            Sandstorm sandstorm = activeSandstorms.get(i);
            sandstorm.update(delta);

            if (sandstorm.isFinished()) {
                activeSandstorms.remove(i);
            }
        }
    }

    private void spawnInitialGraves() {
        Random random = new Random();
        int graveCount = MIN_GRAVES + random.nextInt(MAX_GRAVES - MIN_GRAVES + 1);
        Cell[][] grid = getGrid();

        int spawned = 0;
        int attempts = 0;
        int maxAttempts = graveCount * 10;

        int minCol = 4;
        int maxCol = 8;

        while (spawned < graveCount && attempts < maxAttempts) {
            attempts++;
            int row = random.nextInt(getRows());
            int col = minCol + random.nextInt(maxCol - minCol + 1);

            Cell cell = grid[row][col];
            if (cell.hasObstacle() || !cell.isEmpty()) continue;

            float x = LawnGrid.getCellX(col);
            float y = LawnGrid.getCellY(row);

            Grave grave = new Grave(x, y, row, col, Grave.GraveType.NORMAL);
            cell.setObstacle(grave);
            cell.setPlantable(false);

            addGrave(grave);
            spawned++;
        }
    }

    public void setDeadLineCol(int deadLineCol) {
        this.deadLineCol = deadLineCol;
    }

    public int getDeadLineCol() {
        return deadLineCol;
    }

    public void spawnSandstorm(Zombie zombie, int lane, int targetCol) {
        Sandstorm sandstorm = new Sandstorm(zombie, lane, targetCol, getCols());
        activeSandstorms.add(sandstorm);

        if (zombie != null) {
            addZombie(zombie);
        }
    }

    public List<Sandstorm> getActiveSandstorms() {
        return activeSandstorms;
    }
}
