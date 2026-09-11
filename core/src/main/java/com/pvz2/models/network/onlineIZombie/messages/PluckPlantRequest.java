package com.pvz2.models.network.onlineIZombie.messages;

public class PluckPlantRequest {
    public String matchId;
    public float x, y;

    public PluckPlantRequest(String matchId, float x, float y) {
        this.matchId = matchId;
        this.x = x;
        this.y = y;
    }
}
