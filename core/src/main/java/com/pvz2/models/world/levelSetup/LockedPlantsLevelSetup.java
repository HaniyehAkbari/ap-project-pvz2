package com.pvz2.models.world.levelSetup;

import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.card.PlantCardFactory;
import com.pvz2.models.world.ChapterWorld.FrostbiteCavesWorld;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.mechanics.NormalMechanic;
import com.pvz2.models.world.mechanics.SunSpawnMechanic;
import com.pvz2.models.zombie.wave.Wave;
import com.pvz2.models.zombie.wave.WaveManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LockedPlantsLevelSetup implements LevelSetup {
    private int rows;
    private int cols;
    private List<Wave> waves;

    public LockedPlantsLevelSetup(int rows,
                                  int cols,
                                  List<Wave> waves) {
        this.rows = rows;
        this.cols = cols;
        this.waves = waves;
    }


    @Override
    public void groundSetup(GameWorld world) {
        world.setConveyorMode(false);
        buildGrid(world, rows, cols);

        User user = App.getCurrentUser();
        if(user != null && !user.getUnlockedPlantsLevels().isEmpty()){
            List<PlantType> unlockedPlants = new ArrayList<>(user.getUnlockedPlantsLevels().keySet());

            unlockedPlants.remove(PlantType.IMITATER);

            Collections.shuffle(unlockedPlants);

            int countToLock = Math.min(3, unlockedPlants.size());
            List<PlantType> randomLockedPlants = new ArrayList<>(unlockedPlants.subList(0, countToLock));

            if (world instanceof FrostbiteCavesWorld frostbiteWorld) {
                frostbiteWorld.setLockedPlants(randomLockedPlants);
            }

            for (PlantType type : randomLockedPlants) {
                int level = user.getUnlockedPlantsLevels().getOrDefault(type, 1);
                world.getPlantLists().add(
                        PlantCardFactory.createCard(type, level)
                );
            }
        }

        WaveManager waveManager = new WaveManager(waves);
        world.addMechanic(new NormalMechanic(waveManager));
        world.addMechanic(new SunSpawnMechanic());
        world.registerZombieKillListener(() -> waveManager.onZombieKilled());
    }

    @Override
    public boolean requirePlantSelection() {
        return true;
    }
}
