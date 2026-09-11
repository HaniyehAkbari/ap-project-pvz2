package com.pvz2.models.plant.components;

import com.pvz2.controller.LevelMenuController;
import com.pvz2.models.Damageable;
import com.pvz2.models.plant.AnimationDurations;
import com.pvz2.models.plant.GameComponent;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.obstacles.Obstacle;
import com.pvz2.models.zombie.Zombie;

import java.util.ArrayList;
import java.util.List;

public class DirectionalMeleeComponent implements GameComponent {
    private static final String PLANT_FOOD_INTRO_CLIP = "plantfood_on";
    private static final String PLANT_FOOD_CLIP = "plantfood";
    private static final String PLANT_FOOD_OUTRO_CLIP = "plantfood_off";
    private static final float DEFAULT_PLANT_FOOD_DURATION = 0.8f;

    private final int damage;
    private final float attackIntervalTicks;
    private final float rangeX;
    private float lastAttackTick = 0;

    private float plantFoodTimer = 0f;
    private float introOutroTimer = 0f;

    private float plantFoodIntroDuration = -1f;
    private float plantFoodDuration = -1f;
    private float plantFoodOutroDuration = -1f;

    public DirectionalMeleeComponent(int damage, float attackIntervalTicks, float rangeX) {
        this.damage = damage;
        this.attackIntervalTicks = attackIntervalTicks;
        this.rangeX = rangeX;
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
                dealPlantFoodDamage(owner);
                plantFoodTimer = 0f;
                owner.setState(Plant.State.PLANT_FOOD);}
            return;}
        if (owner.getState() == Plant.State.PLANT_FOOD_OUTRO) {
            introOutroTimer -= delta;
            if (introOutroTimer <= 0f) {
                owner.setState(Plant.State.IDLE);}
            return;}
        if (owner.getState() == Plant.State.PLANT_FOOD) {
            plantFoodTimer += delta;
            if (plantFoodTimer >= plantFoodDuration) {
                if (plantFoodOutroDuration > 0f) {
                    owner.setState(Plant.State.PLANT_FOOD_OUTRO);
                    introOutroTimer = plantFoodOutroDuration;
                } else {
                    owner.setState(Plant.State.IDLE);}
            }return;
        }
        lastAttackTick += delta;
        if (owner.getState() != Plant.State.IDLE) owner.setState(Plant.State.IDLE);
        if (lastAttackTick >= attackIntervalTicks) {
            List<Damageable> rightTargets;
            List<Damageable> leftTargets;
            if ((rightTargets = checkRight(owner)) != null) {
                if (!rightTargets.isEmpty()) {
                    attack(rightTargets);
                    lastAttackTick = 0;
                    if (owner.getState() != Plant.State.HIT_RIGHT) owner.setState(Plant.State.HIT_RIGHT);}
            }
            if ((leftTargets = checkLeft(owner)) != null) {
                if (!leftTargets.isEmpty()) {
                    attack(leftTargets);
                    lastAttackTick = 0;
                    if (rightTargets != null && !rightTargets.isEmpty()){
                        owner.setState(Plant.State.HIT_RIGHT_AND_LEFT);
                        return;
                    }
                    if (owner.getState() != Plant.State.HIT_LEFT) owner.setState(Plant.State.HIT_LEFT);
                }
            }
            if ((rightTargets != null && !rightTargets.isEmpty()) || (leftTargets != null && !leftTargets.isEmpty())){
            }
        }
    }
    public List<Damageable> checkRight(Plant owner) {
        Cell cell = owner.getCell();
        if (cell == null) return null;
        List<Cell> cells = Cell.getCellsInRow(cell, LevelMenuController.getGameCells(owner));
        List<Zombie> rowZombies = Cell.getZombiesInCells(cells);
        List<Damageable> targets = new ArrayList<>();
        for (Zombie zombie : rowZombies) {
            if (zombie.getX() >= owner.getX() && zombie.getX() - owner.getX() <= rangeX) {
                targets.add(zombie);}
        }
        Cell rightCell = Cell.nextCell(cell, LevelMenuController.getGameCells(owner));
        if (rightCell != null){
            Obstacle obstacle = rightCell.getObstacle();
            if (obstacle != null) targets.add(obstacle);
        }
        return targets;
    }
    public List<Damageable> checkLeft(Plant owner) {
        Cell cell = owner.getCell();
        if (cell == null) return null;
        List<Cell> cells = Cell.getCellsInRow(cell, LevelMenuController.getGameCells(owner));
        List<Zombie> rowZombies = Cell.getZombiesInCells(cells);
        List<Damageable> targets = new ArrayList<>();
        for (Zombie zombie : rowZombies) {
            if (zombie.getX() <= owner.getX() && owner.getX() - zombie.getX() <= rangeX) {
                targets.add(zombie);}
        }
        Cell rightCell = Cell.previousCell(cell, LevelMenuController.getGameCells(owner));
        if (rightCell != null){
            Obstacle obstacle = rightCell.getObstacle();
            if (obstacle != null) targets.add(obstacle);
        }
        return targets;
    }
    private void attack(List<Damageable> targets) {
        for (Damageable damageable : targets) {
            damageable.takeDamage(damage, "NORMAL");}
    }

    private void dealPlantFoodDamage(Plant owner) {
        Cell cell = owner.getCell();
        if (cell == null) return;

        int plantFoodDamage = 300;
        List<Cell> cells = Cell.getNeighborCells(cell, LevelMenuController.getGameCells(), 1);
        List<Damageable> targets = new ArrayList<>(Cell.getZombiesInCells(cells));
        for (Cell cell1 : cells){
            Obstacle obstacle = cell1.getObstacle();
            if (obstacle != null){
                targets.add(obstacle);
            }
        }
        for (Damageable zombie : targets) {
            zombie.takeDamage(plantFoodDamage, "NORMAL");
        }
    }

    @Override
    public void activatePlantFood(Plant owner) {
        Cell cell = owner.getCell();
        if (cell == null) return;

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
