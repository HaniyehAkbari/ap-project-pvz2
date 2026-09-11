package com.pvz2.models.plant.components;

import com.pvz2.models.core.App;
import com.pvz2.models.miniGame.IZombie.IZombieLevel;
import com.pvz2.models.plant.AnimationDurations;
import com.pvz2.models.plant.GameComponent;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.PlantAnimationClips;
import com.pvz2.models.world.Sun;

import java.util.ArrayList;

public class SunProducerComponent implements GameComponent {
    private static final float DEFAULT_ACTION_TIME = 0.3f;
    private static final float DEFAULT_ACTION_TIME_INTERVAL = 0.6f;

    private transient final ArrayList<Sun> componentSuns = new ArrayList<>();
    private int sunSize;
    private int sunNumber;
    private float lastProductionTime;
    private float productionTime;
    private boolean doubleSunChance;
    private boolean shroom;
    private float plantationTime;
    private int sunNumberWithPlantFood;
    private boolean checkShroomSize;
    private boolean enable = true;
    private float growTimeToReduce;
    private boolean isInstant;
    private boolean instantProductDone = false;
    private boolean inPlantFood = false;
    private int plantFoodSunsRemaining = 0;
    private float plantFoodTimer = 0f;
    private float plantFoodInterval = 0f;
    private float plantFoodTotalDuration = -1f;
    private static final String PLANT_FOOD_CLIP = "plantfood";
    private static final float DEFAULT_PLANT_FOOD_DURATION = 1.5f;

    // resolved lazily from AnimationDurations once the owner's PlantType is known
    private float actionTime = -1f;
    private float actionTimeInterval = -1f;
    private float currentActionTimer = 0;

    public SunProducerComponent(int sunSize, int sunNumber, float productionTime, boolean doubleSunChance,
                                boolean shroom, int sunNumberWithPlantFood, float growTimeToReduce) {
        this(sunSize, sunNumber, productionTime, doubleSunChance, shroom, sunNumberWithPlantFood,
            growTimeToReduce, false);
    }

    public SunProducerComponent(int sunSize, int sunNumber, float productionTime, boolean doubleSunChance,
                                boolean shroom, int sunNumberWithPlantFood,
                                float growTimeToReduce, boolean isInstant) {
        this.sunSize = sunSize;
        this.sunNumber = sunNumber;
        this.productionTime = productionTime;
        this.doubleSunChance = doubleSunChance;
        this.shroom = shroom;
        this.checkShroomSize = shroom;
        this.sunNumberWithPlantFood = sunNumberWithPlantFood;
        this.growTimeToReduce = growTimeToReduce;
        this.isInstant = isInstant;
        this.lastProductionTime = productionTime - 2;
    }

    private void ensureTimingLoaded(Plant owner) {
        if (actionTime >= 0f) return;
        String clip = PlantAnimationClips.getSpecialClip(owner.getType());
        actionTime = AnimationDurations.getReleaseTime(owner.getType(), clip, DEFAULT_ACTION_TIME);
        actionTimeInterval = AnimationDurations.getDuration(owner.getType(), clip, DEFAULT_ACTION_TIME_INTERVAL);
    }

    private void ensurePlantFoodDurationLoaded(Plant owner) {
        if (plantFoodTotalDuration >= 0f) return;
        plantFoodTotalDuration = AnimationDurations.getDuration(owner.getType(),
            PLANT_FOOD_CLIP, DEFAULT_PLANT_FOOD_DURATION);
    }

    private void tickPlantFood(Plant owner, float delta) {
        plantFoodTimer += delta;
        while (plantFoodSunsRemaining > 0 && plantFoodTimer >= plantFoodInterval) {
            plantFoodTimer -= plantFoodInterval;
            plantFoodSunsRemaining--;
            componentSuns.add(produceSun(owner));
        }
        if (plantFoodSunsRemaining <= 0) {
            inPlantFood = false;
            owner.setState(Plant.State.IDLE);
        }
    }

    @Override
    public void activatePlantFood(Plant owner) {
        plantFoodEffect(owner);
    }

    public void plantFoodEffect(Plant owner) {
        if (sunNumberWithPlantFood <= 0) return;

        ensurePlantFoodDurationLoaded(owner);
        if (shroom) {
            setSunSize(75);
        }

        inPlantFood = true;
        plantFoodSunsRemaining = sunNumberWithPlantFood;
        plantFoodTimer = 0f;
        plantFoodInterval = plantFoodTotalDuration / sunNumberWithPlantFood;
        owner.setState(Plant.State.PLANT_FOOD);
    }

    @Override
    public void update(Plant owner, float delta) {
        ensureTimingLoaded(owner);

        if (App.getCurrentGame() instanceof IZombieLevel) return;

        if (inPlantFood) {
            tickPlantFood(owner, delta);
            return;
        }

        tick(owner, delta);


        if (shroom && checkShroomSize) {
            if (plantationTime > (72 - growTimeToReduce)) {
                checkShroomSize = false;
                setSunSize(75);
            } else if (plantationTime > (24 - growTimeToReduce)) {
                setSunSize(50);
            }
        }
        if (isInstant && instantProductDone) return;

        if (lastProductionTime >= productionTime || isInstant) {
            if (owner.getState() == Plant.State.IDLE){
                owner.setState(Plant.State.SPECIAL);
            }
            currentActionTimer += delta;
            if (currentActionTimer < actionTime) return;

            enable = false;
            lastProductionTime = 0;
            for (int i = 0; i < sunNumber; i++) {
                if (doubleSunChance && Math.random() < 0.2) componentSuns.add(produceSun(owner));
                componentSuns.add(produceSun(owner));
                instantProductDone = true;
            }
        }

        if (!isInstant && componentSuns.isEmpty()) {
            enable = true;
        }
    }

    private Sun produceSun(Plant owner) {
        Sun newSun = App.getCurrentGame(owner).getSunsPool().acquire();
        newSun.reset(owner.getX(), owner.getY(), sunSize, this);
        App.getCurrentGame(owner).getActiveSuns().add(newSun);
        return newSun;
    }
    private void setSunSize(int newSize) {
        this.sunSize = newSize;
    }

    private void tick(Plant owner, float delta) {
        plantationTime += delta;
        if (currentActionTimer >= actionTime){
            currentActionTimer += delta;
            if (currentActionTimer >= actionTimeInterval){
                currentActionTimer = 0;
                owner.setState(Plant.State.IDLE);
                if (isInstant){
                    owner.die();
                    return;
                }
            }
        }
        if (enable) {
            lastProductionTime += delta;
        }
    }

    public ArrayList<Sun> getComponentSuns() {
        return componentSuns;
    }
}
