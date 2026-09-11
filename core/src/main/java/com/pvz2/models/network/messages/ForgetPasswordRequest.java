package com.pvz2.models.network.messages;
public class ForgetPasswordRequest {
    public String username, email;
    public ForgetPasswordRequest(String username, String email) { this.username = username; this.email = email; }
}
