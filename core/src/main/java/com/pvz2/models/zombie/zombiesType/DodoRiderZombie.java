package com.pvz2.models.zombie.zombiesType;

import com.pvz2.controller.GameMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.enums.Zombies;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.zombie.Zombie;

import java.util.HashSet;
import java.util.Set;

public class DodoRiderZombie extends Zombie {

    private static final Set<PlantType> HIGH_HEALTH_PLANTS = Set.of(
        PlantType.WALL_NUT
    );

    private static final Set<PlantType> ROW_REDIRECT_PLANTS = Set.of(
        PlantType.GARLIC
    );

    private static final Set<PlantType> DANGEROUS_PLANTS = Set.of(
        PlantType.POTATO_MINE,
        PlantType.PRIMAL_POTATO_MINE,
        PlantType.CHERRY_BOMB,
        PlantType.JALAPENO,
        PlantType.DOOM_SHROOM,
        PlantType.ICE_SHROOM,
        PlantType.EXPLODE_O_NUT
    );

    private static final Set<PlantType> FLY_OVER_PLANTS = new HashSet<>();
    static {
        FLY_OVER_PLANTS.addAll(HIGH_HEALTH_PLANTS);
        FLY_OVER_PLANTS.addAll(ROW_REDIRECT_PLANTS);
        FLY_OVER_PLANTS.addAll(DANGEROUS_PLANTS);
    }

    private static final float FLY_START_DURATION = 0.25f;
    private static final float FLY_LOOP_DURATION = 0.5f;
    private static final float FLY_END_DURATION = 0.25f;
    private static final float FLY_TOTAL_DURATION =
        FLY_START_DURATION + FLY_LOOP_DURATION + FLY_END_DURATION;

    private enum FlyPhase { NONE, FLY_START, FLY_LOOP, FLY_END }

    private boolean isRiding;
    private FlyPhase flyPhase = FlyPhase.NONE;
    private float flyPhaseTime = 0f;
    private float flyElapsedTotal = 0f;
    private float flyStartX, flyStartY, flyTargetX, flyTargetY;

    public DodoRiderZombie(int health, double speed, int damage) {
        super(Zombies.DODO_RIDER, health, speed, damage);
        this.isRiding = true;
    }

    @Override
    public void update(float delta) {
        if (isDead) return;

        if (flyPhase != FlyPhase.NONE) {
            updateFlying(delta);
            return;
        }

        GameWorld game = App.getCurrentGame();
        if (game == null) {
            super.update(delta);
            return;
        }

        if (isRiding) {
            Cell currentCell = game.getCellAt(this.x, this.y);
            if (currentCell != null) {
                int row = currentCell.getRow();
                int col = currentCell.getCol();

                if (col > 0) {
                    Cell frontCell = game.getGrid()[row][col - 1];
                    if (frontCell != null) {
                        Plant obstacle = frontCell.getPlant();
                        if (obstacle != null && !obstacle.isDead()) {
                            if (obstacle != null && !obstacle.isDead()) {
                                handleObstacle(obstacle, frontCell);
                            }
                        }

                        if (frontCell.getSlippingDir() != 0) {
                            this.x -= App.getCellWidth();
                        }
                    }
                }
            }
        }

        if (flyPhase != FlyPhase.NONE) {
            return;
        }

        super.update(delta);
    }

    private void handleObstacle(Plant plant, Cell cell) {
        PlantType type = plant.getType();

        if (type == PlantType.TALL_NUT) {
            return;
        }

        if (FLY_OVER_PLANTS.contains(type)) {
            flyOverObstacle(type, cell);
        }
    }

    private void flyOverObstacle(PlantType type, Cell cell) {
        if (flyPhase != FlyPhase.NONE) return;

        GameWorld game = App.getCurrentGame();
        if (game == null) return;

        int landingCol = cell.getCol() - 1;
        int row = cell.getRow();

        flyStartX = this.x;
        flyStartY = this.y;

        if (landingCol >= 0) {
            Cell landingCell = game.getGrid()[row][landingCol];
            flyTargetX = landingCell.getX();
            flyTargetY = landingCell.getY();
        } else {
            flyTargetX = this.x - App.getCellWidth();
            flyTargetY = this.y;
        }

        GameMenuController.updateState(
            "Dodo Rider flew over a " + type.name() + " at (" + cell.getX() + "," + cell.getY() + ")");

        startFlying();
    }

    private void startFlying() {
        flyPhase = FlyPhase.FLY_START;
        flyPhaseTime = FLY_START_DURATION;
        flyElapsedTotal = 0f;
    }

    private void updateFlying(float delta) {
        flyElapsedTotal += delta;

        switch (flyPhase) {
            case FLY_START -> {
                flyPhaseTime -= delta;
                if (flyPhaseTime <= 0) {
                    flyPhase = FlyPhase.FLY_LOOP;
                    flyPhaseTime = FLY_LOOP_DURATION;
                }
            }
            case FLY_LOOP -> {
                flyPhaseTime -= delta;
                if (flyPhaseTime <= 0) {
                    flyPhase = FlyPhase.FLY_END;
                    flyPhaseTime = FLY_END_DURATION;
                }
            }
            case FLY_END -> {
                flyPhaseTime -= delta;
                if (flyPhaseTime <= 0) {
                    this.x = flyTargetX;
                    this.y = flyTargetY;
                    flyPhase = FlyPhase.NONE;
                    flyElapsedTotal = 0f;
                    return;
                }
            }
            case NONE -> {
                return;
            }
        }

        float t = Math.min(1f, flyElapsedTotal / FLY_TOTAL_DURATION);
        this.x = flyStartX + (flyTargetX - flyStartX) * t;
        this.y = flyStartY + (flyTargetY - flyStartY) * t;
    }

    @Override
    public void takeDamage(int amount, String damageType) {
        if (isDead) return;

        super.takeDamage(amount, damageType);

        if (!isDead && isRiding && this.health <= this.maxHealth / 2) {
            isRiding = false;
            this.speed = this.originalSpeed * 0.6;
            GameMenuController.updateState("Dodo Rider lost its mount!");
        }
    }

    @Override
    public String getAnimationClip() {
        if (isDead) return "die";

        switch (flyPhase) {
            case FLY_START:
                return "fly_start";
            case FLY_LOOP:
                return "fly_loop";
            case FLY_END:
                return "fly_end";
            case NONE:
            default:
                break;
        }

        return super.getAnimationClip();
    }
}
