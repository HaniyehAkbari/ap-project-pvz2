package com.pvz2.view.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.pvz2.Main;
import com.pvz2.models.network.onlineIZombie.messages.ReactionCategory;
import pvz.skin.BorderedTable;

public final class ReactionBubbleFactory {

    private static final float STICKER_SCALE = 0.45f;

    private static final float MIN_WIDTH = 220f;
    private static final float MIN_HEIGHT = 110f;

    private static final String BORDER_DRAWABLE_KEY = "image_ui_dialog_asset_dialogborder_10";
    private static final float BORDER_SAFETY_MARGIN = 20f;

    public static Actor build(Main game, Skin skin, ReactionCategory category, int index) {
        BorderedTable bubble = new BorderedTable();
        bubble.pad(16);
        switch (category) {
            case TEXT -> {
                Label label = new Label(ReactionAssets.TEXTS[index], skin, "medium");
                label.setWrap(true);
                label.setAlignment(Align.center);
                label.setFontScale(1.3f);
                label.setColor(Color.BLACK);
                bubble.add(label).width(275);
            }
            case EMOJI -> {
                TextureRegion region = game.textureBank.region(ReactionAssets.EMOJI_REGIONS[index]);
                if (region != null) bubble.add(new Image(region)).size(150);
            }
            case STICKER -> {
                PamActor sticker = new PamActor(game.pamPlayer, ReactionAssets.STICKER_PATHS[index],
                    ReactionAssets.STICKER_ANIMATIONS[index], STICKER_SCALE, null);
                Container<PamActor> box = new Container<>(sticker);
                box.center();
                bubble.add(box).size(140);
            }
        }
        Drawable borderArt = skin.getDrawable(BORDER_DRAWABLE_KEY);
        float floorWidth = Math.max(MIN_WIDTH, borderArt.getMinWidth() + BORDER_SAFETY_MARGIN);
        float floorHeight = Math.max(MIN_HEIGHT, borderArt.getMinHeight() + BORDER_SAFETY_MARGIN);


        Container<BorderedTable> sized = new Container<>(bubble);
        sized.minWidth(floorWidth);
        sized.minHeight(floorHeight);

        sized.fill();
        return sized;
    }

    private ReactionBubbleFactory() {}
}
