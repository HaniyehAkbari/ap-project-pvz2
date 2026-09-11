package com.pvz2.models.network.onlineIZombie.messages;

public class ChallengeInvite {
    public String inviteId;
    public String fromUsername;

    public ChallengeInvite(String inviteId, String fromUsername) {
        this.inviteId = inviteId;
        this.fromUsername = fromUsername;
    }
}
