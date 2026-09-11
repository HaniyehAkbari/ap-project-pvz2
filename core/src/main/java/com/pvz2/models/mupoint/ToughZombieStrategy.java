package com.pvz2.models.mupoint;

public class ToughZombieStrategy implements ScoreStrategy {
    @Override
    public int calculatePoints(KillEvent event) {
        int maxHealth = event.getZombie().getMaxHealth();
        if (maxHealth >= 1000) {
            return maxHealth / 10;
        }
        return 0;
    }
}
