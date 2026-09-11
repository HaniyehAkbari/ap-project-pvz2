package com.pvz2.models.plant.components.moveZombieStrategy;

import com.pvz2.models.plant.Plant;
import com.pvz2.models.zombie.Zombie;

public interface MoveZombieStrategy {
    void onUpdate(Plant owner);

    void onTakeDamage(Plant owner, int damage, Zombie attacker);

    void onPlantFood(Plant owner);
}
