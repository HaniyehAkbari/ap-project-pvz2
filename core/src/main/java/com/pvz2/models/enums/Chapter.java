package com.pvz2.models.enums;

public enum Chapter {
    EGYPT,
    BIG_WAVE_BEACH,
    DARK_AGES,
    FROSTBITE_CAVES;

    public static Chapter fromString(String text) {
        if (text == null) {
            return null;
        }

        for (Chapter chapter : Chapter.values()) {
            if (chapter.name().equalsIgnoreCase(text.trim())) {
                return chapter;
            }
        }

        return null;
    }
}
