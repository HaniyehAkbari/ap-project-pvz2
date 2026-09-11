package com.pvz2.models.plant.components;

import com.pvz2.models.plant.GameComponent;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.moveZombieStrategy.MoveZombieStrategy;
import com.pvz2.models.zombie.Zombie;

public class MoveZombieComponent implements GameComponent {
    MoveZombieStrategy strategy;

    public MoveZombieComponent(MoveZombieStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void update(Plant owner, float delta) {
        strategy.onUpdate(owner);
    }

    @Override
    public void activatePlantFood(Plant owner) {
        strategy.onPlantFood(owner);
    }

    @Override
    public int onTakeDamage(Plant owner, int damageAmount, Zombie attacker) {
        strategy.onTakeDamage(owner, damageAmount, attacker);
        return 0;
    }
}
