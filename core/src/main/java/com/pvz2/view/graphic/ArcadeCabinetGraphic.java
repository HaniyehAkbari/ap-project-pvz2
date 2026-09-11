package com.pvz2.view.graphic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.pvz2.models.zombie.zombiesType.PusherZombie;
import com.pvz2.view.util.DamageFlashShader;
import pvz.libpvz.pam.PamPlayer;

public class ArcadeCabinetGraphic {
    private static final String PAM_PATH = "768/FULL/EFFECTS/80S_ARCADE_CABINET/80S_ARCADE_CABINET.PAM";
    private static final float OFFSET_X = -120f;

    private final PusherZombie pusherZombie;

    private float animTime = 0f;
    private String currentClip = "idle";
    private boolean isLoop = true;

    public ArcadeCabinetGraphic(PusherZombie pusherZombie) {
        this.pusherZombie = pusherZombie;
    }

    public void update(float delta, PamPlayer pamPlayer) {
        animTime += delta;

        boolean destroyed = pusherZombie.getObjectHealth() <= 0;
        if (destroyed) {
            if (!currentClip.equals("death")) {
                playClip("death", false);
            }
        } else if (pusherZombie.isPushing()) {
            if (!currentClip.equals("active")) {
                playClip("active", true);
            }
        } else {
            if (!currentClip.equals("idle")) {
                playClip("idle", true);
            }
        }

        if (pamPlayer != null) {
            pamPlayer.loadAsync(PAM_PATH, null);
        }
    }

    public void draw(SpriteBatch batch, PamPlayer pamPlayer) {
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

        pamPlayer.draw(batch, PAM_PATH, currentClip, animTime, renderX, renderY, scaleX, 0.8f, isLoop);

        if (flashAmount > 0f) {
            batch.setShader(null);
        }
    }

    private void playClip(String clip, boolean loop) {
        if (!this.currentClip.equals(clip)) {
            this.currentClip = clip;
            this.isLoop = loop;
            this.animTime = 0f;
        }
    }

    public boolean isDeathAnimationFinished() {
        return "death".equals(currentClip) && animTime >= 1.2f;
    }
}
