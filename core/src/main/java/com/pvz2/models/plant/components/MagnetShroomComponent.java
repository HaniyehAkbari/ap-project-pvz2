package com.pvz2.models.plant.components;

import com.pvz2.controller.LevelMenuController;
import com.pvz2.models.plant.AnimationDurations;
import com.pvz2.models.plant.GameComponent;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.Cell;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.zombie.zombiesType.ArmoredZombie;

import java.util.List;

public class MagnetShroomComponent implements GameComponent {
    private static final String PLANT_FOOD_INTRO_CLIP = "plantfood_on";
    private static final String PLANT_FOOD_CLIP = "plantfood";
    private static final String PLANT_FOOD_OUTRO_CLIP = "plantfood_off";
    private static final float DEFAULT_PLANT_FOOD_DURATION = 1.0f;

    int radius;
    float currentDisableTime = 0f;
    float disableTime = 15f;
    boolean disable = false;

    private float plantFoodTimer = 0f;
    private float introOutroTimer = 0f;

    private float plantFoodIntroDuration = -1f;
    private float plantFoodDuration = -1f;
    private float plantFoodOutroDuration = -1f;

    public MagnetShroomComponent(int radius) {
        this.radius = radius;
    }

    private void ensurePlantFoodDurationsLoaded(Plant owner) {
        if (plantFoodDuration >= 0f) return;
        plantFoodIntroDuration = AnimationDurations.hasClip(owner.getType(), PLANT_FOOD_INTRO_CLIP)
            ? AnimationDurations.getDuration(owner.getType(), PLANT_FOOD_INTRO_CLIP, 0f) : 0f;
        plantFoodDuration = AnimationDurations.getDuration(owner.getType(),
            PLANT_FOOD_CLIP, DEFAULT_PLANT_FOOD_DURATION);
        plantFoodOutroDuration = AnimationDurations.hasClip(owner.getType(), PLANT_FOOD_OUTRO_CLIP)
            ? AnimationDurations.getDuration(owner.getType(), PLANT_FOOD_OUTRO_CLIP, 0f) : 0f;
    }

    @Override
    public void update(Plant owner, float delta) {
        ensurePlantFoodDurationsLoaded(owner);
        if (owner.getState() == Plant.State.PLANT_FOOD_INTRO) {
            introOutroTimer -= delta;
            if (introOutroTimer <= 0f) {
                stripNearbyArmor(owner, 3);
                plantFoodTimer = 0f;
                owner.setState(Plant.State.PLANT_FOOD);
            }
            return;
        }
        if (owner.getState() == Plant.State.PLANT_FOOD) {
            plantFoodTimer += delta;
            if (plantFoodTimer >= plantFoodDuration) {
                if (plantFoodOutroDuration > 0f) {
                    owner.setState(Plant.State.PLANT_FOOD_OUTRO);
                    introOutroTimer = plantFoodOutroDuration;
                } else {
                    owner.setState(Plant.State.IDLE);
                }
            }
            return;
        }
        if (owner.getState() == Plant.State.PLANT_FOOD_OUTRO) {
            introOutroTimer -= delta;
            if (introOutroTimer <= 0f) {
                owner.setState(Plant.State.IDLE);
            }
            return;
        }
        if (disable) {
            currentDisableTime -= delta;
            if (currentDisableTime <= 0) {
                disable = false;
                owner.setState(Plant.State.IDLE);
            }
            return;
        }
        List<Cell> cells = Cell.getNeighborCells(owner.getCell(), LevelMenuController.getGameCells(), radius);
        List<Zombie> zombies = Cell.getZombiesInCells(cells);
        for (Zombie zombie : zombies) {
            if (zombie instanceof ArmoredZombie armoredZombie) {
                armoredZombie.stripArmor();
                disable = true;
                owner.setState(Plant.State.BUSY);
                currentDisableTime = disableTime;
                return;
            }
        }
    }

    private void stripNearbyArmor(Plant owner, int maxCount) {
        List<Cell> cells = Cell.getNeighborCells(owner.getCell(), LevelMenuController.getGameCells(), radius);
        List<Zombie> zombies = Cell.getZombiesInCells(cells);
        int count = 0;
        for (Zombie zombie : zombies) {
            if (zombie instanceof ArmoredZombie armoredZombie) {
                count++;
                armoredZombie.stripArmor();
                if (count == maxCount) {
                    break;
                }
            }
        }
    }

    @Override
    public void activatePlantFood(Plant owner) {
        ensurePlantFoodDurationsLoaded(owner);

        disable = false;

        if (plantFoodIntroDuration > 0f) {
            owner.setState(Plant.State.PLANT_FOOD_INTRO);
            introOutroTimer = plantFoodIntroDuration;
        } else {
            stripNearbyArmor(owner, 3);
            plantFoodTimer = 0f;
            owner.setState(Plant.State.PLANT_FOOD);
        }
    }
}
