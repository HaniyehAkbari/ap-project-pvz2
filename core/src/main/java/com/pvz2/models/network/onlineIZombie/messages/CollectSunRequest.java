package com.pvz2.models.network.onlineIZombie.messages;

public class CollectSunRequest {
    public String matchId;
    public float x, y;
    public CollectSunRequest(String matchId, float x, float y) {
        this.matchId = matchId;
        this.x = x;
        this.y = y;
    }
}
