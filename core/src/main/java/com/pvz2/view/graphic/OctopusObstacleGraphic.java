package com.pvz2.view.graphic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.pvz2.models.world.obstacles.OctopusObstacle;
import com.pvz2.view.util.DamageFlashShader;
import pvz.libpvz.pam.PamPlayer;

import java.util.Random;

public class OctopusObstacleGraphic {
    private static final String PAM_PATH =
        "768/FULL/EFFECTS/ZOMBIE_OCTOPUS_PROJECTILE/ZOMBIE_OCTOPUS_PROJECTILE.PAM";

    private static final String TOSS_CLIP = "animation";
    private static final String[] SIT_CLIPS = {"animation2", "animation3", "animation4"};
    private static final String DIE_CLIP = "die";

    private static final float TOSS_DURATION = 0.5f;
    private static final float DIE_DURATION = 1.0f;

    private final OctopusObstacle obstacle;
    private final String sitClip;

    private float animTime = 0f;
    private String currentClip = TOSS_CLIP;
    private boolean isLoop = false;
    private boolean landed = false;
    private boolean dying = false;

    public OctopusObstacleGraphic(OctopusObstacle obstacle) {
        this.obstacle = obstacle;
        this.sitClip = SIT_CLIPS[new Random().nextInt(SIT_CLIPS.length)];
    }

    public void update(float delta, PamPlayer pamPlayer) {
        animTime += delta;

        if (obstacle.isDestroyed() && !dying) {
            dying = true;
            landed = true;
            currentClip = DIE_CLIP;
            isLoop = false;
            animTime = 0f;
        } else if (!dying && !landed && animTime >= TOSS_DURATION) {
            landed = true;
            currentClip = sitClip;
            isLoop = true;
            animTime = 0f;
        }

        pamPlayer.loadAsync(PAM_PATH, null);
    }

    public void draw(SpriteBatch batch, PamPlayer pamPlayer) {
        float flashAmount = obstacle.getDamageFlashProgress();
        if (flashAmount > 0f) {
            ShaderProgram shader = DamageFlashShader.get();
            batch.setShader(shader);
            shader.setUniformf("u_flashColor", 1f, 1f, 1f);
            shader.setUniformf("u_flashAmount", flashAmount);
        }

        pamPlayer.draw(batch, PAM_PATH, currentClip, animTime,
            obstacle.getX(), obstacle.getY(), 0.8f, 0.8f, isLoop);

        if (flashAmount > 0f) {
            batch.setShader(null);
        }
    }

    public boolean isDeathAnimationFinished() {
        return dying && animTime >= DIE_DURATION;
    }

    public OctopusObstacle getObstacle() {
        return obstacle;
    }
}
