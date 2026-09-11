package com.pvz2.models.plant.components.shooterPlantFoodBehaviors;

import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.ShooterComponent;

public class BurstPlantFood implements PlantFoodBehavior {
    public static final BurstPlantFood INSTANCE = new BurstPlantFood();

    private BurstPlantFood() {
    }

    @Override
    public void activate(Plant owner, ShooterComponent shooterComponent) {
        shooterComponent.setActivePlantFood(true);
        shooterComponent.setBurstDelayTimer(0);
        shooterComponent.setShootingTimer(0);
        shooterComponent.setProjectilesLeftForShoot(shooterComponent.getBurstProjectileNumberOnPlantFood());
    }
}
