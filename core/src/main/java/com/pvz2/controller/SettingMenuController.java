package com.pvz2.controller;

import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.models.core.UserManager;

public class SettingMenuController implements MenuController {
    @Override
    public void changeMenu() {
    }

    @Override
    public void exitMenu() {
    }

    public String changeDifficulty(int newLevel) {
        if (newLevel < 1 || newLevel > 5) {
            return "Difficulty level must be between 1 and 5";
        }

        User user = App.getCurrentUser();
        if (user == null) return "You must be logged in to change settings.";
        user.setGameDifficulty(newLevel);
        UserManager.syncCurrentUser();
        return "Difficulty level changed to " + newLevel;
    }

    public void changeGameSpeed(int newSpeed) {
        User user = App.getCurrentUser();
        if (user == null) return;
        user.setGameSpeed(newSpeed);
        UserManager.syncCurrentUser();
    }

    public void setShowGrid(boolean showGrid) {
        User user = App.getCurrentUser();
        if (user == null) return;
        user.setShowGrid(showGrid);
        UserManager.syncCurrentUser();
    }

    public void setDebugMode(boolean debugMode) {
        User user = App.getCurrentUser();
        if (user == null) return;
        user.setDebugMode(debugMode);
        UserManager.syncCurrentUser();
    }

    public int getCurrentDifficulty() {
        User user = App.getCurrentUser();
        return user != null ? user.getGameDifficulty() : 1;
    }

    public int getCurrentGameSpeed() {
        User user = App.getCurrentUser();
        return user != null ? user.getGameSpeed() : 1;
    }

    public boolean isShowGrid() {
        User user = App.getCurrentUser();
        return user != null && user.isShowGrid();
    }

    public boolean isDebugMode() {
        User user = App.getCurrentUser();
        return user != null && user.isDebugMode();
    }
}
