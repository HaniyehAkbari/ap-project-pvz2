package com.pvz2.models.plant.components.explosiveBehaviors;

import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.explosionRanges.ExplosionRange;
import com.pvz2.models.world.Cell;

import java.util.List;

public class MakeUnplantableBehavior implements ExplosiveBehavior {
    private final ExplosionRange range;

    public MakeUnplantableBehavior(ExplosionRange range) {
        this.range = range;
    }

    @Override
    public void execute(Plant owner) {
        List<Cell> affectedCells = range.getCells(owner);

        for (Cell cell : affectedCells) {
            cell.setCraterTime(300);
        }
    }
}
