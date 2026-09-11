package com.pvz2.models.world.levelSetup;

import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.cellTerrains.LandTerrain;

public interface LevelSetup {
    void groundSetup(GameWorld game);

    boolean requirePlantSelection();

    default void buildGrid(GameWorld world, int rows, int cols) {
        world.setRows(rows);
        world.setCols(cols);
        Cell[][] grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                grid[r][c] = new Cell(r, c, new LandTerrain());
        world.setGrid(grid);
    }
}
