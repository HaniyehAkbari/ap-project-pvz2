package com.pvz2.models.core;

import com.pvz2.Main;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.factory.PlantFactory;
import com.pvz2.models.projectile.Projectile;
import com.pvz2.models.projectile.strikeStrategies.CheckStraightStrike;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.Sun;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.view.screen.OnlineGameScreen;

public class App {
    private static final PlantFactory FACTORY = new PlantFactory();
    private static final float CELL_HEIGHT = 125f;
    private static final float CELL_WIDTH = 142f;
    private static final float FIRST_CELL_Y = 175f;
    private static final float FIRST_CELL_X = 532f;
    private static User currentUser;
    private static GameWorld currentGame;
    private static Main gameApp;
    private static User defaultUser = new User();

    public static User getCurrentUser() {
        if (currentUser == null) return defaultUser;
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        App.currentUser = currentUser;
    }

    public static int getSpeed(){
        if (currentUser != null) return currentUser.getGameSpeed();
        return 1;
    }

    public static GameWorld getCurrentGame() {
        return currentGame;
    }

    public static GameWorld getCurrentGame(boolean canNetworked) {
        if (currentGame != null) return currentGame;
        if (gameApp.getScreen() instanceof OnlineGameScreen onlineGameScreen){
            return onlineGameScreen.getController().getWorld();
        }
        return null;
    }

    public static GameWorld getCurrentGame(Object entity) {
        if (currentGame == null){
            if (entity instanceof Plant plant){
                return plant.getCurrentWorld();
            }
            if (entity instanceof Projectile projectile){
                return projectile.getCurrentWorld();
            }
            if (entity instanceof Zombie zombie){
                return zombie.getCurrentWorld();
            }
            if (entity instanceof Sun sun){
                return sun.getCurrentWorld();
            }
            if (entity instanceof CheckStraightStrike css){
                return css.getCurrentWorld();
            }
        }
        return currentGame;
    }

    public static void setCurrentGame(GameWorld currentGame) {
        App.currentGame = currentGame;
    }

    public static float getCellHeight() {
        return CELL_HEIGHT;
    }

    public static float getFirstCellX() {
        return FIRST_CELL_X;
    }

    public static float getCellWidth() {
        return CELL_WIDTH;
    }

    public static float getFirstCellY() {
        return FIRST_CELL_Y;
    }

    public static PlantFactory getFactory() {
        return FACTORY;
    }

    public static float getCellCenterX(int col) {
        return getFirstCellX() + col * getCellWidth();
    }

    public static float getCellCenterY(int row) {
        return getFirstCellY() + row * getCellHeight();
    }

    public static String getArmoredZombieName(String id) {
        if (id == null) {
            return "Regular Zombie";
        }
        switch (id) {
            case "ZombieArmor1":
                return "ZombieConeHead";
            case "ZombieArmor2":
                return "ZombieBucketHead";
            case "ZombieDarkArmor3":
                return "ZombieKnight";
            case "ZombieArmor4":
                return "ZombieBrickHead";
            default:
                return id;
        }
    }

    public static String getZombieId(String name) {
        if (name == null) {
            return null;
        }
        switch (name) {
            case "ZombieConehead":
            case "ZombieConeHead":
                return "ZombieArmor1";
            case "ZombieBuckethead":
            case "ZombieBucketHead":
                return "ZombieArmor2";
            case "ZombieKnight":
                return "ZombieDarkArmor3";
            case "ZombieBrickhead":
            case "ZombieBrickHead":
                return "ZombieArmor4";
            default:
                return name;
        }
    }

    public static float getCellCenterY(float row) {
        return getFirstCellY() + row * getCellHeight();
    }

    public static void setGameApp(Main gameApp) {
        App.gameApp = gameApp;
    }

    public static Main getGameApp() {
        return gameApp;
    }

}
