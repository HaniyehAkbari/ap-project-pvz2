package com.pvz2.models.plant.components;

import com.pvz2.models.core.App;
import com.pvz2.models.enums.ProjectileType;
import com.pvz2.models.plant.GameComponent;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.visions.StraightVisionStrategy;
import com.pvz2.models.plant.visions.VisionStrategy;
import com.pvz2.models.projectile.Projectile;
import com.pvz2.models.projectile.hitStrategies.CombinedDamageStrategy;
import com.pvz2.models.projectile.movementStrategies.MovementStrategy;
import com.pvz2.models.projectile.strikeStrategies.CheckStraightStrike;
import com.pvz2.models.projectile.strikeStrategies.CheckStrike;

import java.util.function.Supplier;

public class BowlingChargeComponent implements GameComponent {

    private final Bulb[] bulbs = new Bulb[3];
    private float shootingTime;
    private final VisionStrategy visionStrategy = new StraightVisionStrategy(1000, App.getCellHeight(), false);
    private final CombinedDamageStrategy[] damageStrategies = new CombinedDamageStrategy[3];
    private final CheckStrike strikeStrategy = new CheckStraightStrike();
    private final Supplier<MovementStrategy> movementStrategy;
    private final CombinedDamageStrategy plantFoodDamageStrategy;
    private float shootingTimer = 0f;
    private boolean activePlantFood;
    private int plantFoodProjectileCount = 0;
    private final float actionTimeInterval;
    private final float actionTime;
    private float currentActionTimer = 0;

    public BowlingChargeComponent(float shootingTime,
                                  CombinedDamageStrategy firstDamageStrategy,
                                  CombinedDamageStrategy secondDamageStrategy,
                                  CombinedDamageStrategy thirdDamageStrategy,
                                  Supplier<MovementStrategy> movementStrategy,
                                  CombinedDamageStrategy plantFoodDamageStrategy,
                                  int firstCharge, int secondCharge, int thirdCharge,
                                  float actionTimeInterval, float actionTime) {
        this.shootingTime = shootingTime;
        this.actionTimeInterval = actionTimeInterval;
        this.actionTime = actionTime;
        damageStrategies[0] = firstDamageStrategy;
        damageStrategies[1] = secondDamageStrategy;
        damageStrategies[2] = thirdDamageStrategy;
        this.movementStrategy = movementStrategy;
        this.plantFoodDamageStrategy = plantFoodDamageStrategy;
        bulbs[0] = new Bulb(ProjectileType.LARGE_BULB, firstCharge);
        bulbs[1] = new Bulb(ProjectileType.MEDIUM_BULB, secondCharge);
        bulbs[2] = new Bulb(ProjectileType.SMALL_BULB, thirdCharge);
    }

    @Override
    public void activatePlantFood(Plant owner) {
        activePlantFood = true;
        shootingTimer = 0;
        shootingTime = 0.5f;
        owner.setState(Plant.State.PLANT_FOOD);
        plantFoodProjectileCount = 3;
        for (Bulb bulb : bulbs) {
            bulb.isReady = true;
        }
    }

    @Override
    public void update(Plant owner, float delta) {
        if (activePlantFood) {
            plantFoodHandler(owner, delta);
            return;
        }
        if (currentActionTimer >= actionTime){
            currentActionTimer += delta;
            if (currentActionTimer >= actionTimeInterval){
                currentActionTimer = 0;
                owner.setState(Plant.State.IDLE);
            }
        }

        for (Bulb bulb : bulbs) {
            bulb.update(delta);
        }

        if (visionStrategy.findZombie(owner) != null) {
            if (shootingTimer > 0) {
                shootingTimer-= delta;
            } else {
                tryShooting(owner, delta);
            }
        }
    }

    private void tryShooting(Plant owner, float delta) {
        for (int i = 0; i < 3; i++) {
            if (bulbs[i].isReady) {
                if (owner.getState() == Plant.State.IDLE){
                    owner.setState(Plant.State.SPECIAL);
                }
                currentActionTimer += delta;
                if (currentActionTimer < actionTime) return;
                bulbs[i].isReady = false;
                shootingTimer = shootingTime;
                Projectile p = App.getCurrentGame().getProjectilesPool().acquire();
                p.reset(owner.getX(), owner.getY(), damageStrategies[i],
                        movementStrategy.get(), strikeStrategy, bulbs[i].projectileType);
                p.setPierce(1000);
                App.getCurrentGame().getActiveProjectiles().add(p);
                break;
            }
        }
    }

    private void plantFoodHandler(Plant owner, float delta) {
        if (shootingTimer > 0) {
            shootingTimer-= delta;
        } else {
            plantFoodProjectileCount--;
            shootingTimer = shootingTime;
            Projectile p = App.getCurrentGame().getProjectilesPool().acquire();
            p.reset(owner.getX(), owner.getY(), plantFoodDamageStrategy,
                    movementStrategy.get(), strikeStrategy, ProjectileType.SPECIAL_BULB);
            App.getCurrentGame().getActiveProjectiles().add(p);
            if (plantFoodProjectileCount == 0) {
                activePlantFood = false;
                owner.setState(Plant.State.IDLE);
                shootingTime = 2;
            }
        }
    }

    private static class Bulb {
        private final ProjectileType projectileType;
        private final float chargeTime;
        private float currentCharge = 0f;
        private boolean isReady;

        public Bulb(ProjectileType projectileType, float chargeTime) {
            this.projectileType = projectileType;
            this.chargeTime = chargeTime;
        }

        public void update(float delta) {
            if (!isReady) {
                currentCharge += delta;
                if (currentCharge >= chargeTime) {
                    isReady = true;
                    currentCharge = 0f;
                }
            }
        }
    }
}
