package com.pvz2.models.plant.factory;

import com.pvz2.controller.LevelMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantLayer;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.enums.ProjectileType;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.BowlingChargeComponent;
import com.pvz2.models.plant.components.LifespanComponent;
import com.pvz2.models.plant.components.PlacementBehaviorComponent;
import com.pvz2.models.plant.components.ShooterComponent;
import com.pvz2.models.plant.components.shooterPlantFoodBehaviors.BurstPlantFood;
import com.pvz2.models.plant.components.shooterPlantFoodBehaviors.PlantFoodBehavior;
import com.pvz2.models.plant.components.shooterPlantFoodBehaviors.RandomTargetPlantFood;
import com.pvz2.models.plant.components.shooterPlantFoodBehaviors.ThreepeaterPlantFood;
import com.pvz2.models.plant.visions.RotatedVisionStrategy;
import com.pvz2.models.plant.visions.StraightVisionStrategy;
import com.pvz2.models.projectile.Projectile;
import com.pvz2.models.projectile.hitStrategies.CombinedDamageStrategy;
import com.pvz2.models.projectile.movementStrategies.*;
import com.pvz2.models.projectile.strikeStrategies.CheckFumeStrike;
import com.pvz2.models.projectile.strikeStrategies.CheckLobbedStrike;
import com.pvz2.models.projectile.strikeStrategies.CheckStraightStrike;
import com.pvz2.models.world.Cell;
import com.pvz2.models.zombie.Zombie;

import java.util.Map;
import java.util.function.Supplier;

public class ShooterFactory {
    static void register(Map<PlantType, Supplier<Plant>> registry) {
        registry.put(PlantType.PEASHOOTER, ShooterFactory::buildPeaShooter);
        registry.put(PlantType.REPEATER, ShooterFactory::buildRepeater);
        registry.put(PlantType.THREEPEATER, ShooterFactory::buildThreepeater);
        registry.put(PlantType.SNOW_PEA, ShooterFactory::buildSnowPea);
        registry.put(PlantType.XSHOT, ShooterFactory::buildRotobaga);
        registry.put(PlantType.PEA_POD, ShooterFactory::buildPeaPod);
        registry.put(PlantType.SPLIT_PEA, ShooterFactory::buildSplitPea);
        registry.put(PlantType.CITRON, ShooterFactory::buildCitron);
        registry.put(PlantType.BOWLING_BULB, ShooterFactory::buildBowlingBulb);
        registry.put(PlantType.CACTUS, ShooterFactory::buildCactus);
        registry.put(PlantType.FIRE_PEASHOOTER, ShooterFactory::buildFirePeashooter);
        registry.put(PlantType.STARFRUIT, ShooterFactory::buildStarfruit);
        registry.put(PlantType.POISON_PEASHOOTER, ShooterFactory::buildGooPeashooter);
        registry.put(PlantType.MEGA_GATLING, ShooterFactory::buildMegaGatlingPea);
        registry.put(PlantType.SEA_SHROOM, ShooterFactory::buildSeaShroom);
        registry.put(PlantType.PUFF_SHROOM, ShooterFactory::buildPuffShroom);
        registry.put(PlantType.FUME_SHROOM, ShooterFactory::buildFumeShroom);
        registry.put(PlantType.CABBAGE_PULT, ShooterFactory::buildCabbagePult);
        registry.put(PlantType.KERNEL_PULT, ShooterFactory::buildKernelPult);
        registry.put(PlantType.MELON_PULT, ShooterFactory::buildMelonPult);
        registry.put(PlantType.WINTER_MELON, ShooterFactory::buildWinterMelonPult);
        registry.put(PlantType.PEPPER_PULT, ShooterFactory::buildPepperPult);
    }

    private static Plant buildPeaShooter() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.PEASHOOTER, 1);
        int health = level >= 3 ? 450 : 300;
        int damage = level >= 2 ? 30 : 20;
        Plant p = new Plant(PlantType.PEASHOOTER, health, damage);
        ShooterComponent newComponent = new ShooterComponent(ProjectileType.PEA,
            null, 1.5f, 1,
            20, false, () -> new CombinedDamageStrategy(damage, ProjectileType.PEA),
            new CheckStraightStrike(), 0, 1,
            0, 0);
        newComponent.getVisions().add(new StraightVisionStrategy(4000, App.getCellHeight(), false));
        MovementStrategy movementStrategy = new StraightMovementStrategy(700, 0, 0);
        newComponent.getMovementStrategies().add(() -> movementStrategy);
        p.addComponent(newComponent);
        return p;
    }
    private static Plant buildRepeater() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.REPEATER, 1);
        int health = level >= 3 ? 500 : 300;
        int damage = level >= 2 ? 30 : 20;
        Plant p = new Plant(PlantType.REPEATER, health, damage);
        ShooterComponent newComponent = new ShooterComponent(ProjectileType.PEA,
            ProjectileType.GIANT_PEA, 1.5f, 2,
            30, true, () -> new CombinedDamageStrategy(damage, ProjectileType.PEA),
            new CheckStraightStrike(), 1, 1,
            1, 20);
        newComponent.getVisions().add(new StraightVisionStrategy(4000, App.getCellHeight(), false));
        MovementStrategy movementStrategy = new StraightMovementStrategy(700, 0, 0);
        newComponent.getMovementStrategies().add(() -> movementStrategy);
        p.addComponent(newComponent);
        return p;
    }
    private static Plant buildThreepeater() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.THREEPEATER, 1);
        int health = level >= 4 ? 500 : 300;
        int damage = level >= 3 ? 30 : 20;
        Plant p = new Plant(PlantType.THREEPEATER, health, 0);
        ShooterComponent newComponent = new ShooterComponent(ProjectileType.PEA,
            null, 1.5f, 1,
            30, false, () -> new CombinedDamageStrategy(damage, ProjectileType.PEA),
            new CheckStraightStrike(), 0, 1,
            0, 0);
        newComponent.getVisions().add(new StraightVisionStrategy(4000, 3 * App.getCellHeight(), false));
        for (int i = -1; i <= 1; i++) {
            final int finalI = i;
            MovementStrategy movementStrategy = new StraightMovementStrategy(700, 0, finalI * App.getCellHeight());
            newComponent.getMovementStrategies().add(() -> movementStrategy);
        }
        newComponent.setPlantFoodBehavior(ThreepeaterPlantFood.INSTANCE);
        p.addComponent(newComponent);
        return p;
    }
    private static Plant buildSnowPea() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.SNOW_PEA, 1);
        int damage = level >= 2 ? 30 : 20;
        Plant p = new Plant(PlantType.SNOW_PEA, 300, damage);
        ShooterComponent newComponent = new ShooterComponent(ProjectileType.ICE_PEA,
            null, 1.5f, 1,
            20, false, () -> {
            CombinedDamageStrategy strategy = new CombinedDamageStrategy(damage, ProjectileType.ICE_PEA);
            strategy.setElement("ICE");
            if (level >= 3) {
                strategy.setChillTime(strategy.getChillTime() + 20);
            }
            return strategy;
        },
            new CheckStraightStrike(), 0, 1,
            0, 0);
        newComponent.getVisions().add(new StraightVisionStrategy(4000, App.getCellHeight(), false));
        MovementStrategy movementStrategy = new StraightMovementStrategy(700, 0, 0);
        newComponent.getMovementStrategies().add(() -> movementStrategy);
        newComponent.setPlantFoodBehavior(new PlantFoodBehavior() {
            @Override
            public void activate(Plant owner, ShooterComponent shooterComponent) {
                for (Zombie zombie : Cell.getZombiesInCells(Cell.getCellsInRow(owner.getCell(),
                    LevelMenuController.getGameCells()))) {
                    zombie.freeze(5.0f);
                }
                BurstPlantFood.INSTANCE.activate(owner, shooterComponent);
            }
        });
        p.addComponent(newComponent);
        return p;
    }
    private static Plant buildRotobaga() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.XSHOT, 1);
        int damage = level >= 2 ? 20 : 10;
        int health = level >= 3 ? 450 : 300;
        Plant p = new Plant(PlantType.XSHOT, health, damage);
        CombinedDamageStrategy combinedDamageStrategy = new CombinedDamageStrategy(
            damage, ProjectileType.ROTOBAGA_PROJECTILE);
        for (int i = 0; i < 4; i++) {
            ShooterComponent shooterComponent = new ShooterComponent(ProjectileType.ROTOBAGA_PROJECTILE,
                null, 1.5f, 1,
                20, false, () -> combinedDamageStrategy,
                new CheckStraightStrike(), 0, 1,
                0, 0);
            final int finalI = i;
            shooterComponent.getVisions().add(new RotatedVisionStrategy(
                (float) (i * Math.PI / 2 + Math.PI / 4), 100, 2000));
            MovementStrategy movementStrategy = new StraightMovementStrategy(
                (float) (700 * Math.cos(finalI * Math.PI / 2 + Math.PI / 4)),
                (float) (700 * Math.sin(finalI * Math.PI / 2 + Math.PI / 4)), 0);
            shooterComponent.getMovementStrategies().add(() -> movementStrategy);
            p.addComponent(shooterComponent);
            shooterComponent.setBurstDelayMax(0.2f);
        }
        return p;
    }
    private static Plant buildPeaPod() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.PEA_POD, 1);
        int damage = level >= 2 ? 30 : 20;
        int health = level >= 3 ? 500 : 300;
        Plant p = new Plant(PlantType.PEA_POD, health, damage);
        ShooterComponent newComponent = new ShooterComponent(ProjectileType.PEA,
            ProjectileType.GIANT_PEA,
            1.5f, 1, 1,
            true, () -> new CombinedDamageStrategy(damage, ProjectileType.PEA), new CheckStraightStrike(),
            1, 1, 1,
            20);
        newComponent.setGiantBurstDelayTime(1.1f);
        newComponent.getVisions().add(new StraightVisionStrategy(4000, App.getCellHeight(), false));
        MovementStrategy movementStrategy = new StraightMovementStrategy(700, 0, 0);
        newComponent.getMovementStrategies().add(() -> movementStrategy);
        p.addComponent(newComponent);
        p.addComponent(new PlacementBehaviorComponent(PlantLayer.MAIN, true, 5, false));
        return p;
    }
    private static Plant buildSplitPea() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.SPLIT_PEA, 1);
        int damage = level >= 2 ? 30 : 20;
        int health = level >= 3 ? 500 : 300;
        Plant p = new Plant(PlantType.SPLIT_PEA, health, damage);
        for (int i = 0; i <= 1; i++) {
            ShooterComponent newComponent = new ShooterComponent(ProjectileType.PEA,
                null, 1.7f, i + 1,
                20, false, () -> new CombinedDamageStrategy(damage, ProjectileType.PEA),
                new CheckStraightStrike(), 0, 1,
                0, 0);
            final int finalI = i;
            newComponent.getVisions().add(new StraightVisionStrategy(
                1000 * (float) Math.cos(i * Math.PI), App.getCellHeight(), false));
            MovementStrategy movementStrategy = new StraightMovementStrategy(
                700 * (float) Math.cos(finalI * Math.PI), 0, 0);
            newComponent.getMovementStrategies().add(() -> movementStrategy);
            p.addComponent(newComponent);
        }
        return p;
    }
    private static Plant buildCitron() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.CITRON, 1);
        int damage = level >= 3 ? 950 : 800;
        int chargeTime = level >= 2 ? 8 : 9;
        Plant p = new Plant(PlantType.CITRON, 300, damage);
        CombinedDamageStrategy combinedDamageStrategy = new CombinedDamageStrategy(damage, ProjectileType.CITRON);
        ShooterComponent newComponent = new ShooterComponent(ProjectileType.CITRON,
            ProjectileType.PLASMA, chargeTime, 1,
            1, true, () -> combinedDamageStrategy,
            new CheckStraightStrike(), 1, 1,
            100, 20);
        newComponent.getVisions().add(new StraightVisionStrategy(4000, App.getCellHeight(), false));
        MovementStrategy movementStrategy = new StraightMovementStrategy(700, 0, 0);
        newComponent.getMovementStrategies().add(() -> movementStrategy);
        p.addComponent(newComponent);
        return p;
    }
    private static Plant buildBowlingBulb() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.BOWLING_BULB, 1);
        int regenReduce = level >= 2 ? 1 : 0;
        int damageAddition = level >= 3 ? 15 : 0;
        CombinedDamageStrategy first = new CombinedDamageStrategy(180 + damageAddition, ProjectileType.LARGE_BULB);
        CombinedDamageStrategy second = new CombinedDamageStrategy(120 + damageAddition, ProjectileType.MEDIUM_BULB);
        CombinedDamageStrategy third = new CombinedDamageStrategy(40 + damageAddition, ProjectileType.SMALL_BULB);
        CombinedDamageStrategy special = new CombinedDamageStrategy(
            180 + damageAddition, 90 + damageAddition / 2, 100, ProjectileType.MEDIUM_BULB);
        Plant p = new Plant(PlantType.BOWLING_BULB, 300, 0);
        p.addComponent(new BowlingChargeComponent(
            2, first, second, third, () -> new BowlingMovementStrategy(
            600, 0), special, 10 - regenReduce, 5 - regenReduce, 2 - regenReduce, 2.7f, 0.6f));
        return p;
    }
    private static Plant buildCactus() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.CACTUS, 1);
        int damage = level >= 3 ? 40 : 30;
        int pierce = level >= 2 ? 4 : 3;
        Plant p = new Plant(PlantType.CACTUS, 300, damage);
        CombinedDamageStrategy combinedDamageStrategy = new CombinedDamageStrategy(damage, ProjectileType.CACTUS);
        ShooterComponent newComponent = new ShooterComponent(ProjectileType.CACTUS,
            ProjectileType.CACTUS_SPECIAL, 1.5f, 1,
            5, true, () -> combinedDamageStrategy,
            new CheckStraightStrike(), 5, pierce,
            100, 4);
        newComponent.getVisions().add(new StraightVisionStrategy(4000, App.getCellHeight(), false));
        MovementStrategy movementStrategy = new StraightMovementStrategy(700, 0, 0);
        newComponent.getMovementStrategies().add(() -> movementStrategy);
        p.addComponent(newComponent);
        newComponent.setGiantBurstDelayTime(1f);
        return p;
    }
    private static Plant buildFirePeashooter() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.FIRE_PEASHOOTER, 1);
        int damage = level >= 2 ? 60 : 40;
        int health = level >= 3 ? 500 : 300;
        Plant p = new Plant(PlantType.FIRE_PEASHOOTER, health, damage);
        CombinedDamageStrategy combinedDamageStrategy = new CombinedDamageStrategy(damage, ProjectileType.FIRE_PEA);
        combinedDamageStrategy.setElement("FIRE");
        ShooterComponent newComponent = new ShooterComponent(ProjectileType.FIRE_PEA,
            null, 1.5f, 1,
            20, false, () -> combinedDamageStrategy,
            new CheckStraightStrike(), 0, 1,
            0, 0);
        newComponent.getVisions().add(new StraightVisionStrategy(4000, App.getCellHeight(), false));
        MovementStrategy movementStrategy = new StraightMovementStrategy(700, 0, 0);
        newComponent.getMovementStrategies().add(() -> movementStrategy);
        p.addComponent(newComponent);
        p.setFire(true);
        return p;
    }
    private static Plant buildStarfruit() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.STARFRUIT, 1);
        float shootingTime = level >= 2 ? 1.3f : 1.5f;
        int damage = level >= 3 ? 30 : 20;
        Plant p = new Plant(PlantType.STARFRUIT, 300, damage);
        CombinedDamageStrategy combinedDamageStrategy = new CombinedDamageStrategy(damage, ProjectileType.STAR);
        ShooterComponent newComponent = new ShooterComponent(ProjectileType.STAR,
            null, shootingTime, 1,
            20, false, () -> combinedDamageStrategy,
            new CheckStraightStrike(), 0, 1,
            0, 0);
        float angel = (float) -Math.PI / 3;
        for (int i = 0; i < 5; i++) {
            int changeFactor = i == 1 ? 2 : 3;
            final float finalAngel = angel;
            newComponent.getVisions().add(new RotatedVisionStrategy(angel, App.getCellHeight(), 1000));
            MovementStrategy movementStrategy = new StraightMovementStrategy(
                (float) (200 * Math.cos(finalAngel)), (float) (200 * Math.sin(finalAngel)), 0);
            newComponent.getMovementStrategies().add(() -> movementStrategy);
            angel += (float) (changeFactor * Math.PI / 6);
        }
        p.addComponent(newComponent);
        return p;
    }
    private static Plant buildGooPeashooter() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.POISON_PEASHOOTER, 1);
        int health = level >= 3 ? 30 : 20;
        Plant p = new Plant(PlantType.POISON_PEASHOOTER, health, 20);
        CombinedDamageStrategy combinedDamageStrategy = new CombinedDamageStrategy(20, ProjectileType.GOO);
        CombinedDamageStrategy plantFoodStrategy = new CombinedDamageStrategy(20, ProjectileType.GOO_SPECIAL);
        combinedDamageStrategy.setElement("POISON");
        plantFoodStrategy.setElement("POISON");
        if (level >= 2) {
            combinedDamageStrategy.setPoisonDamageOnTick(combinedDamageStrategy.getPoisonDamageOnTick() + 5);
            plantFoodStrategy.setPoisonDamageOnTick(combinedDamageStrategy.getPoisonDamageOnTick());
        }
        ShooterComponent newComponent = new ShooterComponent(ProjectileType.GOO,
            ProjectileType.GOO_SPECIAL, 1.5f, 1,
            1, true, () -> combinedDamageStrategy,
            new CheckStraightStrike(), 1, 1,
            1, 1);
        newComponent.setPlantFoodStrategy(plantFoodStrategy);
        newComponent.getVisions().add(new StraightVisionStrategy(4000, App.getCellHeight(), false));
        MovementStrategy movementStrategy = new StraightMovementStrategy(700, 0, 0);
        newComponent.getMovementStrategies().add(() -> movementStrategy);
        p.addComponent(newComponent);
        return p;
    }
    private static Plant buildMegaGatlingPea() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.MEGA_GATLING, 1);
        int damage = level >= 2 ? 30 : 20;
        int plantFoodChance = level >= 3 ? 10 : 5;
        Plant p = new Plant(PlantType.MEGA_GATLING, 300, damage);
        ShooterComponent newComponent = new ShooterComponent(ProjectileType.PEA,
            ProjectileType.GIANT_PEA, 1.5f, 4,
            30, true, () -> new CombinedDamageStrategy(damage, ProjectileType.PEA),
            new CheckStraightStrike(), 4, 1,
            1, 20);
        newComponent.getVisions().add(new StraightVisionStrategy(4000, App.getCellHeight(), false));
        MovementStrategy movementStrategy = new StraightMovementStrategy(700, 0, 0);
        newComponent.getMovementStrategies().add(() -> movementStrategy);
        newComponent.setAttackCallback(owner -> {
            if (Math.random() < plantFoodChance / 100f) {
                owner.activatePlantFood();
            }
        });
        p.addComponent(newComponent);
        return p;
    }
    private static Plant buildSeaShroom() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.SEA_SHROOM, 1);
        float range = (level >= 2 ? 5 : 4) * App.getCellWidth();
        int damage = level >= 3 ? 25 : 20;
        int lifespan = level >= 4 ? 700 : 600;
        Plant p = new Plant(PlantType.SEA_SHROOM, 300, damage);
        CombinedDamageStrategy combinedDamageStrategy = new CombinedDamageStrategy(damage, ProjectileType.SEA_SHROOM);
        ShooterComponent newComponent = new ShooterComponent(ProjectileType.SEA_SHROOM,
            ProjectileType.SEA_SHROOM, 1.5f, 1,
            20, false, () -> combinedDamageStrategy,
            new CheckStraightStrike(), 0, 1,
            0, 0);
        newComponent.getVisions().add(new StraightVisionStrategy(range, App.getCellHeight(), false));
        MovementStrategy movementStrategy = new StraightMovementStrategy(700, 0, 0){
            @Override
            public boolean isDead(Projectile projectile) {
                return projectile.getX() > p.getX()+range || projectile.getX() < 0 ||
                    projectile.getY() > 1000 || projectile.getY() < 0;
            }
        };
        newComponent.getMovementStrategies().add(() -> movementStrategy);
        newComponent.setPlantFoodFinishDelay(1f);
        p.addComponent(newComponent);
        LifespanComponent lifespanComponent = new LifespanComponent(PlantType.SEA_SHROOM, lifespan);
        if (App.getCurrentGame() != null) App.getCurrentGame().registerShroom(lifespanComponent);
        p.addComponent(lifespanComponent);
        p.addComponent(new PlacementBehaviorComponent(PlantLayer.MAIN, false, 0, true));
        return p;
    }
    private static Plant buildPuffShroom() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.PUFF_SHROOM, 1);
        float range = (level >= 4 ? 5 : 4) * App.getCellWidth();
        int damage = level >= 3 ? 30 : 20;
        int lifespan = level >= 2 ? 700 : 600;
        Plant p = new Plant(PlantType.PUFF_SHROOM, 300, damage);
        CombinedDamageStrategy combinedDamageStrategy = new CombinedDamageStrategy(damage, ProjectileType.SMALL_SHROOM);
        ShooterComponent newComponent = new ShooterComponent(ProjectileType.SMALL_SHROOM,
            ProjectileType.SMALL_SHROOM, 1.5f, 1,
            20, false, () -> combinedDamageStrategy,
            new CheckStraightStrike(), 0, 1,
            0, 0);
        newComponent.getVisions().add(new StraightVisionStrategy(range, App.getCellHeight(), false));
        MovementStrategy movementStrategy = new StraightMovementStrategy(700, 0, 0){
            @Override
            public boolean isDead(Projectile projectile) {
                return projectile.getX() > p.getX()+range || projectile.getX() < 0 ||
                    projectile.getY() > 1000 || projectile.getY() < 0;
            }
        };
        newComponent.getMovementStrategies().add(() -> movementStrategy);
        newComponent.setPlantFoodFinishDelay(1f);
        p.addComponent(newComponent);
        LifespanComponent lifespanComponent = new LifespanComponent(PlantType.PUFF_SHROOM, lifespan);
        if (App.getCurrentGame() != null) App.getCurrentGame().registerShroom(lifespanComponent);
        p.addComponent(lifespanComponent);
        return p;
    }
    private static Plant buildFumeShroom() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.FUME_SHROOM, 1);
        float range = (level >= 2 ? 6 : 5) * App.getCellWidth();
        int damage = level >= 3 ? 30 : 20;
        Plant p = new Plant(PlantType.FUME_SHROOM, 300, damage);
        CombinedDamageStrategy combinedDamageStrategy = new CombinedDamageStrategy(damage, ProjectileType.FUME);
        ShooterComponent newComponent = new ShooterComponent(ProjectileType.FUME,
            ProjectileType.FUME_SPECIAL, 1.5f, 1,
            1, true, () -> combinedDamageStrategy,
            new CheckFumeStrike(range), 1, 100,
            100, 2);
        CombinedDamageStrategy plantFoodStrategy = new CombinedDamageStrategy(damage, ProjectileType.FUME_SPECIAL);
        plantFoodStrategy.setElement("MOVE");
        newComponent.setPlantFoodStrategy(plantFoodStrategy);
        newComponent.getVisions().add(new StraightVisionStrategy(range, App.getCellHeight(), false));
        newComponent.getMovementStrategies().add(() -> new StationaryMovementStrategy(1f));
        p.addComponent(newComponent);
        newComponent.setPlantFoodFinishDelay(3.5f);
        return p;
    }
    private static Plant buildCabbagePult() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.CABBAGE_PULT, 1);
        int damage = level >= 2 ? 50 : 40;
        float shootingTime = level >= 3 ? 2.5f : 2.9f;
        int health = level >= 4 ? 450 : 300;
        Plant p = new Plant(PlantType.CABBAGE_PULT, health, damage);
        CombinedDamageStrategy combinedDamageStrategy = new CombinedDamageStrategy(damage, ProjectileType.CABBAGE);
        ShooterComponent newComponent = new ShooterComponent(ProjectileType.CABBAGE,
            ProjectileType.SPECIAL_CABBAGE, shootingTime, 1,
            0, false, () -> combinedDamageStrategy,
            new CheckLobbedStrike(), 0, 1,
            0, 5);
        newComponent.getVisions().add(new StraightVisionStrategy(4000, App.getCellHeight(), true));
        newComponent.getMovementStrategies().add(LobbedMovementStrategy::new);
        newComponent.setPlantFoodBehavior(new RandomTargetPlantFood(6));
        p.addComponent(newComponent);
        newComponent.setPlantFoodFinishDelay(2f);
        return p;
    }
    private static Plant buildKernelPult() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.KERNEL_PULT, 1);
        float butterChance = level >= 2 ? 0.35f : 0.3f;
        int damageAddition = level >= 3 ? 10 : 0;
        int health = level >= 4 ? 450 : 300;
        Plant p = new Plant(PlantType.KERNEL_PULT, health, 0);
        ShooterComponent newComponent = new ShooterComponent(ProjectileType.KERNEL,
            ProjectileType.BUTTER, 2.9f, 1,
            0, false, () -> new CombinedDamageStrategy(20 + damageAddition, ProjectileType.KERNEL),
            new CheckLobbedStrike(), 0, 1,
            0, 0);
        newComponent.getVisions().add(new StraightVisionStrategy(4000, App.getCellHeight(), true));
        newComponent.getMovementStrategies().add(LobbedMovementStrategy::new);
        newComponent.setAttackCallback(_ -> {
            if (Math.random() < butterChance) {
                newComponent.setBulletType(ProjectileType.BUTTER);
            } else {
                newComponent.setBulletType(ProjectileType.KERNEL);
            }
        });
        CombinedDamageStrategy kernelStrategy = new CombinedDamageStrategy(20 + damageAddition, ProjectileType.KERNEL);
        CombinedDamageStrategy butterStrategy = new CombinedDamageStrategy(40 + damageAddition, ProjectileType.BUTTER);
        butterStrategy.setElement("STUN");
        newComponent.setDamageStrategy(() -> {
            if (newComponent.getBulletType() == ProjectileType.KERNEL) {
                return kernelStrategy;
            }
            return butterStrategy;
        });
        newComponent.setPlantFoodStrategy(butterStrategy);
        newComponent.setPlantFoodBehavior(new RandomTargetPlantFood());
        p.addComponent(newComponent);
        return p;
    }
    private static Plant buildMelonPult() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.MELON_PULT, 1);
        int damage = level >= 3 ? 110 : 80;
        int aoeDamage = level >= 4 ? 55 : 40;
        Plant p = new Plant(PlantType.MELON_PULT, 300, damage);
        CombinedDamageStrategy combinedDamageStrategy = new CombinedDamageStrategy(
            damage, aoeDamage, 150, ProjectileType.MELON);
        ShooterComponent newComponent = new ShooterComponent(ProjectileType.MELON,
            ProjectileType.SPECIAL_MELON, 2.9f, 1,
            0, false, () -> combinedDamageStrategy,
            new CheckLobbedStrike(), 0, 1,
            0, 4);
        newComponent.getVisions().add(new StraightVisionStrategy(4000, App.getCellHeight(), true));
        newComponent.getMovementStrategies().add(LobbedMovementStrategy::new);
        newComponent.setPlantFoodBehavior(new RandomTargetPlantFood(6));
        p.addComponent(newComponent);
        return p;
    }
    private static Plant buildWinterMelonPult() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.WINTER_MELON, 1);
        int aoeDamage = level >= 3 ? 55 : 40;
        Plant p = new Plant(PlantType.WINTER_MELON, 300, 80);
        CombinedDamageStrategy combinedDamageStrategy = new CombinedDamageStrategy(
            80, aoeDamage, 150, ProjectileType.ICE_MELON);
        combinedDamageStrategy.setElement("ICE");
        ShooterComponent newComponent = new ShooterComponent(ProjectileType.ICE_MELON,
            ProjectileType.SPECIAL_ICE_MELON, 2.9f, 1,
            0, false, () -> combinedDamageStrategy,
            new CheckLobbedStrike(), 0, 1,
            0, 4);
        newComponent.getVisions().add(new StraightVisionStrategy(4000, App.getCellHeight(), true));
        newComponent.getMovementStrategies().add(LobbedMovementStrategy::new);
        newComponent.setPlantFoodBehavior(new RandomTargetPlantFood(6));
        p.addComponent(newComponent);
        return p;
    }
    private static Plant buildPepperPult() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.PEPPER_PULT, 1);
        int damage = level >= 2 ? 65 : 50;
        Plant p = new Plant(PlantType.PEPPER_PULT, 300, damage);
        CombinedDamageStrategy combinedDamageStrategy = new CombinedDamageStrategy(
            80, damage / 2, 150, ProjectileType.PEPPER);
        combinedDamageStrategy.setElement("FIRE");
        ShooterComponent newComponent = new ShooterComponent(ProjectileType.PEPPER,
            ProjectileType.SPECIAL_PEPPER, 2.9f, 1,
            0, false, () -> combinedDamageStrategy,
            new CheckLobbedStrike(), 0, 1,
            0, 4);
        newComponent.getVisions().add(new StraightVisionStrategy(4000, App.getCellHeight(), true));
        newComponent.getMovementStrategies().add(LobbedMovementStrategy::new);
        newComponent.setPlantFoodBehavior(new RandomTargetPlantFood(6));
        p.addComponent(newComponent);
        if (level >= 3) p.setWarmRadius(2);
        p.setFire(true);
        return p;
    }
}
