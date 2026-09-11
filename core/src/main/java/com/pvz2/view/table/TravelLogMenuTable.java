package com.pvz2.view.table;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.pvz2.Main;
import com.pvz2.controller.TravelLogMenuController;
import com.pvz2.controller.TravelLogMenuController.MinigameLevelInfo;
import com.pvz2.controller.TravelLogMenuController.QuestGroupView;
import com.pvz2.controller.TravelLogMenuController.VariantView;
import com.pvz2.models.miniGame.MiniGames;
import com.pvz2.models.quest.reward.CurrencyReward;
import com.pvz2.models.quest.reward.RandomSeedPacketReward;
import com.pvz2.models.quest.reward.RandomUnlockReward;
import com.pvz2.models.quest.reward.Reward;
import com.pvz2.models.quest.types.DailyQuest;
import com.pvz2.models.quest.types.EpicChallengeQuest;
import com.pvz2.models.quest.types.MainQuest;
import com.pvz2.view.audios.AudioManager;
import com.pvz2.view.audios.GameMusic;
import com.pvz2.view.audios.GameSFX;
import com.pvz2.view.audios.SFXManager;
import com.pvz2.view.screen.MainMenuScreen;

import java.util.List;

public class TravelLogMenuTable extends Table {

    private enum Tab { MAIN, DAILY, CHALLENGE, MINIGAME }
    private static final Color CHALLENGE_TINT = new Color(0.85f, 0.4f, 0.25f, 1f);
    private static final Color MINIGAME_TINT = new Color(0.55f, 0.35f, 0.85f, 1f);
    private static final Color ACTIVE_TINT = Color.WHITE;
    private static final Color DIVIDER_COLOR = new Color(0.55f, 0.42f, 0.22f, 0.55f);
    private static final Color DESC_COLOR = Color.BLACK;
    private static final Color PROGRESS_TRACK_COLOR = new Color(0.75f, 0.7f, 0.55f, 1f);
    private static final Color PROGRESS_FILL_COLOR = new Color(0.25f, 0.7f, 0.25f, 1f);
    private static final Color TITLE_TAG_COLOR = new Color(1f, 0.82f, 0.1f, 0.95f);
    private static final int COMPACT_LABEL_MAX_LEN = 3;
    private static final int BADGES_PER_ROW = 5;
    private static final float PROGRESS_BAR_WIDTH = 240f;
    private static final float PROGRESS_BAR_HEIGHT = 16f;
    private final TravelLogMenuController controller = new TravelLogMenuController();
    private final Main game;
    private final Skin skin;
    private Tab currentTab = Tab.MAIN;
    private final Table contentTable = new Table();
    private final java.util.Map<Tab, Image> tabBackgrounds = new java.util.EnumMap<>(Tab.class);

    public TravelLogMenuTable(Main game, Skin skin) {
        this.game = game;
        this.skin = skin;
        build();
    }

    private void build() {
        pad(5);
        Table tabBar = new Table();
        tabBar.add(buildTabButton("MAIN", Tab.MAIN)).expandX().fillX().height(45);
        tabBar.add(buildTabButton("DAILY", Tab.DAILY)).expandX().fillX().height(45);
        tabBar.add(buildTabButton("CHALLENGE", Tab.CHALLENGE)).expandX().fillX().height(45);
        tabBar.add(buildTabButton("MINIGAMES", Tab.MINIGAME)).expandX().fillX().height(45);
        add(tabBar).growX().padBottom(16).row();
        contentTable.top();
        add(contentTable).grow().top().row();
        updateTabHighlight();
        refreshContent();
    }


    private Stack buildTabButton(String label, Tab tab) {
        boolean useGreen = (tab == Tab.DAILY);
        TextureRegion texRegion = game.textureBank.region(
            useGreen ? "IMAGE_UI_GENERIC_GREENTAB_ACTIVE" : "IMAGE_UI_GENERIC_BLUETAB_ACTIVE");
        Image bg = new Image(texRegion);
        bg.setColor(baseTint(tab));
        tabBackgrounds.put(tab, bg);
        Label lbl = new Label(label, skin, "medium_outline");
        lbl.setAlignment(Align.center);
        Table lblHolder = new Table();
        lblHolder.add(lbl).expand().center();
        Stack stack = new Stack();
        stack.add(bg);
        stack.add(lblHolder);
        stack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentTab == tab) return;
                currentTab = tab;
                updateTabHighlight();
                refreshContent();
            }
        });
        return stack;
    }

    private Color baseTint(Tab tab) {
        switch (tab) {
            case CHALLENGE:
                return CHALLENGE_TINT;
            case MINIGAME:
                return MINIGAME_TINT;
            default:
                return ACTIVE_TINT;
        }
    }

    private void updateTabHighlight() {
        for (Tab tab : Tab.values()) {
            Image bg = tabBackgrounds.get(tab);
            if (bg == null) continue;
            Color base = baseTint(tab);
            if (tab == currentTab) {
                bg.setColor(base.r, base.g, base.b, 1f);
            } else {
                bg.setColor(base.r, base.g, base.b, 0.5f);
            }
        }
    }

    private void refreshContent() {
        contentTable.clear();
        switch (currentTab) {
            case MAIN:
                buildQuestList(controller.getQuestGroups(MainQuest.class));
                break;
            case DAILY:
                buildQuestList(controller.getQuestGroups(DailyQuest.class));
                break;
            case CHALLENGE:
                buildQuestList(controller.getQuestGroups(EpicChallengeQuest.class));
                break;
            case MINIGAME:
                buildMinigameList();
                break;
        }
    }

    private void buildQuestList(List<QuestGroupView> groups) {
        if (groups.isEmpty()) {
            Label empty = new Label("No quests here right now.", skin);
            empty.setColor(DESC_COLOR);
            contentTable.add(empty).pad(20).row();
            return;
        }
        for (int i = 0; i < groups.size(); i++) {
            contentTable.add(buildQuestRow(groups.get(i))).growX().padTop(i == 0 ? 6 : 0).row();
            if (i < groups.size() - 1) {
                contentTable.add(buildDivider()).growX().height(2).padTop(8).padBottom(8).row();
            }
        }
    }

    private Table buildQuestRow(QuestGroupView group) {
        Table outer = new Table();
        outer.pad(6, 4, 6, 4);
        outer.top();
        Table topRow = new Table();
        topRow.top();
        Table textCol = new Table();
        textCol.top().left();
        Label name = new Label(group.name, skin, "big");
        name.setWrap(true);
        name.setColor(Color.GOLD);
        textCol.add(name).width(400).left().top().row();
        Label desc = new Label(group.description, skin);
        desc.setWrap(true);
        desc.setColor(DESC_COLOR);
        textCol.add(desc).width(400).left().top().padTop(2);
        topRow.add(textCol).left().top().padRight(14);
        topRow.add(buildMarkerArea(group.variants)).left().top().expandX();
        outer.add(topRow).growX().row();
        outer.add(buildProgressArea(group)).growX().left().padTop(10);
        return outer;
    }

    private Table buildProgressArea(QuestGroupView group) {
        Table area = new Table();
        area.left();
        area.add(buildRewardInfo(group.reward)).left().padRight(16);
        area.add(buildProgressBar(group.progress)).width(PROGRESS_BAR_WIDTH)
            .height(PROGRESS_BAR_HEIGHT).left().padRight(14);
        if (group.claimable) {
            TextButton claimBtn = new TextButton("CLAIM", skin, "purple");
            claimBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controller.claimQuestGroup(group);
                    refreshContent();
                    if (game.getScreen() instanceof MainMenuScreen mainMenuScreen){
                        mainMenuScreen.getResourcesTable().update();
                    }
                }
            });
            area.add(claimBtn).width(120).height(36);
        }
        return area;
    }

    private Table buildRewardInfo(Reward reward) {
        Table info = new Table();
        info.left();
        Label label = new Label(getRewardText(reward), skin);
        label.setFontScale(0.95f);
        label.setColor(DESC_COLOR);
        info.add(label).padRight(6);
        info.add(buildRewardIcon(reward)).size(28);
        return info;
    }

    private String getRewardText(Reward reward) {
        if (reward instanceof CurrencyReward) {
            CurrencyReward cr = (CurrencyReward) reward;
            if (cr.getCoins() > 0) return "Reward: " + cr.getCoins();
            if (cr.getGems() > 0) return "Reward: " + cr.getGems();
            return "Reward:";
        }
        if (reward instanceof RandomSeedPacketReward) {
            RandomSeedPacketReward sr = (RandomSeedPacketReward) reward;
            return "Reward: x" + sr.getQuantity();
        }
        if (reward instanceof RandomUnlockReward) {
            return "Reward: Unlock Plant";
        }
        return "Reward:";
    }

    private Stack buildProgressBar(float progress) {
        float pct = Math.max(0f, Math.min(1f, progress));

        Stack bar = new Stack();
        Table trackHolder = new Table();
        trackHolder.add(new Image(createSolidColor(PROGRESS_TRACK_COLOR)))
            .grow();
        bar.add(trackHolder);
        Table fillHolder = new Table();
        fillHolder.left();
        if (pct > 0f) {
            fillHolder.add(new Image(createSolidColor(PROGRESS_FILL_COLOR)))
                .width(PROGRESS_BAR_WIDTH * pct).grow();
        }
        fillHolder.add().expandX();
        bar.add(fillHolder);
        return bar;
    }

    private Actor buildMarkerArea(List<VariantView> variants) {
        if (variants.size() == 1) {
            return buildCheckSlot(variants.get(0).completed);
        }
        if (isCompact(variants)) {
            return buildBadgeGrid(variants);
        }
        return buildNamedList(variants);
    }

    private boolean isCompact(List<VariantView> variants) {
        for (VariantView v : variants) {
            if (v.label == null || v.label.length() > COMPACT_LABEL_MAX_LEN) {
                return false;
            }
        }
        return true;
    }

    private Table buildBadgeGrid(List<VariantView> variants) {
        Table grid = new Table();
        int col = 0;
        for (VariantView v : variants) {
            grid.add(buildBadge(v)).size(34).pad(3);
            col++;
            if (col % BADGES_PER_ROW == 0) grid.row();
        }
        return grid;
    }

    private Stack buildBadge(VariantView v) {
        Stack stack = new Stack();

        TextureRegion bgRegion = game.textureBank.region("IMAGE_UI_HUD_INGAME_SCORE_METER_BG_TEXT");
        if (bgRegion != null) {
            Image bg = new Image(bgRegion);
            stack.add(bg);
        }
        Label num = new Label(v.label, skin);
        num.setFontScale(0.75f);
        num.setAlignment(Align.center);
        num.setColor(v.completed ? DESC_COLOR : new Color(0.35f, 0.35f, 0.35f, 1f));
        Table holder = new Table();
        holder.add(num).expand().center();
        stack.add(holder);
        if (v.completed || v.ready) {
            Image check = new Image(game.textureBank.region(
                "IMAGE_UI_GENERIC_CHECK_MARK_ANIM_CHECK_MARK_ANIM_99X80"));
            Table checkHolder = new Table();
            checkHolder.add(check).size(26, 21).top().right();
            checkHolder.top().right();
            stack.add(checkHolder);
        }
        return stack;
    }

    private Table buildNamedList(List<VariantView> variants) {
        Table list = new Table();
        for (VariantView v : variants) {
            Table line = new Table();
            Label name = new Label(prettify(v.label), skin);
            name.setFontScale(1.1f);
            name.setColor(v.completed ? new Color(0.15f, 0.45f, 0.15f, 1f) : DESC_COLOR);
            line.add(name).left().padRight(6);
            if (v.completed || v.ready) {
                Image check = new Image(game.textureBank.region(
                    "IMAGE_UI_GENERIC_CHECK_MARK_ANIM_CHECK_MARK_ANIM_99X80"));
                line.add(check).size(20, 16);
            }
            list.add(line).left().row();
        }
        return list;
    }

    private String prettify(String enumName) {
        if (enumName == null) return "";
        String spaced = enumName.replace('_', ' ').toLowerCase();
        if (spaced.isEmpty()) return spaced;
        return Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
    }

    private Actor buildCheckSlot(boolean completed) {
        Table slot = new Table();
        if (completed) {
            Image check = new Image(game.textureBank.region(
                "IMAGE_UI_GENERIC_CHECK_MARK_ANIM_CHECK_MARK_ANIM_99X80"));
            slot.add(check).size(32, 26);
        }
        return slot;
    }

    private Image buildRewardIcon(Reward reward) {
        String texKey = "IMAGE_UI_CURRENCY_LUCKOTHEZOMBIE_STACK_5";
        if (reward instanceof CurrencyReward) {
            CurrencyReward cr = (CurrencyReward) reward;
            texKey = cr.getCoins() > 0 ? "IMAGE_UI_COINS_STACK_2" : "IMAGE_UI_GEMS_STACK_1";
        }
        return new Image(game.textureBank.region(texKey));
    }

    private void buildMinigameList() {
        MiniGames[] all = MiniGames.values();
        for (int i = 0; i < all.length; i++) {
            contentTable.add(buildMinigameRow(all[i])).growX().padTop(i == 0 ? 6 : 0).row();
            if (i < all.length - 1) {
                contentTable.add(buildDivider()).growX().height(2).padTop(8).padBottom(8).row();
            }
        }
    }

    private Table buildMinigameRow(MiniGames mg) {
        Table row = new Table();
        row.pad(12, 10, 12, 10);
        row.left().top();

        String bgKey = getMinigameBackgroundKey(mg);
        TextureRegion bgRegion = bgKey != null ? game.textureBank.region(bgKey) : null;
        if (bgRegion != null) {
            row.setBackground(new TextureRegionDrawable(bgRegion));
        }
        row.add(buildMinigameTitleTag(mg)).left().padBottom(12).row();
        List<MinigameLevelInfo> levels = controller.getMinigameLevels(mg);
        Table badgesRow = new Table();
        int col = 0;
        for (MinigameLevelInfo info : levels) {
            badgesRow.add(buildMinigameLevelBadge(info)).size(50).padRight(10);
            col++;
            if (col % BADGES_PER_ROW == 0) badgesRow.row();
        }

        Table bottomRow = new Table();
        bottomRow.add(badgesRow).left();
        bottomRow.add(buildMinigamePlayButton(mg, levels)).right().expandX();

        row.add(bottomRow).growX();

        return row;
    }

    private Table buildMinigameTitleTag(MiniGames mg) {
        Table tag = new Table();
        tag.setBackground(createSolidColor(TITLE_TAG_COLOR));
        tag.pad(4, 12, 4, 12);

        Label title = new Label(prettify(mg.name()), skin, "medium");
        title.setFontScale(0.85f);
        title.setColor(Color.BLACK);
        tag.add(title);

        return tag;
    }

    private String getMinigameBackgroundKey(MiniGames mg) {
        switch (mg) {
            case VASE_BREAKER:
                return "IMAGE_UI_CALENDAR_CALENDAR_CARD_7DAY_BIGWAVEBEACH";
            case BOWLING:
                return "IMAGE_UI_CALENDAR_CALENDAR_CARD_7DAY_FALLFESTIVAL";
            case I_ZOMBIE:
                return "IMAGE_UI_CALENDAR_CALENDAR_CARD_7DAY_BIRTHDAYZ";
            case BEGHOULED:
                return "IMAGE_UI_CALENDAR_CALENDAR_CARD_7DAY_MGPWINTEREVENT";
            case ZOMBOTANY:
                return "IMAGE_UI_CALENDAR_CALENDAR_CARD_7DAY_LUNAR_NEW_YEAR";
            default:
                return null;
        }
    }

    private Actor buildMinigamePlayButton(MiniGames mg, List<MinigameLevelInfo> levels) {
        MinigameLevelInfo target = pickPlayableLevel(levels);
        if (target == null) {
            return new Table();
        }

        TextButton playBtn = new TextButton("PLAY", skin, "purple");
        playBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.selectMinigame(mg.name(), target.level);
                SFXManager.getInstance().playSound(GameSFX.BUTTON_CLICK);
                AudioManager.getInstance().playMusic(GameMusic.HOUSE, true);
            }
        });
        return playBtn;
    }

    private MinigameLevelInfo pickPlayableLevel(List<MinigameLevelInfo> levels) {
        MinigameLevelInfo lastUnlocked = null;
        for (MinigameLevelInfo info : levels) {
            if (!info.unlocked) continue;
            lastUnlocked = info;
            if (!info.completed) {
                return info;
            }
        }
        return lastUnlocked;
    }

    private Stack buildMinigameLevelBadge(MinigameLevelInfo info) {
        Stack stack = new Stack();

        TextureRegion bgRegion = game.textureBank.region("IMAGE_UI_HUD_INGAME_SCORE_METER_FILL");
        if (bgRegion != null) {
            stack.add(new Image(bgRegion));
        }

        Label num = new Label(String.valueOf(info.level), skin);
        num.setAlignment(Align.center);
        num.setColor(DESC_COLOR);
        Table holder = new Table();
        holder.add(num).expand().center();
        stack.add(holder);

        if (info.completed) {
            Image check = new Image(game.textureBank.region(
                "IMAGE_UI_GENERIC_CHECK_MARK_ANIM_CHECK_MARK_ANIM_99X80"));
            Table checkHolder = new Table();
            checkHolder.add(check).size(26, 21).top().right();
            checkHolder.top().right();
            stack.add(checkHolder);
        }

        if (!info.unlocked) {
            stack.setColor(1, 1, 1, 0.4f);
        }

        return stack;
    }


    private Image buildDivider() {
        return new Image(createSolidColor(DIVIDER_COLOR));
    }

    private Drawable createSolidColor(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }
}
