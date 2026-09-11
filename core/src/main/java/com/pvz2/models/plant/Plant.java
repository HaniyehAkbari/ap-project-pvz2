package com.pvz2.models.plant;

import com.pvz2.models.Damageable;
import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.components.ExplosivesComponent;
import com.pvz2.models.plant.components.ImitatorIntroComponent;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.zombie.Zombie;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Plant implements Damageable {
    private PlantType type;
    private int health;
    private int initHealth;
    private float x, y;
    private int damage;
    private transient List<GameComponent> components = new CopyOnWriteArrayList<>();
    private boolean dead = false;
    private boolean sheep = false;
    private boolean disabled = false;
    private transient Cell cell = null;
    private int frozenAmount = 0;
    private boolean freeze = false;
    private float iceHealth = 0;
    private boolean isFire = false;
    private int warmRadius = 1;
    private boolean plantFoodInStart = false;
    private State state = State.IDLE;
    private boolean isImitate;
    public static final int MAX_ICE_HEALTH = 600;
    private boolean isExplosive = false;

    public static final long DAMAGE_FLASH_DURATION_MS = 150L;
    private long lastDamageTimestamp = -1L;
    private boolean burnt = false;

    private boolean isCombining = false;
    private float combineTargetX;
    private float combineTargetY;
    private transient GameWorld currentWorld;

    private final String id = java.util.UUID.randomUUID().toString();

    public String getId() { return id; }

    public enum State {
        IDLE, SPECIAL, ATTACK, UNARMED, TRIGGERED,
        HIT_RIGHT, HIT_LEFT, HIT_RIGHT_AND_LEFT,
        SPECIAL_IDLE,
        DAMAGE, DAMAGE2, DAMAGE3,
        INTRO,
        BUSY,
        IMITATE_IDLE, IMITATE_ATTACK,
        PLANT_FOOD_INTRO, PLANT_FOOD, PLANT_FOOD2, PLANT_FOOD_OUTRO,
        JUMP_UP_LEFT, JUMP_UP_RIGHT, JUMP_DOWN_LEFT, JUMP_DOWN_RIGHT,
        PLANT_FOOD_IDLE
    }

    public Plant(PlantType type, int health, int damage) {
        this.type = type;
        this.health = health;
        this.damage = damage;
        initHealth = health;
    }

    public void startCombineAnimation(float targetX, float targetY) {
        slideTo(targetX, targetY);
    }

    public void slideTo(float targetX, float targetY) {
        this.combineTargetX = targetX;
        this.combineTargetY = targetY;
        this.isCombining = true;
    }

    public static boolean isMushroom(PlantType type) {
        return switch (type) {
            case SUN_SHROOM, PUFF_SHROOM, FUME_SHROOM, SEA_SHROOM,
                 ICE_SHROOM, DOOM_SHROOM, MAGNET_SHROOM -> true;
            default -> false;
        };
    }

    public void addComponent(GameComponent comp) {
        components.add(comp);
        if (comp instanceof ExplosivesComponent){
            isExplosive = true;
        }
    }

    public void update(float delta, GameWorld world){
        if (currentWorld == null) currentWorld = world;
        update(delta);
    }

    public void update(float delta) {
        if (disabled || freeze || sheep) return;
        if (isCombining) {
            float speed = 600f;
            boolean reachedX = false;
            boolean reachedY = false;

            if (Math.abs(x - combineTargetX) > 20f) {
                x += Math.signum(combineTargetX - x) * speed * delta;
            } else {
                x = combineTargetX;
                reachedX = true;
            }

            if (Math.abs(y - combineTargetY) > 20f) {
                y += Math.signum(combineTargetY - y) * speed * delta;
            } else {
                y = combineTargetY;
                reachedY = true;
            }

            if (reachedX && reachedY) {
                isCombining = false;
            }
        }

        if (disabled || freeze || sheep) return;
        if (!isImitate) {
            if (plantFoodInStart) {
                activatePlantFood();
                plantFoodInStart = false;
            }
        }
        for (GameComponent comp : components) {
            if (isImitate && !(comp instanceof ImitatorIntroComponent)) continue;
            comp.update(this, delta);
        }
        if (isFire) {
            checkFire(delta);
        }
    }

    private void checkFire(float delta) {
        List<Cell> neighborCells = Cell.getNeighborCells(cell, App.getCurrentGame(this).getGrid(), warmRadius);
        for (Cell cell1 : neighborCells) {
            if (cell1.getPlant() == null) continue;
            if (cell1.getPlant().freeze) {
                cell1.getPlant().iceHealth -= 60 * delta;
                if (cell1.getPlant().iceHealth <= 0) {
                    cell1.getPlant().unfreeze();
                }
            }
            for (Zombie zombie : Cell.getZombiesInCell(cell1)) {
                if (zombie.getIceHealth() > 0) {
                    zombie.setIceHealth(zombie.getIceHealth() - 60 * delta);
                }
            }
        }
    }

    public void takeDamage(int damage) {
        takeDamage(damage, (Zombie) null);
    }

    @Override
    public void takeDamage(int damage, String damageType) {
    }

    @Override
    public void takeDamage(int damage, Zombie zombie) {
        triggerDamageFlash();
        if (iceHealth > 0) {
            iceHealth -= damage;
            if (iceHealth <= 0) {
                unfreeze();
            }
            return;
        }

        int remainingDamage = damage;
        for (GameComponent comp : components) {
            remainingDamage = comp.onTakeDamage(this, remainingDamage, zombie);
            if (remainingDamage <= 0) {
                break;
            }
        }

        this.health -= remainingDamage;

        if (health <= 0) {
            User user = App.getCurrentUser();
            if (user != null) {
                user.getQuestStats().incrementPlantsLost();
            }
            if (App.getCurrentGame() != null) App.getCurrentGame().notifyPlantEaten();
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

    public void die() {
        if (this.dead) return;

        this.dead = true;
        for (GameComponent component : components) {
            component.onDeath(this, App.getCurrentGame(this).getElapsedTime());
        }

        if (this.cell != null) {
            this.cell.findAndRemovePlant();
            this.cell = null;
        }
    }

    public boolean isDead() {
        return dead;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public PlantType getType() {
        return type;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public <T extends GameComponent> T getComponent(Class<T> componentClass) {
        if (components == null) return null;
        for (GameComponent component : components) {
            if (componentClass.isInstance(component)) {
                return componentClass.cast(component);
            }
        }
        return null;
    }

    public void increaseFrozenAmount() {
        if (frozenAmount == 99 || isFire || freeze) return;
        frozenAmount += 33;
        if (frozenAmount >= 99) {
            frozenAmount = 0;
            freeze = true;
            iceHealth = MAX_ICE_HEALTH;
        }
    }

    public boolean isFreeze() {
        return freeze;
    }

    public void unfreeze() {
        freeze = false;
        iceHealth = 0;
    }

    public void activatePlantFood() {
        for (GameComponent component : components) {
            component.activatePlantFood(this);
        }
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setFire(boolean fire) {
        isFire = fire;
    }

    public int getInitHealth() {
        return initHealth;
    }

    public void setPlantFoodInStart(boolean plantFoodInStart) {
        this.plantFoodInStart = plantFoodInStart;
    }

    public float getIceHealth() {
        return iceHealth;
    }

    public void setWarmRadius(int warmRadius) {
        this.warmRadius = warmRadius;
    }

    public boolean isSheep() {
        return sheep;
    }

    public void setSheep(boolean sheep) {
        this.sheep = sheep;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setImitate(boolean imitate) {
        isImitate = imitate;
    }

    public boolean isImitate() {
        return isImitate;
    }

    public int getFrozenAmount() {
        return frozenAmount;
    }

    public int getRow() {return cell != null ? cell.getRow() : -1;}

    public boolean isBurnt() {
        return burnt;
    }

    public void setBurnt(boolean burnt) {
        this.burnt = burnt;
        if (burnt) {
            die();
        }
    }

    public boolean isExplosive() {
        return isExplosive;
    }

    public GameWorld getCurrentWorld() {
        return currentWorld;
    }

    public boolean isCombining() {
        return isCombining;
    }
}
