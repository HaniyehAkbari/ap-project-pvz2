package com.pvz2.models.world.levelSetup;

import com.pvz2.models.plant.card.PlantCard;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.mechanics.NormalMechanic;
import com.pvz2.models.zombie.wave.Wave;
import com.pvz2.models.zombie.wave.WaveManager;

import java.util.List;

public class PlantWhatYouGetLevelSetup implements LevelSetup {
    private int rows;
    private int cols;
    private List<Wave> waves;
    private List<PlantCard> availablePlants;

    public PlantWhatYouGetLevelSetup(int rows, int cols, List<Wave> waves, List<PlantCard> availablePlants) {
        this.rows = rows;
        this.cols = cols;
        this.waves = waves;
        this.availablePlants = availablePlants;
    }

    @Override
    public void groundSetup(GameWorld world) {
        world.setConveyorMode(false);
        buildGrid(world, rows, cols);
        world.setSun(800);

        world.getPlantLists().addAll(availablePlants);

        WaveManager waveManager = new WaveManager(waves, false);
        world.addMechanic(new NormalMechanic(waveManager));
        world.setPlantingPhase(true);
    }

    @Override
    public boolean requirePlantSelection() {
        return true;
    }


}
