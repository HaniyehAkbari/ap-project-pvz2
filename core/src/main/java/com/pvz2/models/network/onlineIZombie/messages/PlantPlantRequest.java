package com.pvz2.models.network.onlineIZombie.messages;

import com.pvz2.models.enums.PlantType;

public class PlantPlantRequest {
    public String matchId;
    public PlantType type;
    public float x, y;

    public PlantPlantRequest(String matchId, PlantType type, float x, float y) {
        this.matchId = matchId;
        this.type = type;
        this.x = x;
        this.y = y;
    }
}
