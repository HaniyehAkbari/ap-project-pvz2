package com.pvz2.models.zombie;

import com.pvz2.models.core.App;
import com.pvz2.models.core.DifficultyCalculator;
import com.pvz2.models.enums.Zombies;
import com.pvz2.models.miniGame.zombotany.JalapenoZombie;
import com.pvz2.models.miniGame.zombotany.PeashooterZombie;
import com.pvz2.models.miniGame.zombotany.SquashZombie;
import com.pvz2.models.miniGame.zombotany.WallnutZombie;
import com.pvz2.models.zombie.data.ArmorData;
import com.pvz2.models.zombie.data.ArmorProperties;
import com.pvz2.models.zombie.data.ZombieData;
import com.pvz2.models.zombie.data.ZombieProperties;
import com.pvz2.models.zombie.zombiesType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ZombieFactory {
    private static final Logger LOGGER = Logger.getLogger(ZombieFactory.class.getName());
    private static final Map<Zombies, String> ENUM_TO_ALIAS = new HashMap<>();

    static {
        ENUM_TO_ALIAS.put(Zombies.ZOMBIE, "ZombieDefault");
        ENUM_TO_ALIAS.put(Zombies.ARMORED, "ZombieArmor1");
        ENUM_TO_ALIAS.put(Zombies.WIZARD, "ZombieWizard");
        ENUM_TO_ALIAS.put(Zombies.SUN_STEALER, "ZombieRa");
        ENUM_TO_ALIAS.put(Zombies.SPAWNER, "ZombieGargantuar");
        ENUM_TO_ALIAS.put(Zombies.SNORKEL, "ZombieBeachSnorkel");
        ENUM_TO_ALIAS.put(Zombies.RANGED, "ZombieIceAgeHunter");
        ENUM_TO_ALIAS.put(Zombies.PUSHER, "ZombieArcade");
        ENUM_TO_ALIAS.put(Zombies.PHASING, "ZombieNewspaper");
        ENUM_TO_ALIAS.put(Zombies.IMP, "ZombieImp");
        ENUM_TO_ALIAS.put(Zombies.FISHERMAN, "ZombieBeachFisherman");
        ENUM_TO_ALIAS.put(Zombies.ELEMENTAL, "ZombieExplorer");
        ENUM_TO_ALIAS.put(Zombies.DODO_RIDER, "ZombieIceAgeDodo");
        ENUM_TO_ALIAS.put(Zombies.DEFLECTOR, "ZombieDarkJuggler");
        ENUM_TO_ALIAS.put(Zombies.PEASHOOTER_ZOMBIE, "ZombiePeashooter");
        ENUM_TO_ALIAS.put(Zombies.JALAPENO_ZOMBIE, "ZombieJalapeno");
        ENUM_TO_ALIAS.put(Zombies.WALLNUT_ZOMBIE, "ZombieWallnut");
        ENUM_TO_ALIAS.put(Zombies.SQUASH_ZOMBIE, "ZombieSquash");
    }

    private Zombie selectZombie(String objClass, int health, int eatDPS, double speed, ZombieData data) {
        return switch (objClass) {
            case "ZombieGargantuarProps" -> new SpawnerZombie(health, speed, eatDPS, true);
            case "ZombieRaProps" -> new SunStealerZombie(health, speed, eatDPS, true);
            case "ZombieCrystalSkullProps" -> new SunStealerZombie(health, speed, eatDPS, false);
            case "ZombieExplorerProps" -> new ElementalZombie(health, speed, eatDPS, true);
            case "ZombieProspectorProps" -> new ElementalZombie(health, speed, eatDPS, false);
            case "ZombieIceAgeHunterProps" -> new RangedZombie(health, speed, eatDPS, "SNOWBALL");
            case "ZombieBeachOctopusProps" -> new RangedZombie(health, speed, eatDPS, "OCTOPUS");
            case "ZombieTombRaiserProps" -> new RangedZombie(health, speed, eatDPS, "BONE");
            case "ZombieDarkJugglerProps" -> new DeflectorZombie(health, speed, eatDPS, true);
            case "ZombieLostCityJaneProps" -> new DeflectorZombie(health, speed, eatDPS, false);
            case "ZombieDarkWizardProps" -> new WizardZombie(health, speed, eatDPS);
            case "ZombieDarkKingProps" -> new SpawnerZombie(health, speed, eatDPS, false);
            case "ZombieBeachFishermanProps" -> new FishermanZombie(health, eatDPS);
            case "ZombieIceAgeDodoProps" -> new DodoRiderZombie(health, speed, eatDPS);
            case "ZombieModernAllStarProps" -> new PhasingZombie(health, speed, eatDPS, 0, false);
            case "ZombieNewspaperProps" -> new PhasingZombie(health, speed, eatDPS, 800, true);
            case "ZombiePianoProps" -> new PusherZombie(health, speed, eatDPS, "PIANO", 1100);
            case "ZombieArcadeProps" -> new PusherZombie(health, speed, eatDPS, "ARCADE", 1100);
            case "ZombieIceAgeTroglobiteProps" ->
                    new PusherZombie(health, speed, eatDPS, "ICEBLOCK", 600);
            case "ZombieBeachSnorkelProps" -> new SnorkelZombie(health, speed, eatDPS);
            case "ZombieImpProps" -> new ImpZombie(health, speed, eatDPS, false);
            case "ZombieDarkImpDragonProps" -> new ImpZombie(health, speed, eatDPS, true);
            case "ZombieBarrelRollerProps" -> new BarrelRollerZombie(health, speed, eatDPS, 200);
            case "ZombieTurquoiseProps" -> new SunStealerZombie(health, speed, eatDPS, false);
            default -> buildBasicZombie(health, eatDPS, speed, data);
        };
    }

    public Zombie createZombie(String alias) {
        Zombie zombotanyZombie = createZombotanyZombie(alias);
        if (zombotanyZombie != null) {
            return zombotanyZombie;
        }

        ZombieProperties props = ZombieRegistry.getZombieProperties(alias);
        if (props == null) {
            LOGGER.warning("Unknown zombie alias: " + alias + " — using default ZombieDefault.");
            props = ZombieRegistry.getZombieProperties("ZombieDefault");
            if (props == null) {
                throw new IllegalArgumentException("Cannot find even default zombie: " + alias);
            }
            LOGGER.warning("Unknown zombie alias: " + alias + " — using default ZombieDefault.");
            return null;
        }
        String objclass = props.getObjclass();
        ZombieData data = props.getObjdata();

        int difficulty = App.getCurrentUser().getNickname() == null ? 3 : App.getCurrentUser().getGameDifficulty();
        double increaseFactor = DifficultyCalculator.increaseFactor(difficulty);

        int health = (int) Math.round(data.getHitpoints() * increaseFactor);
        int eatDPS = (int) Math.round(data.getEatDPS() * increaseFactor);
        double speed = data.getSpeed();

        Zombie createdZombie = null;

        if ("ZombieDarkImpDragon".equalsIgnoreCase(alias)) {
            createdZombie = new ImpZombie(health, speed, eatDPS, true);
        } else if ("ZombieImp".equalsIgnoreCase(alias)) {
            createdZombie = new ImpZombie(health, speed, eatDPS, false);
        } else {
            createdZombie = selectZombie(objclass, health, eatDPS, speed, data);
        }

        createdZombie.setSpecificName(alias);
        if (Math.random() < 0.05) {
            createdZombie.setGlowing(true);
        }

        return createdZombie;
    }

    private Zombie buildBasicZombie(int health, int eatDPS, double speed, ZombieData data) {
        List<String> armorRefs = data.getZombieArmorProps();
        if (armorRefs != null && !armorRefs.isEmpty()) {
            int totalArmorHealth = 0;
            boolean magnetic = false;
            List<String> currentArmorTypes = new ArrayList<>();
            for (String ref : armorRefs) {
                String alias = extractAlias(ref);
                ArmorProperties armor = ZombieRegistry.getArmorProperties(alias);
                if (armor != null) {
                    ArmorData ad = armor.getObjdata();
                    totalArmorHealth += ad.getBaseHealth();
                    currentArmorTypes.add(ad.getArmorType());
                    if (ad.getArmorFlags().contains("metallic")) {
                        magnetic = true;
                    }
                } else {
                    LOGGER.warning("Armor properties not found for alias: " +
                            alias + " (used in " + data.getClass().getName() + ")");
                }
            }
            ArmoredZombie zombie = new ArmoredZombie(health, speed, eatDPS, totalArmorHealth, magnetic);
            zombie.getArmorTypes().addAll(currentArmorTypes);
            return zombie;
        }
        return new Zombie(Zombies.ZOMBIE, health, speed, eatDPS) {
        };
    }

    private String extractAlias(String ref) {
        if (ref.startsWith("RTID(") && ref.endsWith(")")) {
            String inner = ref.substring(5, ref.length() - 1);
            int atIndex = inner.indexOf('@');
            if (atIndex != -1) {
                return inner.substring(0, atIndex);
            }
            return inner;
        }
        return ref;
    }

    private Zombie createZombotanyZombie(String alias) {
        return switch (alias) {
            case "ZombiePeashooter" -> new PeashooterZombie(Zombies.PEASHOOTER_ZOMBIE, 190, 0.3, 20);
            case "ZombieWallnut" -> new WallnutZombie(Zombies.WALLNUT_ZOMBIE, 1000, 0.07, 100);
            case "ZombieJalapeno" -> new JalapenoZombie(Zombies.JALAPENO_ZOMBIE, 190, 0.3, 100);
            case "ZombieSquash" -> new SquashZombie(Zombies.SQUASH_ZOMBIE, 100, 0.7, 500);
            default -> null;
        };
    }
}
