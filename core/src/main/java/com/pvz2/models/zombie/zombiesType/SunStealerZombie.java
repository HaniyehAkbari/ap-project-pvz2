package com.pvz2.models.zombie.zombiesType;

import com.pvz2.controller.GameMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.Zombies;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.Sun;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.view.util.LawnGrid;

import java.util.List;

public class SunStealerZombie extends Zombie {
    private int stolenSun = 0;
    private final boolean isRa;

    private boolean isStealing = false;
    private float stealingPhaseTimer = 0f;
    private float sunAccTimer = 0f;
    private String animState = "walk";
    private boolean laserFiredThisCycle = false;

    private static final float POWER_UP_DUR = 0.67f;
    private static final float POWER_DUR = 1.0f;
    private static final float TURQUOISE_POWER_DUR = 5.0f;
    private static final float POWER_DOWN_DUR = 1.27f;

    public static final String LASER_PAM_PATH = "768/FULL/EFFECTS/CRYSTALSKULL_BEAM/CRYSTALSKULL_BEAM.PAM";
    public static final String LASER_CLIP = "laser_beam";

    public SunStealerZombie(int health, double speed, int damage, boolean isRa) {
        super(Zombies.SUN_STEALER, health, speed, damage);
        this.isRa = isRa;
        if (isRa) {
            this.specificName = "ZombieRa";
        } else {
            this.specificName = "ZombieTurquoise";
        }
    }

    @Override
    public void update(float delta) {
        if (isDead) return;

        GameWorld game = App.getCurrentGame();
        if (game == null) {
            super.update(delta);
            return;
        }

        if (isRa) {
            updateRaSteal(game, delta);
        } else {
            updateTurquoiseSteal(game, delta);
        }

        if (!isStealing) {
            super.update(delta);
        }
    }

    private void updateRaSteal(GameWorld game, float delta) {
        List<Sun> suns = game.getActiveSuns();
        boolean foundSunInRange = false;
        for (Sun sun : suns) {
            if (sun.isCollected() || sun.isExpired()) continue;
            float dx = this.x - sun.getX();
            float dy = this.y - sun.getY();
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            if (distance < 350f) {
                foundSunInRange = true;
                break;
            }
        }
        if (!isStealing && foundSunInRange) {
            isStealing = true;
            stealingPhaseTimer = 0f;
            animState = "power_up";
        }
        if (isStealing) {
            stealingPhaseTimer += delta;
            if (stealingPhaseTimer < POWER_UP_DUR) {
                animState = "power_up";
            } else if (stealingPhaseTimer < (POWER_UP_DUR + POWER_DUR)) {
                animState = "power";
                for (Sun sun : suns) {
                    if (sun.isCollected() || sun.isExpired()) continue;
                    float dx = this.x - sun.getX();
                    float dy = this.y - sun.getY();
                    float distance = (float) Math.sqrt(dx * dx + dy * dy);
                    if (distance < 350f) {
                        float pullSpeed = 400f * delta;
                        if (distance <= pullSpeed || distance < 20f) {
                            sun.collect();
                            stolenSun += sun.getSize();
                            GameMenuController.updateState("Ra stole a sun of size " + sun.getSize());
                        } else {
                            sun.setX(sun.getX() + (dx / distance) * pullSpeed);
                            sun.setY(sun.getY() + (dy / distance) * pullSpeed);
                        }
                    }
                }
            } else if (stealingPhaseTimer < (POWER_UP_DUR + POWER_DUR + POWER_DOWN_DUR)) {
                animState = "power_down";
            } else {
                isStealing = false;
                stealingPhaseTimer = 0f;
                animState = "walk";
            }
        }
    }

    private void updateTurquoiseSteal(GameWorld game, float delta) {
        if (!isStealing) {
            if (isPlantInFront(game)) {
                isStealing = true;
                stealingPhaseTimer = 0f;
                sunAccTimer = 0f;
                animState = "power_up";
                laserFiredThisCycle = false;
            }
        }

        if (isStealing) {
            stealingPhaseTimer += delta;

            if (stealingPhaseTimer < POWER_UP_DUR) {
                animState = "power_up";
            } else if (stealingPhaseTimer < (POWER_UP_DUR + TURQUOISE_POWER_DUR)) {
                animState = "power";
                sunAccTimer += delta;
                if (sunAccTimer >= 1.0f) {
                    int stolen = game.stealSunFromPlayer(25);
                    stolenSun += stolen;
                    sunAccTimer -= 1.0f;
                }
            } else if (stealingPhaseTimer < (POWER_UP_DUR + TURQUOISE_POWER_DUR + POWER_DOWN_DUR)) {
                animState = "power_down";
                if (!laserFiredThisCycle) {
                    fireLaser(game);
                    laserFiredThisCycle = true;
                }
            } else {
                isStealing = false;
                stealingPhaseTimer = 0f;
                animState = "walk";
            }
        }
    }

    private void fireLaser(GameWorld game) {
        int row = LawnGrid.getRowFromY(this.y);
        int col = LawnGrid.getColFromX(this.x);

        for (int i = 1; i <= 4; i++) {
            int targetCol = col - i;
            if (targetCol < 0) break;
            if (row >= 0 && row < game.getRows() && targetCol < game.getCols()) {
                Cell cell = game.getGrid()[row][targetCol];

                while (cell.findAndRemovePlant()) {
                }
            }
        }
    }

    private boolean isPlantInFront(GameWorld game) {
        int row = LawnGrid.getRowFromY(this.y);
        int col = LawnGrid.getColFromX(this.x);

        for (int i = 1; i <= 4; i++) {
            int targetCol = col - i;
            if (targetCol < 0) break;

            if (row >= 0 && row < game.getRows() && targetCol < game.getCols()) {
                Cell cell = game.getGrid()[row][targetCol];

                if (!cell.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getAnimationClip() {
        if (isDead) return "die";
        if (isStealing) {
            return animState;
        }
        return super.getAnimationClip();
    }

    @Override
    public void die() {
        GameWorld game = App.getCurrentGame();
        if (game != null && stolenSun > 0) {
            if (isRa) {
                game.addSunToPlayer(stolenSun);
            } else {
                game.addSunToPlayer(stolenSun / 2);
            }
        }
        super.die();
    }

    public boolean isRa() { return isRa; }
    public String getTurquoiseAnimState() { return animState; }
}
