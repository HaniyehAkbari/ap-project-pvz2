package com.pvz2.models.zombie;

import com.pvz2.controller.GameMenuController;
import com.pvz2.models.Damageable;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.CollectableType;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.enums.Zombies;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.ChapterWorld.FrostbiteCavesWorld;
import com.pvz2.models.world.Collectable;
import com.pvz2.models.world.GameState;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.zombie.state.WalkingState;
import com.pvz2.models.zombie.state.ZombieState;
import com.pvz2.view.util.LawnGrid;

import java.util.ArrayList;
import java.util.List;

public abstract class Zombie implements Damageable {
    protected Zombies name;
    protected String specificName;
    protected float health;
    protected int maxHealth;
    protected double speed;
    protected double originalSpeed = speed;
    protected int damage;
    protected boolean isDead = false;
    protected float x, y;
    protected ZombieState currentState;
    protected List<String> armorTypes = new ArrayList<>();
    private float slowTimeRemaining = 0f;
    private double slowFactor = 0.5;
    private float disabledTimeRemaining;
    private float freezedTimeRemaining;
    private float onPoisonTimeRemaining;
    private int poisonDamage;
    private float iceHealth = 0;
    private boolean glowing = false;
    private transient GameWorld currentWorld;

    private PlantType killerPlantType;

    private float spawnTime;
    private boolean hasEatenPlant = false;
    private transient GameWorld world;

    public static final long DAMAGE_FLASH_DURATION_MS = 150L;
    private long lastDamageTimestamp = -1L;

    private long shakeRequestTimestamp = -1L;
    private static final long SHAKE_REQUEST_TTL_MS = 300L;

    private final String id = java.util.UUID.randomUUID().toString();

    public String getId() { return id; }


    public Zombie(Zombies name, int health, double speed, int damage) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.speed = speed * 100;
        this.damage = damage / 10;
        this.currentState = new WalkingState();
        this.originalSpeed = this.speed;
    }

    public Zombie(Zombies name, int health, double speed, int damage, GameWorld world) {
        this.world = world;
        this.spawnTime = world.getElapsedTime();

        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.speed = speed * 15;
        this.damage = damage / 10;
        this.currentState = new WalkingState();
        this.originalSpeed = this.speed;
    }

    public void update(float delta, GameWorld world){
        if (currentWorld == null) currentWorld = world;
        update(delta);
    }

    public void update(float delta) {
        if (isDead || health <= 0 || iceHealth > 0) return;
        if (freezedTimeRemaining > 0) {
            freezedTimeRemaining -= delta;
            if (freezedTimeRemaining == 0) applySlow(5.0f, 0.5, true);
            return;
        }
        if (onPoisonTimeRemaining > 0) {
            onPoisonTimeRemaining-= delta;
            health -= poisonDamage * delta;
            if (health <= 0) die();
        }
        if (slowTimeRemaining > 0) {
            slowTimeRemaining-= delta;
            if (slowTimeRemaining == 0f) {
                resetSpeed();
            }
        }


        if (disabledTimeRemaining > 0) {
            disabledTimeRemaining-= delta;
            return;
        }

        Cell currentCell = Cell.findZombieCell(App.getCurrentGame(this).getGrid(), this);
        if (currentCell != null && currentCell.getSlippingDir() != 0) {
            y += App.getCellHeight() * currentCell.getSlippingDir();
            x -= App.getCellWidth() / 2;
        }
        if (currentState != null) {
            currentState.handleAction(this, delta);
        } else {
            currentState = new WalkingState();
        }
    }

    public void move(float delta) {
        this.x -= (float) (this.speed * delta);
    }

    @Override
    public void takeDamage(int amount, String damageType) {
        if (isDead) return;
        triggerDamageFlash();
        if (iceHealth > 0) {
            iceHealth -= amount;
            if (iceHealth <= 0) {
                unfreeze();
            }
            return;
        }
        this.health -= amount;
        if (this.health <= 0) {
            die();
        }
    }

    protected void triggerDamageFlash() {
        this.lastDamageTimestamp = System.currentTimeMillis();
    }

    public boolean isFlashingRed() {
        if (lastDamageTimestamp < 0) return false;
        return (System.currentTimeMillis() - lastDamageTimestamp) < DAMAGE_FLASH_DURATION_MS;
    }

    public float getDamageFlashProgress() {
        if (!isFlashingRed()) return 0f;
        long elapsed = System.currentTimeMillis() - lastDamageTimestamp;
        return 1f - ((float) elapsed / (float) DAMAGE_FLASH_DURATION_MS);
    }

    public void unfreeze() {
        iceHealth = 0;
        freezedTimeRemaining = 0f;
        slowTimeRemaining = 0;
        this.speed = originalSpeed;
    }

    public void die() {
        if (isDead) return;
        this.isDead = true;

        GameWorld world = App.getCurrentGame();
        if (world == null) return;

        if (glowing) {
            Collectable plantFood = new Collectable(this.x, this.y, CollectableType.PLANT_FOOD);
            world.getActiveCollectables().add(plantFood);
            GameMenuController.updateState(
                "\uD83C\uDFC6The glowing zombie dropped a plant food at (" + (int) x + ", " + (int) y + ")");
        }

        if (Math.random() < 0.1) {
            CollectableType type;
            if (Math.random() < 0.33) {
                type = CollectableType.COIN;
            } else if (Math.random() < 0.5) {
                type = CollectableType.POT;
            } else {
                type = CollectableType.DIAMOND;
            }
            Collectable drop = new Collectable(this.x, this.y, type);
            world.getActiveCollectables().add(drop);
            GameMenuController.updateState("\uD83C\uDFC6A zombie dropped a " + type.name().toLowerCase() +
                " at (" + (int) x + ", " + (int) y + ")");
        }

        String displayName = (specificName != null) ? specificName : name.name();
        GameMenuController.updateState("\uD83D\uDC80Zombie of type " + displayName +
            " is dead at (" + (int) x + ", " + (int) y + ")");
    }

    public void applySlow(float delta, double factor, boolean canWorkInFrostbite) {
        if (!canWorkInFrostbite && App.getCurrentGame() instanceof FrostbiteCavesWorld) {
            return;
        }
        if (delta <= 0) return;
        if (slowTimeRemaining == 0) {
            this.originalSpeed = this.speed;
        }
        if (factor < slowFactor) {
            slowFactor = factor;
            this.speed = (originalSpeed * slowFactor);
        }
        if (delta > slowTimeRemaining) {
            slowTimeRemaining = delta;
        }
    }

    public void makePoisoned(int damageOnTick) {
        onPoisonTimeRemaining = 1.0f;
        poisonDamage = damageOnTick;
    }

    public String getAnimationClip() {
        if (isDead) return "die";
        if (freezedTimeRemaining > 0 || iceHealth > 0) return "idle";
        if (disabledTimeRemaining > 0) return "idle";
        return currentState != null ? currentState.getAnimationClip() : "idle";
    }

    public void disableFor(float delta) {
        disabledTimeRemaining = delta;
    }

    public void freeze(float delta) {
        freezedTimeRemaining = delta;
    }

    public boolean isSlowed() {
        return slowTimeRemaining > 0;
    }

    public void resetSpeed() {
        if (originalSpeed == 0) {
            originalSpeed = this.speed;
        }
        this.speed = originalSpeed;
        this.slowTimeRemaining = 0;
        this.slowFactor = 1.0;
    }

    public boolean isBoss() {
        return false;
    }

    public boolean isDead() {
        return isDead;
    }

    public ZombieState getCurrentState() {
        return currentState;
    }

    public void setState(ZombieState state) {
        this.currentState = state;
    }

    @Override
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getY() {
        return y;
    }

    public boolean isNearEndLine() {
        if (App.getCurrentGame() != null && App.getCurrentGame().getState() != GameState.PLAYING) return false;
        float endLineX = App.getCellWidth() / 2f + LawnGrid.getCellX(1);
        return this.x <= endLineX;
    }

    public void setY(float y) {
        this.y = y;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
        if (slowTimeRemaining == 0) {
            this.originalSpeed = speed;
        }
    }

    public void setDamage(int damage) {this.damage = damage;}

    public float getDisabledTicksRemaining() {
        return disabledTimeRemaining;
    }

    public float getFreezedTicksRemaining() {
        return freezedTimeRemaining;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public Zombies getName() {
        return name;
    }

    public String getSpecificName() {
        return specificName;
    }

    public void setSpecificName(String specificName) {
        this.specificName = specificName;
    }

    public int getDamage() {
        return damage;
    }

    public void setKiller(PlantType killer) {
        this.killerPlantType = killer;
    }

    public PlantType getKillerPlantType() {
        return killerPlantType;
    }

    public float getIceHealth() {
        return iceHealth;
    }

    public void setIceHealth(float iceHealth) {
        this.iceHealth = iceHealth;
    }

    public List<String> getArmorTypes() {
        return armorTypes;
    }

    public float getSpawnTime() {
        return spawnTime;
    }

    public boolean hasEatenPlant() {
        return hasEatenPlant;
    }

    public void setHasEatenPlant(boolean hasEatenPlant) {
        this.hasEatenPlant = hasEatenPlant;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public GameWorld getWorld() {
        return world;
    }

    @Override
    public void takeDamage(int damage, Zombie zombie) {

    }

    public void eatBrainAndLeave() {
        this.isDead = true;
        System.out.println("Zombie ate the brain and successfully left the board!");
    }

    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
    }

    public void requestScreenShake() {
        shakeRequestTimestamp = System.currentTimeMillis();
    }

    public boolean consumeScreenShakeRequest() {
        if (shakeRequestTimestamp < 0) return false;
        boolean valid = (System.currentTimeMillis() - shakeRequestTimestamp) < SHAKE_REQUEST_TTL_MS;
        shakeRequestTimestamp = -1L;
        return valid;
    }

    public GameWorld getCurrentWorld() {
        return currentWorld;
    }
}
