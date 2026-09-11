package com.pvz2.models.plant.components;

import com.pvz2.models.plant.GameComponent;
import com.pvz2.models.plant.Plant;

public abstract class WallNutsComponent implements GameComponent {

    private final static float PLANTFOOD_TIME = 2f;
    private float plantfoodTimer = 0f;

    @Override
    public void update(Plant owner, float delta) {
        if (plantfoodTimer > 0){
            plantfoodTimer -= delta;
            if (plantfoodTimer <= 0){
                plantfoodTimer = 0;
                owner.setState(Plant.State.IDLE);
            }
        }
    }

    @Override
    public void activatePlantFood(Plant owner){
        owner.setState(Plant.State.PLANT_FOOD);
        plantfoodTimer = PLANTFOOD_TIME;
    }

}
