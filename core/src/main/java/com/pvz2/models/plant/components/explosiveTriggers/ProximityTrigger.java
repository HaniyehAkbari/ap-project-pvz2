package com.pvz2.models.plant.components.explosiveTriggers;

import com.pvz2.controller.LevelMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.ExplosivesComponent;
import com.pvz2.models.world.Cell;
import com.pvz2.models.zombie.Zombie;

public class ProximityTrigger implements ExplosiveTrigger {
    private final float rangeX;

    public ProximityTrigger(float rangeX) {
        this.rangeX = rangeX;
    }

    @Override
    public boolean shouldTrigger(Plant owner, ExplosivesComponent component) {
        for (Zombie zombie : App.getCurrentGame(owner).getActiveZombies()) {
            boolean checkY = Math.abs(zombie.getY() - owner.getY()) < 5;
            boolean checkX = Math.abs(zombie.getX() - owner.getX()) < rangeX / 2;
            if (checkX && checkY) {
                if (zombie.getX() > owner.getX() + App.getCellWidth() / 2) {
                    component.setTarget(Cell.nextCell(owner.getCell(), LevelMenuController.getGameCells()));
                } else if (zombie.getX() < owner.getX() - App.getCellWidth() / 2) {
                    component.setTarget(Cell.previousCell(owner.getCell(), LevelMenuController.getGameCells()));
                } else component.setTarget(owner.getCell());
                return true;
            }
        }
        return false;
    }
}
