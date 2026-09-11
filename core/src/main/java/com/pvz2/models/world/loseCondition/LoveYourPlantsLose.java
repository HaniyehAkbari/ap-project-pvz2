package com.pvz2.models.world.loseCondition;

import com.pvz2.models.world.GameWorld;

public class LoveYourPlantsLose implements LoseCondition {
    private int maxLosses;
    private int currentLosses = 0;

    public LoveYourPlantsLose(int maxLosses) {
        this.maxLosses = maxLosses;
    }

    public void setGameListener(GameWorld gameWorld){
        gameWorld.registerPlantEatenListener(this::onPlantEaten);
    }

    public void onPlantEaten() {
        currentLosses++;
    }

    @Override
    public boolean checkLose(GameWorld game) {
        return currentLosses >= maxLosses;
    }
}
