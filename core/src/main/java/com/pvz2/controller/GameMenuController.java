package com.pvz2.controller;

import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.models.core.UserManager;
import com.pvz2.models.enums.Chapter;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.greenhouse.GreenHouse;
import com.pvz2.models.miniGame.MiniGameWorld;
import com.pvz2.models.mupoint.MupointManager;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.card.ImitatorCard;
import com.pvz2.models.plant.card.PlantCard;
import com.pvz2.models.plant.components.ImitatorIntroComponent;
import com.pvz2.models.world.*;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.zombie.ZombieFactory;
import com.pvz2.view.graphic.PlantGraphic;
import com.pvz2.view.screen.BeghouledScreen;
import com.pvz2.view.screen.GameScreen;
import com.pvz2.view.screen.MenuScreen;

import java.util.List;

public class GameMenuController implements MenuController {
    private static MenuScreen screen;
    public static void handleWinning(GameWorld gameWorld) {
        User user = App.getCurrentUser();
        if (user == null) return;
        if (gameWorld.isWillUnlockLevel()) {
            user.unlockLevel();
        }
        MupointManager mupointManager = gameWorld.getMupointManager();
        if (mupointManager != null) {
            user.setPlayedMuPoint(true);
            int currentLevelPoints = mupointManager.getTotalMupoints();
            if (currentLevelPoints > user.getMaxMupoint()) {
                user.updateMupointRecord(currentLevelPoints);
            }
        }
        if (!gameWorld.isConveyorMode()){
            for (PlantCard plantCard : gameWorld.getPlantLists()){
                user.getPlantBoosts().remove(plantCard.getType());
            }
        }
        user.save();
        UserManager.syncCurrentUser();
    }

    public static void handleLosing(GameWorld gameWorld) {
        User user = App.getCurrentUser();
        if (user == null) return;
        MupointManager mupointManager = gameWorld.getMupointManager();
        if (mupointManager != null) {
            user.setPlayedMuPoint(true);
            int currentLevelPoints = mupointManager.getTotalMupoints();
            if (currentLevelPoints > user.getMaxMupoint()) {
                user.updateMupointRecord(currentLevelPoints);
            }
        }
        if (!gameWorld.isConveyorMode()){
            for (PlantCard plantCard : gameWorld.getPlantLists()){
                user.getPlantBoosts().remove(plantCard.getType());
            }
        }
        user.save();
        UserManager.syncCurrentUser();
    }

    public static void updateScreenPlants(Plant plant){
        if (screen instanceof GameScreen gameScreen){
            gameScreen.getPlantGraphics().add(new PlantGraphic(plant, gameScreen.getGame().pamPlayer));
        }
    }

    public static void updateState(String state) {

    }

    public static void updateState(String title, String state) {
        if (screen != null){
            screen.addToast(title, state);
        }
    }
    @Override
    public void changeMenu() {}
    @Override
    public void exitMenu() {
        App.setCurrentGame(null);
        App.getCurrentUser().getPlantBoosts().clear();
    }


    public static void restart(){
        User user = App.getCurrentUser();
        if (user == null) return;
        Chapter currentChapter = user.getCurrentChapter();
        int level = user.getCurrentLevel();
        if (currentChapter == null) {
            return;
        }
        GameWorld world = LevelFactory.createLevel(currentChapter, level);
        App.setCurrentGame(world);
        user.setCurrentLevel(level);
        world.initialize();

        screen.fadeAndSwitchScreen(
            new GameScreen(screen.getGame(), world, currentChapter));
    }

    public static void restartBeghouled(GameWorld world){
        User user = App.getCurrentUser();
        if (user == null) return;
        GameWorld newWorld = ((MiniGameWorld) world).getBuilder().get();
        App.setCurrentGame(newWorld);
        ((MenuScreen) App.getGameApp().getScreen()).fadeAndSwitchScreen(
            new BeghouledScreen(App.getGameApp(),
                App.getCurrentGame(), Chapter.EGYPT));
    }

    public static boolean collectSun(float touchX, float touchY) {
        for (Sun sun : App.getCurrentGame().getActiveSuns()) {
            if (sun.getBounds().contains(touchX, touchY) && !sun.isCollected() && !sun.isExploded()) {
                if (sun.getType() == SunType.RADIOACTIVE) {
                    sun.explode();
                } else {
                    sun.collect();
                    App.getCurrentGame().addSunToPlayer(sun.getSize());
                }
                return true;
            }
        }
        return false;
    }
    public static void collectCollectable(float x, float y) {
        for (Collectable collectable : App.getCurrentGame().getActiveCollectables()) {
            if (collectable.isDead()) continue;
            if (Math.abs(collectable.getX() - x) < 20 && Math.abs(collectable.getY() - y) < 20) {
                switch (collectable.getType()) {
                    case POT:
                        GreenHouse greenhouse = App.getCurrentUser().getGreenhouse();
                        greenhouse.unlockFirstLockedPot();
                        collectable.collect();
                        break;
                    case COIN:
                        App.getCurrentUser().setCoins(App.getCurrentUser().getCoins() + 10);
                        if (screen instanceof GameScreen gameScreen){
                            gameScreen.getHud().getResourcesTable().update();
                        }
                        collectable.collect();
                        break;
                    case DIAMOND:
                        App.getCurrentUser().setGems(App.getCurrentUser().getGems() + 1);
                        if (screen instanceof GameScreen gameScreen){
                            gameScreen.getHud().getResourcesTable().update();
                        }
                        collectable.collect();
                        break;
                    case PLANT_FOOD:
                        if (App.getCurrentGame().getPlantFoods() < 3) {
                            App.getCurrentGame().setPlantFoods(App.getCurrentGame().getPlantFoods() + 1);
                            collectable.collect();
                        }
                        break;
                }
                return;
            }
        }
    }

    public static boolean selectAndUnselectPlantfood(){
        GameWorld world = App.getCurrentGame();
        if (world == null) return false;
        unselectPlant();
        unselectShovel();
        if (world.getPlantFoods() == 0){
            world.setSelectedPlantfood(false);
            return false;
        }
        if (world.isSelectedPlantfood()){
            world.setSelectedPlantfood(false);
            return true;
        }
        world.setSelectedPlantfood(true);
        return true;
    }

    public static boolean selectAndUnselectShovel(){
        GameWorld world = App.getCurrentGame();
        if (world == null) return false;
        unselectPlant();
        unselectPlantfood();
        if (world.isSelectedShovel()){
            world.setSelectedShovel(false);
            return true;
        }
        world.setSelectedShovel(true);
        return true;
    }

    public static boolean selectAndUnselectPlant(PlantType type, MenuScreen screen) {
        if (App.getCurrentGame().isPlantSelected()){
            unselectPlantfood();
            unselectShovel();
            if (App.getCurrentGame().getSelectedPlant() == type){
                unselectPlant();
                return false;
            }
            unselectPlant();
        }
        List<PlantCard> gamePlants = App.getCurrentGame().getPlantLists();
        PlantCard selectedCard = null;
        for (PlantCard card : gamePlants) {
            if (card.getType().equals(type)) {
                selectedCard = card;
                break;
            }
        }
        if (selectedCard == null) {
            return false;
        }
        if (!selectedCard.isReady()){
            screen.addToast("Error", "This plant isn't ready!");
            return false;
        }
        if (!(selectedCard.getSunCost() <= App.getCurrentGame().getSun())){
            screen.addToast("Error", "You don't have enough suns!");
            return false;
        }
        App.getCurrentGame().setPlantSelected(true);
        App.getCurrentGame().setSelectedPlant(selectedCard.getType());
        return true;
    }
    public static void unselectPlant() {
        App.getCurrentGame().setSelectedPlant(null);
        App.getCurrentGame().setPlantSelected(false);
    }
    public static void unselectPlantfood(){
        App.getCurrentGame().setSelectedPlantfood(false);
    }
    public static void unselectShovel(){
        App.getCurrentGame().setSelectedShovel(false);
    }


    public static Plant plantPlant(PlantType type, float x, float y) {
        PlantCard selectedCard = null;
        unselectPlant();
        List<PlantCard> gamePlants = App.getCurrentGame().isConveyorMode()?
                App.getCurrentGame().getConveyorBelt() : App.getCurrentGame().getPlantLists();
        for (PlantCard card : gamePlants) {
            if (card.getType().equals(type)) {
                selectedCard = card;
                break;
            }
        }
        if (selectedCard == null) {
            return null;
        }
        return plantPlant(selectedCard, x, y);
    }

    private static Cell findCellAt(GameWorld game, float x, float y) {return Cell.findCell(x, y,
        game.getGrid());}

    public static Plant plantPlant(PlantCard card, float x, float y) {
        Cell selectedCell = findCellAt(App.getCurrentGame(), x, y);
        if (selectedCell == null) {
            return null;
        }
        Plant plant;
        PlantType type;
        boolean isImitator = card instanceof ImitatorCard;

        if (card instanceof ImitatorCard imitatorCard) {
            type = imitatorCard.getTargetType();
            plant = selectedCell.handlePlanting(type,
                    App.getCurrentUser().getUnlockedPlantsLevels().get(PlantType.IMITATER)>=4);
        } else {
            type = card.getType();
            plant = selectedCell.handlePlanting(type, App.getCurrentUser().hasBoost(type));
        }
        if (plant != null){
            if (isImitator) {
                plant.setImitate(true);
                plant.addComponent(new ImitatorIntroComponent());
            }
            if (plant.isExplosive()) {
                App.getCurrentUser().getQuestStats().incrementExplosivePlantsUsed();
            }
            card.setReady(false);
            App.getCurrentGame().setSun(App.getCurrentGame().getSun() - card.getSunCost());
            return plant;
        }
        return null;
    }

    public static void pluckPlant(float x, float y) {
        if (!App.getCurrentGame().isSelectedShovel()) return;
        Cell selectedCell = findCellAt(App.getCurrentGame(), x, y);
        if (selectedCell == null || !selectedCell.findAndRemovePlant()) {
        }
    }

    public static void feedPlant(float x, float y) {
        if (!App.getCurrentGame().isSelectedPlantfood()) return;
        if (App.getCurrentGame().getPlantFoods() <= 0) {
            return;
        }
        Cell selectedCell = null;
        for (Cell[] cells : App.getCurrentGame().getGrid()) {
            if (!(cells[0].getY() + App.getCellHeight() / 2 > y && cells[0].getY() - App.getCellHeight() / 2 < y))
                continue;
            for (Cell cell : cells) {
                if ((cell.getX() + App.getCellWidth() / 2 > x && cell.getX() - App.getCellWidth() / 2 < x)) {
                    selectedCell = cell;
                    break;
                }
            }
        }
        if (selectedCell == null || selectedCell.findPlant() == null) {
            return;
        }
        selectedCell.findPlant().activatePlantFood();
        App.getCurrentGame().setPlantFoods(App.getCurrentGame().getPlantFoods() - 1);
    }

    public static void cheatSpawnZombie(String type, int col, int row) {
        Zombie zombie = new ZombieFactory().createZombie(type);
        if (zombie == null) {
            return;
        }
        Cell cell = App.getCurrentGame().getGrid()[row][col];
        zombie.setX(cell.getX());
        zombie.setY(cell.getY());
        App.getCurrentGame().getActiveZombies().add(zombie);
    }

    public static void showAnnouncement(String message) {
        GameScreen.announce(message);
    }

    public static void setScreen(MenuScreen screen) {
        GameMenuController.screen = screen;
    }

    public static MenuScreen getScreen() {
        return screen;
    }
}
