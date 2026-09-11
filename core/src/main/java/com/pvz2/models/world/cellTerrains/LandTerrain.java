package com.pvz2.models.world.cellTerrains;

import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.PlacementBehaviorComponent;
import com.pvz2.models.world.Cell;

public class LandTerrain implements CellTerrain {
    @Override
    public boolean canPlant(Plant plant, Cell cell) {
        if (plant.getType() == PlantType.HOT_POTATO) {
            return  !cell.isEmpty() && cell.getPlant().isFreeze();
        }
        PlacementBehaviorComponent behavior = plant.getComponent(PlacementBehaviorComponent.class);

        if (behavior != null && behavior.isWaterOnly()) {
            return false;
        }

        return true;
    }


    @Override
    public boolean isWater() {
        return false;
    }

    @Override
    public String getTerminalSymbol() {
        return ".";
    }
}
