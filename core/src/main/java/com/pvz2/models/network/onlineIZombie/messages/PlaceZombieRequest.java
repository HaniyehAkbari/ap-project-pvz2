package com.pvz2.models.network.onlineIZombie.messages;

public class PlaceZombieRequest {
    public String matchId;
    public String zombieType;
    public float x, y;

    public PlaceZombieRequest(String matchId, String zombieType, float x, float y) {
        this.matchId = matchId;
        this.zombieType = zombieType;
        this.x = x;
        this.y = y;
    }
}
