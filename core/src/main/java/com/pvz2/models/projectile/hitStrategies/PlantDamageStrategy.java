package com.pvz2.models.projectile.hitStrategies;

import com.pvz2.models.Damageable;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.projectile.Projectile;
import com.pvz2.models.zombie.Zombie;

import java.util.List;

public class PlantDamageStrategy implements HitStrategy {
    private int damage;
    private String element = "NORMAL"; // مقادیر ممکن: "NORMAL", "ICE", "CHILL"


    public PlantDamageStrategy(int damage, String element) {
        this.damage = damage;
        this.element = element != null ? element : "NORMAL";
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public void increaseDamage(int factor) {
        damage *= factor;
    }


    @Override
    public void applyDamage(Plant target, Projectile projectile) {
        target.takeDamage(damage, (Zombie) null);

        if ("ICE".equalsIgnoreCase(element)) {
            target.increaseFrozenAmount();
        }
    }

    @Override
    public void applyDamage(Damageable target, List<Damageable> allTargets, Projectile projectile) {

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
