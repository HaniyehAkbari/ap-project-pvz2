package com.pvz2.models.projectile.strikeStrategies;

import com.pvz2.models.Damageable;
import com.pvz2.models.core.App;
import com.pvz2.models.plant.Plant;

import java.util.List;

public class CheckPlantStrike implements CheckStrike {
    @Override
    public Damageable strike(double x, double y, double oldX, double oldY,
                             List<Damageable> lastTargets) {
        for (Plant plant : App.getCurrentGame().getActivePlants()) {
            if (CheckStraightStrike.isBetween(x, y, oldX, oldY, plant.getX(), plant.getY()))
                return plant;
        }
        return null;
    }

    @Override
    public Damageable strike(double x, double y, Damageable damageable) {
        return null;
    }
}
