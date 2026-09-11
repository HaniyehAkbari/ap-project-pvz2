package com.pvz2.view.graphic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.pvz2.models.zombie.zombiesType.PusherZombie;
import com.pvz2.view.util.DamageFlashShader;
import pvz.libpvz.pam.PamPlayer;

public class IceBlockGraphic {
    private static final String PAM_PATH =
        "768/INITIAL/EFFECTS/ICEBLOOM_ICE_BLOCK_ZOMBIE/ICEBLOOM_ICE_BLOCK_ZOMBIE.PAM";
    private static final String IDLE_CLIP = "idle";
    private static final float OFFSET_X = -130f;

    private final PusherZombie pusherZombie;
    private float animTime = 0f;

    public IceBlockGraphic(PusherZombie pusherZombie) {
        this.pusherZombie = pusherZombie;
    }

    public void update(float delta, PamPlayer pamPlayer) {
        if (pusherZombie.getObjectHealth() <= 0) return;
        animTime += delta;
        if (pamPlayer != null) {
            pamPlayer.loadAsync(PAM_PATH, null);
        }
    }

    public void draw(SpriteBatch batch, PamPlayer pamPlayer) {
        if (pusherZombie.getObjectHealth() <= 0) return;

        float renderX = pusherZombie.getX() + OFFSET_X;
        float renderY = pusherZombie.getY();
        float scaleX = pusherZombie.getSpeed() < 0 ? -0.8f : 0.8f;

        float flashAmount = pusherZombie.getObjectDamageFlashProgress();
        if (flashAmount > 0f) {
            ShaderProgram shader = DamageFlashShader.get();
            batch.setShader(shader);
            shader.setUniformf("u_flashColor", 1f, 1f, 1f);
            shader.setUniformf("u_flashAmount", flashAmount);
        }

        pamPlayer.draw(batch, PAM_PATH, IDLE_CLIP, animTime, renderX, renderY, scaleX, 0.8f, true);

        if (flashAmount > 0f) {
            batch.setShader(null);
        }
    }
}
