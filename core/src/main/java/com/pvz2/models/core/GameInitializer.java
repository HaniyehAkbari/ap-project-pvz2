package com.pvz2.models.core;

import com.pvz2.models.plant.card.PlantCardFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class GameInitializer {

    private static final String UPGRADES_RESOURCE_PATH = "/data/upgradeRules.json";

    public static void loadPlantUpgrades() {
        try (InputStream is = GameInitializer.class.getResourceAsStream(UPGRADES_RESOURCE_PATH)) {

            if (is == null) {
                throw new IllegalStateException("Upgrades file not found in resources: " + UPGRADES_RESOURCE_PATH);
            }

            try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                PlantCardFactory.init(reader);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
