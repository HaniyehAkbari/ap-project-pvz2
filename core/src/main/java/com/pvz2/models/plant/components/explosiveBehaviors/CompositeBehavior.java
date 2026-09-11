package com.pvz2.models.plant.components.explosiveBehaviors;

import com.pvz2.models.plant.Plant;

import java.util.List;

public class CompositeBehavior implements ExplosiveBehavior {
    private final List<ExplosiveBehavior> behaviors;

    public CompositeBehavior(ExplosiveBehavior... behaviors) {
        this.behaviors = List.of(behaviors);
    }

    @Override
    public void execute(Plant owner) {
        for (ExplosiveBehavior behavior : behaviors) {
            behavior.execute(owner);
        }
    }
}
