package com.pvz2.models.zombie.wave;


public class WaveSpawnEntry {
    private String zombieAlias;
    private int wavePointCost;

    public WaveSpawnEntry(String zombieAlias, int wavePointCost) {
        this.wavePointCost = wavePointCost;
        this.zombieAlias = zombieAlias;

    }

    public int getWavePointCost() {
        return wavePointCost;
    }

    public String getZombie() {
        return zombieAlias;
    }

    public String getZombieAlias() {
        return zombieAlias;
    }
}
