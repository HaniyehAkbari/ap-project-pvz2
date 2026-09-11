package com.pvz2.models.mupoint;

public class ComboKillStrategy implements ScoreStrategy {
    @Override
    public int calculatePoints(KillEvent event) {
        if (!event.isBySplashDamage() && event.getSimultaneousKills() >= 3) {
            return event.getSimultaneousKills() * 40;
        }
        return 0;
    }
}
