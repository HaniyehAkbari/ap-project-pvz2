package com.pvz2.models.network.messages;

import com.pvz2.models.core.User;

public class SaveUserRequest {
    public String token;
    public User user;

    public SaveUserRequest(String token, User user) {
        this.token = token;
        this.user = user;
    }
}
