package com.pvz2.models.plant.components.explosiveTriggers;

import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.ExplosivesComponent;

public interface ExplosiveTrigger {
    boolean shouldTrigger(Plant owner, ExplosivesComponent component);
}
