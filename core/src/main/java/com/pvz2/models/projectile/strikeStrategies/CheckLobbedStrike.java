package com.pvz2.models.projectile.strikeStrategies;

import com.pvz2.models.Damageable;

import java.util.List;

public class CheckLobbedStrike implements CheckStrike {
    @Override
    public Damageable strike(double x, double y, double oldX, double oldY, List<Damageable> lastTargets) {
        return null;
    }

    @Override
    public Damageable strike(double x, double y, Damageable zombie) {
        if (Math.abs(zombie.getX() - x) < 40 && Math.abs(zombie.getY() - y) < 40) {
            return zombie;
        }
        return null;
    }
}
