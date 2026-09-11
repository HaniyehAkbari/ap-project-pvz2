package com.pvz2.models.zombie;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvz2.models.zombie.data.ArmorProperties;
import com.pvz2.models.zombie.data.ZombieProperties;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ZombieRegistry {
    private static final Logger LOGGER = Logger.getLogger(ZombieRegistry.class.getName());
    private static final Map<String, ZombieProperties> ZOMBIE_MAP = new HashMap<>();
    private static final Map<String, ArmorProperties> ARMOR_MAP = new HashMap<>();
    private static boolean initialized = false;
    private static final String ZOMBIES_RESOURCE_PATH = "/data/zombies.json";
    private static final String ARMORS_RESOURCE_PATH = "/data/ArmorTypeData.json";

    public static void init() {
        if (initialized) return;
        loadZombies();
        loadArmors();
        initialized = true;
    }

    private static void loadZombies() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        try (InputStream is = ZombieRegistry.class.getResourceAsStream(ZOMBIES_RESOURCE_PATH)) {
            if (is == null) {
                LOGGER.severe(ZOMBIES_RESOURCE_PATH + " not found in resources!");
                return;
            }

            // خواندن مستقیم از InputStream توسط Jackson
            List<ZombieProperties> list = mapper.readValue(is,
                mapper.getTypeFactory().constructCollectionType(List.class, ZombieProperties.class));

            for (ZombieProperties zp : list) {
                if (zp.getAliases() != null) {
                    for (String alias : zp.getAliases()) {
                        ZOMBIE_MAP.put(alias, zp);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Failed to load zombies.json: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void loadArmors() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        try (InputStream is = ZombieRegistry.class.getResourceAsStream(ARMORS_RESOURCE_PATH)) {
            if (is == null) {
                LOGGER.severe(ARMORS_RESOURCE_PATH + " not found in resources!");
                return;
            }

            // خواندن مستقیم از InputStream توسط Jackson
            List<ArmorProperties> list = mapper.readValue(is,
                mapper.getTypeFactory().constructCollectionType(List.class, ArmorProperties.class));

            for (ArmorProperties ap : list) {
                if (ap.getAliases() != null) {
                    for (String alias : ap.getAliases()) {
                        ARMOR_MAP.put(alias, ap);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Failed to load ArmorTypeData.json: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static ZombieProperties getZombieProperties(String alias) {
        return ZOMBIE_MAP.get(alias);
    }

    public static ArmorProperties getArmorProperties(String alias) {
        return ARMOR_MAP.get(alias);
    }
}
