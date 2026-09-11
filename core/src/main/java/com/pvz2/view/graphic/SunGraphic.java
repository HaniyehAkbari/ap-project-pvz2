package com.pvz2.view.graphic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.pvz2.Main;
import com.pvz2.models.core.App;
import com.pvz2.models.world.GameState;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.Sun;
import com.pvz2.models.world.SunType;
import pvz.libpvz.pam.PamPlayer;

public class SunGraphic {
    /** Same value as ZombieGraphic/ProjectileGraphic — keep them matching so a falling sun
     *  and a walking zombie don't visibly move at different "smoothness" from each other. */
    private static final float NETWORK_LERP_SPEED = 15f;

    private Sun sun;
    private float animTime = 0f;
    private boolean popping = false;
    private boolean popFinished = false;

    private float displayX, displayY;
    private boolean networked = false;

    public SunGraphic(Sun sun) {
        this.sun = sun;
        this.displayX = sun.getX();
        this.displayY = sun.getY();
    }

    public void update(float delta) {
        GameWorld world = App.getCurrentGame();
        if (world != null && world.getState() != GameState.PLAYING) delta = 0;
        animTime += delta;

        if ((sun.isCollected() || sun.isExploded()) && !popping) {
            popping = true;
            animTime = 0f;
        }

        if (popping && animTime >= 0.4f) {
            popFinished = true;
            sun.setExpired(true);
            sun.collect();
        }

        updateDisplayPosition(delta);
    }

    private void updateDisplayPosition(float delta) {
        if (!networked) {
            // offline: same live object every frame, already exact — no smoothing needed or wanted
            displayX = sun.getX();
            displayY = sun.getY();
            return;
        }

        float t = Math.min(1f, NETWORK_LERP_SPEED * delta);
        displayX = MathUtils.lerp(displayX, sun.getX(), t);
        displayY = MathUtils.lerp(displayY, sun.getY(), t);
    }

    public void draw(SpriteBatch batch, PamPlayer pamPlayer, Main game) {
        if (popFinished) return;

        String pamPath = getPamPath(sun.getType());
        String animState = popping ? "attack" : "animation";

        float scale = getScale(sun.getType());

        try {
            pamPlayer.draw(
                batch,
                pamPath,
                animState,
                animTime* App.getSpeed(),
                displayX,
                displayY,
                scale,
                scale,
                !popping
            );
        } catch (Exception e) {
        }
    }

    private String getPamPath(SunType type) {
        if (type == SunType.RADIOACTIVE) {
            return "768/FULL/EFFECTS/SUN_BOMB/SUN_BOMB.PAM";
        }
        return "768/INITIAL/EFFECTS/SUN/SUN.PAM";
    }

    public static float getScale(SunType type) {
        if (type == null) return 1f;
        switch (type) {
            case SPECIAL: return 1.5f;
            case RADIOACTIVE: return 1.1f;
            default: return 1f;
        }
    }

    public void updateModel(Sun newSun) {
        this.sun = newSun;
        this.networked = true;
    }
    public Sun getSun() { return sun; }
    public boolean isPopFinished() { return popFinished; }
}
