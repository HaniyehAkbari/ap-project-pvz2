package com.pvz2.models.miniGame.beghouled;

import com.pvz2.models.enums.PlantType;

public class PlantUpgrade {
    private final PlantType from;
    private final PlantType to;
    private final int cost;

    public PlantUpgrade(PlantType from, PlantType to, int cost) {
        this.from = from;
        this.to = to;
        this.cost = cost;
    }

    public PlantType getFrom() {
        return from;
    }

    public PlantType getTo() {
        return to;
    }

    public int getCost() {
        return cost;
    }
}
