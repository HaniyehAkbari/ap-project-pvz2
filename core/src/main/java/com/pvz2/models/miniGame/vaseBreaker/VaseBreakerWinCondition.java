package com.pvz2.models.miniGame.vaseBreaker;

import com.pvz2.models.core.App;
import com.pvz2.models.core.UserManager;
import com.pvz2.models.miniGame.MiniGameLevels;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.winCondition.WinCondition;

public class VaseBreakerWinCondition implements WinCondition {
    MiniGameLevels currentLevel;

    @Override
    public boolean checkWin(GameWorld gameWorld) {
        if (gameWorld instanceof VaseBreakerLevel level) {

            for (Vase vase : level.getVases()) {
                if (!vase.isBroken()) {
                    return false;
                }
            }
            if (level.getActiveZombies().isEmpty()) {
                if (currentLevel != null){
                    App.getCurrentUser().getMiniGameLevels().add(currentLevel);
                    if (currentLevel.level != 3) App.getCurrentUser().notifyMinigameUnlocked(
                            currentLevel.miniGame.name() + " " + (currentLevel.level+1));
                }
                App.getCurrentUser().save();
                UserManager.syncCurrentUser();
            }
            return level.getActiveZombies().isEmpty();
        }
        return false;
    }

    @Override
    public void setCurrentLevel(MiniGameLevels currentLevel) {
        this.currentLevel = currentLevel;
    }
}
