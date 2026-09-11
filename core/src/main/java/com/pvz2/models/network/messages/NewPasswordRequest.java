package com.pvz2.models.network.messages;
public class NewPasswordRequest {
    public String username;
    public String newPassword;
    public NewPasswordRequest(String username, String newPassword)
    { this.username = username; this.newPassword = newPassword; }
}
