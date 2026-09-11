package com.pvz2.models.shop;

import com.pvz2.models.enums.PlantType;

import java.time.LocalDate;

public class DailyOffer extends ShopItem {
    private PlantType plantType;
    private LocalDate offerDate;
    private boolean isPurchased;

    public DailyOffer(PlantType type, int coinCost) {
        super("6", type.name(), coinCost, 0, 1, false);
        this.plantType = type;
        this.offerDate = LocalDate.now();
        this.isPurchased = false;
    }

    public boolean isAvailableToday() {
        return !isPurchased && offerDate.equals(LocalDate.now());
    }

    public PlantType getPlantType() {
        return plantType;
    }
}
