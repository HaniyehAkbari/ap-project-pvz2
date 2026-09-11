package com.pvz2.models.network.messages;
public class AckResponse {
    public boolean success;
    public String message;
    public AckResponse() {}
    public AckResponse(boolean success) { this.success = success; }
}
