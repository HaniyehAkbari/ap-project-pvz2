package com.pvz2.models.zombie.wave;

import com.badlogic.gdx.Gdx;
import com.pvz2.models.core.App;
import com.pvz2.models.core.DifficultyCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Wave {
    private int waveNumber;
    private List<WaveSpawnEntry> spawnEntries;
    private float spawnDelayTicks;
    private int currentIndex;
    private float ticksSinceLastSpawn;
    private boolean isFinalWave;

    public Wave(int waveNumber,
                int totalCost,
                List<WaveSpawnEntry> spawnEntries,
                int spawnDelayTicks,
                boolean isFinalWave) {
        this.waveNumber = waveNumber;
        this.spawnEntries = new ArrayList<>(spawnEntries);
        this.spawnDelayTicks = spawnDelayTicks;
        this.currentIndex = 0;
        this.ticksSinceLastSpawn = 0;
        this.isFinalWave = isFinalWave;
    }

    public static Wave generateRandomWave(int waveNumber,
                                          int totalCost,
                                          List<WaveSpawnEntry> availableEntries,
                                          int spawnDelayTicks, boolean isFinalWave,
                                          Random random) {
        List<WaveSpawnEntry> generatedEntries = new ArrayList<>();
        int currentCost = 0;

        while (currentCost < totalCost && !availableEntries.isEmpty()) {
            WaveSpawnEntry randomEntry = availableEntries.get(random.nextInt(availableEntries.size()));

            if (currentCost + randomEntry.getWavePointCost() <= totalCost) {
                generatedEntries.add(new WaveSpawnEntry(randomEntry.getZombieAlias(), randomEntry.getWavePointCost()));
                currentCost += randomEntry.getWavePointCost();
            } else {
                boolean canAddAny = false;
                for (WaveSpawnEntry entry : availableEntries) {
                    if (currentCost + entry.getWavePointCost() <= totalCost) {
                        canAddAny = true;
                        break;
                    }
                }
                if (!canAddAny) break;
            }
        }

        if (generatedEntries.isEmpty() && !availableEntries.isEmpty()) {
            WaveSpawnEntry cheapest = availableEntries.get(0);
            for (WaveSpawnEntry entry : availableEntries) {
                if (entry.getWavePointCost() < cheapest.getWavePointCost()) {
                    cheapest = entry;
                }
            }
            generatedEntries.add(new WaveSpawnEntry(cheapest.getZombieAlias(), cheapest.getWavePointCost()));
        }

        return new Wave(waveNumber, totalCost, generatedEntries, spawnDelayTicks, isFinalWave);
    }

    public static List<Wave> generateWaves(int totalWaves,
                                           int baseDifficulty,
                                           List<WaveSpawnEntry> availableEntries,
                                           int spawnDelayTicks,
                                           Random random) {

        int userDifficulty = App.getCurrentUser().getGameDifficulty();
        double decreaseFactor = DifficultyCalculator.decreaseFactor(userDifficulty);

        List<Wave> waves = new ArrayList<>();
        for (int i = 1; i <= totalWaves; i++) {
            boolean isFinal = (i == totalWaves);
            double difficulty = baseDifficulty * Math.pow(1.25, i - 1);
            if (isFinal) {
                difficulty *= 1.5;
            }
            int cost = (int) Math.round(difficulty * decreaseFactor);

            Wave wave = generateRandomWave(i, cost, availableEntries, i == 1 ? 5 : spawnDelayTicks, isFinal, random);
            waves.add(wave);
        }
        return waves;
    }

    public static List<Wave> generateWaves(int totalWaves,
                                           int baseDifficulty,
                                           List<WaveSpawnEntry> availableEntries,
                                           int spawnDelayTicks) {
        return generateWaves(totalWaves, baseDifficulty, availableEntries, spawnDelayTicks, new Random());
    }

    public boolean isFinishedSpawning() {
        return currentIndex >= spawnEntries.size();
    }

    public WaveSpawnEntry getNextSpawn() {
        if (isFinishedSpawning()) return null;
        if (ticksSinceLastSpawn < spawnDelayTicks) {
            ticksSinceLastSpawn += Gdx.graphics.getDeltaTime();
            return null;
        }
        ticksSinceLastSpawn = 0;
        return spawnEntries.get(currentIndex++);
    }

    public void resetSpawning() {
        this.currentIndex = 0;
        this.ticksSinceLastSpawn = 0;
    }

    public int getTotalZombieCount() {
        return spawnEntries.size();
    }
    public int getWaveNumber() {
        return waveNumber;
    }
    public boolean isFlagWave() {
        return isFinalWave;
    }
    public List<WaveSpawnEntry> getSpawnEntries() {return spawnEntries;}
}
