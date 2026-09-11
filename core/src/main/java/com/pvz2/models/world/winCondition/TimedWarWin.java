package com.pvz2.models.world.winCondition;

import com.pvz2.models.core.App;
import com.pvz2.models.core.UserManager;
import com.pvz2.models.miniGame.MiniGameLevels;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.loseCondition.TimedWarLose;

public class TimedWarWin implements WinCondition {
    private final TimedWarLose loseCondition;

    public TimedWarWin(TimedWarLose loseCondition) {
        this.loseCondition = loseCondition;
    }

    @Override
    public boolean checkWin(GameWorld game) {
        if (loseCondition.getCurrentKills() >= loseCondition.getTargetKills()) {
            App.getCurrentUser().save();
            UserManager.syncCurrentUser();
        }
        return loseCondition.getCurrentKills() >= loseCondition.getTargetKills();
    }

    @Override
    public void setCurrentLevel(MiniGameLevels currentLevel) {

    }
}
