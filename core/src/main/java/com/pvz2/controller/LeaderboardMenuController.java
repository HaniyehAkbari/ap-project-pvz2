package com.pvz2.controller;

import com.pvz2.models.core.UserManager;
import com.pvz2.models.network.messages.LeaderboardEntry;

import java.util.List;

public class LeaderboardMenuController {
    public List<LeaderboardEntry> loadLeaderboard() {
        return UserManager.getLeaderboard();
    }
}
