package com.pvz2.models.plant.factory;

import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.card.PlantCard;
import com.pvz2.models.plant.components.MintComponent;

import java.util.Map;
import java.util.function.Supplier;

public class MintFactory {
    static void register(Map<PlantType, Supplier<Plant>> registry) {
        registry.put(PlantType.ENLIGHTEN_MINT, () -> buildMint(PlantType.ENLIGHTEN_MINT));
        registry.put(PlantType.APPEASE_MINT, () -> buildMint(PlantType.APPEASE_MINT));
        registry.put(PlantType.ARMA_MINT, () -> buildMint(PlantType.ARMA_MINT));
        registry.put(PlantType.BOMBARD_MINT, () -> buildMint(PlantType.BOMBARD_MINT));
        registry.put(PlantType.ENFORCE_MINT, () -> buildMint(PlantType.ENFORCE_MINT));
        registry.put(PlantType.REINFORCE_MINT, () -> buildMint(PlantType.REINFORCE_MINT));
        registry.put(PlantType.ENCHANT_MINT, () -> buildMint(PlantType.ENCHANT_MINT));
        registry.put(PlantType.SPEAR_MINT, () -> buildMint(PlantType.SPEAR_MINT));
        registry.put(PlantType.CONTAIN_MINT, () -> buildMint(PlantType.CONTAIN_MINT));

    }

    private static Plant buildMint(PlantType type) {
        int level = App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(type, 1);
        boolean resetCooldown = level >= 4;
        Plant p = new Plant(type, 300, 0);
        p.addComponent(new MintComponent(type, _ -> {
            for (Plant plant : App.getCurrentGame().getActivePlants()) {
                if (plant.getType().family == type.family) {
                    plant.activatePlantFood();
                }
            }
            if (resetCooldown) {
                for (PlantCard card : App.getCurrentGame().getPlantLists()) {
                    card.reset();
                }
            }
        }, 5f));
        return p;
    }
}
