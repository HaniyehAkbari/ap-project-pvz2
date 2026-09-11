package com.pvz2.models.network;

import com.google.gson.Gson;
import com.pvz2.models.core.GameInitializer;
import com.pvz2.models.core.PasswordHasher;
import com.pvz2.models.core.User;
import com.pvz2.models.core.UserDataManager;
import com.pvz2.models.network.messages.*;
import com.pvz2.models.network.onlineIZombie.messages.*;
import com.pvz2.models.zombie.ZombieRegistry;
import com.pvz2.models.network.onlineIZombie.Match;
import com.pvz2.models.network.onlineIZombie.MatchManager;
import com.pvz2.models.network.onlineIZombie.ServerGameController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {
    private static final int PORT = 8080;
    private static final Gson GSON = NetworkGson.INSTANCE;

    private final Map<String, ClientHandler> onlineUsers = new ConcurrentHashMap<>();

    private final Map<String, String> sessionTokens = new ConcurrentHashMap<>();

    private final MatchManager matchManager = new MatchManager(this::getOrCreateGameController);
    private final Map<String, ServerGameController> gameControllers = new ConcurrentHashMap<>();
    private final Map<String, Boolean> recoveryVerified = new ConcurrentHashMap<>();
    private final Object accountLock = new Object();

    public static void main(String[] args) {
        new GameServer().start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);
            ZombieRegistry.init();
            GameInitializer.loadPlantUpgrades();
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection: " + clientSocket.getRemoteSocketAddress());
                ClientHandler handler = new ClientHandler(clientSocket, this);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dispatch(ClientHandler sender, NetworkMessage msg) {
        switch (msg.type) {
            case "LOGIN" -> handleLogin(sender, msg);
            case "REGISTER" -> handleRegister(sender, msg);
            case "AUTO_LOGIN" -> handleAutoLogin(sender, msg);
            case "LOGOUT" -> handleLogout(sender, msg);
            case "UPDATE_USERNAME" -> handleUpdateUsername(sender, msg);
            case "CHALLENGE" -> handleChallenge(sender, msg);
            case "CHALLENGE_ANSWER" -> handleChallengeAnswer(sender, msg);
            case "RANDOM_MATCH" -> handleRandomMatch(sender, msg);
            case "CANCEL_RANDOM_MATCH" -> handleCancelRandomMatch(sender, msg);
            case "PLANT_PLANT" -> handlePlantPlant(sender, msg);
            case "PLUCK_PLANT" -> handlePluckPlant(sender, msg);
            case "COLLECT_SUN" -> handleCollectSun(sender, msg);
            case "COLLECT_BRAIN" -> handleCollectBrain(sender, msg);
            case "PLACE_ZOMBIE" -> handlePlaceZombie(sender, msg);
            case "SEND_REACTION" -> handleSendReaction(sender, msg);
            case "GET_LEADERBOARD" -> handleGetLeaderboard(sender, msg);
            case "SAVE_USER" -> handleSaveUser(sender, msg);
            case "FORGET_PASSWORD" -> handleForgetPassword(sender, msg);
            case "ANSWER_SECURITY_QUESTION" -> handleAnswerSecurityQuestion(sender, msg);
            case "NEW_PASSWORD" -> handleNewPassword(sender, msg);
            case "CHANGE_PASSWORD" -> handleChangePassword(sender, msg);
            default -> System.err.println("Unknown message type: " + msg.type);
        }
    }

    private void handleChangePassword(ClientHandler sender, NetworkMessage msg) {
        ChangePasswordRequest req = GSON.fromJson(msg.payload, ChangePasswordRequest.class);
        AckResponse response = new AckResponse();

        String username = sessionTokens.get(req.token);
        if (username == null) {
            response.success = false;
            response.message = "Session expired. Please log in again.";
            sender.send("CHANGE_PASSWORD", msg.requestId, response);
            return;
        }

        User user = UserDataManager.loadUser(username);
        if (user == null || !user.checkPassword(req.oldPassword)) {
            response.success = false;
            response.message = "Your password is incorrect.";
            sender.send("CHANGE_PASSWORD", msg.requestId, response);
            return;
        }
        if (user.checkPassword(req.newPassword)) {
            response.success = false;
            response.message = "New password must be different from your current password.";
            sender.send("CHANGE_PASSWORD", msg.requestId, response);
            return;
        }

        user.setHashPassword(PasswordHasher.hashSHA256(req.newPassword));
        response.success = UserDataManager.saveUser(user);
        response.message = response.success ? "Your password changed." : "Failed to save the new password.";
        sender.send("CHANGE_PASSWORD", msg.requestId, response);
    }

    private void handleGetLeaderboard(ClientHandler sender, NetworkMessage msg) {
        List<User> allUsers = UserDataManager.loadAllUsers();

        List<LeaderboardEntry> entries = allUsers.stream().map(u -> {
            LeaderboardEntry e = new LeaderboardEntry();
            e.username = u.getUsername();
            e.unlockedChapter = u.getUnlockedChapter();
            e.unlockedLevel = u.getUnlockedLevel();
            e.miniGamesCompleted = u.getMiniGameLevels().size();
            e.dailyQuestsCount = u.getDailyQuestsCount();
            e.normalQuestsCount = u.getNormalQuestsCount();
            e.maxMupoint = u.getMaxMupoint();
            e.hasPlayedMuPoint = u.isPlayedMuPoint();
            return e;
        }).toList();

        LeaderboardResponse response = new LeaderboardResponse();
        response.entries = entries;
        sender.send("GET_LEADERBOARD", msg.requestId, response);
    }


    private void handleSaveUser(ClientHandler sender, NetworkMessage msg) {
        SaveUserRequest req = GSON.fromJson(msg.payload, SaveUserRequest.class);
        AckResponse response = new AckResponse();

        String username = sessionTokens.get(req.token);
        if (username == null) {
            response.success = false;
            sender.send("SAVE_USER", msg.requestId, response);
            return;
        }
        req.user.setUsername(username);
        response.success = UserDataManager.saveUser(req.user);
        sender.send("SAVE_USER", msg.requestId, response);
    }

    private void handleLogin(ClientHandler sender, NetworkMessage msg) {
        LoginRequest req = GSON.fromJson(msg.payload, LoginRequest.class);
        User user = UserDataManager.loadUser(req.username);
        boolean success = user != null && user.checkPassword(req.password);

        LoginResponse response = new LoginResponse();
        if (success) {

            String token = UUID.randomUUID().toString();
            sessionTokens.put(token, req.username);
            sender.setUsername(req.username);
            registerOnline(req.username, sender);

            response.success = true;
            response.user = user;
            response.token = token;
        } else {
            response.success = false;
            response.errorMessage = "Invalid username or password.";
        }

        sender.send("LOGIN", msg.requestId, response);
    }

    private void handleRegister(ClientHandler sender, NetworkMessage msg) {
        RegisterRequest req = GSON.fromJson(msg.payload, RegisterRequest.class);

        synchronized (accountLock) {
            if (UserDataManager.userExists(req.username)) {
                sender.send("REGISTER", msg.requestId, response(false, "Username already exists."));
                return;
            }

            User newUser = new User();
            newUser.setUsername(req.username);
            newUser.setHashPassword(PasswordHasher.hashSHA256(req.password));
            newUser.setNickname(req.nickname);
            newUser.setEmail(req.email);
            newUser.setGender(req.gender);
            newUser.setSecurityQ(req.securityQ);
            newUser.setSecurityA(PasswordHasher.hashSHA256(req.securityA));

            boolean saved = UserDataManager.saveUser(newUser);
            sender.send("REGISTER", msg.requestId, response(saved,
                saved ? "Register successfully. Redirecting to Login Menu..." : "Error: could not save user data."));
        }
    }

    private RegisterResponse response(boolean success, String message) {
        RegisterResponse r = new RegisterResponse();
        r.success = success;
        r.message = message;
        return r;
    }

    private void handleUpdateUsername(ClientHandler sender, NetworkMessage msg) {
        UpdateUsernameRequest req = GSON.fromJson(msg.payload, UpdateUsernameRequest.class);
        RegisterResponse response = new RegisterResponse();

        synchronized (accountLock) {
            String oldUsername = sessionTokens.get(req.token);
            if (oldUsername == null) {
                response.success = false;
                response.message = "Session expired. Please log in again.";
                sender.send("UPDATE_USERNAME", msg.requestId, response);
                return;
            }

            if (UserDataManager.userExists(req.newUsername)) {
                response.success = false;
                response.message = "That username is already taken.";
                sender.send("UPDATE_USERNAME", msg.requestId, response);
                return;
            }

            User user = UserDataManager.loadUser(oldUsername);
            if (user == null) {
                response.success = false;
                response.message = "User not found.";
                sender.send("UPDATE_USERNAME", msg.requestId, response);
                return;
            }

            user.setUsername(req.newUsername);
            boolean saved = UserDataManager.updateUsername(oldUsername, user);

            if (saved) {
                sessionTokens.put(req.token, req.newUsername);
                onlineUsers.remove(oldUsername);
                onlineUsers.put(req.newUsername, sender);
                sender.setUsername(req.newUsername);
            }

            response.success = saved;
            response.message = saved ? "Username updated successfully." : "Could not save the new username.";
            sender.send("UPDATE_USERNAME", msg.requestId, response);
        }
    }

    private void handleAutoLogin(ClientHandler sender, NetworkMessage msg) {
        AutoLoginRequest req = GSON.fromJson(msg.payload, AutoLoginRequest.class);
        String username = sessionTokens.get(req.token);

        LoginResponse response = new LoginResponse();
        if (username == null) {
            response.success = false;
            response.errorMessage = "Session expired. Please log in again.";
        } else {
            User user = UserDataManager.loadUser(username);
            if (user == null) {
                sessionTokens.remove(req.token);
                response.success = false;
                response.errorMessage = "User no longer exists.";
            } else {
                sender.setUsername(username);
                registerOnline(username, sender);

                response.success = true;
                response.user = user;
                response.token = req.token;
            }
        }
        sender.send("AUTO_LOGIN", msg.requestId, response);
    }

    private void handleLogout(ClientHandler sender, NetworkMessage msg) {
        LogoutRequest req = GSON.fromJson(msg.payload, LogoutRequest.class);
        sessionTokens.remove(req.token);
        if (sender.getUsername() != null) {
            onlineUsers.remove(sender.getUsername());
            sender.setUsername(null);
        }
        sender.send("LOGOUT", msg.requestId, new AckResponse(true));
    }

    private void handleChallenge(ClientHandler sender, NetworkMessage msg) {
        if (sender.getUsername() == null) {
            ChallengeResponse response = new ChallengeResponse();
            response.success = false;
            response.errorMessage = "You must be logged in to challenge someone.";
            sender.send("CHALLENGE", msg.requestId, response);
            return;
        }
        ChallengeRequest req = GSON.fromJson(msg.payload, ChallengeRequest.class);
        ClientHandler target = getOnlineUser(req.opponentUsername);
        ChallengeResponse response = matchManager.challenge(sender, target);
        sender.send("CHALLENGE", msg.requestId, response);
    }

    private void handleChallengeAnswer(ClientHandler sender, NetworkMessage msg) {
        ChallengeAnswerRequest req = GSON.fromJson(msg.payload, ChallengeAnswerRequest.class);
        ChallengeAnswerResponse response = matchManager.answerChallenge(sender, req.inviteId, req.accept);
        sender.send("CHALLENGE_ANSWER", msg.requestId, response);
    }

    private void handleRandomMatch(ClientHandler sender, NetworkMessage msg) {
        if (sender.getUsername() == null) {
            RandomMatchResponse response = new RandomMatchResponse();
            response.success = false;
            response.errorMessage = "You must be logged in to find a match.";
            sender.send("RANDOM_MATCH", msg.requestId, response);
            return;
        }
        RandomMatchResponse response = matchManager.joinRandomQueue(sender);
        sender.send("RANDOM_MATCH", msg.requestId, response);
    }

    private void handleCancelRandomMatch(ClientHandler sender, NetworkMessage msg) {
        matchManager.cancelRandomQueue(sender);
        sender.send("CANCEL_RANDOM_MATCH", msg.requestId, new AckResponse(true));
    }

    private void handlePlantPlant(ClientHandler sender, NetworkMessage msg) {
        PlantPlantRequest req = GSON.fromJson(msg.payload, PlantPlantRequest.class);
        ServerGameController controller = gameControllerFor(sender, req.matchId);
        if (controller != null) controller.handlePlantPlant(sender, req);
    }

    private void handlePluckPlant(ClientHandler sender, NetworkMessage msg) {
        PluckPlantRequest req = GSON.fromJson(msg.payload, PluckPlantRequest.class);
        ServerGameController controller = gameControllerFor(sender, req.matchId);
        if (controller != null) controller.handlePluckPlant(sender, req);
    }

    private void handleCollectSun(ClientHandler sender, NetworkMessage msg) {
        CollectSunRequest req = GSON.fromJson(msg.payload, CollectSunRequest.class);
        ServerGameController controller = gameControllerFor(sender, req.matchId);
        if (controller != null) controller.handleCollectSun(sender, req);
    }

    private void handleCollectBrain(ClientHandler sender, NetworkMessage msg) {
        CollectBrainRequest req = GSON.fromJson(msg.payload, CollectBrainRequest.class);
        ServerGameController controller = gameControllerFor(sender, req.matchId);
        if (controller != null) controller.handleCollectBrain(sender, req);
    }

    private void handlePlaceZombie(ClientHandler sender, NetworkMessage msg) {
        PlaceZombieRequest req = GSON.fromJson(msg.payload, PlaceZombieRequest.class);
        ServerGameController controller = gameControllerFor(sender, req.matchId);
        if (controller != null) controller.handlePlaceZombie(sender, req);
    }

    private void handleSendReaction(ClientHandler sender, NetworkMessage msg) {
        SendReactionRequest req = GSON.fromJson(msg.payload, SendReactionRequest.class);
        if (req.index < 0 || req.index > 2) return;

        Match match = matchManager.getMatchOf(sender);
        if (match == null || !match.getMatchId().equals(req.matchId)) return;

        ClientHandler opponent = match.getOpponentOf(sender);
        if (opponent == null) return;

        opponent.send("REACTION", null,
            new ReactionReceived(req.matchId, sender.getUsername(), req.category, req.index));
    }

    private void handleForgetPassword(ClientHandler sender, NetworkMessage msg) {
        ForgetPasswordRequest req = GSON.fromJson(msg.payload, ForgetPasswordRequest.class);
        ForgetPasswordResponse response = new ForgetPasswordResponse();

        User user = UserDataManager.loadUser(req.username);
        if (user == null) {
            response.success = false;
            response.message = "This username doesn't exist.";
        } else if (!user.getEmail().equals(req.email)) {
            response.success = false;
            response.message = "Email is not correct.";
        } else {
            recoveryVerified.remove(req.username);
            response.success = true;
            response.message = "Please answer security question:";
            response.securityQ = user.getSecurityQ();
        }
        sender.send("FORGET_PASSWORD", msg.requestId, response);
    }

    private void handleAnswerSecurityQuestion(ClientHandler sender, NetworkMessage msg) {
        AnswerSecurityQuestionRequest req = GSON.fromJson(msg.payload, AnswerSecurityQuestionRequest.class);
        AckResponse response = new AckResponse();

        User user = UserDataManager.loadUser(req.username);
        if (user != null && user.checkSeqA(req.answer)) {
            recoveryVerified.put(req.username, true);
            response.success = true;
        } else {
            recoveryVerified.remove(req.username);
            response.success = false;
        }
        sender.send("ANSWER_SECURITY_QUESTION", msg.requestId, response);
    }

    private void handleNewPassword(ClientHandler sender, NetworkMessage msg) {
        NewPasswordRequest req = GSON.fromJson(msg.payload, NewPasswordRequest.class);
        AckResponse response = new AckResponse();

        if (!Boolean.TRUE.equals(recoveryVerified.get(req.username))) {
            response.success = false;
            sender.send("NEW_PASSWORD", msg.requestId, response);
            return;
        }

        User user = UserDataManager.loadUser(req.username);
        if (user == null) {
            response.success = false;
        } else {
            user.setHashPassword(PasswordHasher.hashSHA256(req.newPassword));
            response.success = UserDataManager.saveUser(user);
            recoveryVerified.remove(req.username);
        }
        sender.send("NEW_PASSWORD", msg.requestId, response);
    }

    private ServerGameController gameControllerFor(ClientHandler sender, String matchId) {
        Match match = matchManager.getMatchOf(sender);
        if (match == null || !match.getMatchId().equals(matchId)) return null;
        return getOrCreateGameController(match);
    }

    private ServerGameController getOrCreateGameController(Match match) {
        return gameControllers.computeIfAbsent(match.getMatchId(), id -> {
            ServerGameController controller = new ServerGameController(match, () -> {
                gameControllers.remove(id);
                matchManager.endMatch(id);
            });
            controller.start();
            return controller;
        });
    }

    public void registerOnline(String username, ClientHandler handler) {
        onlineUsers.put(username, handler);
    }

    public void onDisconnect(ClientHandler handler) {
        if (handler.getUsername() != null) {
            onlineUsers.remove(handler.getUsername());
        }
        Match match = matchManager.getMatchOf(handler);
        matchManager.handleDisconnect(handler);
        if (match != null) {
            ServerGameController controller = gameControllers.remove(match.getMatchId());
            if (controller != null) controller.stop();
        }
    }

    public ClientHandler getOnlineUser(String username) {
        return onlineUsers.get(username);
    }
}
