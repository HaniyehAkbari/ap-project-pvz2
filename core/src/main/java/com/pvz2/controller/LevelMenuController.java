package com.pvz2.controller;

import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.models.enums.Chapter;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.LevelFactory;
import com.pvz2.view.screen.GameScreen;
import com.pvz2.view.screen.LevelMenuScreen;
import com.pvz2.view.audios.AudioManager;
import com.pvz2.view.audios.GameMusic;

public class LevelMenuController implements MenuController {

    private final LevelMenuScreen levelMenuScreen;

    public LevelMenuController(LevelMenuScreen levelMenuScreen) {
        this.levelMenuScreen = levelMenuScreen;
    }

    public static Cell[][] getGameCells() {
        return App.getCurrentGame().getGrid();
    }

    public static Cell[][] getGameCells(Plant owner) {
        return App.getCurrentGame(owner).getGrid();
    }

    @Override
    public void changeMenu() {

    }

    @Override
    public void exitMenu() {

    }

    public String chooseLevel(int level) {
        User user = App.getCurrentUser();
        Chapter currentChapter = user.getCurrentChapter();
        if (currentChapter == null) {
            return "no chapter selected!";
        }
        if (currentChapter.ordinal() >= user.getUnlockedChapter()) {
            return "this chapter is locked!";
        }
        if (user.getUnlockedChapter() == currentChapter.ordinal() + 1 && user.getUnlockedLevel() < level) {
            return "this level is locked!";
        }

        GameWorld world = LevelFactory.createLevel(currentChapter, level);
        App.setCurrentGame(world);
        user.setCurrentLevel(level);
        world.initialize();

        levelMenuScreen.fadeAndSwitchScreen(
                new GameScreen(levelMenuScreen.getGame(), world, currentChapter));
        AudioManager.getInstance().playMusic(GameMusic.HOUSE, true);
        return "level started!";
    }
}
