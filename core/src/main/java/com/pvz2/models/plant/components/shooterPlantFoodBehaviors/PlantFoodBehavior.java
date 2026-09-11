package com.pvz2.models.plant.components.shooterPlantFoodBehaviors;

import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.ShooterComponent;

public interface PlantFoodBehavior {
    void activate(Plant owner, ShooterComponent shooterComponent);
    default void update(Plant owner, ShooterComponent shooterComponent, float delta) {}
    default boolean isFinished() { return true; }
}
