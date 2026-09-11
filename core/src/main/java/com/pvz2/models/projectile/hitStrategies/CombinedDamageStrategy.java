package com.pvz2.models.projectile.hitStrategies;

import com.pvz2.models.Damageable;
import com.pvz2.models.enums.ProjectileType;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.projectile.Projectile;
import com.pvz2.models.projectile.movementStrategies.BowlingMovementStrategy;
import com.pvz2.models.zombie.Zombie;

import java.util.List;

public class CombinedDamageStrategy implements HitStrategy {
    private int damage;
    private int neighborDamage = 0;
    private float radius = 0;
    private String element = "NORMAL";
    private float chillTime = 5f;
    private int poisonDamageOnTick = 20;
    private ProjectileType projectileType;

    private CombinedDamageStrategy(int damage, int neighborDamage, float radius,
                                   String element, float chillTime, int poisonDamageOnTick,
                                   ProjectileType projectileType) {
        this.damage = damage;
        this.neighborDamage = neighborDamage;
        this.radius = radius;
        this.element = element;
        this.chillTime = chillTime;
        this.poisonDamageOnTick = poisonDamageOnTick;
        this.projectileType = projectileType;
    }

    public CombinedDamageStrategy(int damage, ProjectileType projectileType) {
        this.damage = damage;
        this.projectileType = projectileType;
    }

    public CombinedDamageStrategy(int damage, int neighborDamage, float radius, ProjectileType projectileType) {
        this.damage = damage;
        this.neighborDamage = neighborDamage;
        this.radius = radius;
        this.projectileType = projectileType;
    }

    public int getPoisonDamageOnTick() {
        return poisonDamageOnTick;
    }

    public void setPoisonDamageOnTick(int poisonDamageOnTick) {
        this.poisonDamageOnTick = poisonDamageOnTick;
    }

    public float getChillTime() {
        return chillTime;
    }

    public void setChillTime(float chillTime) {
        this.chillTime = chillTime;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    public CombinedDamageStrategy changeDamage(int damage) {
        return new CombinedDamageStrategy(damage, neighborDamage, radius,
                this.element, this.chillTime, this.poisonDamageOnTick, projectileType);
    }

    @Override
    public void applyDamage(Damageable target, List<Damageable> allTargets, Projectile projectile) {
        String type = element != null ? element : "NORMAL";
        if (projectile.getLastTarget().contains(target)) return;
        if (projectile.getMovementStrategy() instanceof BowlingMovementStrategy bowlingMove) {
            bowlingMove.onHit(target, projectile);
        }
        target.takeDamage(damage, type);
        System.out.println(damage);
        projectile.addTarget(target);
        applySpecialDamage(target);

        if (target instanceof Zombie && projectile.getPlantType() != null) {
            ((Zombie) target).setKiller(projectile.getPlantType());
        }

        if (radius > 0) {
            for (Damageable extraTarget : allTargets) {
                if (extraTarget != target && projectile.distanceTo(extraTarget) <= radius) {
                    extraTarget.takeDamage(neighborDamage, type);
                    applySpecialDamage(extraTarget);
                    if (extraTarget instanceof Zombie && projectile.getPlantType() != null) {
                        ((Zombie) extraTarget).setKiller(projectile.getPlantType());
                    }
                }
            }
        }
    }

    private void applySpecialDamage(Damageable target) {
        if (element == null) return;
        switch (element) {
            case "POISON":
                if (target instanceof Zombie zombie) {
                    if (projectileType == ProjectileType.GOO) {
                        zombie.makePoisoned(poisonDamageOnTick);
                    } else {
                        zombie.makePoisoned(poisonDamageOnTick);
                    }
                }
                break;
            case "ICE":
                if (target instanceof Zombie zombie) {
                    zombie.applySlow(chillTime, 0.5, false);
                }
                break;
            case "STUN":
                if (target instanceof Zombie zombie){
                    zombie.disableFor(5.0f);
                }
                break;
            case "FIRE":
                if (target instanceof Zombie zombie) {
                    if (zombie.getIceHealth() > 0) {
                        zombie.setIceHealth(0);
                    }
                    zombie.unfreeze();
                }
                break;
            case "MOVE":
                if (target instanceof Zombie zombie) {
                    zombie.setX(zombie.getX() + 100);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void increaseDamage(int factor) {
        damage *= factor;
    }



    @Override
    public void applyDamage(Plant plant, Projectile projectile) {

    }

    @Override
    public String getElement() {
        return element;
    }

    @Override
    public void setElement(String element) {
        this.element = element;
    }


}
