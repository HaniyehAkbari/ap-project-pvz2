package com.pvz2.models.mupoint;

import com.pvz2.models.miniGame.MiniGameWorld;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.levelSetup.LevelSetup;
import com.pvz2.models.world.levelSetup.NormalLevelSetup;
import com.pvz2.models.world.loseCondition.LoseCondition;
import com.pvz2.models.world.loseCondition.NormalLose;
import com.pvz2.models.world.winCondition.NormalWin;
import com.pvz2.models.world.winCondition.WinCondition;
import com.pvz2.models.zombie.wave.Wave;
import com.pvz2.models.zombie.wave.WaveSpawnEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MuPointLevel {
    public static GameWorld createMuPointLevel() {
        int rows = 5;
        int cols = 9;

        List<WaveSpawnEntry> availableZombies = List.of(
            new WaveSpawnEntry("ZombieDefault", 100),
            new WaveSpawnEntry("ZombieArmor1", 200),
            new WaveSpawnEntry("ZombieArmor2", 300),
            new WaveSpawnEntry("ZombieArmor4", 400),
            new WaveSpawnEntry("ZombieTombRaiser", 300),
            new WaveSpawnEntry("ZombieRa", 100),
            new WaveSpawnEntry("ZombieExplorer", 250),
            new WaveSpawnEntry("ZombieExplorer", 250),
            new WaveSpawnEntry("ZombieBarrelRoller", 500)
        );

        List<Wave> waves = Wave.generateWaves(5, 600,
                availableZombies, 3, new Random(10));

        LevelSetup levelSetup = new NormalLevelSetup(rows, cols, waves);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();

        GameWorld world = new MiniGameWorld(
                levelSetup,
                new ArrayList<>(List.of(loseCondition)),
                winCondition,
                new ArrayList<>(),
            MuPointLevel::createMuPointLevel
        );
        world.setWillUnlockLevel(false);

        MupointManager mupointManager = new MupointManager();


        world.setMupointManager(mupointManager);
        return world;
    }
}
