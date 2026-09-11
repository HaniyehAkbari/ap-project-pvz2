package com.pvz2.models.world.cellTerrains;

import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.Cell;

public interface CellTerrain {
    boolean canPlant(Plant plant, Cell cell);

    boolean isWater();

    String getTerminalSymbol();
}
