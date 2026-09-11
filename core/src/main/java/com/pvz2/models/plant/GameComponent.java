package com.pvz2.models.plant;

import com.pvz2.models.zombie.Zombie;

public interface GameComponent {
    void update(Plant owner, float delta);

    void activatePlantFood(Plant owner);

    default int onTakeDamage(Plant owner, int damageAmount, Zombie attacker) {
        return damageAmount;
    }

    default void onDeath(Plant owner, float delta) {
    }
}
