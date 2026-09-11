package com.pvz2.view.graphic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.pvz2.Main;
import com.pvz2.models.core.App;
import com.pvz2.models.world.GameState;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.network.onlineIZombie.BrainCurrency;
import pvz.libpvz.pam.PamPlayer;

public class BrainCurrencyGraphic {

    private static final float NETWORK_LERP_SPEED = 15f;

    private BrainCurrency brainCurrency;
    private float animTime = 0f;

    private float displayX, displayY;
    private boolean networked = false;

    public BrainCurrencyGraphic(BrainCurrency brainCurrency) {
        this.brainCurrency = brainCurrency;
        this.displayX = brainCurrency.getX();
        this.displayY = brainCurrency.getY();
    }

    public void update(float delta) {
        GameWorld world = App.getCurrentGame();
        if (world != null && world.getState() != GameState.PLAYING) delta = 0;
        animTime += delta;

        if (brainCurrency.isCollected()) {
            animTime = 0f;
        }

        updateDisplayPosition(delta);
    }

    private void updateDisplayPosition(float delta) {
        if (!networked) {
            displayX = brainCurrency.getX();
            displayY = brainCurrency.getY();
            return;
        }

        float t = Math.min(1f, NETWORK_LERP_SPEED * delta);
        displayX = MathUtils.lerp(displayX, brainCurrency.getX(), t);
        displayY = MathUtils.lerp(displayY, brainCurrency.getY(), t);
    }

    public void draw(SpriteBatch batch, PamPlayer pamPlayer, Main game) {
        String pamPath = "768/FULL/ZOMBIE/POWER_BRAIN_PROJECTILE/POWER_BRAIN_PROJECTILE.PAM";
        String animState = "animation";

        float scale = 0.8f;

        try {
            pamPlayer.draw(
                batch,
                pamPath,
                animState,
                animTime,
                displayX,
                displayY,
                scale,
                scale,
                true
            );
        } catch (Exception e) {
        }
    }
    public void updateModel(BrainCurrency newBrainCurrency) {
        this.brainCurrency = newBrainCurrency;
        this.networked = true;
    }
}
