package com.pvz2.models.quest;

import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantFamily;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.quest.reward.CurrencyReward;
import com.pvz2.models.quest.reward.RandomSeedPacketReward;
import com.pvz2.models.quest.reward.RandomUnlockReward;
import com.pvz2.models.quest.reward.Reward;
import com.pvz2.models.quest.types.DailyQuest;
import com.pvz2.models.quest.types.EpicChallengeQuest;
import com.pvz2.models.quest.types.MainQuest;
import com.pvz2.models.world.ChapterWorld.DarkAgesWorld;
import com.pvz2.models.world.GameWorld;

import java.time.LocalDate;
import java.util.function.Predicate;

public class QuestFactory {

    // 1. Daily Sun Collector
    public static DailyQuest createDailySunCollectorQuest(int sunAmount) {
        String date = LocalDate.now().toString();
        String id = "daily_sun_" + sunAmount + "_" + date;
        String desc = "Collect " + sunAmount + " sun units in one day";
        Predicate<QuestStats> condition = stats -> stats.getSunsCollectedToday() >= sunAmount;
        int coinReward = sunAmount / 100;
        Reward reward = new CurrencyReward(coinReward, 0);
        DailyQuest quest = new DailyQuest(id, desc, QuestPriority.MEDIUM, condition, reward);
        quest.setName("Daily Sun Collector");
        return quest;
    }

    // 2. Chapter Hunter (grouped: one row per chapter, quest itself is shared)
    public static MainQuest createChapterHunterQuest(String chapter) {
        String date = LocalDate.now().toString();
        String id = "main_hunter_" + chapter + "_" + date;
        String desc = "Defeat 50 zombies from " + chapter + " chapter";
        Predicate<QuestStats> condition = stats ->
            stats.getZombiesKilledByChapter().getOrDefault(chapter, 0) >= 50;
        Reward reward = new RandomSeedPacketReward(10);
        MainQuest quest = new MainQuest(id, desc, condition, reward);
        quest.setName("Chapter Hunter");
        quest.setGroupId("main_chapter_hunter");
        quest.setVariantLabel(chapter);
        return quest;
    }

    // 3. Professional Plant Killer (grouped: one row per plant)
    public static DailyQuest createPlantKillerQuest(PlantType plant) {
        String date = LocalDate.now().toString();
        String id = "daily_plant_killer_" + plant.name() + "_" + date;
        String desc = "Kill 10 zombies only with " + plant.name();
        Predicate<QuestStats> condition = stats ->
            stats.getZombiesKilledByPlant().getOrDefault(plant, 0) >= 10 &&
                stats.isOnlyPlantKills() &&
                stats.getExclusivePlantUsed() == plant;
        Reward reward = new RandomUnlockReward();
        DailyQuest quest = new DailyQuest(id, desc, QuestPriority.HIGH, condition, reward);
        quest.setName("Professional Plant Killer");
        quest.setGroupId("daily_plant_killer");
        quest.setVariantLabel(plant.name());
        return quest;
    }

    // 4. Only Cactus
    public static DailyQuest createCactusOnlyQuest() {
        String date = LocalDate.now().toString();
        String id = "daily_cactus_only" + "_" + date;
        String desc = "Kill 10 zombies only with Cactus";
        Predicate<QuestStats> condition = stats ->
            stats.getZombiesKilledByPlant().getOrDefault(PlantType.CACTUS, 0) >= 10 &&
                stats.isOnlyPlantKills() &&
                stats.getExclusivePlantUsed() == PlantType.CACTUS;
        Reward reward = new CurrencyReward(0, 20);
        DailyQuest quest = new DailyQuest(id, desc, QuestPriority.HIGH, condition, reward);
        quest.setName("Only Cactus");
        return quest;
    }

    // 5. Economic Vegetarian (grouped: one row per n)
    public static MainQuest createEconomicVegetarianQuest(int n) {
        String date = LocalDate.now().toString();
        String id = "main_eco_" + n + "_" + date;
        String desc = "Win a level without losing more than " + n + " plants";
        Predicate<QuestStats> condition = stats ->
            stats.isLevelWon() && stats.getPlantsLostInLevel() <= n;
        int seedCount = 20 - n;
        Reward reward = new RandomSeedPacketReward(seedCount);
        MainQuest quest = new MainQuest(id, desc, condition, reward, true);
        quest.setName("Economic Vegetarian");
        quest.setGroupId("main_eco_vegetarian");
        quest.setVariantLabel("\u2264" + n);
        return quest;
    }


    // 6. استاد دفاع (Epic)
    public static EpicChallengeQuest createMasterDefenseQuest() {
        String date = LocalDate.now().toString();
        String id = "epic_master_defense_" + date;
        String desc = "Finish a level with exactly 0 sun remaining";
        Predicate<QuestStats> condition = stats -> stats.getFinalSunCount() == 0;
        Reward reward = new CurrencyReward(0, 200);
        EpicChallengeQuest quest = new EpicChallengeQuest(id, desc, condition, reward, true);
        quest.setName("Master Defense");
        return quest;
    }

    // 7. سرعت عمل (Main)
    public static MainQuest createSpeedQuest() {
        String date = LocalDate.now().toString();
        String id = "main_speed_" + date;
        String desc = "Kill 10 zombies in less than 30 seconds from the start of first wave";
        Predicate<QuestStats> condition = stats -> {
            if (!stats.isFirstWaveStarted()) return false;
            long elapsed = System.currentTimeMillis() - stats.getFirstWaveStartTime();
            return stats.getZombiesKilledInFirstWave() >= 10 && elapsed < 30000;
        };
        Reward reward = new CurrencyReward(500, 0);
        MainQuest quest = new MainQuest(id, desc, condition, reward);
        quest.setName("Quick Action");
        return quest;
    }

    // 8. تخریب‌گر حرفه‌ای (Daily)
    public static DailyQuest createExplosiveDestroyerQuest() {
        String date = LocalDate.now().toString();
        String id = "daily_explosive_destroyer_" + date;
        String desc = "Use 3 explosive plants in one level";
        Predicate<QuestStats> condition = stats -> stats.getExplosivePlantsUsedInLevel() >= 3;
        Reward reward = new CurrencyReward(100, 0);
        DailyQuest quest = new DailyQuest(id, desc, QuestPriority.LOW, condition, reward);
        quest.setName("Professional Destroyer");
        return quest;
    }

    // 9. تقارن (Daily)
    public static DailyQuest createSymmetryQuest() {
        String date = LocalDate.now().toString();
        String id = "daily_symmetry_" + date;
        String desc = "Finish a level with a symmetric garden (except middle row)";
        Predicate<QuestStats> condition = stats -> stats.isSymmetryAchieved();
        Reward reward = new CurrencyReward(500, 0);
        DailyQuest quest = new DailyQuest(id, desc, QuestPriority.HIGH, condition, reward, true);
        quest.setName("Symmetry");
        return quest;
    }

    // 10. کشتار خانوادگی (Daily, grouped: one row per family)
    public static DailyQuest createFamilySlaughterQuest(PlantFamily family) {
        String date = LocalDate.now().toString();
        String id = "daily_family_slaughter_" + family.name() + "_" + date;
        String desc = "Only use plants from " + family.name() + " family to kill zombies";
        Predicate<QuestStats> condition = stats ->
            stats.isOnlyFamilyKills() &&
                stats.getExclusiveFamilyUsed() == family &&
                stats.getTotalZombiesKilled() >= 10;
        Reward reward = new CurrencyReward(1000, 0);
        DailyQuest quest = new DailyQuest(id, desc, QuestPriority.MEDIUM, condition, reward);
        quest.setName("Family Slaughter");
        quest.setGroupId("daily_family_slaughter");
        quest.setVariantLabel(family.name());
        return quest;
    }

    // 11. شکوفایی در محدودیت‌ها (Daily, grouped: one row per family)
    public static DailyQuest createFlourishInRestrictionsQuest(PlantFamily family) {
        String date = LocalDate.now().toString();
        String id = "daily_flourish_" + family.name() + "_" + date;
        String desc = "Win a level without using any plant from " + family.name() + " family";
        Predicate<QuestStats> condition = stats ->
            stats.isLevelWon() && !stats.getFamiliesUsedInLevel().contains(family);
        Reward reward = new CurrencyReward(0, 100);
        DailyQuest quest = new DailyQuest(id, desc, QuestPriority.HIGH, condition, reward, true);
        quest.setName("Flourish in Restrictions");
        quest.setGroupId("daily_flourish");
        quest.setVariantLabel(family.name());
        return quest;
    }

    // 12. شب یا صبح (Epic)
    public static EpicChallengeQuest createNightOrMorningQuest() {
        String date = LocalDate.now().toString();
        String id = "epic_night_morning_" + date;
        String desc = "Finish a day level using only mushroom plants (night plants)";
        Predicate<QuestStats> condition = stats -> {
            if (!stats.isLevelWon()) return false;
            GameWorld game = App.getCurrentGame();
            if (game == null) return false;
            if (game instanceof DarkAgesWorld) return false;
            return stats.getTotalPlantsUsedInLevel() > 0 &&
                stats.getTotalPlantsUsedInLevel() == stats.getMushroomPlantsUsedInLevel();
        };
        Reward reward = new CurrencyReward(0, 20);
        EpicChallengeQuest quest = new EpicChallengeQuest(id, desc, condition, reward, true);
        quest.setName("Night or Morning");
        return quest;
    }

    // 13. برد پشت برد (Daily)
    public static DailyQuest createWinStreakQuest() {
        String date = LocalDate.now().toString();
        String id = "daily_win_streak_" + date;
        String desc = "Win 5 consecutive levels with maximum difficulty (level 5)";
        Predicate<QuestStats> condition = stats -> stats.getConsecutiveWinsMaxDifficulty() >= 5;
        Reward reward = new CurrencyReward(5000, 0);
        DailyQuest quest = new DailyQuest(id, desc, QuestPriority.MEDIUM, condition, reward, true);
        quest.setName("Win Streak");
        return quest;
    }

    // 14. تقریبا پیروز (Daily)
    public static DailyQuest createAlmostVictoryQuest() {
        String date = LocalDate.now().toString();
        String id = "daily_almost_victory_" + date;
        String desc = "Kill 10 zombies in the first column of a row without a lawnmower";
        Predicate<QuestStats> condition = stats ->
            stats.getZombiesKilledInFirstColumnWithoutMower() >= 10;
        Reward reward = new CurrencyReward(300, 0);
        DailyQuest quest = new DailyQuest(id, desc, QuestPriority.MEDIUM, condition, reward);
        quest.setName("Almost Victory");
        return quest;
    }

    // 15. OCD نَمَنَ (Daily)
    public static DailyQuest createOCDQuest() {
        String date = LocalDate.now().toString();
        String id = "daily_ocd_" + date;
        String desc = "Win a level with no symmetry in the garden (except middle row)";
        Predicate<QuestStats> condition = stats ->
            stats.isLevelWon() && !stats.isSymmetryAchieved();
        Reward reward = new CurrencyReward(800, 0);
        DailyQuest quest = new DailyQuest(id, desc, QuestPriority.MEDIUM, condition, reward, true);
        quest.setName("OCD");
        return quest;
    }

    // 16. روز ابری (Daily)
    public static DailyQuest createCloudyDayQuest() {
        String date = LocalDate.now().toString();
        String id = "daily_cloudy_day_" + date;
        String desc = "Win a level using only 3 sun-producing plants";
        Predicate<QuestStats> condition = stats ->
            stats.isLevelWon() && stats.getSunProducerPlantsInLevel() == 3;
        Reward reward = new CurrencyReward(0, 10);
        DailyQuest quest = new DailyQuest(id, desc, QuestPriority.HIGH, condition, reward, true);
        quest.setName("Cloudy Day");
        return quest;
    }

    // 17. یه ستون کمتر (Daily, grouped: one row, one marker per column)
    public static DailyQuest createOneLessColumnQuest(int n) {
        String date = LocalDate.now().toString();
        String id = "daily_one_less_column_" + n + "_" + date;
        String desc = "Win a level without planting any plant in column " + n;
        Predicate<QuestStats> condition = stats ->
            stats.isLevelWon() && stats.getEmptyColumnsInLevel().contains(n);
        Reward reward = new CurrencyReward(0, 10);
        DailyQuest quest = new DailyQuest(id, desc, QuestPriority.HIGH, condition, reward, true);
        quest.setName("One Less Column");
        quest.setGroupId("daily_one_less_column");
        quest.setVariantLabel(String.valueOf(n + 1));
        return quest;
    }

    // 18. سطر بی دفاع (Daily, grouped: one row, one marker per row)
    public static DailyQuest createDefenselessRowQuest(int n) {
        String date = LocalDate.now().toString();
        String id = "daily_defenseless_row_" + n + "_" + date;
        String desc = "Win a level without planting any plant in row " + n;
        Predicate<QuestStats> condition = stats ->
            stats.isLevelWon() && stats.getEmptyRowsInLevel().contains(n);
        Reward reward = new CurrencyReward(0, 20);
        DailyQuest quest = new DailyQuest(id, desc, QuestPriority.HIGH, condition, reward, true);
        quest.setName("Defenseless Row");
        quest.setGroupId("daily_defenseless_row");
        quest.setVariantLabel(String.valueOf(n + 1));
        return quest;
    }

    // 19. صلیب بی دفاع (Daily, grouped: one row, one marker per n)
    public static DailyQuest createCrossDefenselessQuest(int n) {
        String date = LocalDate.now().toString();
        String id = "daily_cross_defenseless_" + n + "_" + date;
        String desc = "Win a level with column " + n + " and row " + n + " empty";
        Predicate<QuestStats> condition = stats ->
            stats.isLevelWon() &&
                stats.getEmptyColumnForCross() == n &&
                stats.getEmptyRowForCross() == n;
        Reward reward = new CurrencyReward(0, 25);
        DailyQuest quest = new DailyQuest(id, desc, QuestPriority.HIGH, condition, reward, true);
        quest.setName("Cross Defenseless");
        quest.setGroupId("daily_cross_defenseless");
        quest.setVariantLabel(String.valueOf(n + 1));
        return quest;
    }

    // 20. وقت چمن‌زنی (Epic, grouped: one row, one marker per threshold)
    public static EpicChallengeQuest createLawnmowerTimeQuest(int n) {
        String date = LocalDate.now().toString();
        String id = "epic_lawnmower_time_" + n + "_" + date;
        String desc = "Kill at least " + n + " zombies using lawnmowers";
        Predicate<QuestStats> condition = stats -> stats.getLawnmowerKills() >= n;
        Reward reward = new CurrencyReward(0, n);
        EpicChallengeQuest quest = new EpicChallengeQuest(id, desc, condition, reward);
        quest.setName("Lawnmower Time");
        quest.setGroupId("epic_lawnmower_time");
        quest.setVariantLabel(String.valueOf(n));
        return quest;
    }
}
