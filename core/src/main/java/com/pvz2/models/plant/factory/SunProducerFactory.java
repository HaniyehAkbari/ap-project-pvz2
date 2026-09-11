package com.pvz2.models.plant.factory;

import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.SunProducerComponent;

import java.util.Map;
import java.util.function.Supplier;

public class SunProducerFactory {
    static void register(Map<PlantType, Supplier<Plant>> registry) {
        registry.put(PlantType.SUNFLOWER, SunProducerFactory::buildSunflower);
        registry.put(PlantType.TWIN_SUNFLOWER, SunProducerFactory::buildTwinSunflower);
        registry.put(PlantType.SUN_SHROOM, SunProducerFactory::buildSunShroom);
        registry.put(PlantType.PRIMAL_SUNFLOWER, SunProducerFactory::buildPrimalSunflower);
        registry.put(PlantType.GOLD_BLOOM, SunProducerFactory::buildGoldBloom);
    }

    private static Plant buildSunflower() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.SUNFLOWER, 1);
        int health = level >= 3 ? 450 : 300;
        int prodTime = level >= 2 ? 24 : 22;
        boolean doubleSunChance = level == 4;
        Plant p = new Plant(PlantType.SUNFLOWER, health, 0);
        p.addComponent(new SunProducerComponent(50, 1, prodTime, doubleSunChance, false, 3, 0));
        return p;
    }

    private static Plant buildTwinSunflower() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.TWIN_SUNFLOWER, 1);
        int health = level >= 3 ? 450 : 300;
        int prodTime = level >= 2 ? 24 : 22;
        Plant p = new Plant(PlantType.TWIN_SUNFLOWER, health, 0);
        p.addComponent(new SunProducerComponent(50, 2, prodTime, false, false, 5, 0));
        return p;
    }

    private static Plant buildSunShroom() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.SUN_SHROOM, 1);
        int health = level >= 3 ? 450 : 300;
        int growTimeReduce = level >= 2 ? 5 : 0;
        boolean doubleSunChance = level == 4;
        Plant p = new Plant(PlantType.SUN_SHROOM, health, 0);
        p.addComponent(new SunProducerComponent(25, 1, 24, doubleSunChance, true, 3,
            growTimeReduce));
        return p;
    }

    private static Plant buildPrimalSunflower() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.PRIMAL_SUNFLOWER, 1);
        int health = level >= 3 ? 450 : 300;
        int prodTime = level >= 2 ? 24 : 22;
        Plant p = new Plant(PlantType.PRIMAL_SUNFLOWER, health, 0);
        p.addComponent(new SunProducerComponent(75, 1, prodTime, false, false, 3, 0));
        return p;
    }

    private static Plant buildGoldBloom() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.GOLD_BLOOM, 1);
        Plant p = new Plant(PlantType.GOLD_BLOOM, 0, 0);
        int sunSize = level >= 3 ? 150 : 125;
        p.addComponent(new SunProducerComponent(sunSize, 3, 0, false, false, 0, 0, true));
        return p;
    }
}
