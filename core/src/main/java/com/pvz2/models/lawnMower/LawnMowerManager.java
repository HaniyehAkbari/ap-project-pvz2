package com.pvz2.models.lawnMower;

import com.pvz2.models.core.App;
import com.pvz2.models.world.GameState;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.view.util.LawnGrid;

import java.util.ArrayList;
import java.util.List;

public class LawnMowerManager {
    private final int totalRows;
    private final float cellWidth;
    private final double startX;
    private final double maxX;

    private List<LawnMower> mowers;
    private boolean enabled = true;

    public LawnMowerManager(GameWorld world) {
        this.totalRows = world.getRows();
        this.cellWidth = App.getCellWidth();

        this.startX = App.getFirstCellX() - cellWidth;
        this.maxX = App.getFirstCellX() + world.getCols() * cellWidth + cellWidth;

        this.mowers = new ArrayList<>();
        resetMowers();
    }

    private void resetMowers() {
        mowers.clear();
        for (int i = 0; i < totalRows; i++) {
            mowers.add(new LawnMower(i, startX, maxX));
        }
    }

    public void updateMowers(List<Zombie> allZombies, float delta) {
        if (!enabled) {
            return;
        }

        for (LawnMower mower : mowers) {
            List<Zombie> zombiesInRow = getZombiesInRow(allZombies, mower.getRow());

            for (Zombie z : zombiesInRow) {
                if (!z.isDead() && z.getX() <= App.getFirstCellX() - App.getCellWidth()/2) {   // <<< فیکس شد
                    if (!mower.isSpent() && !mower.isActive()) {
                        mower.activate();
                    } else if (mower.isSpent()) {
                        App.getCurrentGame().setState(GameState.LOST);
                        z.setSpeed(0);
                    }
                }
            }

            if (mower.isActive()) {
                mower.mowZombies(zombiesInRow);
                mower.move(delta);
            }
        }
    }

    private int getRowFromY(float y) {
        int row = LawnGrid.getRowFromY(y);
        return Math.max(0, Math.min(row, totalRows - 1));
    }

    private List<Zombie> getZombiesInRow(List<Zombie> allZombies, int row) {
        List<Zombie> zombiesInRow = new ArrayList<>();
        for (Zombie z : allZombies) {
            if (getRowFromY(z.getY()) == row) {
                zombiesInRow.add(z);
            }
        }
        return zombiesInRow;
    }

    public List<LawnMower> getMowers() {
        return mowers;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            mowers.clear();
        } else if (mowers.isEmpty()) {
            resetMowers();
        }
    }
}
