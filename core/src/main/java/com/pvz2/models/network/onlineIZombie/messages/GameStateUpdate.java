package com.pvz2.models.network.onlineIZombie.messages;

import com.pvz2.models.miniGame.IZombie.OnlineIZombieLevel;

public class GameStateUpdate {
    public String matchId;
    public OnlineIZombieLevel world;

    public GameStateUpdate(String matchId, OnlineIZombieLevel world) {
        this.matchId = matchId;
        this.world = world;
    }
}
