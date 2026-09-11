package com.pvz2.models.world.obstacles;

import com.pvz2.controller.GameMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.CollectableType;
import com.pvz2.models.world.Collectable;
import com.pvz2.models.world.GameWorld;

public class Grave extends Obstacle {
    private GraveType type;
    private boolean isCollected = false;
    private int row;
    private int col;

    private final int maxHealth;
    public boolean isDying = false;

    public Grave(float x, float y, int row, int col, GraveType type) {
        super(x, y, 700);
        this.maxHealth = 700;
        this.row = row;
        this.col = col;
        this.type = type;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public GraveType getType() {
        return type;
    }


    public boolean blocksProjectiles() {
        return !isDestroyed && !isDying;
    }

    @Override
    public void takeDamage(int amount, String type) {
        if (isDestroyed || isDying) return;

        triggerDamageFlash();
        this.health -= amount;
        GameMenuController.updateState("grave in (" + x + ", " + y + ") health: " + health);

        if (this.health <= 0) {
            this.health = 0;
            this.isDying = true; // انیمیشن شکستن شروع می‌شود
            releaseContent();
        }
    }

    public int getDamageStage() {
        if (health > (maxHealth * 2) / 3) return 0;
        if (health > maxHealth / 3) return 1;
        return 2;
    }

    public void releaseContent() {
        if (isCollected) return;

        GameWorld game = App.getCurrentGame();
        if (game == null) return;

        if (type == GraveType.SUN) {
            game.getActiveSuns().add(game.getSunsPool().acquire());
        } else if (type == GraveType.PLANT_FOOD) {
            game.getActiveCollectables().add(new Collectable(x, y, CollectableType.PLANT_FOOD));
        }
        isCollected = true;
    }

    public boolean isDying() {
        return isDying;
    }

    public void markDestroyed() { this.isDestroyed = true; }

    public enum GraveType {
        NORMAL,
        SUN,
        PLANT_FOOD
    }

    @Override
    public void die() {
        super.die();
        isDying = true;
    }
}
