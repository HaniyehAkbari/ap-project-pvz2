package com.pvz2.models.network.messages;

import com.pvz2.models.core.User;

public class LoginResponse {
    public boolean success;
    public User user;
    public String token;
    public String errorMessage;
    public LoginResponse() {}
}
