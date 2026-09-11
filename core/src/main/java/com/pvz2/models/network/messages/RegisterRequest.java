package com.pvz2.models.network.messages;

public class RegisterRequest {
    public String username, password, nickname, email, gender, securityQ, securityA;
    public RegisterRequest(String username, String password, String nickname, String email,
                           String gender, String securityQ, String securityA) {
        this.username = username; this.password = password; this.nickname = nickname;
        this.email = email; this.gender = gender; this.securityQ = securityQ; this.securityA = securityA;
    }
}
