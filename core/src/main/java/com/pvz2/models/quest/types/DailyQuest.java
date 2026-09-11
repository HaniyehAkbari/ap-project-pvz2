package com.pvz2.models.quest.types;

import com.pvz2.models.quest.Quest;
import com.pvz2.models.quest.QuestPriority;
import com.pvz2.models.quest.QuestStats;
import com.pvz2.models.quest.reward.Reward;

import java.time.LocalDate;
import java.util.function.Predicate;

public class DailyQuest extends Quest {
    private LocalDate questDate;

    public DailyQuest(String id, String description, QuestPriority priority,
                      Predicate<QuestStats> condition, Reward reward) {
        this(id, description, priority, condition, reward, false);
    }

    public DailyQuest(String id, String description, QuestPriority priority,
                      Predicate<QuestStats> condition, Reward reward, boolean endGameDependent) {
        super(id, description, priority, condition, reward, endGameDependent);
        this.questDate = LocalDate.now();
    }

    public boolean isAvailableToday() {
        return LocalDate.now().equals(questDate) && !isCompleted();
    }
}
