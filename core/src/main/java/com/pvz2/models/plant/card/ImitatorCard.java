package com.pvz2.models.plant.card;

import com.pvz2.models.enums.PlantType;

public class ImitatorCard extends PlantCard {
    private final PlantType targetType;

    public ImitatorCard(PlantType targetType, int sunCost, float maxCooldownTicks) {
        super(PlantType.IMITATER, sunCost, maxCooldownTicks);
        this.targetType = targetType;
    }

    public PlantType getTargetType() {
        return targetType;
    }
}
