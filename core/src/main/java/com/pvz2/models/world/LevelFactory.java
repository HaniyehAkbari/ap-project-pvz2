package com.pvz2.models.world;

import com.pvz2.models.enums.Chapter;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.card.PlantCard;
import com.pvz2.models.world.ChapterWorld.AncientEgyptWorld;
import com.pvz2.models.world.ChapterWorld.BigWaveBeachWorld;
import com.pvz2.models.world.ChapterWorld.DarkAgesWorld;
import com.pvz2.models.world.ChapterWorld.FrostbiteCavesWorld;
import com.pvz2.models.world.levelSetup.*;
import com.pvz2.models.world.loseCondition.*;
import com.pvz2.models.world.mechanics.DarkAgesMechanic;
import com.pvz2.models.world.mechanics.Mechanic;
import com.pvz2.models.world.winCondition.NormalWin;
import com.pvz2.models.world.winCondition.TimedWarWin;
import com.pvz2.models.world.winCondition.WinCondition;
import com.pvz2.models.zombie.wave.Wave;
import com.pvz2.models.zombie.wave.WaveSpawnEntry;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LevelFactory {
    public static GameWorld createLevel(Chapter chapter, int level) {
        return switch (chapter) {
            case EGYPT -> switch (level) {
                case 1 -> createAncientEgyptLevel1();
                case 2 -> createAncientEgyptLevel2();
                case 3 -> createAncientEgyptLevel3();
                case 4 -> createAncientEgyptLevel4();
                default -> throw new IllegalArgumentException("invalid level for egypt chapter");
            };
            case BIG_WAVE_BEACH -> switch (level) {
                case 1 -> createBigWaveBeachLevel1();
                case 2 -> createBigWaveBeachLevel2();
                case 3 -> createBigWaveBeachLevel3();
                case 4 -> createBigWaveBeachLevel4();
                default ->
                    throw new IllegalArgumentException("invalid level for big wave beach chapter");
            };
            case DARK_AGES -> switch (level) {
                case 1 -> createDarkAgesLevel1();
                case 2 -> createDarkAgesLevel2();
                case 3 -> createDarkAgesLevel3();
                case 4 -> createDarkAgesLevel4();
                default -> throw new IllegalArgumentException("invalid level for dark ages");
            };
            case FROSTBITE_CAVES -> switch (level) {
                case 1 -> createFrostbiteCavesLevel1();
                case 2 -> createFrostbiteCavesLevel2();
                case 3 -> createFrostbiteCavesLevel3();
                case 4 -> createFrostbiteCavesLevel4();
                default -> throw new IllegalArgumentException("invalid level for frostbite caves");
            };
        };
    }

    private static GameWorld createAncientEgyptLevel1() {
        int rows = 5;
        int cols = 9;
        List<WaveSpawnEntry> availableZombies = List.of(
            new WaveSpawnEntry("ZombieDefault", 250),
            new WaveSpawnEntry("ZombieArmor1", 500),
            new WaveSpawnEntry("ZombieArmor2", 630),
            new WaveSpawnEntry("ZombieRa", 505),
            new WaveSpawnEntry("ZombieExplorer", 510)
        );
        List<Wave> waves = Wave.generateWaves(10, 500, availableZombies, 1);
        LevelSetup levelSetup = new NormalLevelSetup(rows, cols, waves);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();

        AncientEgyptWorld world = new AncientEgyptWorld(
            levelSetup,
            new ArrayList<>(List.of(loseCondition)),
            winCondition,
            new ArrayList<>()
        );
        world.setCurrentChapter(Chapter.EGYPT);
        world.setWillUnlockLevel(true);
        world.setStartingDialogs(List.of(
            "DAVE:Greetings, neighbor! I'm Crazy Dave!",
            "PENNY:User Dave, my scanners detect a 99.9% probability of an imminent Zombie attack.",
            "DAVE:Why do they call me Crazy Dave? Because I put a pot on my head!",
            "PENNY:That... does not logically correlate to the current threat level.",
            "DAVE:Look! Mummies are coming to eat your hot sauce! Plant these Peashooters!",
            "DAVE:WABBA WABBA RAAGH!"
        ));
        return world;
    }

    private static GameWorld createAncientEgyptLevel2() {
        int rows = 5;
        int cols = 9;
        List<WaveSpawnEntry> availableZombies = List.of(
            new WaveSpawnEntry("ZombieDefault", 100),
            new WaveSpawnEntry("ZombieArmor1", 250),
            new WaveSpawnEntry("ZombieTombRaiser", 250),
            new WaveSpawnEntry("ZombieRa", 250),
            new WaveSpawnEntry("ZombiePiano", 700)
        );
        List<Wave> waves = Wave.generateWaves(10, 190, availableZombies, 1);
        LevelSetup levelSetup = new DeadLineLevelSetup(rows, cols, 4, waves);
        LoseCondition loseCondition = new DeadLineLose(4);
        System.out.println("if zombie pass deadLine (col = 4), you will lose");
        WinCondition winCondition = new NormalWin();
        AncientEgyptWorld world = new AncientEgyptWorld(
            levelSetup,
            new ArrayList<>(List.of(loseCondition)),
            winCondition,
            new ArrayList<>()
        );
        world.setCurrentChapter(Chapter.EGYPT);
        world.setWillUnlockLevel(true);
        world.setStartingDialogs(List.of(
            "DAVE:Whoa! See that red flower line on the sand?",
            "DAVE:If a zombie steps over that line, my taco will fall on the ground!",
            "DAVE:DON'T LET THEM CROSS IT!"
        ));
        return world;
    }

    private static GameWorld createAncientEgyptLevel3() {
        int rows = 5;
        int cols = 9;
        List<WaveSpawnEntry> availableZombies = List.of(
            new WaveSpawnEntry("ZombieDefault", 100),
            new WaveSpawnEntry("ZombieArmor1", 220),
            new WaveSpawnEntry("ZombieArmor2", 300),
            new WaveSpawnEntry("ZombieArmor4", 700),
            new WaveSpawnEntry("ZombieTombRaiser", 300),
            new WaveSpawnEntry("ZombieRa", 260),
            new WaveSpawnEntry("ZombieExplorer", 250),
            new WaveSpawnEntry("ZombieBarrelRoller", 700)
        );
        List<Wave> waves = Wave.generateWaves(7, 200, availableZombies, 1);
        List<PlantCard> plantCards = List.of(
            new PlantCard(PlantType.PEASHOOTER, 0, 0),
            new PlantCard(PlantType.PEA_POD, 0, 0),
            new PlantCard(PlantType.WALL_NUT, 0, 0),
            new PlantCard(PlantType.STARFRUIT, 0, 0),
            new PlantCard(PlantType.XSHOT, 0, 0),
            new PlantCard(PlantType.CHERRY_BOMB, 0, 0),
            new PlantCard(PlantType.MELON_PULT, 0, 0),
            new PlantCard(PlantType.SQUASH, 0, 0),
            new PlantCard(PlantType.JALAPENO, 0, 0),
            new PlantCard(PlantType.SNOW_PEA, 0, 0),
            new PlantCard(PlantType.REPEATER, 0, 0)
        );
        LevelSetup levelSetup = new ConveyorLevelSetup(rows, cols, waves, plantCards);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();
        AncientEgyptWorld world = new AncientEgyptWorld(
            levelSetup,
            new ArrayList<>(List.of(loseCondition)),
            winCondition,
            new ArrayList<>()
        );
        world.setCurrentChapter(Chapter.EGYPT);
        world.setWillUnlockLevel(true);
        world.setStartingDialogs(List.of(
            "PENNY:Analyzing anomaly: localized temporal distortion is providing flora directly.",
            "PENNY:Deploy them rapidly to maintain defensive integrity."
        ));
        return world;
    }

    private static GameWorld createAncientEgyptLevel4() {
        int rows = 5;
        int cols = 9;
        List<WaveSpawnEntry> availableZombies = List.of(
            new WaveSpawnEntry("ZombieDefault", 180),
            new WaveSpawnEntry("ZombieArmor1", 200),
            new WaveSpawnEntry("ZombieDarkArmor3", 600),
            new WaveSpawnEntry("ZombieTombRaiser", 300),
            new WaveSpawnEntry("ZombieRa", 250),
            new WaveSpawnEntry("ZombieExplorer", 300),
            new WaveSpawnEntry("ZombieBarrelRoller", 500),
            new WaveSpawnEntry("ZombieGargantuar", 700)

        );
        List<Wave> waves = Wave.generateWaves(10, 180, availableZombies, 1);
        LevelSetup levelSetup = new NormalLevelSetup(rows, cols, waves);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();
        AncientEgyptWorld world = new AncientEgyptWorld(
            levelSetup,
            new ArrayList<>(List.of(loseCondition)),
            winCondition,
            new ArrayList<>()
        );
        world.setCurrentChapter(Chapter.EGYPT);
        world.setWillUnlockLevel(true);
        world.setStartingDialogs(List.of(
            "PENNY:Scanning indicates a massive wave of undead approaching.",
            "DAVE:One last push, neighbor! The mummies brought their big sandy friends!",
            "DAVE:Let's show them the power of green!"
        ));
        return world;
    }

    private static GameWorld createBigWaveBeachLevel1() {
        int rows = 5;
        int cols = 9;
        List<WaveSpawnEntry> availableZombies = List.of(
            new WaveSpawnEntry("ZombieDefault", 100),
            new WaveSpawnEntry("ZombieArmor1", 200),
            new WaveSpawnEntry("ZombieArmor2", 300),
            new WaveSpawnEntry("ZombieBeachSnorkel", 200),
            new WaveSpawnEntry("ZombieBeachOctopus", 400)
         );
        List<Wave> waves = Wave.generateWaves(6, 180, availableZombies, 1);
        LevelSetup levelSetup = new BigWaveBeachLevelSetup(6, rows, cols, waves);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();
        BigWaveBeachWorld world = new BigWaveBeachWorld(
            levelSetup,
            new ArrayList<>(List.of(loseCondition)),
            winCondition,
            new ArrayList<>()
        );
        world.setCurrentChapter(Chapter.BIG_WAVE_BEACH);
        world.setWillUnlockLevel(true);
        world.setStartingDialogs(List.of(
            "DAVE:Surf's up, neighbor! Welcome to Big Wave Beach!",
            "PENNY:Warning: Saltwater environment detected. Rust probability increasing.",
            "DAVE:Don't forget your sunscreen... and your octo-repellent!",
            "PENNY:Zombies here love seafood and BRAINS!"
        ));

        return world;
    }

    private static GameWorld createBigWaveBeachLevel2() {
        int rows = 5;
        int cols = 9;
        List<WaveSpawnEntry> availableZombies = List.of(
            new WaveSpawnEntry("ZombieDefault", 100),
            new WaveSpawnEntry("ZombieArmor1", 250),
            new WaveSpawnEntry("ZombieDarkArmor3", 500),
            new WaveSpawnEntry("ZombieBeachFisherman", 400),
            new WaveSpawnEntry("ZombieBeachSnorkel", 300)

        );
        List<Wave> waves = Wave.generateWaves(10, 180, availableZombies, 1);
        LevelSetup levelSetup = new BigWaveBeachLevelSetup(4, rows, cols, waves);
        LoseCondition loseCondition1 = new NormalLose();
        WinCondition winCondition = new NormalWin();
        BigWaveBeachWorld world = new BigWaveBeachWorld(
            levelSetup,
            new ArrayList<>(List.of( loseCondition1)),
            winCondition,
            new ArrayList<>()
        );
        world.setCurrentChapter(Chapter.BIG_WAVE_BEACH);
        world.setWillUnlockLevel(true);
        world.setStartingDialogs(List.of(
            "DAVE:Look at those endangered plants in the sand!",
            "DAVE:They are like my pet rocks, but greener!",
            "DAVE:Protect them with your life... or with Wall-nuts!"
        ));
        return world;
    }

    private static GameWorld createBigWaveBeachLevel3() {
        int rows = 5;
        int cols = 9;
        List<WaveSpawnEntry> availableZombies = List.of(
            new WaveSpawnEntry("ZombieDefault", 100),
            new WaveSpawnEntry("ZombieArmor2", 300),
            new WaveSpawnEntry("ZombieBeachFisherman", 400),
            new WaveSpawnEntry("ZombieBeachSnorkel", 200),
            new WaveSpawnEntry("ZombieBeachOctopus", 500),
            new WaveSpawnEntry("ZombieArcade", 500)
        );
        List<Wave> waves = Wave.generateWaves(10, 180, availableZombies, 1);
        LevelSetup levelSetup = new BigWaveBeachLevelSetup(5, rows, cols, waves);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();
        BigWaveBeachWorld world = new BigWaveBeachWorld(
            levelSetup,
            new ArrayList<>(List.of(loseCondition)),
            winCondition,
            new ArrayList<>()
        );
        world.setCurrentChapter(Chapter.BIG_WAVE_BEACH);
        world.setWillUnlockLevel(true);
        world.setStartingDialogs(List.of(
            "PENNY:Alert: Rising tide patterns indicate severe temporal constraints.",
            "PENNY:Eliminate hostile entities before the temporal window collapses."
        ));
        return world;
    }

    private static GameWorld createBigWaveBeachLevel4() {
        int rows = 5;
        int cols = 9;
        List<WaveSpawnEntry> availableZombies = List.of(
            new WaveSpawnEntry("ZombieDefault", 100),
            new WaveSpawnEntry("ZombieArmor1", 250),
            new WaveSpawnEntry("ZombieBeachFisherman", 400),
            new WaveSpawnEntry("ZombieBeachSnorkel", 300),
            new WaveSpawnEntry("ZombieNewspaper", 500),
            new WaveSpawnEntry("ZombieArcade", 600),
            new WaveSpawnEntry("ZombieModernAllStar", 700)
        );
        List<Wave> waves = Wave.generateWaves(10, 200, availableZombies, 1);
        LevelSetup levelSetup = new BigWaveBeachLevelSetup(3, rows, cols, waves);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();
        BigWaveBeachWorld world = new BigWaveBeachWorld(
            levelSetup,
            new ArrayList<>(List.of(loseCondition)),
            winCondition,
            new ArrayList<>()
        );
        world.setCurrentChapter(Chapter.BIG_WAVE_BEACH);
        world.setWillUnlockLevel(true);
        world.setStartingDialogs(List.of(
            "DAVE:The ultimate beach party!",
            "PENNY:Detecting extreme hostile acoustic vibrations.",
            "DAVE:The zombies brought the boombox... and the teeth!",
            "DAVE:Don't forget your swimming trunks!"
        ));
        return world;
    }

    private static GameWorld createDarkAgesLevel1() {
        int rows = 5;
        int cols = 9;
        List<WaveSpawnEntry> availableZombies = List.of(
            new WaveSpawnEntry("ZombieDefault", 140),
            new WaveSpawnEntry("ZombieArmor1", 300),
            new WaveSpawnEntry("ZombieDarkJuggler", 600),
            new WaveSpawnEntry("ZombieWizard", 400),
            new WaveSpawnEntry("ZombieModernAllStar", 700)

        );
        List<Wave> waves = Wave.generateWaves(15, 180, availableZombies, 1);
        LevelSetup levelSetup = new NormalLevelSetup(rows, cols, waves);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();
        ArrayList<Mechanic> mechanics = new ArrayList<>();
        mechanics.add(new DarkAgesMechanic());
        DarkAgesWorld world = new DarkAgesWorld(
            levelSetup,
            new ArrayList<>(List.of(loseCondition)),
            winCondition,
            mechanics
        );
        world.setCurrentChapter(Chapter.DARK_AGES);
        world.setWillUnlockLevel(true);
        world.setStartingDialogs(List.of(
            "DAVE:Spooky! It's dark and scary out here!",
            "PENNY:Solar energy severely depleted. No sun drops from the sky at night.",
            "DAVE:Use Mushrooms, neighbor! They thrive in the shadow!"
        ));
        return world;
    }

    private static GameWorld createDarkAgesLevel2() {
        int rows = 5;
        int cols = 9;
        List<WaveSpawnEntry> availableZombies = List.of(
            new WaveSpawnEntry("ZombieDefault", 100),
            new WaveSpawnEntry("ZombieDarkImpDragon", 120),
            new WaveSpawnEntry("ZombieDarkKing", 500),
            new WaveSpawnEntry("ZombieLostCityJane", 150),
            new WaveSpawnEntry("ZombieCrystalSkull", 400)

        );
        List<Wave> waves = Wave.generateWaves(8, 100, availableZombies, 1);
        LevelSetup levelSetup = new NormalLevelSetup(rows, cols, waves);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();
        ArrayList<Mechanic> mechanics = new ArrayList<>();
        mechanics.add(new DarkAgesMechanic());
        DarkAgesWorld world = new DarkAgesWorld(
            levelSetup,
            new ArrayList<>(List.of(loseCondition)),
            winCondition,
            mechanics
        );
        world.setCurrentChapter(Chapter.DARK_AGES);
        world.setWillUnlockLevel(true);
        world.setStartingDialogs(List.of(
            "DAVE:Night Ops! Watch out for Wizard Zombies!",
            "DAVE:They turn my favorite plants into SHEEP!",
            "DAVE:BAAAH! See?!"
        ));
        return world;
    }

    private static GameWorld createDarkAgesLevel3() {
        int rows = 5;
        int cols = 9;
        List<WaveSpawnEntry> availableZombies = List.of(
            new WaveSpawnEntry("ZombieDefault", 100),
            new WaveSpawnEntry("ZombieArmor1", 200),
            new WaveSpawnEntry("ZombieDarkArmor3", 600),
            new WaveSpawnEntry("ZombieDarkJuggler", 450),
            new WaveSpawnEntry("ZombieDarkKing", 800),
            new WaveSpawnEntry("ZombieProspector", 500)

        );
        List<Wave> waves = Wave.generateWaves(12, 120, availableZombies, 1);
        LevelSetup levelSetup = new NormalLevelSetup(rows, cols, waves);
        LoveYourPlantsLose loseCondition = new LoveYourPlantsLose(5);
        WinCondition winCondition = new NormalWin();
        ArrayList<Mechanic> mechanics = new ArrayList<>();
        mechanics.add(new DarkAgesMechanic());
        DarkAgesWorld world = new DarkAgesWorld(
            levelSetup,
            new ArrayList<>(List.of(loseCondition)),
            winCondition,
            mechanics
        );
        loseCondition.setGameListener(world);
        world.setCurrentChapter(Chapter.DARK_AGES);
        world.setWillUnlockLevel(true);
        world.setStartingDialogs(List.of(
            "PENNY:Restriction: Maximum acceptable botanical casualties is set to 5.",
            "PENNY:Warning: Exceeding this limit will result in catastrophic failure."
        ));
        return world;
    }

    private static GameWorld createDarkAgesLevel4() {
        int rows = 5;
        int cols = 9;
        List<WaveSpawnEntry> availableZombies = List.of(
            new WaveSpawnEntry("ZombieDefault", 100),
            new WaveSpawnEntry("ZombieDarkImpDragon", 110),
            new WaveSpawnEntry("ZombieDarkArmor3", 300),
            new WaveSpawnEntry("ZombieWizard", 200),
            new WaveSpawnEntry("ZombieDarkKing", 600)
        );
        List<Wave> waves = Wave.generateWaves(10, 100, availableZombies, 1);
        LevelSetup levelSetup = new NormalLevelSetup(rows, cols, waves);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();
        ArrayList<Mechanic> mechanics = new ArrayList<>();
        mechanics.add(new DarkAgesMechanic());
        DarkAgesWorld world = new DarkAgesWorld(
            levelSetup,
            new ArrayList<>(List.of(loseCondition)),
            winCondition,
            mechanics
        );
        world.setCurrentChapter(Chapter.DARK_AGES);
        world.setWillUnlockLevel(true);
        world.setStartingDialogs(List.of(
            "PENNY:Ambient light levels reaching absolute zero.",
            "DAVE:The darkest night of all! I hear clanking armor!",
            "DAVE:Did somebody order a dragon?"
        ));
        return world;
    }

    private static GameWorld createFrostbiteCavesLevel1() {
        int rows = 5;
        int cols = 9;
        List<WaveSpawnEntry> availableZombies = List.of(
            new WaveSpawnEntry("ZombieDefault", 100),
            new WaveSpawnEntry("ZombieArmor1", 200),
            new WaveSpawnEntry("ZombieArmor2", 500),
            new WaveSpawnEntry("ZombieIceAgeHunter", 300),
            new WaveSpawnEntry("ZombieNewspaper", 400)

        );
        List<Wave> waves = Wave.generateWaves(8, 180, availableZombies, 1);
        LevelSetup levelSetup = new NormalLevelSetup(rows, cols, waves);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();
        FrostbiteCavesWorld world = new FrostbiteCavesWorld(
            levelSetup,
            new ArrayList<>(List.of(loseCondition)),
            winCondition,
            new ArrayList<>()
        );
        world.setCurrentChapter(Chapter.FROSTBITE_CAVES);
        world.setWillUnlockLevel(true);
        world.setStartingDialogs(List.of(
            "DAVE:BRRRR! My pot is frozen to my head!",
            "PENNY:Extreme sub-zero temperatures detected. Flora freezing probability is high.",
            "DAVE:Use warm plants to melt the ice!"
        ));
        return world;
    }

    private static GameWorld createFrostbiteCavesLevel2() {
        int rows = 5;
        int cols = 9;
        List<WaveSpawnEntry> availableZombies = List.of(
            new WaveSpawnEntry("ZombieDefault", 100),
            new WaveSpawnEntry("ZombieArmor1", 250),
            new WaveSpawnEntry("ZombieArmor2", 450),
            new WaveSpawnEntry("ZombieCrystalSkull", 500),
            new WaveSpawnEntry("ZombieIceAgeDodo", 500),
            new WaveSpawnEntry("ZombieIceAgeHunter", 300)
        );
        List<Wave> waves = Wave.generateWaves(10, 180, availableZombies, 1);
        LevelSetup levelSetup = new NormalLevelSetup(rows, cols, waves);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();
        FrostbiteCavesWorld world = new FrostbiteCavesWorld(
            levelSetup,
            new ArrayList<>(List.of(loseCondition)),
            winCondition,
            new ArrayList<>()
        );
        world.setCurrentChapter(Chapter.FROSTBITE_CAVES);
        world.setWillUnlockLevel(true);
        world.setStartingDialogs(List.of(
            "DAVE:You get what you get and you don't get upset!",
            "DAVE:Use the provided seed cards carefully!"
        ));
        return world;
    }

    private static GameWorld createFrostbiteCavesLevel3() {
        int rows = 5;
        int cols = 9;
        List<WaveSpawnEntry> availableZombies = List.of(
            new WaveSpawnEntry("ZombieDefault", 100),
            new WaveSpawnEntry("ZombieArmor1", 200),
            new WaveSpawnEntry("ZombieModernAllStar", 500),
            new WaveSpawnEntry("ZombieCrystalSkull", 400),
            new WaveSpawnEntry("ZombieIceAgeTroglobite", 500),
            new WaveSpawnEntry("ZombieProspector", 400)
        );
        List<Wave> waves = Wave.generateWaves(9, 180, availableZombies, 1);
        LevelSetup levelSetup = new LockedPlantsLevelSetup(rows, cols, waves);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();
        FrostbiteCavesWorld world = new FrostbiteCavesWorld(
            levelSetup,
            new ArrayList<>(List.of(loseCondition)),
            winCondition,
            new ArrayList<>()
        );
        world.setCurrentChapter(Chapter.FROSTBITE_CAVES);
        world.setWillUnlockLevel(true);
        world.setStartingDialogs(List.of(
            "PENNY:Botanical loadout has been locked.",
            "PENNY:Please proceed with the current pre-configured strategy."
        ));
        return world;
    }

    private static GameWorld createFrostbiteCavesLevel4() {
        int rows = 5;
        int cols = 9;
        List<WaveSpawnEntry> availableZombies = List.of(
            new WaveSpawnEntry("ZombieDefault", 100),
            new WaveSpawnEntry("ZombieArmor1", 200),
            new WaveSpawnEntry("ZombieIceAgeDodo", 400),
            new WaveSpawnEntry("ZombieIceAgeHunter", 250),
        new WaveSpawnEntry("ZombieIceAgeTroglobite", 600),
            new WaveSpawnEntry("ZombieGargantuar", 850)
        );
        List<Wave> waves = Wave.generateWaves(10, 180, availableZombies, 1);
        LevelSetup levelSetup = new NormalLevelSetup(rows, cols, waves);
        LoseCondition loseCondition = new NormalLose();
        WinCondition winCondition = new NormalWin();
        AncientEgyptWorld world = new AncientEgyptWorld(
            levelSetup,
            new ArrayList<>(List.of(loseCondition)),
            winCondition,
            new ArrayList<>()
        );
        world.setCurrentChapter(Chapter.FROSTBITE_CAVES);
        world.setStartingDialogs(List.of(
            "DAVE:The final freeze!",
            "PENNY:Scanning for massive cryogenic entities...",
            "DAVE:The Yeti is coming... maybe? I don't know!",
            "DAVE:Stay warm, neighbor!"
        ));
        return world;
    }
}
