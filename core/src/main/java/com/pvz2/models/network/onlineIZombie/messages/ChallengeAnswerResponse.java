package com.pvz2.models.network.onlineIZombie.messages;

public class ChallengeAnswerResponse {
    public boolean success;
    public String errorMessage;
    public ChallengeAnswerResponse(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }
}
