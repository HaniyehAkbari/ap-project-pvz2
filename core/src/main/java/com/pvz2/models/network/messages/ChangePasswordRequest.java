package com.pvz2.models.network.messages;
public class ChangePasswordRequest {
    public String token, oldPassword, newPassword;
    public ChangePasswordRequest(String token, String oldPassword, String newPassword) {
        this.token = token; this.oldPassword = oldPassword; this.newPassword = newPassword;
    }
}
