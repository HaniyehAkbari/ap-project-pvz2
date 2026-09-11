package com.pvz2.models.world.cellTerrains;

import com.pvz2.models.enums.PlantLayer;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.PlacementBehaviorComponent;
import com.pvz2.models.world.Cell;

public class WaterTerrain implements CellTerrain {
    @Override
    public boolean canPlant(Plant plant, Cell cell) {
        PlacementBehaviorComponent behavior = plant.getComponent(PlacementBehaviorComponent.class);

        if (behavior != null) {
            if (behavior.isWaterOnly()) {
                return true;
            }
            return !cell.isLayerEmpty(PlantLayer.BASE);
        }

        return !cell.isLayerEmpty(PlantLayer.BASE);
    }

    @Override
    public boolean isWater() {
        return true;
    }

    @Override
    public String getTerminalSymbol() {
        return "~";
    }
}
