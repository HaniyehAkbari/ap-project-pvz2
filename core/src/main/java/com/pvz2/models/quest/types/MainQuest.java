package com.pvz2.models.quest.types;

import com.pvz2.models.quest.Quest;
import com.pvz2.models.quest.QuestPriority;
import com.pvz2.models.quest.QuestStats;
import com.pvz2.models.quest.reward.Reward;

import java.util.function.Predicate;

public class MainQuest extends Quest {
    public MainQuest(String id, String description, Predicate<QuestStats> condition, Reward reward) {
        this(id, description, condition, reward, false);
    }

    public MainQuest(String id, String description, Predicate<QuestStats> condition,
                     Reward reward, boolean endGameDependent) {
        super(id, description, QuestPriority.CRITICAL, condition, reward, endGameDependent);
    }
}
