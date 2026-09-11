package com.pvz2.models.network.onlineIZombie.messages;

public class ReactionReceived {
    public String matchId;
    public String fromUsername;
    public ReactionCategory category;
    public int index;

    public ReactionReceived(String matchId, String fromUsername, ReactionCategory category, int index) {
        this.matchId = matchId;
        this.fromUsername = fromUsername;
        this.category = category;
        this.index = index;
    }
}
