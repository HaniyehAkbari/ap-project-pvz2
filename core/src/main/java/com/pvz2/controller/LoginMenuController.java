package com.pvz2.controller;

import com.pvz2.models.core.UserManager;
import com.pvz2.view.screen.LoginMenuScreen;
import com.pvz2.view.screen.MainMenuScreen;
import com.pvz2.view.screen.SignupMenuScreen;

public class LoginMenuController implements MenuController {
    private LoginMenuScreen screen;
    private String recoveringUsername = null; // just remembered locally to pass along to the next two steps

    public LoginMenuController(LoginMenuScreen screen) {
        this.screen = screen;
    }

    @Override
    public void changeMenu() {
        screen.fadeAndSwitchScreen(new MainMenuScreen(screen.getGame()));
    }

    @Override
    public void exitMenu() {
        screen.fadeAndSwitchScreen(new SignupMenuScreen(screen.getGame()));
    }

    public String loginUser(String username, String password, boolean stayLoggedIn) {
        return UserManager.login(username, password, stayLoggedIn);
    }

    public String forgetPassword(String username, String email) {
        String result = UserManager.forgetPassword(username, email);
        if (result.startsWith("Please answer")) {
            this.recoveringUsername = username;
        }
        return result;
    }

    public String answerSQ(String answer) {
        if (recoveringUsername == null) {
            return "Please enter your username and email first.";
        }
        return UserManager.answerSecurityQuestion(recoveringUsername, answer)
            ? "Enter your new password:"
            : "Your answer is incorrect.";
    }

    public String newPassword(String password) {
        if (recoveringUsername == null) {
            return "Please enter your username and email first.";
        }
        boolean success = UserManager.submitNewPassword(recoveringUsername, password);
        if (success) recoveringUsername = null;
        return success ? "Your password changed successfully." : "Failed to save the new password. Please try again.";
    }
}
