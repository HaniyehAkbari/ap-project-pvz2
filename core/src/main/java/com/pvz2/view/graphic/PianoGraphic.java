package com.pvz2.view.graphic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.pvz2.models.zombie.zombiesType.PusherZombie;
import com.pvz2.view.util.DamageFlashShader;
import pvz.libpvz.pam.PamPlayer;

public class PianoGraphic {
    private static final String PAM_PATH = "768/FULL/ZOMBIE/PIANO/PIANO.PAM";
    private static final float OFFSET_X = -60f;

    private final PusherZombie pusherZombie;

    private float animTime = 0f;
    private String currentClip = "idle";
    private boolean isLoop = true;

    private int lastObjectHealth;
    private boolean inDamageClip = false;
    private float damageClipTimer = 0f;
    private static final float DAMAGE_CLIP_DURATION = 0.5f;

    public PianoGraphic(PusherZombie pusherZombie) {
        this.pusherZombie = pusherZombie;
        this.lastObjectHealth = pusherZombie.getObjectHealth();
    }

    public void update(float delta, PamPlayer pamPlayer) {
        animTime += delta;

        int currentHealth = pusherZombie.getObjectHealth();
        boolean destroyed = currentHealth <= 0;

        if (destroyed) {
            if (!currentClip.equals("die")) {
                playClip("die", false);
            }
        } else {
            if (inDamageClip) {
                damageClipTimer += delta;
                if (damageClipTimer >= DAMAGE_CLIP_DURATION) {
                    inDamageClip = false;
                }
            }

            if (currentHealth < lastObjectHealth && !inDamageClip) {
                inDamageClip = true;
                damageClipTimer = 0f;
                playClip("damage", false);
            } else if (!inDamageClip && !currentClip.equals("play")) {
                playClip("play", true);
            }
        }

        lastObjectHealth = currentHealth;

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
        return "die".equals(currentClip) && animTime >= 1.2f;
    }
}
