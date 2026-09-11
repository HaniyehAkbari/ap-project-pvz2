package com.pvz2.models.quest;

import com.pvz2.controller.GameMenuController;
import com.pvz2.models.core.User;
import com.pvz2.models.enums.Chapter;
import com.pvz2.models.enums.PlantFamily;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.quest.types.DailyQuest;
import com.pvz2.models.quest.types.MainQuest;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class QuestManager {
    private List<Quest> activeQuests = new ArrayList<>();
    private List<Quest> completedQuests = new ArrayList<>();

      private final Set<String> generatedQuestIds = new HashSet<>();

       private boolean initialized = false;

    public void ensureInitialized() {
        if (initialized) return;
        initialized = true;
        generateDailyQuests();
        generateMainQuests();
        generateEpicQuests();
    }

    private void addQuestIfNew(Quest quest) {
        if (quest == null) return;
        if (!generatedQuestIds.add(quest.getId())) {
            return;
        }
        activeQuests.add(quest);
    }

    public void checkAllQuests(User user, boolean isGameEnded) {
        ensureInitialized();
        QuestStats stats = user.getQuestStats();
        Collections.sort(activeQuests);
        for (Quest quest : activeQuests) {
            if (quest.isEndGameDependent() && !isGameEnded) {
                continue;
            }
            if (!quest.isCompleted() && !quest.isReadyToClaim() && quest.checkCompletion(stats)) {
                quest.markReadyToClaim();
                String msg = "✅ Quest ready to claim: " + quest.getDescription();
                GameMenuController.updateState(msg);
            }
        }
    }

    public void checkAllQuests(User user) {
        checkAllQuests(user, false);
    }

    public boolean claimQuest(User user, String questId) {
        ensureInitialized();
        Iterator<Quest> iterator = activeQuests.iterator();
        while (iterator.hasNext()) {
            Quest quest = iterator.next();
            if (quest.getId().equals(questId) && quest.isReadyToClaim() && !quest.isCompleted()) {
                quest.complete(user);
                String msg = "🎉 Quest reward claimed: " + quest.getDescription();
                GameMenuController.updateState(msg);
                user.addCompletedQuest(quest.getId());
                completedQuests.add(quest);
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public void generateDailyQuests() {
        // 1. آفتاب‌گیر روزانه (با مقدار تصادفی از 3000، 4000، 5000)
        int sunAmount = getRandomSunAmount();
        DailyQuest sunQuest = QuestFactory.createDailySunCollectorQuest(sunAmount);
        addQuestIfNew(sunQuest);

        // 3. plant باز حرفه‌ای (یک گیاه تصادفی که قدرت کشتن دارد)
        PlantType randomKillerPlant = getRandomKillerPlant();
        if (randomKillerPlant != null) {
            DailyQuest plantKillerQuest = QuestFactory.createPlantKillerQuest(randomKillerPlant);
            addQuestIfNew(plantKillerQuest);
        }

        // 4. only cactus (همیشه فعال)
        DailyQuest cactusQuest = QuestFactory.createCactusOnlyQuest();
        addQuestIfNew(cactusQuest);

        // 8. تخریب‌گر حرفه‌ای
        addQuestIfNew(QuestFactory.createExplosiveDestroyerQuest());

        // 9. تقارن
        addQuestIfNew(QuestFactory.createSymmetryQuest());

        // 10. کشتار خانوادگی (برای هر خانواده به‌جز خانواده‌های خاص)
        for (PlantFamily family : PlantFamily.values()) {
            if (family != PlantFamily.SUN_PRODUCER && family != PlantFamily.MODIFIER) {
                addQuestIfNew(QuestFactory.createFamilySlaughterQuest(family));
            }
        }

        // 11. شکوفایی در محدودیت‌ها (برای هر خانواده به جز خانواده‌های خاص)
        for (PlantFamily family : PlantFamily.values()) {
            if (family != PlantFamily.SUN_PRODUCER && family != PlantFamily.MODIFIER) {
                addQuestIfNew(QuestFactory.createFlourishInRestrictionsQuest(family));
            }
        }

        // 13. برد پشت برد
        addQuestIfNew(QuestFactory.createWinStreakQuest());

        // 14. تقریبا پیروز
        addQuestIfNew(QuestFactory.createAlmostVictoryQuest());

        // 15. OCD نَمَنَ
        addQuestIfNew(QuestFactory.createOCDQuest());
    }

    public void generateMainQuests() {
        // 2. شکارچی فصل (برای هر فصلی که کاربر آن را باز کرده است)
        for (Chapter chapter : getAvailableChapters()) {
            MainQuest chapterQuest = QuestFactory.createChapterHunterQuest(chapter.name());
            addQuestIfNew(chapterQuest);
        }

        // 5. گیاه‌خوار اقتصادی (برای n های 0 تا 5)
        for (int n = 0; n <= 5; n++) {
            MainQuest ecoQuest = QuestFactory.createEconomicVegetarianQuest(n);
            addQuestIfNew(ecoQuest);
        }

        // 7. سرعت عمل
        addQuestIfNew(QuestFactory.createSpeedQuest());

        // 16. روز ابری
        addQuestIfNew(QuestFactory.createCloudyDayQuest());

        // 17. یه ستون کمتر (برای n=1 تا تعداد ستون‌های بازی، مثلاً 9)
        for (int n = 0; n < 9; n++) {
            addQuestIfNew(QuestFactory.createOneLessColumnQuest(n));
        }

        // 18. سطر بی دفاع (برای n=0 تا تعداد سطرها، مثلاً 5)
        for (int n = 0; n < 5; n++) {
            addQuestIfNew(QuestFactory.createDefenselessRowQuest(n));
        }

        // 19. صلیب بی دفاع (برای n های 0 تا min(سطرها, ستون‌ها))
        int minDim = Math.min(5, 9);
        for (int n = 0; n < minDim; n++) {
            addQuestIfNew(QuestFactory.createCrossDefenselessQuest(n));
        }
    }

    // متد جدید برای تولید کوئست‌های Epic
    public void generateEpicQuests() {
        // 6. استاد دفاع
        addQuestIfNew(QuestFactory.createMasterDefenseQuest());
        // 12. شب یا صبح
        addQuestIfNew(QuestFactory.createNightOrMorningQuest());

        // 20. وقت چمن‌زنی (برای n های 10، 20، 30، 40، 50)
        int[] options = {10, 20, 30, 40, 50};
        for (int n : options) {
            addQuestIfNew(QuestFactory.createLawnmowerTimeQuest(n));
        }
    }

    private int getRandomSunAmount() {
        int[] options = {3000, 4000, 5000};
        return options[new Random().nextInt(options.length)];
    }

    private PlantType getRandomKillerPlant() {
        List<PlantType> killerPlants = Arrays.stream(PlantType.values())
            .filter(pt -> pt.family != PlantFamily.SUN_PRODUCER && pt != PlantType.GRAVE_BUSTER)
            .collect(Collectors.toList());
        if (killerPlants.isEmpty()) return null;
        return killerPlants.get(new Random().nextInt(killerPlants.size()));
    }

    private List<Chapter> getAvailableChapters() {
        return Arrays.asList(Chapter.values());
    }


    public void resetDailyIfNeeded(User user) {
        ensureInitialized();
        LocalDate today = LocalDate.now();
        QuestStats stats = user.getQuestStats();
        if (!today.equals(stats.getLastResetDate())) {
            stats.resetDailyStats();
            activeQuests.removeIf(q -> q instanceof DailyQuest && !((DailyQuest) q).isAvailableToday());
        }
    }

    public List<Quest> getActiveQuests() {
        ensureInitialized();
        return activeQuests;
    }

    public List<Quest> getCompletedQuests() {
        return completedQuests;
    }
}
