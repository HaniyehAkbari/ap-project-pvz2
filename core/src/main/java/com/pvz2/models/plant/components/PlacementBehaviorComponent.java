package com.pvz2.models.plant.components;

import com.pvz2.models.enums.PlantLayer;
import com.pvz2.models.plant.GameComponent;
import com.pvz2.models.plant.Plant;

public class PlacementBehaviorComponent implements GameComponent {
    private final PlantLayer targetLayer;
    private final boolean isStackable;
    private final int maxStack;
    private final boolean waterOnly;

    private int currentStack = 1;


    public PlacementBehaviorComponent(PlantLayer targetLayer, boolean isStackable, int maxStack, boolean waterOnly) {
        this.targetLayer = targetLayer;
        this.isStackable = isStackable;
        this.maxStack = maxStack;
        this.waterOnly = waterOnly;
    }


    public boolean tryIncrementStack() {
        if (isStackable && currentStack < maxStack) {
            currentStack++;

            return true;
        }
        return false;
    }

    @Override
    public void update(Plant owner, float delta) {

    }

    @Override
    public void activatePlantFood(Plant owner) {

    }

    public PlantLayer getTargetLayer() {
        return targetLayer;
    }

    public boolean isStackable() {
        return isStackable;
    }

    public boolean isWaterOnly() {
        return waterOnly;
    }

    public int getCurrentStack() {
        return currentStack;
    }
}
