package com.pvz2.models.miniGame.beghouled;

import com.pvz2.models.core.App;
import com.pvz2.models.core.UserManager;
import com.pvz2.models.miniGame.MiniGameLevels;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.winCondition.WinCondition;

public class BeghouledWinCondition implements WinCondition {
    MiniGameLevels currentLevel;

    @Override
    public boolean checkWin(GameWorld game) {
        BeghouledMechanics mechanics = game.getMechanic(BeghouledMechanics.class);
        if (mechanics == null) return false;
        if (mechanics.getScore() >= mechanics.getTargetScore()) {
            if (currentLevel != null){
                App.getCurrentUser().getMiniGameLevels().add(currentLevel);
                if (currentLevel.level != 3) App.getCurrentUser().notifyMinigameUnlocked(
                        currentLevel.miniGame.name() + " " + (currentLevel.level+1));
            }
            App.getCurrentUser().save();
            UserManager.syncCurrentUser();
        }
        return mechanics.getScore() >= mechanics.getTargetScore();
    }

    @Override
    public void setCurrentLevel(MiniGameLevels currentLevel) {
        this.currentLevel = currentLevel;
    }
}
