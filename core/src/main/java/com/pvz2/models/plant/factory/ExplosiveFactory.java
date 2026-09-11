package com.pvz2.models.plant.factory;

import com.pvz2.controller.GameMenuController;
import com.pvz2.controller.LevelMenuController;
import com.pvz2.models.core.App;

import com.pvz2.models.enums.PlantLayer;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.ExplosivesComponent;
import com.pvz2.models.plant.components.PlacementBehaviorComponent;
import com.pvz2.models.plant.components.explosionRanges.CircularRange;
import com.pvz2.models.plant.components.explosionRanges.LineRange;
import com.pvz2.models.plant.components.explosiveBehaviors.*;
import com.pvz2.models.plant.components.explosiveTriggers.InstantTrigger;
import com.pvz2.models.plant.components.explosiveTriggers.ProximityTrigger;
import com.pvz2.models.world.Cell;
import com.pvz2.models.zombie.Zombie;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ExplosiveFactory {

    static void register(Map<PlantType, Supplier<Plant>> registry) {
        registry.put(PlantType.POTATO_MINE, ExplosiveFactory::buildPotatoMine);
        registry.put(PlantType.PRIMAL_POTATO_MINE, ExplosiveFactory::buildPrimalPotatoMine);
        registry.put(PlantType.CHERRY_BOMB, ExplosiveFactory::buildCherryBomb);
        registry.put(PlantType.SQUASH, ExplosiveFactory::buildSquash);
        registry.put(PlantType.GRAPESHOT, ExplosiveFactory::buildGrapeshot);
        registry.put(PlantType.JALAPENO, ExplosiveFactory::buildJalapeno);
        registry.put(PlantType.DOOM_SHROOM, ExplosiveFactory::buildDoomShroom);
        registry.put(PlantType.TANGLE_KELP, ExplosiveFactory::buildTangleKelp);
        registry.put(PlantType.ICEBURG, ExplosiveFactory::buildIcebergLettuce);
        registry.put(PlantType.ICE_SHROOM, ExplosiveFactory::buildIceShroom);
        registry.put(PlantType.HOT_POTATO, ExplosiveFactory::buildHotPotato);
        registry.put(PlantType.GRAVE_BUSTER, ExplosiveFactory::buildGraveBuster);
    }


    private static Plant buildPotatoMine() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.POTATO_MINE, 1);
        int armTime = level >= 2 ? 12 : 15;
        int damage = level >= 4 ? 2400 : 1800;
        Plant p = new Plant(PlantType.POTATO_MINE, 300, damage);
        ExplosivesComponent component = new ExplosivesComponent(
                new ProximityTrigger(App.getCellWidth()/2),
                new AreaDamageBehavior(damage, new CircularRange(0)), armTime, 0.65f);
        component.setPlantFoodBehavior((_, comp) -> {
            comp.instantArm();

            List<Cell> emptyCells = App.getCurrentGame().findTwoEmptyCell(false);

            for (Cell cell : emptyCells) {
                Plant clone = cell.handlePlanting(PlantType.POTATO_MINE);
                cell.getPlant().getComponent(ExplosivesComponent.class).instantArm();
                cell.getPlant().getComponent(ExplosivesComponent.class).setPlantFoodBehavior(null);
                GameMenuController.updateScreenPlants(clone);
            }
        });
        p.addComponent(component);
        return p;
    }

    private static Plant buildPrimalPotatoMine() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.PRIMAL_POTATO_MINE, 1);
        int armTime = level >= 2 ? 4 : 5;
        int damage = level >= 4 ? 2800 : 2400;
        Plant p = new Plant(PlantType.PRIMAL_POTATO_MINE, 300, damage);
        ExplosivesComponent component = new ExplosivesComponent(
                new ProximityTrigger(App.getCellWidth()/2),
                new AreaDamageBehavior(damage, new CircularRange(1)), armTime, 0.65f);
        component.setPlantFoodBehavior((_, comp) -> {
            comp.instantArm();

            List<Cell> emptyCells = App.getCurrentGame().findTwoEmptyCell(false);

            for (Cell cell : emptyCells) {
                Plant clone = cell.handlePlanting(PlantType.PRIMAL_POTATO_MINE);
                cell.getPlant().getComponent(ExplosivesComponent.class).instantArm();
                cell.getPlant().getComponent(ExplosivesComponent.class).setPlantFoodBehavior(null);
                GameMenuController.updateScreenPlants(clone);
            }
        });
        p.addComponent(component);
        return p;
    }

    private static Plant buildCherryBomb() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.CHERRY_BOMB, 1);
        int damage = level >= 3 ? 2400 : 1800;
        Plant p = new Plant(PlantType.CHERRY_BOMB, 1000, damage);
        p.addComponent(new ExplosivesComponent(
                InstantTrigger.INSTANCE,
                new AreaDamageBehavior(damage, new CircularRange(1)), 0, 0.65f));
        return p;
    }

    private static Plant buildSquash() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.SQUASH, 1);
        int damage = level >= 3 ? 2400 : 1800;
        Plant p = new Plant(PlantType.SQUASH, 1000, damage);
        ExplosivesComponent component = new ExplosivesComponent(
            new ProximityTrigger(App.getCellWidth() * 2.9f),
            new AreaDamageBehavior(damage, new CircularRange(0)), 0, 0.8f);
        component.setJumpsToTarget(true);
        if (level >= 4) component.setLives(2);
        component.setPlantFoodBehavior((_, _) -> {});
        p.addComponent(component);
        return p;
    }

    private static Plant buildGrapeshot() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.GRAPESHOT, 1);
        int damage = level >= 2 ? 2400 : 1800;
        int bounceMax = level >= 3 ? 5 : 4;
        Plant p = new Plant(PlantType.GRAPESHOT, 1000, damage);
        p.addComponent(new ExplosivesComponent(InstantTrigger.INSTANCE,
                new CompositeBehavior(new AreaDamageBehavior(
                        damage, new CircularRange(1)), new GrapeshotBehavior(bounceMax)), 0,
            1.65f));
        return p;
    }

    private static Plant buildJalapeno() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.JALAPENO, 1);
        int damage = level >= 3 ? 2400 : 1800;
        Plant p = new Plant(PlantType.JALAPENO, 1000, damage);
        p.addComponent(new ExplosivesComponent(InstantTrigger.INSTANCE,
                new CompositeBehavior(new AreaDamageBehavior(damage, LineRange.INSTANCE),
                        new MeltIceBehavior(LineRange.INSTANCE)), 0, 0.65f));
        p.setFire(true);
        return p;
    }

    private static Plant buildDoomShroom() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.DOOM_SHROOM, 1);
        int damage = level >= 3 ? 2600 : 1800;
        Plant p = new Plant(PlantType.DOOM_SHROOM, 1000, damage);
        p.addComponent(new ExplosivesComponent(InstantTrigger.INSTANCE,
                new CompositeBehavior(new AreaDamageBehavior(damage,
                        new CircularRange(2)),
                    new MakeUnplantableBehavior(new CircularRange(0))), 0, 2));
        return p;
    }

    private static Plant buildTangleKelp() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.TANGLE_KELP, 1);
        Plant p = new Plant(PlantType.TANGLE_KELP, 1000, 20000);
        ExplosivesComponent component = new ExplosivesComponent(
                new ProximityTrigger(App.getCellWidth()/2),
                new SingleTargetDamageBehavior(new CircularRange(0)), 0, 2.5f);
        if (level >= 3) component.setLives(2);
        component.setPlantFoodBehavior((_, _) -> {
            List<Zombie> waterZombies = App.getCurrentGame().getActiveZombies().stream()
                    .filter(zombie -> {
                        Cell zombieCell = Cell.findZombieCell(LevelMenuController.getGameCells(), zombie);
                        return zombieCell != null && zombieCell.isWater();
                    })
                    .collect(Collectors.toList());
            Collections.shuffle(waterZombies);
            waterZombies.stream()
                    .limit(3)
                    .forEach(Zombie::die);
        });
        p.addComponent(component);
        p.addComponent(new PlacementBehaviorComponent(PlantLayer.MAIN, false, 0, true));
        return p;
    }

    private static Plant buildIcebergLettuce() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.ICEBURG, 1);
        int freezeTime = level >= 3 ? 6 : 4;
        Plant p = new Plant(PlantType.ICEBURG, 1000, 0);
        ExplosivesComponent component = new ExplosivesComponent(
                new ProximityTrigger(App.getCellWidth()/2),
                new FreezeZombieBehavior(new CircularRange(0), freezeTime), 0, 1.5f);
        component.setPlantFoodBehavior((_, _) -> {
            App.getCurrentGame().getActiveZombies()
                    .forEach(zombie -> zombie.freeze(7.0f));
        });
        p.addComponent(component);
        return p;
    }

    private static Plant buildIceShroom() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.ICE_SHROOM, 1);
        int freezeTime = level >= 2 ? 6 : 4;
        int damage = level >= 4 ? 50 : 0;
        Plant p = new Plant(PlantType.ICE_SHROOM, 1000, damage);
        p.addComponent(new ExplosivesComponent(InstantTrigger.INSTANCE,
                new CompositeBehavior(new FreezeZombieBehavior(new CircularRange(10), freezeTime),
                        new AreaDamageBehavior(damage, new CircularRange(10))), 0, 1.1f));
        return p;
    }

    private static Plant buildHotPotato() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.HOT_POTATO, 1);
        int radius = level >= 3 ? 1 : 0;
        Plant p = new Plant(PlantType.HOT_POTATO, 1000, 0);
        ExplosivesComponent component = new ExplosivesComponent(InstantTrigger.INSTANCE,
                new MeltIceBehavior(new CircularRange(radius)), 0, 4.5f);
        if (level >= 4) {
            component.scheduleDelayedBehavior(new AreaDamageBehavior(100, new CircularRange(1)));
        }
        p.addComponent(component);
        p.setFire(true);
        return p;
    }

    private static Plant buildGraveBuster() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.GRAVE_BUSTER, 1);
        int delay = level >= 2 ? 3 : 2;
        Plant p = new Plant(PlantType.GRAVE_BUSTER, 300, 0);
        ExplosivesComponent component = new ExplosivesComponent(
                InstantTrigger.INSTANCE, new RemoveGraveBehavior(), 0, delay);
        if (level >= 4) {
            component.scheduleDelayedBehavior(new AreaDamageBehavior(100, new CircularRange(1)));
        }
        p.addComponent(component);
        return p;
    }
}
