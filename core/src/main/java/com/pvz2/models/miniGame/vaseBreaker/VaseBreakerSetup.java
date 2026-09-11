package com.pvz2.models.miniGame.vaseBreaker;

import com.pvz2.models.enums.PlantType;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.levelSetup.LevelSetup;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.zombie.ZombieFactory;

import java.util.List;
import java.util.Random;

public class VaseBreakerSetup implements LevelSetup {
    private final List<String> normalVaseZombies;
    private final List<String> giantVaseZombies;
    private final List<PlantType> possiblePlants;
    private int rows;
    private int cols;

    public VaseBreakerSetup(int rows, int cols,
                            List<String> normalVaseZombies,
                            List<String> giantVaseZombies,
                            List<PlantType> possiblePlants) {
        this.rows = rows;
        this.cols = cols;
        this.normalVaseZombies = normalVaseZombies;
        this.giantVaseZombies = giantVaseZombies;
        this.possiblePlants = possiblePlants;
    }

    @Override
    public void groundSetup(GameWorld world) {
        world.setConveyorMode(false);
        buildGrid(world, rows, cols);

        VaseBreakerLevel level = (VaseBreakerLevel) world;

        Random random = new Random();

        for (int r = 0; r < rows; r++) {
            for (int c = 4; c < cols; c++) {
                VaseType type = getRandomVaseType(random);

                Zombie hiddenZombie = null;
                SeedPacket hiddenSeed = null;

                if (type == VaseType.PLANT) {
                    PlantType randomPlant = possiblePlants.get(random.nextInt(possiblePlants.size()));
                    float spawnX = c * 100 + 50;
                    float spawnY = r * 100 + 50;
                    hiddenSeed = new SeedPacket(spawnX, spawnY, randomPlant);
                } else {
                    hiddenZombie = createZombieForVase(type, random);
                }

                Vase vase = new Vase(r, c, type, hiddenZombie, hiddenSeed);
                level.addVase(vase);
            }
        }
    }

    @Override
    public boolean requirePlantSelection() {
        return false;
    }

    private VaseType getRandomVaseType(Random random) {
        int r = random.nextInt(10);
        if (r < 4) return VaseType.PLANT;
        if (r < 9) return VaseType.NORMAL;
        return VaseType.GIANT;
    }


    private Zombie createZombieForVase(VaseType type, Random random) {
        ZombieFactory factory = new ZombieFactory();
        List<String> pool = (type == VaseType.GIANT) ? giantVaseZombies : normalVaseZombies;
        if (pool == null || pool.isEmpty()) return null;
        String alias = pool.get(random.nextInt(pool.size()));
        return factory.createZombie(alias);
    }
}
