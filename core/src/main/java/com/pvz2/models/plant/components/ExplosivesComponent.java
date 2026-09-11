package com.pvz2.models.plant.components;

import com.pvz2.controller.LevelMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.AnimationDurations;
import com.pvz2.models.plant.GameComponent;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.explosiveBehaviors.ExplosiveBehavior;
import com.pvz2.models.plant.components.explosiveTriggers.ExplosiveTrigger;
import com.pvz2.models.plant.components.explosiveTriggers.InstantTrigger;
import com.pvz2.models.world.Cell;
import com.pvz2.models.zombie.Zombie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExplosivesComponent implements GameComponent {
    private static final String PLANT_FOOD_INTRO_CLIP = "plantfood_on";
    private static final String PLANT_FOOD_CLIP = "plantfood";
    private static final String PLANT_FOOD_OUTRO_CLIP = "plantfood_off";
    private static final float DEFAULT_PLANT_FOOD_DURATION = 1.0f;

    private ExplosiveBehavior explosiveBehavior;
    private ExplosiveTrigger triggerStrategy;
    private float postTriggerDelay = 2.0f;
    private float maxPostTriggerDelay = 2.0f;
    private boolean isArmed;
    private boolean isTriggered = false;
    private float armTimer;
    private int lives = 1;
    private Cell target;

    private ExplosivePlantFoodBehavior plantFoodBehavior;
    private ExplosiveBehavior delayedBehavior;
    private ExplodeCallback explodeCallback;

    private float plantFoodTimer = 0f;
    private float introOutroTimer = 0f;

    private float plantFoodIntroDuration = -1f;
    private float plantFoodDuration = -1f;
    private float plantFoodOutroDuration = -1f;
    private boolean jumpsToTarget = false;
    private float jumpArcHeight = 120f;
    private boolean jumping = false;
    private boolean jumpingRight = false;
    private float jumpOriginX, jumpOriginY;

    private final List<Cell> plantFoodJumpTargets = new ArrayList<>();
    private int plantFoodJumpIndex = -1;
    private boolean plantFoodJumpActive = false;
    private float plantFoodJumpTimer = 0f;
    private float plantFoodOriginX, plantFoodOriginY;

    public ExplosivesComponent(ExplosiveTrigger trigger, ExplosiveBehavior behavior,
                               float armTime, float maxPostTriggerDelay) {
        this.triggerStrategy = trigger;
        this.explosiveBehavior = behavior;
        this.armTimer = armTime;
        this.isArmed = (armTime <= 0);
        this.maxPostTriggerDelay = maxPostTriggerDelay;
        this.postTriggerDelay = maxPostTriggerDelay;
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

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public void update(Plant owner, float delta) {
        ensurePlantFoodDurationsLoaded(owner);

        if (plantFoodJumpActive) {
            updatePlantFoodJump(owner, delta);
            return;
        }

        if (updatePlantFoodStates(owner, delta)) return;
        if (updateArming(owner, delta)) return;
        updateTriggered(owner, delta);
    }

    private boolean updatePlantFoodStates(Plant owner, float delta) {
        if (owner.getState() == Plant.State.PLANT_FOOD_INTRO) {
            introOutroTimer -= delta;
            if (introOutroTimer <= 0f) {
                runPlantFoodBehavior(owner);
                plantFoodTimer = 0f;
                owner.setState(Plant.State.PLANT_FOOD);
            }
            return true;
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
            return true;
        }

        if (owner.getState() == Plant.State.PLANT_FOOD_OUTRO) {
            introOutroTimer -= delta;
            if (introOutroTimer <= 0f) {
                owner.setState(Plant.State.IDLE);
            }
            return true;
        }

        return false;
    }

    private boolean updateArming(Plant owner, float delta) {
        if (!isArmed) {
            if (owner.getState() != Plant.State.UNARMED) {
                owner.setState(Plant.State.UNARMED);
            }
            armTimer -= delta;
            if (armTimer <= 0) {
                isArmed = true;
                owner.setState(Plant.State.IDLE);
            }
            return true;
        }
        return false;
    }

    private void updateTriggered(Plant owner, float delta) {
        if (isTriggered) {
            if (jumping) {
                updateJumpAnimation(owner, delta);
            } else {
                if (owner.getState() != Plant.State.TRIGGERED) {
                    owner.setState(Plant.State.TRIGGERED);
                }
            }

            postTriggerDelay -= delta;
            if (postTriggerDelay <= 0) {
                finishTrigger(owner);
            }
            return;
        }

        if (owner.getState() != Plant.State.IDLE) {
            owner.setState(Plant.State.IDLE);
        }

        if (triggerStrategy.shouldTrigger(owner, this)) {
            isTriggered = true;
            jumping = jumpsToTarget && target != null && target != owner.getCell();
            if (jumping) {
                jumpOriginX = owner.getX();
                jumpOriginY = owner.getY();
                jumpingRight = target.getX() > jumpOriginX;
            }
        }
    }

    private void updateJumpAnimation(Plant owner, float delta) {
        float t = maxPostTriggerDelay > 0f
            ? Math.min(1f, 1f - (postTriggerDelay / maxPostTriggerDelay)) : 1f;
        boolean ascending = t < 0.5f;

        Plant.State phaseState = ascending
            ? (jumpingRight ? Plant.State.JUMP_UP_RIGHT : Plant.State.JUMP_UP_LEFT)
            : (jumpingRight ? Plant.State.JUMP_DOWN_RIGHT : Plant.State.JUMP_DOWN_LEFT);
        if (owner.getState() != phaseState) owner.setState(phaseState);

        float x = lerp(jumpOriginX, target.getX(), t);
        float y = lerp(jumpOriginY, target.getY(), t) + jumpArcHeight * 4f * t * (1f - t);
        owner.setX((int) x);
        owner.setY((int) y);
    }

    private void finishTrigger(Plant owner) {
        if (jumping) {
            owner.setX((int) target.getX());
            owner.setY((int) target.getY());
            jumping = false;
        }
        lives--;
        explosiveBehavior.execute(owner);

        if (explodeCallback != null) {
            explodeCallback.onExplode(owner);
        }

        if (lives > 0) {
            postTriggerDelay = maxPostTriggerDelay;
            isTriggered = false;
            owner.setState(Plant.State.IDLE);
        } else {
            if (delayedBehavior != null) {
                delayedBehavior.execute(owner);
            }
            if (owner.getType() == PlantType.HOT_POTATO) owner.setCell(null);
            owner.die();
        }
    }

    private void updatePlantFoodJump(Plant owner, float delta) {
        Cell currentTarget = plantFoodJumpTargets.get(plantFoodJumpIndex);

        float t = maxPostTriggerDelay > 0f
            ? Math.min(1f, 1f - (plantFoodJumpTimer / maxPostTriggerDelay)) : 1f;
        boolean ascending = t < 0.5f;

        Plant.State phaseState = ascending
            ? (jumpingRight ? Plant.State.JUMP_UP_RIGHT : Plant.State.JUMP_UP_LEFT)
            : (jumpingRight ? Plant.State.JUMP_DOWN_RIGHT : Plant.State.JUMP_DOWN_LEFT);
        if (owner.getState() != phaseState) owner.setState(phaseState);

        float x = lerp(jumpOriginX, currentTarget.getX(), t);
        float y = lerp(jumpOriginY, currentTarget.getY(), t) + jumpArcHeight * 4f * t * (1f - t);
        owner.setX((int) x);
        owner.setY((int) y);

        plantFoodJumpTimer -= delta;
        if (plantFoodJumpTimer <= 0f) {
            owner.setX((int) currentTarget.getX());
            owner.setY((int) currentTarget.getY());

            this.target = currentTarget;
            explosiveBehavior.execute(owner);

            plantFoodJumpIndex++;
            if (plantFoodJumpIndex < plantFoodJumpTargets.size()) {
                jumpOriginX = owner.getX();
                jumpOriginY = owner.getY();
                Cell nextTarget = plantFoodJumpTargets.get(plantFoodJumpIndex);
                jumpingRight = nextTarget.getX() > jumpOriginX;
                plantFoodJumpTimer = maxPostTriggerDelay;
            } else {
                owner.setX((int) plantFoodOriginX);
                owner.setY((int) plantFoodOriginY);
                this.target = null;
                plantFoodJumpActive = false;
                plantFoodJumpTargets.clear();
                plantFoodJumpIndex = -1;
                owner.setState(Plant.State.IDLE);
            }
        }
    }

    @Override
    public void activatePlantFood(Plant owner) {
        if (plantFoodBehavior == null) return;

        if (jumpsToTarget) {
            startPlantFoodJumpSequence(owner);
            return;
        }

        if (triggerStrategy instanceof InstantTrigger) {
            runPlantFoodBehavior(owner);
            return;
        }

        ensurePlantFoodDurationsLoaded(owner);

        if (plantFoodIntroDuration > 0f) {
            owner.setState(Plant.State.PLANT_FOOD_INTRO);
            introOutroTimer = plantFoodIntroDuration;
        } else {
            runPlantFoodBehavior(owner);
            plantFoodTimer = 0f;
            owner.setState(Plant.State.PLANT_FOOD);
        }
    }

    private void startPlantFoodJumpSequence(Plant owner) {
        List<Zombie> allZombies = new ArrayList<>(App.getCurrentGame().getActiveZombies());
        Collections.shuffle(allZombies);

        Cell[][] grid = LevelMenuController.getGameCells();
        plantFoodJumpTargets.clear();
        for (Zombie zombie : allZombies) {
            Cell zombieCell = Cell.findZombieCell(grid, zombie);
            if (zombieCell != null) {
                plantFoodJumpTargets.add(zombieCell);
            }
            if (plantFoodJumpTargets.size() == 2) break;
        }

        if (plantFoodJumpTargets.isEmpty()) return;
        plantFoodOriginX = owner.getX();
        plantFoodOriginY = owner.getY();
        plantFoodJumpIndex = 0;
        plantFoodJumpActive = true;

        jumpOriginX = owner.getX();
        jumpOriginY = owner.getY();
        Cell firstTarget = plantFoodJumpTargets.get(0);
        jumpingRight = firstTarget.getX() > jumpOriginX;
        plantFoodJumpTimer = maxPostTriggerDelay;
    }

    private void runPlantFoodBehavior(Plant owner) {
        plantFoodBehavior.execute(owner, this);
    }

    public void scheduleDelayedBehavior(ExplosiveBehavior behavior) {
        this.delayedBehavior = behavior;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setPlantFoodBehavior(ExplosivePlantFoodBehavior plantFoodBehavior) {
        this.plantFoodBehavior = plantFoodBehavior;
    }

    public void setPostTriggerDelay(float postTriggerDelay) {
        this.postTriggerDelay = postTriggerDelay;
    }

    public Cell getTarget() {
        return target;
    }

    public void setTarget(Cell target) {
        this.target = target;
    }

    public void instantArm() {
        isArmed = true;
    }

    public void setExplodeCallback(ExplodeCallback callback) {
        this.explodeCallback = callback;
    }

    public void setJumpsToTarget(boolean jumpsToTarget) {
        this.jumpsToTarget = jumpsToTarget;
    }

    public interface ExplodeCallback {
        void onExplode(Plant owner);
    }
}
