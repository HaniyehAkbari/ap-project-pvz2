package com.pvz2.models.miniGame.beghouled;

import com.pvz2.models.enums.PlantType;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.levelSetup.LevelSetup;
import com.pvz2.models.world.mechanics.NormalMechanic;
import com.pvz2.models.zombie.wave.Wave;
import com.pvz2.models.zombie.wave.WaveManager;

import java.util.List;

public class BeghouledSetup implements LevelSetup {
    private final int rows;
    private final int cols;
    private final List<PlantType> availablePlantTypes;
    private final List<PlantUpgrade> upgrades;
    private final int targetScore;
    private List<Wave> waves;

    public BeghouledSetup(int rows, int cols, List<PlantType> availablePlantTypes,
                          List<PlantUpgrade> upgrades, int targetScore,
                          List<Wave> waves) {
        this.rows = rows;
        this.cols = cols;
        this.availablePlantTypes = availablePlantTypes;
        this.upgrades = upgrades;
        this.targetScore = targetScore;
        this.waves = waves;
    }


    @Override
    public void groundSetup(GameWorld world) {
        world.setConveyorMode(false);
        buildGrid(world, rows, cols);

        BeghouledMechanics mechanics = new BeghouledMechanics(availablePlantTypes, upgrades, targetScore);
        world.addMechanic(mechanics);


        WaveManager waveManager = new WaveManager(waves);
        waveManager.setRepeatForever(true);
        world.addMechanic(new NormalMechanic(waveManager));

        world.registerZombieKillListener(waveManager::onZombieKilled);


        mechanics.fillRandomPlants(world);
    }

    @Override
    public boolean requirePlantSelection() {
        return false;
    }
}
