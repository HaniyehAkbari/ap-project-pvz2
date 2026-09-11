package com.pvz2.view.table;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class CooldownOverlay extends Actor {
    private final TextureRegion pixelRegion;
    private float progress = 0f;

    public CooldownOverlay(TextureRegion whitePixelRegion) {
        this.pixelRegion = whitePixelRegion;
        setColor(0f, 0f, 0f, 0.65f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (progress <= 0) return;

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        float fullWidth = getWidth();
        float fullHeight = getHeight();
        float currentHeight = fullHeight * progress;

        batch.draw(pixelRegion, getX(), getY(), fullWidth, currentHeight);

        batch.setColor(Color.WHITE);
    }

    public void setProgress(float progress) {
        this.progress = Math.min(1.0f, Math.max(0.0f, progress));
    }

}
