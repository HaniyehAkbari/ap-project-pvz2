package com.pvz2.models.plant.components.moveZombieStrategy;

import com.pvz2.controller.LevelMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.Cell;
import com.pvz2.models.zombie.Zombie;

public class AttractStrategy implements MoveZombieStrategy {
    @Override
    public void onUpdate(Plant owner) {
        float plantX = owner.getX();
        float plantY = owner.getY();
        float attractionRangeX = 3 * App.getCellWidth();

        for (Zombie zombie : App.getCurrentGame().getActiveZombies()) {
            Cell zombieCell = Cell.findZombieCell(LevelMenuController.getGameCells(), zombie);
            if (zombieCell == null) continue;
            boolean isAdjacentLane = Math.abs(zombieCell.getRow() - owner.getCell().getRow()) == 1;
            boolean isInFront = zombie.getX() >= plantX && zombie.getX() <= plantX + attractionRangeX;

            if (isAdjacentLane && isInFront) {
                zombie.setY(plantY);
            }
        }
    }

    @Override
    public void onTakeDamage(Plant owner, int damage, Zombie attacker) {

    }

    @Override
    public void onPlantFood(Plant owner) {
        for (Zombie zombie : App.getCurrentGame().getActiveZombies()) {
            Cell zombieCell = Cell.findZombieCell(LevelMenuController.getGameCells(), zombie);
            if (zombieCell == null) continue;
            boolean isAdjacentLane = Math.abs(zombieCell.getRow() - owner.getCell().getRow()) == 1;

            if (isAdjacentLane) {
                zombie.setY(owner.getY());
            }
        }
        owner.setHealth(owner.getInitHealth());
    }
}
