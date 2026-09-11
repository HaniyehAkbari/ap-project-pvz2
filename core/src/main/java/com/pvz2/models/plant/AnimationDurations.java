package com.pvz2.models.plant;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pvz2.models.enums.PlantType;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AnimationDurations {
    private static final String RESOURCE_PATH = "/data/animation_durations.json";

    public static class ClipTiming {
        public float duration;
        public Float releaseTime; // null when not applicable
    }

    private static Map<String, Map<String, ClipTiming>> data;

    private static void ensureLoaded() {
        if (data != null) return;
        data = new HashMap<>();
        try (InputStream is = AnimationDurations.class.getResourceAsStream(RESOURCE_PATH)) {
            if (is == null) {
                System.err.println("AnimationDurations: missing " + RESOURCE_PATH);
                return;
            }
            Reader reader = new InputStreamReader(is);
            Type type = new TypeToken<Map<String, Map<String, ClipTiming>>>() {}.getType();
            Map<String, Map<String, ClipTiming>> parsed = new Gson().fromJson(reader, type);
            if (parsed != null) data = parsed;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ClipTiming lookup(PlantType type, String clip) {
        ensureLoaded();
        Map<String, ClipTiming> clips = data.get(type.name());
        return (clips != null) ? clips.get(clip) : null;
    }

    public static boolean hasClip(PlantType type, String clip) {
        return lookup(type, clip) != null;
    }

    public static float getDuration(PlantType type, String clip, float fallback) {
        ClipTiming t = lookup(type, clip);
        return (t != null) ? t.duration : fallback;
    }

    public static float getReleaseTime(PlantType type, String clip, float fallback) {
        ClipTiming t = lookup(type, clip);
        return (t != null && t.releaseTime != null) ? t.releaseTime : fallback;
    }
}
