package com.pvz2.models;

import com.pvz2.models.zombie.Zombie;

public interface Damageable {
    void takeDamage(int damage, String damageType);

    void takeDamage(int damage, Zombie zombie);

    float getX();

    float getY();
}
