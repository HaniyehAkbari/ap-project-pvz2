package com.pvz2.models.projectile.hitStrategies;

import com.pvz2.models.Damageable;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.projectile.Projectile;

import java.util.List;

public interface HitStrategy {
    void applyDamage(Damageable target, List<Damageable> allTargets, Projectile projectile);

    String getElement();

    void setElement(String element);

    int getDamage();

    void increaseDamage(int factor);

    void applyDamage(Plant plant, Projectile projectile);
}
