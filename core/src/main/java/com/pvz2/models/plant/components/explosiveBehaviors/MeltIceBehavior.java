package com.pvz2.models.plant.components.explosiveBehaviors;

import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.explosionRanges.ExplosionRange;
import com.pvz2.models.world.Cell;
import com.pvz2.models.zombie.Zombie;

import java.util.List;

public class MeltIceBehavior implements ExplosiveBehavior {
    private ExplosionRange area;


    public MeltIceBehavior(ExplosionRange area) {
        this.area = area;
    }

    @Override
    public void execute(Plant owner) {
        List<Cell> affectedCells = area.getCells(owner);

        for (Cell cell : affectedCells) {
            if (!cell.isEmpty() && cell.getPlant().isFreeze()) {
                cell.getPlant().unfreeze();
            }
            for (Zombie zombie : Cell.getZombiesInCell(cell)){
                if (zombie.getIceHealth() > 0){
                    zombie.unfreeze();
                }
            }
        }
    }

}
