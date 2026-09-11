package com.pvz2.controller;

import com.pvz2.models.core.*;

import java.util.List;

public class ProfileMenuController implements MenuController {
    private SignupMenuController signupMenuController = new SignupMenuController();

    @Override
    public void changeMenu() {
    }

    @Override
    public void exitMenu() {
    }

    public String getCurrentUsername() {
        User user = App.getCurrentUser();
        return user == null ? "" : user.getUsername();
    }

    public String getCurrentNickname() {
        User user = App.getCurrentUser();
        return user == null ? "" : user.getNickname();
    }

    public String getCurrentEmail() {
        User user = App.getCurrentUser();
        return user == null ? "" : user.getEmail();
    }

    public String changeUsername(String newUsername) {
        User user = App.getCurrentUser();

        if (user.getUsername().equals(newUsername)) {
            return "new username and your username are similar.";
        }

        List<String> errors = signupMenuController.getUsernameErrors(newUsername);
        if (!errors.isEmpty()) {
            return String.join("\n", errors);
        }

        return UserManager.changeUsername(newUsername);
    }

    public String changeNickname(String newNickname) {
        User user = App.getCurrentUser();

        if (user.getNickname().equals(newNickname)) {
            return "new nickname and your nickname are similar.";
        }

        List<String> errors = signupMenuController.getNicknameErrors(newNickname);
        if (!errors.isEmpty()) {
            return String.join("\n", errors);
        }

        user.setNickname(newNickname);
        user.save();
        UserManager.syncCurrentUser();
        return "your nickname changed";
    }

    public String changeEmail(String newEmail) {
        User user = App.getCurrentUser();

        if (user.getEmail().equals(newEmail)) {
            return "new email and your email are similar.";
        }

        List<String> errors = signupMenuController.getEmailErrors(newEmail);
        if (!errors.isEmpty()) {
            return String.join("\n", errors);
        }

        user.setEmail(newEmail);
        user.save();
        UserManager.syncCurrentUser();
        return "your email changed";
    }

    public String changePassword(String password, String newPassword) {
        List<String> errors = signupMenuController.getPasswordErrors(newPassword, newPassword);
        if (!errors.isEmpty()) {
            return String.join("\n", errors);
        }
        return UserManager.changePassword(password, newPassword);
    }

    public String showInfo() {
        User user = App.getCurrentUser();
        return "Username: " + user.getUsername() + "\n" +
            "Nickname: " + user.getNickname() + "\n" +
            "Games played: " + user.getGamesPlayed() + "\n" +
            "Coins: " + user.getCoins() + "\n" +
            "Gems: " + user.getGems() + "\n" +
            "Levels completed: " + user.getCompletedLevels() + "\n" +
            "Mu point: " + user.getMaxMupoint();
    }
}
