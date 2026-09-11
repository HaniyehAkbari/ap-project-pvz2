package com.pvz2.models.core;

import com.pvz2.models.enums.Chapter;
import com.pvz2.models.enums.NewsType;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.greenhouse.GreenHouse;
import com.pvz2.models.miniGame.MiniGameLevels;
import com.pvz2.models.quest.Quest;
import com.pvz2.models.quest.QuestManager;
import com.pvz2.models.quest.QuestStats;

import java.time.LocalDate;
import java.util.*;

public class User {
    private QuestStats questStats;
    private String username;
    private String hashPassword;
    private String nickname;
    private String email;
    private String gender;
    private String securityQ;
    private String securityA;
    private int gamesPlayed;
    private Set<MiniGameLevels> completedMiniGames = EnumSet.noneOf(MiniGameLevels.class);
    private int unlockedChapter;
    private transient Chapter currentChapter;
    private int currentLevel;
    private int unlockedLevel;
    private int coins;
    private int gems;
    private HashMap<PlantType, Integer> seedPackets;
    private HashMap<PlantType, Integer> unlockedPlantsLevels;
    private HashMap<PlantType, Boolean> plantBoosts;
    private HashMap<String, Boolean> showedZombies;
    private List<News> newsList = new ArrayList<>();
    private GreenHouse greenhouse;
    private int gameDifficulty = 3;
    private int plantFoods = 0;
    private transient QuestManager questManager = new QuestManager();
    private Set<String> completedQuestIds = new HashSet<>();
    private int maxMupoint = 0;
    private LocalDate dailyOfferPurchaseDate;
    private boolean dailyOfferPurchasedToday;
    private int gameSpeed = 1;
    private boolean showGrid = false;
    private boolean debugMode = false;
    private boolean playedMuPoint = false;

    public User() {
        this.plantBoosts = new HashMap<>();
        this.unlockedPlantsLevels = new HashMap<>();
        this.seedPackets = new HashMap<>();
        this.showedZombies = new HashMap<>();
        this.greenhouse = new GreenHouse();
        this.coins = 10000;
        this.gems = 1000;
        this.unlockedChapter = 1;
        this.unlockedLevel = 1;
        this.questStats = new QuestStats();
        gamesPlayed = 0;
        maxMupoint = 0;
        putInitialPlants();
        putZombies();
    }

    private void putZombies() {
        showedZombies.put("ZombieDefault", false);
        showedZombies.put("ZombieConeHead", false);
        showedZombies.put("ZombieBucketHead", false);
        showedZombies.put("ZombieBrickHead", false);
        showedZombies.put("ZombieKnight", false);
        showedZombies.put("ZombieGargantuar", false);
        showedZombies.put("ZombieImp", false);
        showedZombies.put("ZombieRa", false);
        showedZombies.put("ZombieExplorer", false);
        showedZombies.put("ZombieTombRaiser", false);
        showedZombies.put("ZombieIceAgeDodo", false);
        showedZombies.put("ZombieIceAgeHunter", false);
        showedZombies.put("ZombieIceAgeTroglobite", false);
        showedZombies.put("ZombieBeachFisherman", false);
        showedZombies.put("ZombieBeachOctopus", false);
        showedZombies.put("ZombieBeachSnorkel", false);
        showedZombies.put("ZombieDarkJuggler", false);
        showedZombies.put("ZombieWizard", false);
        showedZombies.put("ZombieDarkKing", false);
        showedZombies.put("ZombieDarkImpDragon", false);
        showedZombies.put("ZombieModernAllStar", false);
        showedZombies.put("ZombieLostCityJane", false);
        showedZombies.put("ZombieCrystalSkull", false);
        showedZombies.put("ZombieProspector", false);
        showedZombies.put("ZombiePiano", false);
        showedZombies.put("ZombieArcade", false);
        showedZombies.put("ZombieNewspaper", false);
        showedZombies.put("ZombieBarrelRoller", false);
    }

    private void putInitialPlants() {
        unlockedPlantsLevels.put(PlantType.SUNFLOWER, 1);
        unlockedPlantsLevels.put(PlantType.PEASHOOTER, 1);
        unlockedPlantsLevels.put(PlantType.CABBAGE_PULT, 1);
        unlockedPlantsLevels.put(PlantType.POTATO_MINE, 1);
        unlockedPlantsLevels.put(PlantType.CHERRY_BOMB, 1);
        unlockedPlantsLevels.put(PlantType.ICEBURG, 1);
        unlockedPlantsLevels.put(PlantType.WALL_NUT, 1);
        unlockedPlantsLevels.put(PlantType.GRAVE_BUSTER, 1);
        unlockedPlantsLevels.put(PlantType.REPEATER, 1);
        unlockedPlantsLevels.put(PlantType.SNOW_PEA, 1);
        unlockedPlantsLevels.put(PlantType.LILY_PAD, 1);
        unlockedPlantsLevels.put(PlantType.GIANT_WALLNUT, 1);
        unlockedPlantsLevels.put(PlantType.EXPLODE_O_NUT, 1);

    }

    public void afterLoad() {
        if (this.plantBoosts == null) this.plantBoosts = new HashMap<>();
        if (this.unlockedPlantsLevels == null) this.unlockedPlantsLevels = new HashMap<>();
        if (this.seedPackets == null) this.seedPackets = new HashMap<>();
        if (this.greenhouse == null) this.greenhouse = new GreenHouse();
        if (this.questStats == null) this.questStats = new QuestStats();
        if (this.questManager == null) {
            this.questManager = new QuestManager();
        }
        if (this.completedQuestIds == null) {
            this.completedQuestIds = new HashSet<>();
        }
        if (dailyOfferPurchaseDate == null) dailyOfferPurchaseDate = null;
    }

    public void initQuests() {
        questManager.generateMainQuests();
        questManager.generateEpicQuests();
        questManager.resetDailyIfNeeded(this);
        questManager.generateDailyQuests();
        for (Quest q : questManager.getActiveQuests()) {
            if (completedQuestIds.contains(q.getId())) {
                q.setCompleted(true);
            }
        }
    }
    public void save() {
    }

    public void addSeedPackets(PlantType type, int amount) {
        int currentSeeds = this.seedPackets.getOrDefault(type, 0);
        this.seedPackets.put(type, currentSeeds + amount);
        save();
        UserManager.syncCurrentUser();
    }

    public int getSeedPacketsCount(PlantType type) {
        return this.seedPackets.getOrDefault(type, 0);
    }
    public HashMap<PlantType, Integer> getSeedPackets() {
        return seedPackets;
    }
    public boolean checkPassword(String password) {
        String hashedInput = PasswordHasher.hashSHA256(password);
        return hashedInput.equals(this.hashPassword);
    }
    public void unlockPlant(PlantType plantType) {
        this.unlockedPlantsLevels.put(plantType, 1);
        save();
        UserManager.syncCurrentUser();
    }
    public void addCoins(int amount) {
        this.coins += amount;
        save();
        UserManager.syncCurrentUser();
    }
    public boolean spendCoins(int amount) {
        if (coins < amount) return false;
        coins -= amount;
        save();
        UserManager.syncCurrentUser();
        return true;
    }
    public void addGems(int amount) {
        this.gems += amount;
        UserManager.syncCurrentUser();
        save();
    }
    public boolean spendGems(int amount) {
        if (gems < amount) return false;
        gems -= amount;
        save();
        UserManager.syncCurrentUser();
        return true;
    }
    public HashMap<PlantType, Integer> getUnlockedPlantsLevels() {
        return unlockedPlantsLevels;
    }
    public boolean hasBoost(PlantType type) {
        return plantBoosts.getOrDefault(type, false);
    }
    public void addBoost(PlantType type) {
        plantBoosts.put(type, true);
        save();
        UserManager.syncCurrentUser();
    }
    public List<PlantType> getUnlockedPlantTypesWithPlantFood() {
        List<PlantType> result = new ArrayList<>();
        if (unlockedPlantsLevels == null) return result;
        for (PlantType type : unlockedPlantsLevels.keySet()) {
            if (type != PlantType.MARIGOLD && hasPlantFoodAbility(type)) {
                result.add(type);
            }
        }
        if (result.isEmpty()) {
            result.add(PlantType.PEASHOOTER);
        }
        return result;
    }

    private boolean hasPlantFoodAbility(PlantType type) {
        if (type == null || type == PlantType.MARIGOLD) return false;
        switch (type) {
            case GOLD_BLOOM:
            case CHERRY_BOMB:
            case GRAPESHOT:
            case JALAPENO:
            case DOOM_SHROOM:
            case ICE_SHROOM:
            case HOT_POTATO:
            case GRAVE_BUSTER:
                return false;
            default:
                return true;
        }
    }

    public boolean addPlantFood(int count) {
        if (this.plantFoods + count > 3) {
            return false;
        }
        this.plantFoods += count;
        save();
        UserManager.syncCurrentUser();
        return true;
    }

    public GreenHouse getGreenhouse() {
        return greenhouse;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public int getCoins() {
        return coins;
    }
    public void setCoins(int coins) {
        this.coins = coins;
    }
    public int getGems() {
        return gems;
    }
    public void setGems(int gems) {
        this.gems = gems;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public int getPlantFoods() {
        return plantFoods;
    }
    public void setPlantFoods(int count) {
        this.plantFoods = count;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setSecurityA(String securityA) {
        this.securityA = securityA;
    }
    public boolean checkSeqA(String answer) {
        String hashedInput = PasswordHasher.hashSHA256(answer);
        return hashedInput.equals(this.securityA);
    }
    public String getSecurityQ() {
        return securityQ;
    }
    public void setSecurityQ(String securityQ) {
        this.securityQ = securityQ;
    }
    public List<News> getAllNews() {
        return newsList;
    }
    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }
    public Chapter getCurrentChapter() {
        return currentChapter;
    }
    public void setCurrentChapter(Chapter currentChapter) {
        this.currentChapter = currentChapter;
    }
    public int getGameDifficulty() {
        return gameDifficulty;
    }
    public void setGameDifficulty(int gameDifficulty) {
        if (gameDifficulty < 1 || gameDifficulty > 5) {
            throw new IllegalArgumentException("Difficulty level must be between 1 and 5");
        }
        this.gameDifficulty = gameDifficulty;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }
    public int getUnlockedChapter() {
        return unlockedChapter;
    }
    public int getUnlockedLevel() {
        return unlockedLevel;
    }
    public void addNews(News news) {
        newsList.add(news);
    }
    public HashMap<String, Boolean> getShowedZombies() {
        return showedZombies;
    }
    public void notifyPlantUnlock(String plantName) {
        addNews(new News(
                "Unlock plant",
                "plant" + plantName,
                NewsType.PLANT_UNLOCKED
        ));
    }
    public void notifyZombieUnlock(String zombieName) {
        addNews(new News(
                "Unlock zombie",
                "zombie" + zombieName,
                NewsType.ZOMBIE_UNLOCKED
        ));
    }

    public void notifyLevelUnlock(String levelName) {
        addNews(new News(
                "Unlock new level",
                "level" + levelName,
                NewsType.LEVEL_UNLOCKED
        ));
    }

    public void notifyMinigameUnlocked(String minigameName) {
        addNews(new News(
                "Unlock minigame",
                "minigame " + minigameName,
                NewsType.MINIGAME_UNLOCKED
        ));
    }

    public QuestManager getQuestManager() {
        return questManager;
    }
    public QuestStats getQuestStats() {
        return questStats;
    }
    public void addCompletedQuest(String questId) {
        completedQuestIds.add(questId);
        save();
        UserManager.syncCurrentUser();
    }

    public void unlockLevel() {
        if (unlockedLevel == 4 && unlockedChapter == 4) return;
        if (!(unlockedChapter-1 == currentChapter.ordinal() && unlockedLevel == currentLevel)) return;
        currentLevel = -1;
        int newLevel = unlockedLevel == 4 ? 1 : unlockedLevel + 1;
        int newChapter = newLevel == 1 ? unlockedChapter + 1 : unlockedChapter;
        unlockedLevel = newLevel;
        unlockedChapter = newChapter;
        notifyLevelUnlock(newChapter + "-" + newLevel);
        save();
        UserManager.syncCurrentUser();
    }

    public HashMap<PlantType, Boolean> getPlantBoosts() {
        return plantBoosts;
    }
    public int getMaxMupoint() {
        return maxMupoint;
    }
    public void updateMupointRecord(int currentScore) {
        this.playedMuPoint = true;
        if (currentScore > this.maxMupoint) {
            this.maxMupoint = currentScore;
        }
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }
    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }
    public boolean hasPurchasedDailyOfferToday() {
        return dailyOfferPurchasedToday
                && dailyOfferPurchaseDate != null
                && dailyOfferPurchaseDate.equals(LocalDate.now());
    }

    public void markDailyOfferPurchased() {
        this.dailyOfferPurchaseDate = LocalDate.now();
        this.dailyOfferPurchasedToday = true;
        save();
        UserManager.syncCurrentUser();
    }

    public int getNormalQuestsCount() {
        if (completedQuestIds == null) return 0;
        return (int) completedQuestIds.stream()
                .filter(id -> !id.startsWith("daily_"))
                .count();
    }

    public int getDailyQuestsCount() {
        if (completedQuestIds == null) return 0;
        return (int) completedQuestIds.stream()
                .filter(id -> id.startsWith("daily_"))
                .count();
    }

    public int getCompletedLevels() {
        return completedMiniGames.size() + (unlockedChapter - 1) * 4 + unlockedLevel - 1;
    }

    public Set<MiniGameLevels> getMiniGameLevels() {
        return completedMiniGames;
    }
    public int getGameSpeed() {
        return gameSpeed;
    }
    public void setGameSpeed(int gameSpeed) {
        this.gameSpeed = gameSpeed;
    }
    public boolean isShowGrid() {
        return showGrid;
    }
    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }
    public boolean isDebugMode() {
        return debugMode;
    }
    public int getCurrentLevel() {
        return currentLevel;
    }
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
    public void setPlayedMuPoint(boolean playedMuPoint) {
        this.playedMuPoint = playedMuPoint;
    }
    public boolean isPlayedMuPoint() {
        return playedMuPoint;
    }
    public Set<String> getCompletedQuestIds() {
        return completedQuestIds;
    }
    public LocalDate getDailyOfferPurchaseDate() {
        return dailyOfferPurchaseDate;
    }
}
