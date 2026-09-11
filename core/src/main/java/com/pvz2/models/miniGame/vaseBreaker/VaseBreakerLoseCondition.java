package com.pvz2.models.miniGame.vaseBreaker;

import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.loseCondition.LoseCondition;
import com.pvz2.models.zombie.Zombie;

public class VaseBreakerLoseCondition implements LoseCondition {
    @Override
    public boolean checkLose(GameWorld gameWorld) {
        if (!(gameWorld instanceof VaseBreakerLevel level)) {
            return false;
        }

        for (Zombie zombie : level.getActiveZombies()) {
            if (zombie.getX() <= 100) {
                return true;
            }
        }

        boolean allVasesBroken = level.getVases().stream().allMatch(Vase::isBroken);
        boolean noPlantsAvailable = level.getActivePlants().isEmpty() && level.getPlantLists().isEmpty();
        boolean zombiesAlive = !level.getActiveZombies().isEmpty();

        return allVasesBroken && noPlantsAvailable && zombiesAlive;
    }
}
