package com.pvz2.view.graphic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.pvz2.models.world.obstacles.BarrelObstacle;
import com.pvz2.view.util.DamageFlashShader;
import pvz.libpvz.pam.PamPlayer;

public class BarrelObstacleGraphic {
    private static final String BARREL_PAM_PATH =
        "768/FULL/ZOMBIE/ZOMBIE_PIRATE_BARREL_PUSHER_BARREL/ZOMBIE_PIRATE_BARREL_PUSHER_BARREL.PAM";
    private static final float DIE_DURATION = 1.0f;

    private final BarrelObstacle obstacle;
    private String currentClip = "roll";
    private float animTime = 0f;
    private boolean isLoop = true;
    private boolean breakFinished = false;

    public BarrelObstacleGraphic(BarrelObstacle obstacle) {
        this.obstacle = obstacle;
    }

    public void update(float delta, PamPlayer pamPlayer) {
        if (breakFinished) return;

        if (obstacle.isDestroyed() && !currentClip.equals("die")) {
            currentClip = "die";
            isLoop = false;
            animTime = 0f;
        }

        animTime += delta;

        if (currentClip.equals("die") && animTime >= DIE_DURATION) {
            breakFinished = true;
        }

        pamPlayer.loadAsync(BARREL_PAM_PATH, null);
    }

    public void draw(SpriteBatch batch, PamPlayer pamPlayer) {
        if (breakFinished) return;

        float flashAmount = obstacle.getDamageFlashProgress();
        if (flashAmount > 0f) {
            ShaderProgram shader = DamageFlashShader.get();
            batch.setShader(shader);
            shader.setUniformf("u_flashColor", 1f, 1f, 1f);
            shader.setUniformf("u_flashAmount", flashAmount);
        }

        pamPlayer.draw(batch, BARREL_PAM_PATH, currentClip, animTime,
            obstacle.getX(), obstacle.getY(), 0.8f, 0.8f, isLoop);

        if (flashAmount > 0f) {
            batch.setShader(null);
        }
    }

    public boolean isBreakFinished() {
        return breakFinished;
    }

    public BarrelObstacle getObstacle() {
        return obstacle;
    }


}
