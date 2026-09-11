package com.pvz2.models.world.loseCondition;

import com.pvz2.models.world.GameWorld;

public class SaveOurSeedsLose implements LoseCondition {
    private boolean protectedPlantEaten = false;

    public void onProtectedPlantEaten() {
        protectedPlantEaten = true;
    }

    @Override
    public boolean checkLose(GameWorld game) {
        return protectedPlantEaten;
    }
}
