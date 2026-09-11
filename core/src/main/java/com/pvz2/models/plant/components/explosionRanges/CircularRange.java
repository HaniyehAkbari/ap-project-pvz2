package com.pvz2.models.plant.components.explosionRanges;

import com.pvz2.controller.LevelMenuController;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.ExplosivesComponent;
import com.pvz2.models.world.Cell;

import java.util.List;


public class CircularRange implements ExplosionRange {
    private final int radius;

    public CircularRange(int radius) {
        this.radius = radius;
    }

    @Override
    public List<Cell> getCells(Plant owner) {
        if (owner.getComponent(ExplosivesComponent.class) == null ||
                owner.getComponent(ExplosivesComponent.class).getTarget() == null) {
            return Cell.getNeighborCells(owner.getCell(), LevelMenuController.getGameCells(owner), radius);
        }
        return Cell.getNeighborCells(owner.getComponent(ExplosivesComponent.class).getTarget(),
                LevelMenuController.getGameCells(owner), radius);
    }
}
