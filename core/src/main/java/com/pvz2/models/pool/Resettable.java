package com.pvz2.models.pool;

import com.pvz2.models.enums.ProjectileType;
import com.pvz2.models.plant.components.SunProducerComponent;
import com.pvz2.models.projectile.hitStrategies.HitStrategy;
import com.pvz2.models.projectile.movementStrategies.MovementStrategy;
import com.pvz2.models.projectile.strikeStrategies.CheckStrike;

public interface Resettable {
    void reset(float x, float y, int size, SunProducerComponent component);

    void reset(float x, float y);

    void reset(float x, float y, HitStrategy hitStrategy,
               MovementStrategy movementStrategy, CheckStrike checkStrike, ProjectileType type);
}
