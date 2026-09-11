package com.pvz2.models.miniGame.IZombie;

import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.loseCondition.LoseCondition;

public class IZombieLose implements LoseCondition {
    @Override
    public boolean checkLose(GameWorld game) {
        if (game instanceof IZombieLevel level) {

            if (!level.getActiveZombies().isEmpty()) {
                return false;
            }

            return level.getSun() < level.getMinZombieCost();
        }
        return false;
    }
}
