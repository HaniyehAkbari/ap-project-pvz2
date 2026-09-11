package com.pvz2.models.enums;

import com.pvz2.models.network.messages.LeaderboardEntry;

import java.util.Comparator;

public enum LeaderboardSortField {
    LAST_STAGE(Comparator.comparingInt(LeaderboardEntry::getCompletedMainLevels)),
    MINI_GAMES(Comparator.comparingInt(field -> field.miniGamesCompleted)),
    DAILY_QUESTS(Comparator.comparingInt(field -> field.dailyQuestsCount)),
    NORMAL_QUESTS(Comparator.comparingInt(field -> field.normalQuestsCount)),
    HIGH_SCORE(Comparator.comparingInt(field -> field.maxMupoint));

    private final Comparator<LeaderboardEntry> comparator;

    LeaderboardSortField(Comparator<LeaderboardEntry> comparator) {
        this.comparator = comparator;
    }

    public Comparator<LeaderboardEntry> getComparator() {
        return comparator;
    }
}
