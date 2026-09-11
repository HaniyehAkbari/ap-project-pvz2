package com.pvz2.models.plant.components;

import com.pvz2.controller.LevelMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.plant.AnimationDurations;
import com.pvz2.models.plant.GameComponent;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.Cell;
import com.pvz2.models.zombie.Zombie;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChomperMeleeComponent implements GameComponent {
    private static final String BITE_CLIP = "special";
    private static final float DEFAULT_BITE_DURATION = 0.6f;

    private static final String PLANT_FOOD_INTRO_CLIP = "plantfood_on";
    private static final String PLANT_FOOD_CLIP = "plantfood";
    private static final String PLANT_FOOD_OUTRO_CLIP = "plantfood_off";
    private static final float DEFAULT_PLANT_FOOD_DURATION = 1.0f;

    private final float digestTime;
    private boolean isDigesting = false;
    private float digestProgressTime = 0f;
    private float biteAnimTimer = 0f;

    private float plantFoodTimer = 0f;
    private float introOutroTimer = 0f;

    private float biteDuration = -1f;
    private float plantFoodIntroDuration = -1f; // 0 means "no intro configured"
    private float plantFoodDuration = -1f;
    private float plantFoodOutroDuration = -1f; // 0 means "no outro configured"

    public ChomperMeleeComponent(float digestTime) {
        this.digestTime = digestTime;
    }

    private void ensureBiteDurationLoaded(Plant owner) {
        if (biteDuration >= 0f) return;
        biteDuration = AnimationDurations.getDuration(owner.getType(), BITE_CLIP, DEFAULT_BITE_DURATION);
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
        ensureBiteDurationLoaded(owner);
        ensurePlantFoodDurationsLoaded(owner);
        if (owner.getState() == Plant.State.PLANT_FOOD_INTRO) {
            introOutroTimer -= delta;
            if (introOutroTimer <= 0f) {
                dealPlantFoodDamage(owner);
                plantFoodTimer = 0f;
                owner.setState(Plant.State.PLANT_FOOD);}
            return;
        }
        if (owner.getState() == Plant.State.PLANT_FOOD) {
            plantFoodTimer += delta;
            if (plantFoodTimer >= plantFoodDuration) {
                if (plantFoodOutroDuration > 0f) {
                    owner.setState(Plant.State.PLANT_FOOD_OUTRO);
                    introOutroTimer = plantFoodOutroDuration;
                } else {
                    owner.setState(Plant.State.IDLE);}}
            return;
        }
        if (owner.getState() == Plant.State.PLANT_FOOD_OUTRO) {
            introOutroTimer -= delta;
            if (introOutroTimer <= 0f) {
                owner.setState(Plant.State.IDLE);
            }
            return;
        }
        if (isDigesting) {
            digestProgressTime += delta;
            if (owner.getState() == Plant.State.SPECIAL) {
                biteAnimTimer += delta;
                if (biteAnimTimer >= biteDuration) {
                    owner.setState(Plant.State.SPECIAL_IDLE);}
            }
            if (digestProgressTime >= digestTime) {
                isDigesting = false;
                digestProgressTime = 0f;
                biteAnimTimer = 0f;
                owner.setState(Plant.State.IDLE);
            }
            return;
        }
        if (owner.getState() != Plant.State.IDLE) {
            owner.setState(Plant.State.IDLE);
        }
        Zombie target = findTargetZombie(owner);
        if (target != null) {
            swallowZombie(owner, target);}
    }

    private Zombie findTargetZombie(Plant owner) {
        Cell cell = owner.getCell();
        if (cell == null) return null;

        List<Cell> cells = Cell.getCellsInRow(cell, LevelMenuController.getGameCells());
        List<Zombie> rowZombies = Cell.getZombiesInCells(cells);
        List<Zombie> targets = new ArrayList<>();
        for (Zombie zombie : rowZombies) {
            if (zombie.getX() >= owner.getX() && zombie.getX() - owner.getX() <= App.getCellWidth() * 1.5f) {
                targets.add(zombie);
            }
        }
        if (!targets.isEmpty()) {
            return targets.getFirst();
        }
        return null;
    }

    private void swallowZombie(Plant owner, Zombie zombie) {
        if (zombie.isBoss()) return;
        zombie.takeDamage((int) zombie.getHealth(), "NORMAL");
        this.isDigesting = true;
        this.digestProgressTime = 0f;
        this.biteAnimTimer = 0f;
        owner.setState(Plant.State.SPECIAL);
    }

    private void dealPlantFoodDamage(Plant owner) {
        Cell cell = owner.getCell();
        if (cell == null) return;

        List<Cell> cells = Cell.getCellsInRow(cell, LevelMenuController.getGameCells());
        List<Zombie> rowZombies = Cell.getZombiesInCells(cells);

        List<Zombie> targets = rowZombies.stream()
            .filter(zombie -> zombie.getX() >= owner.getX())
            .sorted(Comparator.comparingDouble(Zombie::getX))
            .limit(3)
            .toList();

        for (Zombie target : targets) {
            if (target.isBoss()) continue;
            target.takeDamage((int) target.getHealth(), "NORMAL");
        }
    }

    @Override
    public void activatePlantFood(Plant owner) {
        ensureBiteDurationLoaded(owner);
        ensurePlantFoodDurationsLoaded(owner);

        if (plantFoodIntroDuration > 0f) {
            owner.setState(Plant.State.PLANT_FOOD_INTRO);
            introOutroTimer = plantFoodIntroDuration;
        } else {
            dealPlantFoodDamage(owner);
            plantFoodTimer = 0f;
            owner.setState(Plant.State.PLANT_FOOD);
        }
    }
}
