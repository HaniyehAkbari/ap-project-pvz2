package com.pvz2.models.core;

public class DifficultyCalculator {
    private static final int BASE_DIFFICULTY = 3;

    public static double increaseFactor(int gameDifficulty) {
        return gameDifficulty / (double) BASE_DIFFICULTY;
    }

    public static double decreaseFactor(int gameDifficulty) {
        return (double) BASE_DIFFICULTY / gameDifficulty;
    }
}
