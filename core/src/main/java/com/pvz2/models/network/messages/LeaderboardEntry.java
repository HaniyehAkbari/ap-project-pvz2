package com.pvz2.models.network.messages;

public class LeaderboardEntry {
    public String username;
    public int unlockedChapter;
    public int unlockedLevel;
    public int miniGamesCompleted;
    public int dailyQuestsCount;
    public int normalQuestsCount;
    public int maxMupoint;
    public boolean hasPlayedMuPoint;
    public int getCompletedMainLevels() {
        return (unlockedChapter - 1) * 4 + unlockedLevel - 1;
    }

    public LeaderboardEntry() {}
}
