package com.pvz2.models.miniGame;

import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.miniGame.IZombie.IZombieLevel;
import com.pvz2.models.miniGame.IZombie.IZombieLose;
import com.pvz2.models.miniGame.IZombie.IZombieSetup;
import com.pvz2.models.miniGame.IZombie.IZombieWin;
import com.pvz2.models.miniGame.beghouled.BeghouledSetup;
import com.pvz2.models.miniGame.beghouled.BeghouledWinCondition;
import com.pvz2.models.miniGame.beghouled.PlantUpgrade;
import com.pvz2.models.miniGame.bowling.BowlingSetup;
import com.pvz2.models.miniGame.vaseBreaker.VaseBreakerLevel;
import com.pvz2.models.miniGame.vaseBreaker.VaseBreakerLoseCondition;
import com.pvz2.models.miniGame.vaseBreaker.VaseBreakerSetup;
import com.pvz2.models.miniGame.vaseBreaker.VaseBreakerWinCondition;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.levelSetup.LevelSetup;
import com.pvz2.models.world.levelSetup.NormalLevelSetup;
import com.pvz2.models.world.loseCondition.LoseCondition;
import com.pvz2.models.world.loseCondition.NormalLose;
import com.pvz2.models.world.winCondition.NormalWin;
import com.pvz2.models.world.winCondition.WinCondition;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.zombie.ZombieFactory;
import com.pvz2.models.zombie.wave.Wave;
import com.pvz2.models.zombie.wave.WaveSpawnEntry;

import java.util.ArrayList;
import java.util.List;

public class MiniGameFactory {

    public static GameWorld createMiniGameLevel(MiniGames miniGame, int level) {
        return switch (miniGame) {
            case VASE_BREAKER -> switch (level) {
                case 1 -> createVaseBreakerLevel1();
                case 2 -> createVaseBreakerLevel2();
                case 3 -> createVaseBreakerLevel3();
                default -> throw new IllegalArgumentException("invalid level for vase breaker");
            };

            case BOWLING -> switch (level) {
                case 1 -> createBowlingLevel1();
                case 2 -> createBowlingLevel2();
                case 3 -> createBowlingLevel3();
                default -> throw new IllegalArgumentException("invalid level for Bowling");
            };

            case BEGHOULED -> switch (level) {
                case 1 -> createBeghouledLevel1();
                case 2 -> createBeghouledLevel2();
                case 3 -> createBeghouledLevel3();
                default -> throw new IllegalArgumentException("invalid level for Beghouled");
            };

            case I_ZOMBIE -> switch (level) {
                case 1 -> createIZombieLevel1();
                case 2 -> createIZombieLevel2();
                case 3 -> createIZombieLevel3();
                default -> throw new IllegalArgumentException("invalid level for I Zombie");
            };

            case ZOMBOTANY -> switch (level) {
                case 1 -> createZombotanyLevel1();
                case 2 -> createZombotanyLevel2();
                case 3 -> createZombotanyLevel3();
                default -> throw new IllegalArgumentException("invalid level for egypt chapter");
            };
        };

    }

    private static GameWorld createVaseBreakerLevel1() {
        int rows = 5, cols = 9;

        List<String> normalVaseZombies = List.of("ZombieDefault", "ZombieArmor1");
        List<String> giantVaseZombies = List.of("ZombieGargantuar");
        List<PlantType> possiblePlants = List.of(PlantType.PEASHOOTER, PlantType.WALL_NUT,
            PlantType.CHOMPER, PlantType.CHERRY_BOMB);

        LevelSetup levelSetup = new VaseBreakerSetup(rows, cols, normalVaseZombies, giantVaseZombies, possiblePlants);
        LoseCondition loseCondition = new VaseBreakerLoseCondition();
        WinCondition winCondition = new VaseBreakerWinCondition();
        winCondition.setCurrentLevel(MiniGameLevels.VASE_BREAKER_1);


        return new VaseBreakerLevel(
                levelSetup,
                new ArrayList<>(List.of(loseCondition)),
                winCondition,
                new ArrayList<>(),
            MiniGameFactory::createVaseBreakerLevel1
        );

    }

    private static GameWorld createVaseBreakerLevel2() {
        int rows = 5, cols = 9;

        List<String> normalVaseZombies = List.of("ZombieDefault", "ZombieArmor1");
        List<String> giantVaseZombies = List.of("ZombieGargantuar");
        List<PlantType> possiblePlants = List.of(PlantType.PEASHOOTER, PlantType.WALL_NUT, PlantType.SNOW_PEA);

        LevelSetup levelSetup = new VaseBreakerSetup(rows, cols, normalVaseZombies, giantVaseZombies, possiblePlants);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new VaseBreakerWinCondition();
        winCondition.setCurrentLevel(MiniGameLevels.VASE_BREAKER_2);


        return new VaseBreakerLevel(
                levelSetup,
                new ArrayList<>(List.of(loseCondition)),
                winCondition,
                new ArrayList<>(),
            MiniGameFactory::createVaseBreakerLevel2
        );

    }

    private static GameWorld createVaseBreakerLevel3() {
        int rows = 5, cols = 9;

        List<String> normalVaseZombies = List.of("ZombieArmor1", "ZombieWizard", "ZombieArmor2");
        List<String> giantVaseZombies = List.of("ZombieGargantuar", "ZombieDarkKing");
        List<PlantType> possiblePlants = List.of(PlantType.PEASHOOTER, PlantType.WALL_NUT,
                PlantType.SNOW_PEA, PlantType.CHOMPER);

        LevelSetup levelSetup = new VaseBreakerSetup(rows, cols, normalVaseZombies, giantVaseZombies, possiblePlants);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new VaseBreakerWinCondition();
        winCondition.setCurrentLevel(MiniGameLevels.VASE_BREAKER_3);


        return new VaseBreakerLevel(
                levelSetup,
                new ArrayList<>(List.of(loseCondition)),
                winCondition,
                new ArrayList<>(),
            MiniGameFactory::createVaseBreakerLevel1
        );

    }

    private static GameWorld createBowlingLevel1() {
        int rows = 5, cols = 9, redLineCol = 3;

        List<WaveSpawnEntry> zombies = List.of(
                new WaveSpawnEntry("ZombieDefault", 100),
                new WaveSpawnEntry("ZombieArmor1", 150)

        );

        LevelSetup levelSetup = new BowlingSetup(rows, cols, redLineCol, zombies, 3, 500);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();
        winCondition.setCurrentLevel(MiniGameLevels.BOWLING_1);

        return new MiniGameWorld(
                levelSetup,
                new ArrayList<>(List.of(loseCondition)),
                winCondition,
                new ArrayList<>(),
            MiniGameFactory::createBowlingLevel1
            );
    }

    private static GameWorld createBowlingLevel2() {
        int rows = 5, cols = 9, redLineCol = 3;

        List<WaveSpawnEntry> zombies = List.of(
                new WaveSpawnEntry("ZombieDefault", 100),
                new WaveSpawnEntry("ZombieArmor1", 150),
                new WaveSpawnEntry("ZombieArmor2", 150)
        );

        LevelSetup levelSetup = new BowlingSetup(rows, cols, redLineCol, zombies, 4, 250);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();
        winCondition.setCurrentLevel(MiniGameLevels.BOWLING_2);

        return new MiniGameWorld(
                levelSetup,
                new ArrayList<>(List.of(loseCondition)),
                winCondition,
                new ArrayList<>(),
            MiniGameFactory::createBowlingLevel2
        );

    }

    private static GameWorld createBowlingLevel3() {
        int rows = 5, cols = 9, redLineCol = 3;

        List<WaveSpawnEntry> zombies = List.of(
                new WaveSpawnEntry("ZombieArmor1", 150),
                new WaveSpawnEntry("ZombieNewspaper", 200),
                new WaveSpawnEntry("ZombieGargantuar", 400)
        );

        LevelSetup levelSetup = new BowlingSetup(rows, cols, redLineCol, zombies, 5, 350);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();
        winCondition.setCurrentLevel(MiniGameLevels.BOWLING_3);

        return new MiniGameWorld(
                levelSetup,
                new ArrayList<>(List.of(loseCondition)),
                winCondition,
                new ArrayList<>(),
            MiniGameFactory::createBowlingLevel3
        );
    }

    private static GameWorld createIZombieLevel1() {
        List<Zombie> availableZombies = List.of(
                new ZombieFactory().createZombie("ZombieArmor1"),
                new ZombieFactory().createZombie("ZombieArmor2"),
                new ZombieFactory().createZombie("ZombieArmor4"),
                new ZombieFactory().createZombie("ZombieBarrelRoller")
        );

        LevelSetup levelSetup = new IZombieSetup(5, 9, availableZombies);
        LoseCondition loseCondition = new IZombieLose();
        WinCondition winCondition = new IZombieWin();
        winCondition.setCurrentLevel(MiniGameLevels.I_ZOMBIE_1);

        return new IZombieLevel(
                levelSetup,
                new ArrayList<>(List.of(loseCondition)),
                winCondition,
                new ArrayList<>()
        );
    }

    private static GameWorld createIZombieLevel2() {
        List<Zombie> availableZombies = List.of(
                new ZombieFactory().createZombie("ZombieDefault"),
                new ZombieFactory().createZombie("ZombieArmor1"),
                new ZombieFactory().createZombie("ZombieArmor3")
        );

        LevelSetup levelSetup = new IZombieSetup(5, 9, availableZombies);
        LoseCondition loseCondition = new IZombieLose();
        WinCondition winCondition = new IZombieWin();
        winCondition.setCurrentLevel(MiniGameLevels.I_ZOMBIE_2);

        return new IZombieLevel(
                levelSetup,
                new ArrayList<>(List.of(loseCondition)),
                winCondition,
                new ArrayList<>()
        );
    }

    private static GameWorld createIZombieLevel3() {
        List<Zombie> availableZombies = List.of(
                new ZombieFactory().createZombie("ZombieArmor1"),
                new ZombieFactory().createZombie("ZombieDarkImpDragon")
        );

        LevelSetup levelSetup = new IZombieSetup(5, 9, availableZombies);
        LoseCondition loseCondition = new IZombieLose();
        WinCondition winCondition = new IZombieWin();
        winCondition.setCurrentLevel(MiniGameLevels.I_ZOMBIE_3);

        return new IZombieLevel(
                levelSetup,
                new ArrayList<>(List.of(loseCondition)),
                winCondition,
                new ArrayList<>()
        );
    }

    private static GameWorld createBeghouledLevel1() {
        List<PlantType> plants = new ArrayList<>(List.of(
                PlantType.PEASHOOTER, PlantType.STARFRUIT, PlantType.WALL_NUT,
                PlantType.CABBAGE_PULT, PlantType.MELON_PULT
        ));


        List<PlantUpgrade> upgrades = List.of(
                new PlantUpgrade(PlantType.PEASHOOTER, PlantType.REPEATER, 500),
                new PlantUpgrade(PlantType.WALL_NUT, PlantType.TALL_NUT, 500)
        );

        ensurePlantsUnlocked(plants);
        ensurePlantsUnlocked(upgrades.stream().map(PlantUpgrade::getTo).toList());

        List<WaveSpawnEntry> zombies = List.of(
                new WaveSpawnEntry("ZombieDefault", 100)
        );
        List<Wave> waves = Wave.generateWaves(6, 500, zombies, 10);


        LevelSetup levelSetup = new BeghouledSetup(5, 9, plants, upgrades, 50, waves);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new BeghouledWinCondition();
        winCondition.setCurrentLevel(MiniGameLevels.BEGHOULED_1);

        return new MiniGameWorld(
                levelSetup,
                new ArrayList<>(List.of(loseCondition)),
                winCondition,
                new ArrayList<>(),
            MiniGameFactory::createBeghouledLevel1
        );
    }

    private static GameWorld createBeghouledLevel2() {
        List<PlantType> plants = new ArrayList<>(List.of(
                PlantType.PEASHOOTER, PlantType.CHOMPER, PlantType.WALL_NUT,
                PlantType.CABBAGE_PULT, PlantType.GARLIC
        ));
        List<PlantUpgrade> upgrades = List.of(
                new PlantUpgrade(PlantType.PEASHOOTER, PlantType.REPEATER, 500),
                new PlantUpgrade(PlantType.WALL_NUT, PlantType.TALL_NUT, 500),
                new PlantUpgrade(PlantType.CABBAGE_PULT, PlantType.MELON_PULT, 1000)
        );
        List<WaveSpawnEntry> zombies = List.of(
                new WaveSpawnEntry("ZombieDefault", 100),
                new WaveSpawnEntry("ZombieArmor1", 150),
                new WaveSpawnEntry("ZombieArmor3", 200)
        );
        List<Wave> waves = Wave.generateWaves(6, 500, zombies, 10);

        LevelSetup levelSetup = new BeghouledSetup(5, 9, plants, upgrades, 60, waves);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new BeghouledWinCondition();
        winCondition.setCurrentLevel(MiniGameLevels.BEGHOULED_2);

        return new MiniGameWorld(
                levelSetup,
                new ArrayList<>(List.of(loseCondition)),
                winCondition,
                new ArrayList<>(),
            MiniGameFactory::createBeghouledLevel2
        );

    }

    private static GameWorld createBeghouledLevel3() {
        List<PlantType> plants = new ArrayList<>(List.of(
                PlantType.PEASHOOTER, PlantType.CITRON, PlantType.WALL_NUT,
                PlantType.CABBAGE_PULT, PlantType.MELON_PULT
        ));
        List<PlantUpgrade> upgrades = List.of(
                new PlantUpgrade(PlantType.PEASHOOTER, PlantType.REPEATER, 500),
                new PlantUpgrade(PlantType.WALL_NUT, PlantType.TALL_NUT, 500),
                new PlantUpgrade(PlantType.CABBAGE_PULT, PlantType.MELON_PULT, 1000),
                new PlantUpgrade(PlantType.MELON_PULT, PlantType.WINTER_MELON, 750)
        );
        List<WaveSpawnEntry> zombies = List.of(
                new WaveSpawnEntry("ZombieArmor1", 150),
                new WaveSpawnEntry("ZombieGargantuar", 400)
        );
        List<Wave> waves = Wave.generateWaves(6, 500, zombies, 10);

        LevelSetup levelSetup = new BeghouledSetup(5, 9, plants, upgrades, 80, waves);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new BeghouledWinCondition();
        winCondition.setCurrentLevel(MiniGameLevels.BEGHOULED_3);

        return new MiniGameWorld(
                levelSetup,
                new ArrayList<>(List.of(loseCondition)),
                winCondition,
                new ArrayList<>(),
            MiniGameFactory::createBeghouledLevel3
        );
    }

    private static GameWorld createZombotanyLevel1() {
        List<WaveSpawnEntry> zombies = List.of(
                new WaveSpawnEntry("ZombiePeashooter", 150),
                new WaveSpawnEntry("ZombieWallnut", 200)
        );

        LevelSetup levelSetup = new
                NormalLevelSetup(5, 9, Wave.generateWaves(3, 200, zombies, 40));
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();
        winCondition.setCurrentLevel(MiniGameLevels.ZOMBOTANY_1);

        return new MiniGameWorld(
                levelSetup,
                new ArrayList<>(List.of(loseCondition)),
                winCondition,
                new ArrayList<>(),
            MiniGameFactory::createZombotanyLevel1
        );
    }

    private static GameWorld createZombotanyLevel2() {
        List<WaveSpawnEntry> zombies = List.of(
                new WaveSpawnEntry("ZombiePeashooter", 150),
                new WaveSpawnEntry("ZombieWallnut", 200),
                new WaveSpawnEntry("ZombieSquash", 150)
        );

        LevelSetup levelSetup = new NormalLevelSetup(5, 9,
                Wave.generateWaves(4, 300, zombies, 40));
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();
        winCondition.setCurrentLevel(MiniGameLevels.ZOMBOTANY_2);

        return new MiniGameWorld(
                levelSetup,
                new ArrayList<>(List.of(loseCondition)),
                winCondition,
                new ArrayList<>(),
            MiniGameFactory::createZombotanyLevel2
        );
    }

    private static GameWorld createZombotanyLevel3() {
        List<WaveSpawnEntry> zombies = List.of(
                new WaveSpawnEntry("ZombiePeashooter", 150),
                new WaveSpawnEntry("ZombieWallnut", 200),
                new WaveSpawnEntry("ZombieJalapeno", 150),
                new WaveSpawnEntry("ZombieSquash", 150)
        );

        LevelSetup levelSetup = new NormalLevelSetup(5, 9,
                Wave.generateWaves(5, 400, zombies, 20));
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();
        winCondition.setCurrentLevel(MiniGameLevels.ZOMBOTANY_3);

        return new MiniGameWorld(
                levelSetup,
                new ArrayList<>(List.of(loseCondition)),
                winCondition,
                new ArrayList<>(),
            MiniGameFactory::createZombotanyLevel3
        );
    }

    private static void ensurePlantsUnlocked(List<PlantType> plantTypes) {
        User user = App.getCurrentUser();
        for (PlantType type : plantTypes) {
            if (!user.getUnlockedPlantsLevels().containsKey(type)) {
                user.unlockPlant(type);
            }
        }
    }

}
