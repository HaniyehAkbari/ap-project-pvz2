package com.pvz2.controller;

import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.models.enums.Chapter;
import com.pvz2.models.miniGame.MiniGameFactory;
import com.pvz2.models.miniGame.MiniGameLevels;
import com.pvz2.models.miniGame.MiniGames;
import com.pvz2.models.miniGame.vaseBreaker.VaseBreakerLevel;
import com.pvz2.models.quest.Quest;
import com.pvz2.models.quest.reward.Reward;
import com.pvz2.models.world.GameWorld;
import com.pvz2.view.screen.BeghouledScreen;
import com.pvz2.view.screen.GameScreen;
import com.pvz2.view.screen.VaseBreakerScreen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TravelLogMenuController implements MenuController {

    public static class QuestGroupView {
         public final String name;
        public final String description;
        public final Reward reward;
        public final List<VariantView> variants;

        public final float progress;

        public final boolean claimable;

        public QuestGroupView(String name, String description, Reward reward, List<VariantView> variants) {
            this.name = name;
            this.description = description;
            this.reward = reward;
            this.variants = variants;

            int total = variants.size();
            int done = 0;
            boolean allDone = true;
            boolean anyUnclaimed = false;
            for (VariantView v : variants) {
                if (v.completed || v.ready) {
                    done++;
                } else {
                    allDone = false;
                }
                if (v.ready && !v.completed) {
                    anyUnclaimed = true;
                }
            }
            this.progress = total == 0 ? 0f : (float) done / total;
            this.claimable = allDone && anyUnclaimed;
        }

        public List<String> getClaimableQuestIds() {
            List<String> ids = new ArrayList<>();
            for (VariantView v : variants) {
                if (v.ready && !v.completed) {
                    ids.add(v.questId);
                }
            }
            return ids;
        }
    }

    public static class VariantView {
        public final String label;
        public final boolean completed;
        public final boolean ready;
        public final String questId;

        public VariantView(String label, boolean completed, boolean ready, String questId) {
            this.label = label;
            this.completed = completed;
            this.ready = ready;
            this.questId = questId;
        }
    }

    public static class MinigameLevelInfo {
        public final MiniGames game;
        public final int level;
        public final boolean unlocked;
        public final boolean completed;

        public MinigameLevelInfo(MiniGames game, int level, boolean unlocked, boolean completed) {
            this.game = game;
            this.level = level;
            this.unlocked = unlocked;
            this.completed = completed;
        }
    }

    @Override
    public void changeMenu() {
    }

    @Override
    public void exitMenu() {
    }

    public List<QuestGroupView> getQuestGroups(Class<? extends Quest> type) {
        List<QuestGroupView> result = new ArrayList<>();
        User user = App.getCurrentUser();
        if (user == null) return result;

        List<Quest> allQuests = new ArrayList<>();
        allQuests.addAll(user.getQuestManager().getActiveQuests());
        allQuests.addAll(user.getQuestManager().getCompletedQuests());

        List<Quest> filtered = allQuests.stream()
            .filter(type::isInstance)
            .sorted((q1, q2) -> q1.getPriority().compareTo(q2.getPriority()))
            .collect(Collectors.toList());

        LinkedHashMap<String, List<Quest>> grouped = new LinkedHashMap<>();
        for (Quest q : filtered) {
            String key = q.getGroupId() != null ? q.getGroupId() : q.getId();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(q);
        }

        for (List<Quest> group : grouped.values()) {
            Quest first = group.get(0);
            List<VariantView> variants = new ArrayList<>();
            for (Quest q : group) {
                String label = q.getVariantLabel() != null ? q.getVariantLabel() : "";
                variants.add(new VariantView(label, q.isCompleted(), q.isReadyToClaim(), q.getId()));
            }
            result.add(new QuestGroupView(first.getName(), first.getDescription(), first.getReward(), variants));
        }
        return result;
    }

    public boolean claimQuestGroup(QuestGroupView group) {
        User user = App.getCurrentUser();
        if (user == null) return false;

        boolean claimedAny = false;
        for (String questId : group.getClaimableQuestIds()) {
            if (user.getQuestManager().claimQuest(user, questId)) {
                claimedAny = true;
            }
        }
        return claimedAny;
    }

    public List<MinigameLevelInfo> getMinigameLevels(MiniGames game) {
        List<MinigameLevelInfo> result = new ArrayList<>();
        User user = App.getCurrentUser();
        if (user == null) return result;

        for (MiniGameLevels lvl : MiniGameLevels.values()) {
            if (lvl.miniGame != game) continue;
            boolean completed = user.getMiniGameLevels().contains(lvl);
            boolean unlocked = isMinigameLevelUnlocked(user, game, lvl.level);
            result.add(new MinigameLevelInfo(game, lvl.level, unlocked, completed));
        }
        return result;
    }

    private boolean isMinigameLevelUnlocked(User user, MiniGames game, int level) {
        if (level == 1) return true;
        int previousLevel = level - 1;
        return user.getMiniGameLevels().stream()
            .anyMatch(ml -> ml.miniGame == game && ml.level == previousLevel);
    }

    public void selectMinigame(String minigameName, int level) {
        MiniGames selected = parseMinigameName(minigameName);
        User user = App.getCurrentUser();
        if (selected == null || user == null) {
            return;
        }

        if (!isMinigameLevelUnlocked(user, selected, level)) {
            return;
        }

        try {
            GameWorld world = MiniGameFactory.createMiniGameLevel(selected, level);
            App.setCurrentGame(world);
            switch (selected) {
                case VASE_BREAKER -> {
                    App.getGameApp().setScreen(new VaseBreakerScreen(App.getGameApp(), (VaseBreakerLevel) world));
                }
                case BEGHOULED -> {
                    App.getGameApp().setScreen(new BeghouledScreen(App.getGameApp(),
                        App.getCurrentGame(), Chapter.EGYPT));
                }
                default -> {
                    App.getGameApp().setScreen(new GameScreen(App.getGameApp(), world, Chapter.EGYPT));
                }
                }
        } catch (IllegalArgumentException e) {
        }
    }

    private MiniGames parseMinigameName(String name) {
        return switch (name.toLowerCase()) {
            case "beghouled" -> MiniGames.BEGHOULED;
            case "bowling" -> MiniGames.BOWLING;
            case "vasebreaker", "vase_breaker" -> MiniGames.VASE_BREAKER;
            case "izombie", "i_zombie" -> MiniGames.I_ZOMBIE;
            case "zombotany" -> MiniGames.ZOMBOTANY;
            default -> null;
        };
    }
}
