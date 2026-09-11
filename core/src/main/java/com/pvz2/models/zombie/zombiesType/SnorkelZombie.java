package com.pvz2.models.zombie.zombiesType;

import com.pvz2.models.core.App;
import com.pvz2.models.enums.Zombies;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.zombie.state.EatingState;
import com.pvz2.view.util.LawnGrid;

public class SnorkelZombie extends Zombie {
    private boolean underwater;

    public SnorkelZombie(int health, double speed, int damage) {
        super(Zombies.SNORKEL, health, speed, damage);
        this.underwater = false;
    }

    @Override
    public void update(float delta) {
        if (isDead) return;

        GameWorld game = App.getCurrentGame();
        if (game == null) {
            super.update(delta);
            return;
        }

        int col = LawnGrid.getColFromX(this.x);
        int row = LawnGrid.getRowFromY(this.y);
        boolean inWater;

        if (row >= 0 && row < game.getRows() && col >= 0 && col < game.getCols()) {
            Cell cell = game.getGrid()[row][col];
            inWater = cell != null && cell.isWater();
        } else {
            boolean offGridToTheRight = this.x > LawnGrid.getCellX(LawnGrid.COLS - 1) + LawnGrid.CELL_WIDTH / 2f;
            inWater = offGridToTheRight;
        }

        if (this.getCurrentState() instanceof EatingState) {
            underwater = false;
        } else {
            underwater = inWater;
        }

        super.update(delta);
    }

    public boolean isUnderwater() {
        return underwater;
    }

    @Override
    public void takeDamage(int amount, String damageType) {
        if (isDead) return;

        if (underwater) {
            return;
        }

        super.takeDamage(amount, damageType);
    }
}
