package com.pvz2.models.projectile.strikeStrategies;

import com.pvz2.models.Damageable;
import com.pvz2.models.world.GameWorld;

import java.util.List;

public interface CheckStrike {
    Damageable strike(double x, double y, double oldX, double oldY, List<Damageable> lastTargets);

    Damageable strike(double x, double y, Damageable damageable);

    default void setWorld(GameWorld world){}
}
