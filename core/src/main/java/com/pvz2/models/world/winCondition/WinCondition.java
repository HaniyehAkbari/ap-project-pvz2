package com.pvz2.models.world.winCondition;

import com.pvz2.models.miniGame.MiniGameLevels;
import com.pvz2.models.world.GameWorld;

public interface WinCondition {
    boolean checkWin(GameWorld game);

    void setCurrentLevel(MiniGameLevels currentLevel);
}
