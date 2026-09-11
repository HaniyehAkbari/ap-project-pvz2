package com.pvz2.view.graphic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.CollectableType;
import com.pvz2.models.world.*;
import pvz.libpvz.pam.PamPlayer;

public class CollectableGraphic {
    private final Collectable collectable;
    private float animTime = 0f;

    public CollectableGraphic(Collectable collectable) {
        this.collectable = collectable;
    }

    public void update(float delta) {
        GameWorld world = App.getCurrentGame();
        if (world != null && world.getState() != GameState.PLAYING) delta = 0;
        animTime += delta;
    }

    public void draw(SpriteBatch batch, PamPlayer pamPlayer) {
        try {
            pamPlayer.draw(
                batch,
                getPamPath(collectable.getType()),
                getClip(collectable.getType()),
                animTime* App.getSpeed(),
                collectable.getX(),
                collectable.getY(),
                0.5f,
                0.5f,
                true
            );
        } catch (Exception e) {
        }
    }

    private String getPamPath(CollectableType type) {
        return switch (type){
            case POT -> "768/INITIAL/ZEN_GARDEN/SPROUTDOOBER/SPROUTDOOBER.PAM";
            case COIN -> "768/INITIAL/EFFECTS/COIN_SILVER/COIN_SILVER.PAM";
            case DIAMOND -> "768/INITIAL/EFFECTS/COIN_DIAMOND/COIN_DIAMOND.PAM";
            case PLANT_FOOD -> "768/INITIAL/EFFECTS/PLANTFOOD_PICKUP/PLANTFOOD_PICKUP.PAM";
        };
    }

    private String getClip(CollectableType type){
        return switch (type){
            case POT, COIN -> "animation";
            case DIAMOND, PLANT_FOOD -> "idle";
        };
    }
}
