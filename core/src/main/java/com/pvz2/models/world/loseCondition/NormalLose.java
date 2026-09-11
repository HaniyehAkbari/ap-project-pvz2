package com.pvz2.models.world.loseCondition;

import com.pvz2.models.world.GameWorld;

public class NormalLose implements LoseCondition {
    @Override
    public boolean checkLose(GameWorld game) {
        return game.getActiveZombies().stream()
                .anyMatch(zombie -> {
                    if (zombie.getX() < 0) {
                        return game.getLawnMowerManager().getMowers().stream()
                                .noneMatch(lawnMower -> lawnMower.getRow() == (int) zombie.getY()
                                        && !lawnMower.isActive());
                    }
                    return false;
                });
    }
}
