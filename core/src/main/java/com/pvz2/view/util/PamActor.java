package com.pvz2.view.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;

import java.util.HashMap;

public class PamActor extends Actor {
    private final PamPlayer pamPlayer;
    private final String pamPath;
    private final String clipName;
    private ClipRef clipRef;
    private float stateTime = 0f;
    private float scale;
    private HashMap<String, Boolean> partVisibility;

    public PamActor(PamPlayer pamPlayer, String pamPath, String clipName, float scale,
                    HashMap<String, Boolean> partVisibility) {
        this.pamPlayer = pamPlayer;
        this.pamPath = pamPath;
        this.clipName = clipName;
        this.scale = scale;
        this.partVisibility = partVisibility;

        this.pamPlayer.loadAsync(pamPath, () -> {
            this.clipRef = pamPlayer.getClip(pamPath, clipName);
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (clipRef == null) {
            clipRef = pamPlayer.getClip(pamPath, clipName);
        }

        if (clipRef != null) {
            if (partVisibility == null){
                pamPlayer.draw(batch, clipRef, stateTime, getX(), getY(), scale, scale,
                    true);
            }
            else{
                pamPlayer.draw(batch, clipRef, stateTime, getX(), getY(), scale, scale, true,
                    partVisibility);
            }

        }
    }
}
