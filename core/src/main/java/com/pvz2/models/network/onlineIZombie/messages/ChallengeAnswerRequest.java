package com.pvz2.models.network.onlineIZombie.messages;

public class ChallengeAnswerRequest {
    public String inviteId;
    public boolean accept;

    public ChallengeAnswerRequest(String inviteId, boolean accept) {
        this.inviteId = inviteId;
        this.accept = accept;
    }
}
