package com.pvz2.models.world.obstacles;

import com.pvz2.controller.GameMenuController;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.Cell;

public class OctopusObstacle extends Obstacle {
    private Plant targetPlant;
    private Cell cell;
    private boolean dying = false;

    public OctopusObstacle(float x, float y, Plant targetPlant, Cell cell) {
        super(x, y, 200);
        this.targetPlant = targetPlant;
        this.cell = cell;
        if (targetPlant != null) {
            targetPlant.setDisabled(true);
        }
    }

    @Override
    public boolean blocksProjectiles() {
        return !isDestroyed;
    }

    @Override
    public void takeDamage(int amount, String type) {
        if (isDestroyed) return;
        triggerDamageFlash();
        this.health -= amount;
        if (this.health <= 0) {
            this.health = 0;
            die();
        }
    }

    @Override
    public void die() {
        if (dying) return;
        dying = true;

        if (targetPlant != null && !targetPlant.isDead()) {
            targetPlant.setDisabled(false);
            GameMenuController.updateState("Octopus destroyed, plant at (" +
                    targetPlant.getX() + ", " + targetPlant.getY() + ") is free!");
        } else {
            GameMenuController.updateState("Octopus destroyed at (" + x + ", " + y + ")");
        }
        if (cell != null) {
            cell.removeObstacle();
        }
        super.die();
    }
}
