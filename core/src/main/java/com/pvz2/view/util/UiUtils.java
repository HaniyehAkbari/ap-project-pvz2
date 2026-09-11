package com.pvz2.view.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public final class UiUtils {

    private UiUtils() {
    }

    private static final java.util.Map<Integer, TextureRegion> SOLID_COLOR_CACHE = new java.util.HashMap<>();


    public static TextureRegion getSolidColorRegion(Color color) {
        int key = Color.rgba8888(color);
        return SOLID_COLOR_CACHE.computeIfAbsent(key, k -> createSolidColorRegion(color));
    }

    public static Drawable createSolidColor(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    public static TextureRegion createSolidColorRegion(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegion(texture);
    }

    public static Drawable darkChipBackground() {
        return createSolidColor(new Color(0f, 0f, 0f, 0.55f));
    }
}
