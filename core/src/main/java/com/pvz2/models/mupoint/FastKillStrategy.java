package com.pvz2.models.mupoint;

public class FastKillStrategy implements ScoreStrategy {

    @Override
    public int calculatePoints(KillEvent event) {

        if (event.getSurvivalTime() < 5) {
            return (int) ((10 - event.getSurvivalTime()) * 3);
        }

        return 0;
    }
}
