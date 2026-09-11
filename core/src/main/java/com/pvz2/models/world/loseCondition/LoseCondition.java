package com.pvz2.models.world.loseCondition;

import com.pvz2.models.world.GameWorld;

public interface LoseCondition {
    boolean checkLose(GameWorld game);
}
