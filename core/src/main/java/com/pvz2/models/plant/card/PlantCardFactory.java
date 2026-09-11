package com.pvz2.models.plant.card;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pvz2.models.enums.PlantType;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

public class PlantCardFactory {
    private static Map<String, Map<Integer, UpgradeConfig>> upgradeRules;

    public static void init(Reader reader) {
        Gson gson = new Gson();
        Type typeOfHashMap = new TypeToken<Map<String, Map<Integer, UpgradeConfig>>>() {
        }.getType();
        upgradeRules = gson.fromJson(reader, typeOfHashMap);
    }

    public static PlantCard createCard(PlantType type, int userLevel) {
        PlantCard card = new PlantCard(type, type.baseSunCost, type.baseCoolDown);

        if (upgradeRules != null && upgradeRules.containsKey(type.name())) {
            UpgradeConfig config = upgradeRules.get(type.name()).get(userLevel);

            if (config != null) {
                card.setSunCost(card.getSunCost() + config.getSunCostModifier());
                card.setMaxCooldownTicks(card.getMaxCooldownTicks() - config.getCooldownReductionTicks());
            }
        }

        return card;
    }

    public static ImitatorCard createImitatorCard(PlantType targetType, int targetLevel, int imitatorLevel) {
        PlantCard baseTargetCard = createCard(targetType, targetLevel);

        int finalSunCost = baseTargetCard.getSunCost();
        float finalMaxCooldownTicks = baseTargetCard.getMaxCooldownTicks();

        if (upgradeRules != null && upgradeRules.containsKey(PlantType.IMITATER.name())) {
            UpgradeConfig imitatorConfig = upgradeRules.get(PlantType.IMITATER.name()).get(imitatorLevel);

            if (imitatorConfig != null) {
                finalSunCost += imitatorConfig.getSunCostModifier();
                finalMaxCooldownTicks -= imitatorConfig.getCooldownReductionTicks();
            }
        }

        finalSunCost = Math.max(0, finalSunCost);
        finalMaxCooldownTicks = Math.max(1, finalMaxCooldownTicks);

        return new ImitatorCard(targetType, finalSunCost, finalMaxCooldownTicks);
    }
}
