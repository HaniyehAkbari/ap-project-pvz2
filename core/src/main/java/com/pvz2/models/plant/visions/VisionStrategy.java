package com.pvz2.models.plant.visions;

import com.pvz2.models.Damageable;
import com.pvz2.models.plant.Plant;

public interface VisionStrategy {
    static boolean isBetween(float number, float a, float b) {
        return number >= Math.min(a, b) && number <= Math.max(a, b);
    }

    Damageable findZombie(Plant owner);
}
