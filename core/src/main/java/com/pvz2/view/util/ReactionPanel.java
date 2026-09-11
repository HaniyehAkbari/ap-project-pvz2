package com.pvz2.view.util;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.pvz2.Main;
import com.pvz2.models.network.onlineIZombie.messages.ReactionCategory;
import pvz.skin.BorderedTable;

import java.util.function.BiConsumer;

public class ReactionPanel extends BorderedTable {
    private static final String TEXT_STYLE = "brown";
    private static final String EMOJI_STYLE = "purple";
    private static final String STICKER_STYLE = "green";
    private static final String PANEL_FONT = "FBUSV8C5EI_1";
    private static final float STICKER_SCALE = 0.4f;

    private final Main game;
    private final Skin skin;
    private final BiConsumer<ReactionCategory, Integer> onPick;

    private final Table tabsRow = new Table();
    private final Table content = new Table();

    public ReactionPanel(Main game, Skin skin, BiConsumer<ReactionCategory, Integer> onPick) {
        this.game = game;
        this.skin = skin;
        this.onPick = onPick;
        build();
        showCategory(ReactionCategory.TEXT);
    }

    private void build() {
        top();
        pad(16);
        defaults().space(8);

        TextButton textTab = coloredTextButton("TEXT", TEXT_STYLE, 0.7f);
        TextButton emojiTab = coloredTextButton("EMOJI", EMOJI_STYLE, 0.7f);
        TextButton stickerTab = coloredTextButton("STICKER", STICKER_STYLE, 0.7f);
        tabsRow.defaults().growX().height(44);
        tabsRow.add(textTab).padRight(6);
        tabsRow.add(emojiTab).padRight(6);
        tabsRow.add(stickerTab);

        textTab.addListener(tabListener(ReactionCategory.TEXT));
        emojiTab.addListener(tabListener(ReactionCategory.EMOJI));
        stickerTab.addListener(tabListener(ReactionCategory.STICKER));

        add(tabsRow).growX().padLeft(7).padRight(7).padTop(20).row();
        add(content).grow().padLeft(7).padRight(7).padTop(14);
    }

    private ClickListener tabListener(ReactionCategory category) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showCategory(category);
            }
        };
    }

    private void showCategory(ReactionCategory category) {
        content.clear();
        switch (category) {
            case TEXT -> buildTextContent();
            case EMOJI -> buildEmojiContent();
            case STICKER -> buildStickerContent();
        }
    }

    private void buildTextContent() {
        content.top();
        content.defaults().growX().space(10);
        for (int i = 0; i < ReactionAssets.TEXTS.length; i++) {
            int index = i;
            TextButton btn = coloredTextButton(ReactionAssets.TEXTS[i], TEXT_STYLE, 0.5f);
            Label label = btn.getLabel();
            label.setWrap(true);
            label.setAlignment(Align.center);
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onPick.accept(ReactionCategory.TEXT, index);
                }
            });
            content.add(btn).minHeight(56).row();
        }
    }

    private void buildEmojiContent() {
        content.center();
        Table grid = new Table();
        grid.defaults().space(14);
        for (int i = 0; i < ReactionAssets.EMOJI_REGIONS.length; i++) {
            int index = i;
            TextureRegion region = game.textureBank.region(ReactionAssets.EMOJI_REGIONS[i]);
            TextButton btn = coloredTextButton("", EMOJI_STYLE, 1f);
            if (region != null) {
                Image icon = new Image(region);
                Container<Image> iconBox = new Container<>(icon);
                iconBox.center();
                btn.add(iconBox).size(80);
            }
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onPick.accept(ReactionCategory.EMOJI, index);
                }
            });
            grid.add(btn).size(110);
        }
        content.add(grid);
    }

    private void buildStickerContent() {
        content.center();
        Table grid = new Table();
        grid.defaults().space(14);
        for (int i = 0; i < ReactionAssets.STICKER_PATHS.length; i++) {
            int index = i;
            PamActor sticker = new PamActor(game.pamPlayer, ReactionAssets.STICKER_PATHS[i],
                ReactionAssets.STICKER_ANIMATIONS[i], STICKER_SCALE, null);
            Container<PamActor> stickerBox = new Container<>(sticker);
            stickerBox.center();
            TextButton btn = coloredTextButton("", STICKER_STYLE, 1f);
            btn.add(stickerBox).size(80);
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onPick.accept(ReactionCategory.STICKER, index);
                }
            });
            grid.add(btn).size(110);
        }
        content.add(grid);
    }

    private TextButton coloredTextButton(String text, String styleName, float fontScale) {
        TextButton btn = new TextButton(text, skin, styleName);
        try {
            BitmapFont font = skin.getFont(PANEL_FONT);
            TextButton.TextButtonStyle base = skin.get(styleName, TextButton.TextButtonStyle.class);
            TextButton.TextButtonStyle styled = new TextButton.TextButtonStyle(base);
            styled.font = font;
            btn.setStyle(styled);
        } catch (Exception ignored) {
        }
        btn.getLabel().setFontScale(fontScale);
        return btn;
    }
}
