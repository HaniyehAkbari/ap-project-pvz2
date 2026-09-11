package com.pvz2.models.plant.components.explosiveBehaviors;

import com.pvz2.models.core.App;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.explosionRanges.ExplosionRange;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.obstacles.Obstacle;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.view.audios.GameSFX;
import com.pvz2.view.audios.SFXManager;

import java.util.DoubleSummaryStatistics;
import java.util.List;

public class AreaDamageBehavior implements ExplosiveBehavior {
    private int damage;
    private ExplosionRange area;

    public AreaDamageBehavior(int damage, ExplosionRange area) {
        this.damage = damage;
        this.area = area;
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
        if (App.getCurrentGame() != null){
            SFXManager.getInstance().playSound(GameSFX.CHERRY_BOMB);
        }

        for (Zombie zombie : App.getCurrentGame(owner).getActiveZombies()) {
            if (zombie.getY() <= maxY && zombie.getY() >= minY && zombie.getX() <= maxX && zombie.getX() >= minX) {
                zombie.takeDamage(damage, "NORMAL");
            }
        }
        for (Obstacle obstacle : App.getCurrentGame(owner).getActiveObstacles()) {
            if (obstacle.getY() <= maxY && obstacle.getY() >= minY &&
                    obstacle.getX() <= maxX && obstacle.getX() >= minX) {
                obstacle.takeDamage(damage, "NORMAL");
            }
        }
    }

}
