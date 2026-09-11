package com.pvz2.models.zombie.zombiesType;

import com.pvz2.controller.GameMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.Zombies;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.zombie.Zombie;

import java.util.Random;

public class PusherZombie extends Zombie {
    private static final float ROW_SWITCH_INTERVAL = 5.0f;

    public static final String OBJECT_PIANO = "PIANO";
    public static final String OBJECT_ICEBLOCK = "ICEBLOCK";
    public static final String OBJECT_ARCADE = "ARCADE";

    public static final long OBJECT_DAMAGE_FLASH_DURATION_MS = 150L;

    private String objectName;
    private int objectHealth;
    private float rowSwitchCooldown = 0f;
    private Random random = new Random();
    private long lastObjectDamageTimestamp = -1L;

    public PusherZombie(int health, double speed, int damage, String pushedObjectName, int objHealth) {
        super(Zombies.PUSHER, health, speed, damage);
        this.objectName = pushedObjectName;
        this.objectHealth = objHealth;
    }

    @Override
    public void takeDamage(int amount, String damageType) {
        if (isDead) return;
        if (objectHealth > 0) {
            triggerObjectDamageFlash();
            int excess = amount - objectHealth;
            if (excess > 0) {
                objectHealth = 0;
                super.takeDamage(excess, damageType);
            } else {
                objectHealth -= amount;
            }
        } else {
            super.takeDamage(amount, damageType);
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
        Cell zombieCell = Cell.findZombieCell(game.getGrid(), this);
        boolean crushed = false;

        if (objectHealth > 0 && zombieCell != null) {
            Plant plantHere = zombieCell.getPlant();
            if (plantHere != null && !plantHere.isDead()) {
                plantHere.die();
                crushed = true;
                if (OBJECT_ICEBLOCK.equals(objectName)) {
                    destroyObject();
                }
                GameMenuController.updateState(
                        objectName + " crushed plant at (" + plantHere.getX() + ", " + plantHere.getY() + ")");
            }
        }

        if (OBJECT_PIANO.equals(objectName) && objectHealth > 0) {
            if (rowSwitchCooldown <= 0) {
                switchOwnRow(game);
                rowSwitchCooldown = ROW_SWITCH_INTERVAL;
            } else {
                rowSwitchCooldown -= delta;
            }
        }

        if (!crushed) {
            super.update(delta);
        } else {
            this.move(delta);
        }
    }

    @Override
    public String getAnimationClip() {
        if (isDead) return "die";
        if (isPushing()) {
            return OBJECT_PIANO.equals(objectName) ? "play" : "push";
        }
        return super.getAnimationClip();
    }

    /**
     * True only while this zombie is actually able to move forward and still
     * has an object to push (not dead, not frozen, not disabled). Used both
     * for this zombie's own animation clip and for the pushed object's
     * graphic (idle vs. active/push) so they never show a "pushing" pose
     * while standing still (e.g. during the level intro pan).
     */
    public boolean isPushing() {
        return !isDead
                && objectHealth > 0
                && getFreezedTicksRemaining() <= 0
                && getDisabledTicksRemaining() <= 0
                && getIceHealth() <= 0;
    }

    private void destroyObject() {
        if (objectHealth > 0) {
            objectHealth = 0;
            triggerObjectDamageFlash();
        }
    }

    private void triggerObjectDamageFlash() {
        this.lastObjectDamageTimestamp = System.currentTimeMillis();
    }

    public boolean isObjectFlashing() {
        if (lastObjectDamageTimestamp < 0) return false;
        return (System.currentTimeMillis() - lastObjectDamageTimestamp) < OBJECT_DAMAGE_FLASH_DURATION_MS;
    }

    public float getObjectDamageFlashProgress() {
        if (!isObjectFlashing()) return 0f;
        long elapsed = System.currentTimeMillis() - lastObjectDamageTimestamp;
        return 1f - ((float) elapsed / (float) OBJECT_DAMAGE_FLASH_DURATION_MS);
    }

    private void switchOwnRow(GameWorld game) {
        Cell currentCell = Cell.findZombieCell(game.getGrid(), this);
        if (currentCell == null) return;

        int currentRow = currentCell.getRow();
        int targetRow = currentRow + (random.nextBoolean() ? 1 : -1);
        if (targetRow < 0 || targetRow >= game.getRows()) {
            targetRow = currentRow + (random.nextBoolean() ? -1 : 1);
            if (targetRow < 0 || targetRow >= game.getRows()) return;
        }

        this.setY(App.getCellCenterY(targetRow));
        GameMenuController.updateState("Pianist switched to row " + (targetRow + 1));
    }

    public String getObjectName() {
        return objectName;
    }

    public int getObjectHealth() {
        return objectHealth;
    }
}
