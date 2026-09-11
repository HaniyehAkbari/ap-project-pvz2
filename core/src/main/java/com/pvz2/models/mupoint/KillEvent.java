package com.pvz2.models.mupoint;

import com.pvz2.models.zombie.Zombie;

public class KillEvent {
    private Zombie zombie;
    private float spawnTime;
    private float deathTime;
    private int simultaneousKills;
    private boolean bySplashDamage;
    private boolean plantEatenInLine;

    public KillEvent(Zombie zombie, float spawnTime, float deathTime,
                     int simultaneousKills, boolean bySplashDamage, boolean plantEatenInLine) {
        this.zombie = zombie;
        this.spawnTime = spawnTime;
        this.deathTime = deathTime;
        this.simultaneousKills = simultaneousKills;
        this.bySplashDamage = bySplashDamage;
        this.plantEatenInLine = plantEatenInLine;
    }

    public Zombie getZombie() {
        return zombie;
    }

    public float getSurvivalTime() {
        return deathTime - spawnTime;
    }

    public int getSimultaneousKills() {
        return simultaneousKills;
    }

    public boolean isBySplashDamage() {
        return bySplashDamage;
    }

    public boolean hasEatenPlant() {
        return plantEatenInLine;
    }
}
