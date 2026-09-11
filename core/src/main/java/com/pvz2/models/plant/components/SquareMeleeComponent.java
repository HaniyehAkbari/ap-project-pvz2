package com.pvz2.models.plant.components;

import com.pvz2.models.core.App;
import com.pvz2.models.plant.AnimationDurations;
import com.pvz2.models.plant.GameComponent;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.PlantAnimationClips;
import com.pvz2.models.world.Cell;
import com.pvz2.models.zombie.Zombie;

import java.util.List;

public class SquareMeleeComponent implements GameComponent {
    private final int maxStage;
    private final float stage2Time = 24f;
    private final float stage3Time = 72f;
    private final float stage4Time = 144f;
    private final int baseDamage;
    private float plantationTime = 0f;
    private int currentStage = 1;
    private boolean hasGrowing;
    private float attackIntervalTime;
    private float lastAttackTime = 0f;
    private boolean isPlantfood = false;
    private float plantFoodTimer = -1f;
    private float plantFoodTotalDuration = -1f;
    private float plantFoodActionTime;
    private boolean plantFoodActionDone = false;
    private static final float DEFAULT_PLANT_FOOD_DURATION = 1.5f;

    public SquareMeleeComponent(int baseDamage, float attackIntervalTime, boolean hasGrowing, int maxStage) {
        this.baseDamage = baseDamage;
        this.maxStage = maxStage;
        this.hasGrowing = hasGrowing;
        this.attackIntervalTime = attackIntervalTime;
    }

    public SquareMeleeComponent(int baseDamage, float attackIntervalTime) {
        this(baseDamage, attackIntervalTime, false, 1);
    }

    @Override
    public void update(Plant owner, float delta) {
        plantationTime+= delta;

        if (isPlantfood) {
            tickPlantFood(owner, delta);
            return;
        }

        lastAttackTime+= delta;
        owner.setState(Plant.State.IDLE);

        if (hasGrowing) checkGrowth();

        if (lastAttackTime >= attackIntervalTime) {
            performSonicWaveAttack(owner);
            owner.setState(Plant.State.ATTACK);
            lastAttackTime = 0f;
        }
    }

    private void checkGrowth() {
        if (currentStage == 1 && plantationTime >= stage2Time) {
            currentStage = 2;
        } else if (currentStage == 2 && plantationTime >= stage3Time) {
            currentStage = 3;
        } else if (currentStage == 3 && maxStage >= 4 && plantationTime >= stage4Time) {
            currentStage = 4;
        }
    }

    private int getCurrentDamage() {
        return baseDamage + (currentStage - 1) * 15;
    }

    private void performSonicWaveAttack(Plant owner) {
        Cell cell = owner.getCell();
        if (cell == null) return;

        int currentDmg = getCurrentDamage();
        int radius = (currentStage >= 3) ? 2 : 1;

        List<Zombie> targets = Cell.getZombiesInCells(Cell.getNeighborCells(
                cell, App.getCurrentGame().getGrid(), radius));
        for (Zombie zombie : targets) {
            zombie.takeDamage(currentDmg, "NORMAL");
        }
    }

    private void ensurePlantFoodDurationLoaded(Plant owner) {
        if (plantFoodTotalDuration >= 0f) return;
        plantFoodTotalDuration = AnimationDurations.getDuration(owner.getType(),
            PlantAnimationClips.getPlantFoodClip(owner.getType()), DEFAULT_PLANT_FOOD_DURATION);
        plantFoodActionTime = AnimationDurations.getReleaseTime(owner.getType(),
            PlantAnimationClips.getPlantFoodClip(owner.getType()), DEFAULT_PLANT_FOOD_DURATION);
    }

    private void tickPlantFood(Plant owner, float delta) {
        plantFoodTimer += delta;
        if (plantFoodTimer < plantFoodActionTime) return;

        if (!plantFoodActionDone){
            plantFoodActionDone = true;
            Cell cell = owner.getCell();
            if (cell != null) {
                int pfSlamDamage = 350;
                List<Zombie> targets = Cell.getZombiesInCells(Cell.getNeighborCells(
                    cell, App.getCurrentGame().getGrid(), 2));
                for (Zombie zombie : targets) {
                    zombie.takeDamage(pfSlamDamage, "NORMAL");
                }
            }
        }

        if (plantFoodTimer >= plantFoodTotalDuration){
            isPlantfood = false;
            plantFoodActionDone = false;
            plantFoodTimer = 0;
            owner.setState(Plant.State.IDLE);
        }
    }

    @Override
    public void activatePlantFood(Plant owner) {
        this.currentStage = maxStage;
        this.plantationTime = (maxStage == 4) ? stage4Time : stage3Time;
        owner.setState(Plant.State.PLANT_FOOD);
        isPlantfood = true;
        ensurePlantFoodDurationLoaded(owner);
    }
}
