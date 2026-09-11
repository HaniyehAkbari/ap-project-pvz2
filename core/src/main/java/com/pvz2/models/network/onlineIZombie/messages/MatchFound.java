package com.pvz2.models.network.onlineIZombie.messages;

public class MatchFound {
    public String matchId;
    public String opponentUsername;
    public String side;
    public MatchFound(String matchId, String opponentUsername, String side) {
        this.matchId = matchId;
        this.opponentUsername = opponentUsername;
        this.side = side;
    }
}
