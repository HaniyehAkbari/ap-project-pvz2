package com.pvz2.models.plant.components;

import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.GameComponent;
import com.pvz2.models.plant.Plant;

public class LifespanComponent implements GameComponent {
    private final PlantType type;
    private final float maxLifeTime;
    private float timer = 0f;

    public LifespanComponent(PlantType type, float maxLifeTime) {
        this.type = type;
        this.maxLifeTime = maxLifeTime;
    }

    @Override
    public void activatePlantFood(Plant owner) {
        App.getCurrentGame().triggerSmallShroomsPlantFood(type);
    }

    public void onGlobalPlantFoodActivated(PlantType type) {
        if (this.type.equals(type)) {
            timer = 0f;
        }
    }

    @Override
    public void update(Plant owner, float delta) {
        timer+=delta;
        if (timer >= maxLifeTime) {
            owner.die();
        }
    }

    @Override
    public void onDeath(Plant owner, float delta) {
        if (App.getCurrentGame() != null){
            App.getCurrentGame().unregisterPuffShroom(this);
        }
    }
}
