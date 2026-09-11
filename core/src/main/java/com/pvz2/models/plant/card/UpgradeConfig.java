package com.pvz2.models.plant.card;

public class UpgradeConfig {
    private final int sunCostModifier;
    private final int cooldownReductionTicks;

    public UpgradeConfig(int sunCostModifier, int cooldownReductionTicks) {
        this.sunCostModifier = sunCostModifier;
        this.cooldownReductionTicks = cooldownReductionTicks;
    }

    public int getSunCostModifier() {
        return sunCostModifier;
    }

    public int getCooldownReductionTicks() {
        return cooldownReductionTicks;
    }
}
