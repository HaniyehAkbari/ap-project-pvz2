package com.pvz2.models.miniGame;

public enum MiniGameLevels {
    VASE_BREAKER_1(MiniGames.VASE_BREAKER, 1),
    VASE_BREAKER_2(MiniGames.VASE_BREAKER, 2),
    VASE_BREAKER_3(MiniGames.VASE_BREAKER, 3),
    BOWLING_1(MiniGames.BOWLING, 1),
    BOWLING_2(MiniGames.BOWLING, 2),
    BOWLING_3(MiniGames.BOWLING, 3),
    I_ZOMBIE_1(MiniGames.I_ZOMBIE, 1),
    I_ZOMBIE_2(MiniGames.I_ZOMBIE, 2),
    I_ZOMBIE_3(MiniGames.I_ZOMBIE, 3),
    BEGHOULED_1(MiniGames.BEGHOULED, 1),
    BEGHOULED_2(MiniGames.BEGHOULED, 2),
    BEGHOULED_3(MiniGames.BEGHOULED, 3),
    ZOMBOTANY_1(MiniGames.ZOMBOTANY, 1),
    ZOMBOTANY_2(MiniGames.ZOMBOTANY, 2),
    ZOMBOTANY_3(MiniGames.ZOMBOTANY, 3);

    public final MiniGames miniGame;
    public final int level;
    MiniGameLevels(MiniGames miniGame, int level){
        this.miniGame = miniGame;
        this.level = level;
    }
}
