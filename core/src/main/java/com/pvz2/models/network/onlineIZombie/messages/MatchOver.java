package com.pvz2.models.network.onlineIZombie.messages;

public class MatchOver {
    public String matchId;
    public String winnerSide;
    public String message;

    public MatchOver(String matchId, String winnerSide, String message) {
        this.matchId = matchId;
        this.winnerSide = winnerSide;
        this.message = message;
    }
}
