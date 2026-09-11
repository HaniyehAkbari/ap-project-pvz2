package com.pvz2.models.world.loseCondition;

import com.pvz2.models.world.GameWorld;

public class TimedWarLose implements LoseCondition {
    private float timeLimit;
    private int targetKills;
    private int currentKills = 0;
    private float startTime = -1;

    public TimedWarLose(long timeLimit, int targetKills) {
        this.timeLimit = timeLimit;
        this.targetKills = targetKills;
    }

    public void onZombieKilled() {
        currentKills++;
    }


    @Override
    public boolean checkLose(GameWorld game) {
        if (startTime == -1) {
            startTime = game.getElapsedTime();
        }
        float elapsed = game.getElapsedTime() - startTime;

        return elapsed >= timeLimit && currentKills < targetKills;
    }

    public int getCurrentKills() {
        return currentKills;
    }

    public int getTargetKills() {
        return targetKills;
    }

}
