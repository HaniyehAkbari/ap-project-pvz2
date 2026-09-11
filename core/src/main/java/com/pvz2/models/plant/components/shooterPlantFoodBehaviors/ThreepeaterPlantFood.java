package com.pvz2.models.plant.components.shooterPlantFoodBehaviors;

import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.ShooterComponent;
import com.pvz2.models.projectile.movementStrategies.StraightMovementStrategy;

public class ThreepeaterPlantFood implements PlantFoodBehavior {
    public static final ThreepeaterPlantFood INSTANCE = new ThreepeaterPlantFood();
    private static final int TOTAL_PLANT_FOOD_SHOTS = 30;

    private ThreepeaterPlantFood() {
    }

    @Override
    public void activate(Plant owner, ShooterComponent shooterComponent) {
        shooterComponent.setActivePlantFood(true);
        shooterComponent.setBurstDelayTimer(0);
        shooterComponent.setShootingTimer(0);
        shooterComponent.setProjectilesLeftForShoot(TOTAL_PLANT_FOOD_SHOTS);

        shooterComponent.getMovementStrategies().clear();

        float[] baseDirections = {-1.0f, 0.0f, 1.0f};

        for (float dir : baseDirections) {
            final float directionFactor = dir;

            shooterComponent.getMovementStrategies().add(() -> {
                int remaining = shooterComponent.getProjectilesLeftForShoot();
                int currentShot = TOTAL_PLANT_FOOD_SHOTS - remaining;
                float progress = (float) currentShot / (TOTAL_PLANT_FOOD_SHOTS - 1);

                float spreadFactor = 200f + (progress * 600f);
                float currentSpeedY = directionFactor * spreadFactor;

                return new StraightMovementStrategy(700f, currentSpeedY, 0);
            });
        }
    }
}
