package com.pvz2.models.network.onlineIZombie.messages;

public class SendReactionRequest {
    public String matchId;
    public ReactionCategory category;
    public int index;

    public SendReactionRequest(String matchId, ReactionCategory category, int index) {
        this.matchId = matchId;
        this.category = category;
        this.index = index;
    }
}
