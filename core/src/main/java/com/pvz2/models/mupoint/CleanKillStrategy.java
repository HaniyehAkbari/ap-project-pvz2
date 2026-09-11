package com.pvz2.models.mupoint;

public class CleanKillStrategy implements ScoreStrategy {
    @Override
    public int calculatePoints(KillEvent event) {
        if (!event.hasEatenPlant()) {
            return 30;
        }
        return 0;
    }
}
