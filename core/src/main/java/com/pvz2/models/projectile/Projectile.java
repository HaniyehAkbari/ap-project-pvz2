package com.pvz2.models.projectile;

import com.pvz2.models.Damageable;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.enums.ProjectileType;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.SunProducerComponent;
import com.pvz2.models.pool.Resettable;
import com.pvz2.models.projectile.hitStrategies.HitStrategy;
import com.pvz2.models.projectile.hitStrategies.PlantDamageStrategy;
import com.pvz2.models.projectile.movementStrategies.MovementStrategy;
import com.pvz2.models.projectile.strikeStrategies.CheckStrike;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.obstacles.Grave;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.zombie.zombiesType.DeflectorZombie;
import com.pvz2.models.zombie.zombiesType.SnorkelZombie;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Projectile implements Resettable {
    private float x, y;
    private float originX, originY;
    private float targetX, targetY;
    private HitStrategy hitStrategy;
    private MovementStrategy movementStrategy;
    private CheckStrike strikeStrategy;
    private ProjectileType type;
    private Damageable target;
    private int pierce;
    private boolean dead = false;
    private PlantType plantType;
    private List<Damageable> lastTargets = new ArrayList<>();
    private String id = java.util.UUID.randomUUID().toString();
    private transient GameWorld currentWorld;

    void checkProjectilesTowardPlants(double oldX, double oldY) {
        Damageable plantTarget = null;
        if (type != null && type.movement != null) {
            plantTarget = strikeStrategy.strike(x, y, oldX, oldY, null);
        }

        if (plantTarget instanceof Plant plant) {
            if (hitStrategy != null) {
                hitStrategy.applyDamage(plant, this);
            }

            pierce--;
            if (pierce == 0) {
                dead = true;
            }
        }
    }

    public void update(float delta, GameWorld world){
        if (currentWorld == null) currentWorld = world;
        update(delta);
    }

    public void update(float delta) {
        double oldX = x;
        double oldY = y;
        movementStrategy.move(this, delta);
        GameWorld game = App.getCurrentGame(this);
        if (hitStrategy instanceof PlantDamageStrategy) {
            checkProjectilesTowardPlants(oldX, oldY);
            return;}
        Damageable zombie = null;
        if (type != null && type.movement != null) {
            if (type.movement.equals("STRAIGHT") && !(hitStrategy instanceof PlantDamageStrategy)) {
                if (game != null) {
                    Cell currentCell = game.getCellAt(this.x, this.y);
                    if (currentCell != null && currentCell.getPlant() != null) {
                        Plant p = currentCell.getPlant();
                        if (p.isFreeze() && (p.getX() > this.originX + 20) || (p.getX() < this.originX - 20) ) {
                            p.takeDamage(hitStrategy.getDamage(), (Zombie) null);
                            pierce--;
                            if (pierce <= 0) {
                                dead = true;
                                return;}}}}
            }
            if (type.movement.equals("STRAIGHT")) {
                strikeStrategy.setWorld(currentWorld);
                zombie = strikeStrategy.strike(x, y, oldX, oldY, lastTargets);
            } else if (type.movement.equals("LOBBED")) {
                zombie = strikeStrategy.strike(x, y, target);}}
        if (zombie instanceof SnorkelZombie snorkel) {
            if (snorkel.isUnderwater() && type != null && "STRAIGHT".equals(type.movement)) {
                zombie = null;}}
        if (zombie != null) {
            if (zombie instanceof DeflectorZombie deflector) {
                if (deflector.tryDeflect(this)) {
                    dead = true;
                    return;}
            }
            if (hitStrategy != null) {
                hitStrategy.applyDamage(zombie, Stream.concat(
                        App.getCurrentGame(this).getActiveZombies().stream(),
                        App.getCurrentGame(this).getActiveObstacles().stream().filter(Grave.class::isInstance)
                ).toList(), this);
            }
            pierce--;
            if (pierce == 0) {
                dead = true;
                return;}
        }
        if (movementStrategy.isDead(this)){
            dead = true;}
    }
    @Override
    public void reset(float x, float y, int size, SunProducerComponent component) {}
    @Override
    public void reset(float x, float y) {}
    @Override
    public void reset(float x, float y,
                      HitStrategy hitStrategy, MovementStrategy movementStrategy,
                      CheckStrike checkStrike, ProjectileType type) {
        this.id = java.util.UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        originX = x;
        originY = y;
        this.hitStrategy = hitStrategy;
        lastTargets.clear();
        this.movementStrategy = movementStrategy;
        this.strikeStrategy = checkStrike;
        this.type = type;
        pierce = 1;
        targetX = 0;
        targetY = 0;
        dead = false;
        this.plantType = null;
    }

    public void setPierce(int pierce) {
        this.pierce = pierce;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public ProjectileType getType() {
        return type;
    }

    public void setType(ProjectileType type) {
        this.type = type;
    }

    public double distanceTo(Damageable target) {
        return Math.sqrt((target.getX() - x) * (target.getX() - x) + (target.getY() - y) * (target.getY() - y));
    }

    public float getOriginX() {
        return originX;
    }

    public float getOriginY() {
        return originY;
    }

    public void setTarget(Damageable target) {
        this.target = target;
        if (target != null) {
            targetX = target.getX();
            targetY = target.getY();
        }
    }

    public List<Damageable> getLastTarget() {
        return lastTargets;
    }

    public void addTarget(Damageable lastTarget) {
        this.lastTargets.add(lastTarget);
    }

    public boolean isDead() {
        return dead;
    }

    public HitStrategy getHitStrategy() {
        return hitStrategy;
    }

    public float getTargetX() {
        return targetX;
    }

    public float getTargetY() {
        return targetY;
    }

    public PlantType getPlantType() {
        return plantType;
    }

    public MovementStrategy getMovementStrategy() {
        return movementStrategy;
    }
    public GameWorld getCurrentWorld() {
        return currentWorld;
    }
    public int getPierce() {
        return pierce;
    }
    public String getId() { return id; }
    public void setPlantType(PlantType plantType) {
        this.plantType = plantType;
    }
}
