package com.pvz2.models.world.winCondition;

import com.pvz2.models.core.App;
import com.pvz2.models.core.UserManager;
import com.pvz2.models.miniGame.MiniGameLevels;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.mechanics.NormalMechanic;

public class NormalWin implements WinCondition {
    MiniGameLevels currentLevel;

    @Override
    public boolean checkWin(GameWorld game) {
        NormalMechanic mechanic = game.getMechanic(NormalMechanic.class);
        if (mechanic.getWaveManager().isLevelCompleted() && game.getActiveZombies().isEmpty()) {
            if (currentLevel != null){
                App.getCurrentUser().getMiniGameLevels().add(currentLevel);
                if (currentLevel.level != 3) App.getCurrentUser().notifyMinigameUnlocked(
                        currentLevel.miniGame.name() + " " + (currentLevel.level+1));
            }

            App.getCurrentUser().save();
            UserManager.syncCurrentUser();
        }

        return mechanic.getWaveManager().isLevelCompleted() &&
                game.getActiveZombies().isEmpty();
    }

    @Override
    public void setCurrentLevel(MiniGameLevels currentLevel) {
        this.currentLevel = currentLevel;
    }
}
