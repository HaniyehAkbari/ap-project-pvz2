package com.pvz2.models.quest.types;

import com.pvz2.models.quest.Quest;
import com.pvz2.models.quest.QuestPriority;
import com.pvz2.models.quest.QuestStats;
import com.pvz2.models.quest.reward.Reward;

import java.util.function.Predicate;

public class EpicChallengeQuest extends Quest {
    public EpicChallengeQuest(String id, String description, Predicate<QuestStats> condition, Reward reward) {
        this(id, description, condition, reward, false);
    }

    public EpicChallengeQuest(String id, String description, Predicate<QuestStats> condition,
                              Reward reward, boolean endGameDependent) {
        super(id, description, QuestPriority.HIGH, condition, reward, endGameDependent);
    }
}
