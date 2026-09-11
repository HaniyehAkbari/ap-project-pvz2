package com.pvz2.view.graphic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pvz2.models.world.ChapterWorld.FrostbiteCavesWorld;
import com.pvz2.view.util.LawnGrid;
import pvz.libpvz.pam.PamPlayer;

public class WindGraphic {
    private float stateTime = 0;
    private static final String PAM_PATH = "768/FULL/EFFECTS/FROSTBITE_CHILL_WIND/FROSTBITE_CHILL_WIND.PAM";
    private static final String PAM_CLIP = "animation";
    private static final float WORLD_X = LawnGrid.getCellX(4);
    private final float worldY;

    public WindGraphic(FrostbiteCavesWorld.Wind wind) {
        worldY = LawnGrid.getCellY(wind.getRow())+20;
    }

    public void updateAndDraw(float delta, PamPlayer pamPlayer, SpriteBatch batch){
        stateTime += delta;
        pamPlayer.draw(batch, PAM_PATH, PAM_CLIP, stateTime, WORLD_X, worldY,0.8f, 0.5f, false);
    }
}
