package com.pvz2.models.plant.components.explosiveTriggers;

import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.ExplosivesComponent;

public class InstantTrigger implements ExplosiveTrigger {
    public static final InstantTrigger INSTANCE = new InstantTrigger();

    private InstantTrigger() {
    }

    @Override
    public boolean shouldTrigger(Plant owner, ExplosivesComponent component) {
        return true;
    }
}
