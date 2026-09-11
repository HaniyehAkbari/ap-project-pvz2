package com.pvz2.models.plant.factory;

import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.ChomperMeleeComponent;
import com.pvz2.models.plant.components.DirectionalMeleeComponent;
import com.pvz2.models.plant.components.SquareMeleeComponent;

import java.util.Map;
import java.util.function.Supplier;

public class MeleeFactory {

    static void register(Map<PlantType, Supplier<Plant>> registry) {
        registry.put(PlantType.BONK_CHOY, MeleeFactory::buildBonkChoy);
        registry.put(PlantType.PHAT_BEET, MeleeFactory::buildPhatBeet);
        registry.put(PlantType.WASABI_WHIP, MeleeFactory::buildWasabiWhip);
        registry.put(PlantType.CHOMPER, MeleeFactory::buildChomper);
        registry.put(PlantType.KIWIBEAST, MeleeFactory::buildKiwibeast);
    }

    private static Plant buildBonkChoy() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.BONK_CHOY, 1);

        int health = (level >= 4) ? 500 : 300;
        int damage = (level >= 2) ? 20 : 15;
        float interval = (level >= 3) ? 0.2f : 0.3f;

        Plant plant = new Plant(PlantType.BONK_CHOY, health, damage);
        plant.addComponent(new DirectionalMeleeComponent(damage, interval, 1.5f * App.getCellWidth()));
        return plant;
    }

    private static Plant buildWasabiWhip() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.WASABI_WHIP, 1);

        int health = (level >= 4) ? 500 : 300;
        int damage = (level >= 2) ? 50 : 40;
        float rangeX = ((level >= 3) ? 2.5f : 1.5f) * App.getCellWidth();

        Plant plant = new Plant(PlantType.WASABI_WHIP, health, damage);
        plant.addComponent(new DirectionalMeleeComponent(damage, 2, rangeX));
        plant.setFire(true);
        return plant;
    }

    private static Plant buildPhatBeet() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.PHAT_BEET, 1);
        int damage = (level >= 2) ? 25 : 15;
        int health = (level >= 4) ? 500 : 300;
        float interval = (level >= 3) ? 3.0f : 4.0f;

        Plant plant = new Plant(PlantType.PHAT_BEET, health, damage);
        plant.addComponent(new SquareMeleeComponent(damage, interval));
        return plant;
    }

    private static Plant buildKiwibeast() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.KIWIBEAST, 1);
        int health = (level >= 2) ? 500 : 300;
        int baseDamage = (level >= 3) ? 30 : 15;
        int maxStage = (level >= 4) ? 4 : 3;

        Plant plant = new Plant(PlantType.KIWIBEAST, health, baseDamage);
        plant.addComponent(new SquareMeleeComponent(baseDamage, 3.0f, true, maxStage));
        return plant;
    }

    private static Plant buildChomper() {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(PlantType.CHOMPER, 1);

        int health = (level >= 3) ? 500 : 300;

        float digestSeconds = 40f;
        if (level >= 2) digestSeconds -= 2;
        if (level >= 4) digestSeconds -= 3;



        Plant plant = new Plant(PlantType.CHOMPER, health, 9999);
        plant.addComponent(new ChomperMeleeComponent(digestSeconds));

        return plant;
    }
}
