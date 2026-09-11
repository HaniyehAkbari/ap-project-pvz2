package com.pvz2.models.plant.components.moveZombieStrategy;

import com.pvz2.controller.LevelMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.Cell;
import com.pvz2.models.zombie.Zombie;

import java.util.List;

public class EjectStrategy implements MoveZombieStrategy {
    @Override
    public void onUpdate(Plant owner) {

    }

    @Override
    public void onTakeDamage(Plant owner, int damage, Zombie attacker) {
        ejectZombie(owner, attacker);
    }

    private void ejectZombie(Plant owner, Zombie attacker) {
        if (owner.getCell().getRow() == 0) {
            attacker.setY(attacker.getY() + App.getCellHeight());
        } else if (owner.getCell().getRow() == App.getCurrentGame().getRows() - 1) {
            attacker.setY(attacker.getY() - App.getCellHeight());
        } else {
            attacker.setY(attacker.getY() + (Math.random() < 0.5 ? 1 : -1) * App.getCellHeight());
        }
    }

    @Override
    public void onPlantFood(Plant owner) {
        List<Cell> cells = Cell.getCellsInRow(owner.getCell(), LevelMenuController.getGameCells());
        List<Zombie> zombieList = Cell.getZombiesInCells(cells);
        for (Zombie zombie : zombieList) {
            ejectZombie(owner, zombie);
        }
    }
}
