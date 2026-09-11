package com.pvz2.models.network.messages;

import com.pvz2.models.core.News;
import com.pvz2.models.core.User;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.miniGame.MiniGameLevels;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class UserProfile {
    public String username;
    public String nickname;

    public int gamesPlayed;
    public int coins;
    public int gems;

    public int unlockedChapter;
    public int currentLevel;
    public int unlockedLevel;

    public Set<MiniGameLevels> completedMiniGames;

    public HashMap<PlantType, Integer> seedPackets;
    public HashMap<PlantType, Integer> unlockedPlantsLevels;
    public HashMap<PlantType, Boolean> plantBoosts;
    public HashMap<String, Boolean> showedZombies;

    public List<News> newsList;

    public int gameDifficulty;
    public int plantFoods;
    public int maxMupoint;

    public boolean playedMuPoint;

    public Set<String> completedQuestIds;
    public LocalDate dailyOfferPurchaseDate;
    public boolean dailyOfferPurchasedToday;

    public int gameSpeed;
    public boolean showGrid;
    public boolean debugMode;

    public UserProfile() {}

    public static UserProfile from(User user) {
        UserProfile p = new UserProfile();
        p.username = user.getUsername();
        p.nickname = user.getNickname();

        p.gamesPlayed = user.getGamesPlayed();
        p.coins = user.getCoins();
        p.gems = user.getGems();

        p.unlockedChapter = user.getUnlockedChapter();
        p.currentLevel = user.getCurrentLevel();
        p.unlockedLevel = user.getUnlockedLevel();

        p.completedMiniGames = user.getMiniGameLevels();

        p.seedPackets = user.getSeedPackets();
        p.unlockedPlantsLevels = user.getUnlockedPlantsLevels();
        p.plantBoosts = user.getPlantBoosts();
        p.showedZombies = user.getShowedZombies();

        p.newsList = user.getAllNews();

        p.gameDifficulty = user.getGameDifficulty();
        p.plantFoods = user.getPlantFoods();
        p.maxMupoint = user.getMaxMupoint();
        p.playedMuPoint = user.isPlayedMuPoint();

        p.completedQuestIds = user.getCompletedQuestIds();
        p.dailyOfferPurchaseDate = user.getDailyOfferPurchaseDate();
        p.dailyOfferPurchasedToday = user.hasPurchasedDailyOfferToday();

        p.gameSpeed = user.getGameSpeed();
        p.showGrid = user.isShowGrid();
        p.debugMode = user.isDebugMode();

        return p;
    }
}
