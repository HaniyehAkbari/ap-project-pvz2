package com.pvz2.models.core;

import com.pvz2.models.network.NetworkClient;
import com.pvz2.models.network.NetworkMessage;
import com.pvz2.models.network.messages.*;

import java.util.List;

public class UserManager {
    private static User currentUser;
    private static String currentSessionToken;

    public static boolean loadInitialUser() {
        String token = UserDataManager.getSessionToken();
        if (token == null) return false;

        try {
            AutoLoginRequest req = new AutoLoginRequest(token);
            NetworkMessage reply = NetworkClient.get().sendRequest("AUTO_LOGIN", req, 5000);
            LoginResponse resp = NetworkClient.get().parsePayload(reply, LoginResponse.class);

            if (resp.success) {
                applyLoggedInUser(resp.user, resp.token);
                return true;
            } else {
                UserDataManager.clearSessionToken();
                return false;
            }
        } catch (InterruptedException e) {
            return false;
        }
    }

    public static String login(String username, String password, boolean stayLoggedIn) {
        try {
            LoginRequest req = new LoginRequest(username, password);
            NetworkMessage reply = NetworkClient.get().sendRequest("LOGIN", req, 5000);
            LoginResponse resp = NetworkClient.get().parsePayload(reply, LoginResponse.class);

            if (resp.success) {
                applyLoggedInUser(resp.user, resp.token);
                if (stayLoggedIn) {
                    UserDataManager.saveSessionToken(resp.token);
                }
                return "Login successful! Welcome " + currentUser.getNickname();
            } else {
                return resp.errorMessage;
            }
        } catch (InterruptedException e) {
            return "Error: could not reach server.";
        }
    }

    private static void applyLoggedInUser(User user, String token) {
        user.afterLoad();
        currentUser = user;
        currentSessionToken = token;
        App.setCurrentUser(user);
        user.initQuests();
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        if (currentUser != null) {
            if (currentSessionToken != null) {
                try {
                    NetworkClient.get().sendRequest("LOGOUT", new LogoutRequest(currentSessionToken), 3000);
                } catch (InterruptedException ignored) {
                }
            }
            currentUser = null;
            currentSessionToken = null; // NEW
            App.setCurrentUser(null);
            UserDataManager.clearSessionToken();
        }
    }

    public static void syncCurrentUser() {
        new Thread(() -> {
            if (currentUser == null || currentSessionToken == null) return;
            try {
                SaveUserRequest req = new SaveUserRequest(currentSessionToken, currentUser);
                NetworkMessage reply = NetworkClient.get().sendRequest("SAVE_USER", req, 5000);
                NetworkClient.get().parsePayload(reply, AckResponse.class);
            } catch (InterruptedException e) {
            }
        }).start();
    }

    public static String changeUsername(String newUsername) {
        if (currentSessionToken == null) return "You must be logged in to do this.";

        try {
            UpdateUsernameRequest req = new UpdateUsernameRequest(currentSessionToken, newUsername);
            NetworkMessage reply = NetworkClient.get().sendRequest("UPDATE_USERNAME", req, 5000);
            RegisterResponse resp = NetworkClient.get().parsePayload(reply, RegisterResponse.class);

            if (resp.success && currentUser != null) {
                currentUser.setUsername(newUsername);
            }
            return resp.message;
        } catch (InterruptedException e) {
            return "Error: could not reach server.";
        }
    }

    public static String changePassword(String oldPassword, String newPassword) {
        if (currentSessionToken == null) return "You must be logged in to do this.";
        try {
            ChangePasswordRequest req = new ChangePasswordRequest(currentSessionToken, oldPassword, newPassword);
            NetworkMessage reply = NetworkClient.get().sendRequest("CHANGE_PASSWORD", req, 5000);
            AckResponse resp = NetworkClient.get().parsePayload(reply, AckResponse.class);
            if (resp.success && currentUser != null) {
                currentUser.setHashPassword(PasswordHasher.hashSHA256(newPassword));
            }
            return resp.message;
        } catch (InterruptedException e) {
            return "Error: could not reach server.";
        }
    }

    public static String register(String username, String password, String nickname, String email,
                                  String gender, String securityQ, String securityA) {
        try {
            RegisterRequest req = new RegisterRequest(username,
                password, nickname, email, gender, securityQ, securityA);
            NetworkMessage reply = NetworkClient.get().sendRequest("REGISTER", req, 5000);
            RegisterResponse resp = NetworkClient.get().parsePayload(reply, RegisterResponse.class);
            return resp.message;
        } catch (InterruptedException e) {
            return "Error: could not reach server.";
        }
    }


    public static List<LeaderboardEntry> getLeaderboard() {
        try {
            NetworkMessage reply = NetworkClient.get().sendRequest("GET_LEADERBOARD", new Object(), 5000);
            LeaderboardResponse resp = NetworkClient.get().parsePayload(reply, LeaderboardResponse.class);
            return resp.entries;
        } catch (InterruptedException e) {
            return List.of();
        }
    }

    public static String forgetPassword(String username, String email) {
        try {
            ForgetPasswordRequest req = new ForgetPasswordRequest(username, email);
            NetworkMessage reply = NetworkClient.get().sendRequest("FORGET_PASSWORD", req, 5000);
            ForgetPasswordResponse resp = NetworkClient.get().parsePayload(reply, ForgetPasswordResponse.class);
            return resp.success ? resp.message + "\n" + resp.securityQ : resp.message;
        } catch (InterruptedException e) {
            return "Error: could not reach server.";
        }
    }

    public static boolean answerSecurityQuestion(String username, String answer) {
        try {
            AnswerSecurityQuestionRequest req = new AnswerSecurityQuestionRequest(username, answer);
            NetworkMessage reply = NetworkClient.get().sendRequest("ANSWER_SECURITY_QUESTION", req, 5000);
            AckResponse resp = NetworkClient.get().parsePayload(reply, AckResponse.class);
            return resp.success;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public static boolean submitNewPassword(String username, String newPassword) {
        try {
            NewPasswordRequest req = new NewPasswordRequest(username, newPassword);
            NetworkMessage reply = NetworkClient.get().sendRequest("NEW_PASSWORD", req, 5000);
            AckResponse resp = NetworkClient.get().parsePayload(reply, AckResponse.class);
            return resp.success;
        } catch (InterruptedException e) {
            return false;
        }
    }
}
