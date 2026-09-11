package com.pvz2.models.plant.components;

import com.pvz2.models.plant.Plant;

@FunctionalInterface
public interface ExplosivePlantFoodBehavior {
    void execute(Plant owner, ExplosivesComponent component);
}
