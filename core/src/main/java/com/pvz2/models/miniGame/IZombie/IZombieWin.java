package com.pvz2.models.miniGame.IZombie;

import com.pvz2.models.core.App;
import com.pvz2.models.core.UserManager;
import com.pvz2.models.miniGame.MiniGameLevels;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.winCondition.WinCondition;

public class IZombieWin implements WinCondition {
    MiniGameLevels currentLevel;

    @Override
    public boolean checkWin(GameWorld game) {
        if (game instanceof IZombieLevel level) {
            if (level.getBrains().isEmpty()) return false;
            for (Brain brain : level.getBrains()) {
                if (!brain.isEaten()) return false;
            }
            if (currentLevel != null){
                App.getCurrentUser().getMiniGameLevels().add(currentLevel);
                if (currentLevel.level != 3) App.getCurrentUser().notifyMinigameUnlocked(
                        currentLevel.miniGame.name() + " " + (currentLevel.level+1));
            }
            App.getCurrentUser().save();
            UserManager.syncCurrentUser();

            return true;
        }
        return false;
    }

    @Override
    public void setCurrentLevel(MiniGameLevels currentLevel) {
        this.currentLevel = currentLevel;
    }
}
