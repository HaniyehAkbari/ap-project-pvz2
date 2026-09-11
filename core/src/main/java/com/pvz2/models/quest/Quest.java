package com.pvz2.models.quest;

import com.pvz2.models.core.User;
import com.pvz2.models.quest.reward.Reward;

import java.util.function.Predicate;

public abstract class Quest implements Comparable<Quest> {
    private String id;
     private String name;
    private String description;
    private QuestPriority priority;
    private boolean isCompleted;
    private boolean readyToClaim;
    private Reward reward;
    private transient Predicate<QuestStats> condition;
    private boolean endGameDependent;

    private String groupId;
    private String variantLabel;

    public Quest(String id, String description, QuestPriority priority,
                 Predicate<QuestStats> condition, Reward reward, boolean endGameDependent) {
        this.id = id;
        this.description = description;
        this.priority = priority;
        this.condition = condition;
        this.reward = reward;
        this.isCompleted = false;
        this.readyToClaim = false;
        this.endGameDependent = endGameDependent;
    }

    public boolean isEndGameDependent() {
        return endGameDependent;
    }

    public void markReadyToClaim() {
        if (!this.isCompleted) {
            this.readyToClaim = true;
        }
    }

    public boolean isReadyToClaim() {
        return readyToClaim;
    }

    public void complete(User user) {
        if (!this.isCompleted && this.reward != null) {
            this.reward.apply(user);
            this.isCompleted = true;
        }
    }

    public boolean checkCompletion(QuestStats stats) {
        if (!isCompleted && !readyToClaim && condition != null && condition.test(stats)) {
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(Quest other) {
        return this.priority.compareTo(other.priority);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name != null ? name : description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
        if (completed) {
            this.readyToClaim = true;
        }
    }

    public QuestPriority getPriority() {
        return priority;
    }

    public Reward getReward() {
        return reward;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getVariantLabel() {
        return variantLabel;
    }

    public void setVariantLabel(String variantLabel) {
        this.variantLabel = variantLabel;
    }
}
