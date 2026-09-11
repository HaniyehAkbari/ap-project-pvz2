package com.pvz2.view.graphic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pvz2.models.core.App;
import com.pvz2.models.world.Sandstorm;
import pvz.libpvz.pam.PamPlayer;

public class SandstormGraphic {
    private final Sandstorm sandstorm;
    private static final String SANDSTORM_PAM_PATH = "768/INITIAL/EFFECTS/SANDSTORM_TOP/SANDSTORM_TOP.PAM";

    public SandstormGraphic(Sandstorm sandstorm) {
        this.sandstorm = sandstorm;
    }

    public void draw(SpriteBatch batch, PamPlayer pamPlayer) {
        if (sandstorm.isFinished()) return;

        String animName;
        switch (sandstorm.getState()) {
            case INTRO:
                animName = "intro";
                break;
            case OUTRO:
                animName = "outro";
                break;
            case LOOP:
            default:
                animName = "loop";
                break;
        }

        try {
            pamPlayer.draw(
                batch,
                SANDSTORM_PAM_PATH,
                animName,
                sandstorm.getStateTime()* App.getSpeed(),
                sandstorm.getX(),
                sandstorm.getY(),
                1.0f,
                1.0f,
                true
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
