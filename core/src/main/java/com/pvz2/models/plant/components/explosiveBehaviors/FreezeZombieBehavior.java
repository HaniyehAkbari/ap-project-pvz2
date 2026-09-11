package com.pvz2.models.plant.components.explosiveBehaviors;

import com.pvz2.models.core.App;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.explosionRanges.ExplosionRange;
import com.pvz2.models.world.Cell;
import com.pvz2.models.zombie.Zombie;

import java.util.DoubleSummaryStatistics;
import java.util.List;

public class FreezeZombieBehavior implements ExplosiveBehavior {
    private ExplosionRange area;
    private float freezeTime;

    public FreezeZombieBehavior(ExplosionRange area, int freezeTime) {
        this.area = area;
        this.freezeTime = freezeTime;
    }

    @Override
    public void execute(Plant owner) {
        List<Cell> cells = area.getCells(owner);
        DoubleSummaryStatistics xStats = cells.stream()
                .mapToDouble(Cell::getX)
                .summaryStatistics();
        double minX = xStats.getMin() - App.getCellWidth() / 2;
        double maxX = xStats.getMax() + App.getCellWidth() / 2;
        DoubleSummaryStatistics yStats = cells.stream()
                .mapToDouble(Cell::getY)
                .summaryStatistics();
        double minY = yStats.getMin();
        double maxY = yStats.getMax();

        for (Zombie zombie : App.getCurrentGame().getActiveZombies()) {
            if (zombie.getY() <= maxY && zombie.getY() >= minY && zombie.getX() <= maxX && zombie.getX() >= minX) {
                zombie.freeze(freezeTime);
            }
        }
    }
}
