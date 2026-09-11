package com.pvz2.models.plant.components.explosiveBehaviors;

import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.obstacles.Grave;

public class RemoveGraveBehavior implements ExplosiveBehavior {
    @Override
    public void execute(Plant owner) {
        Cell currentCell = owner.getCell();
        if (currentCell != null && currentCell.hasObstacle()) {
            if (currentCell.getObstacle() instanceof Grave grave){
                currentCell.removeObstacle();
                grave.die();
            }
        }
    }
}
