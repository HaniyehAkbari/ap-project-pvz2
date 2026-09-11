package com.pvz2.models.network.onlineIZombie;

import com.pvz2.models.network.ClientHandler;

import java.util.UUID;

public class Match {
    public enum Side { PLANTS, ZOMBIES }

    private final String matchId;
    private final ClientHandler plantsPlayer;
    private final ClientHandler zombiesPlayer;

    public Match(ClientHandler playerA, ClientHandler playerB) {
        this.matchId = UUID.randomUUID().toString();
        if (Math.random() < 0.5) {
            this.plantsPlayer = playerA;
            this.zombiesPlayer = playerB;
        } else {
            this.plantsPlayer = playerB;
            this.zombiesPlayer = playerA;
        }
    }

    public String getMatchId() { return matchId; }
    public ClientHandler getPlantsPlayer() { return plantsPlayer; }
    public ClientHandler getZombiesPlayer() { return zombiesPlayer; }

    public ClientHandler getOpponentOf(ClientHandler player) {
        if (player == plantsPlayer) return zombiesPlayer;
        if (player == zombiesPlayer) return plantsPlayer;
        return null;
    }

    public Side getSideOf(ClientHandler player) {
        if (player == plantsPlayer) return Side.PLANTS;
        if (player == zombiesPlayer) return Side.ZOMBIES;
        return null;
    }

}
