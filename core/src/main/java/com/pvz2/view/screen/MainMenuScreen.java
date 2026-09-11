package com.pvz2.view.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.pvz2.Main;
import com.pvz2.controller.MainMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.core.UserDataManager;
import com.pvz2.models.core.UserManager;
import com.pvz2.models.enums.Chapter;
import com.pvz2.models.mupoint.MuPointLevel;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.network.NetworkClient;
import com.pvz2.models.network.onlineIZombie.messages.ChallengeInvite;
import com.pvz2.view.audios.AudioManager;
import com.pvz2.view.audios.GameMusic;
import com.pvz2.view.audios.GameSFX;
import com.pvz2.view.audios.SFXManager;
import com.pvz2.view.table.*;
import pvz.libpvz.textures.TextureBank;
import pvz.skin.BorderedTable;

import java.util.function.Supplier;

public class MainMenuScreen extends MenuScreen {
    private MainMenuController controller;

    private TextureRegion bg;
    private Image logoImg;
    private Image unreadBadge;

    private TextButton playBtn;
    private ImageButton newsBtn;
    private ImageButton settingsBtn;
    private Button leaderboardBtn;
    private Button muPoint;
    private Button profileBtn;
    private ImageButton backBtn;
    private ImageButton travelLogBtn;
    private ImageButton onlineGameBtn;

    private ResourcesTable resourcesTable = new ResourcesTable(App.getCurrentUser(), game);

    private Table mainTable;
    private Table topBar;
    private Table centerTable;
    private Table bottomBar;
    private Table badgeOverlay;
    private Stack newsStack;

    private boolean hasUnreadNews = true;

    public MainMenuScreen(Main game) {
        super(game);
        this.controller = new MainMenuController(this);
    }

    private void initFields() {
        if (mainTable != null) return;
        bg = game.textureBank.region("IMAGE_MAINMENU_BACKGROUND");
        TextureRegion logo = game.textureBank.region("IMAGE_UI_MAINMENU_PVZ2_LOGO_HORIZONTAL");
        logoImg = (logo != null) ? new Image(logo) : null;
        playBtn = new TextButton("PLAY", game.skin, "purple");
        newsBtn = createImageButton(
            "IMAGE_UI_HUD_NEWSBUTTON_BUTTONS_HUD_NEWS_NORMAL",
            "IMAGE_UI_HUD_NEWSBUTTON_BUTTONS_HUD_NEWS_SELECTED",
            game.textureBank
        );
        settingsBtn = createImageButton(
            "IMAGE_UI_HUD_SETTINGSBUTTON_BUTTONS_HUD_SETTINGS_NORMAL",
            "IMAGE_UI_HUD_SETTINGSBUTTON_BUTTONS_HUD_SETTINGS_SELECTED",
            game.textureBank
        );
        onlineGameBtn = createImageButton(
            "IMAGE_UI_GAMECENTER_ANDROID_GAMECENTER",
            "IMAGE_UI_GAMECENTER_ANDROID_GAMECENTER_PRESS",
            game.textureBank
        );
        leaderboardBtn = new TextButton("", skin, "brown");
        Image cup = new Image(game.textureBank.region("IMAGE_UI_GAMECENTER_ICON"));
        leaderboardBtn.add(cup);
        muPoint = new TextButton("", skin, "brown");
        Image star = new Image(game.textureBank.region("IMAGE_UI_GENERIC_STAR_ICON"));
        muPoint.add(star);
        profileBtn = new TextButton("", skin, "brown");
        Image prof = new Image(game.textureBank.region("IMAGE_UI_MAINMENU_MM_PLAYERICON"));
        profileBtn.add(prof).padRight(5);
        travelLogBtn = createImageButton(
            "IMAGE_UI_GENERIC_BUTTON_HUD_MINIGAMES_ALT_SELECTED",
            "IMAGE_UI_GENERIC_BUTTON_HUD_MINIGAMES_ALT_SELECTED",
            game.textureBank
        );
        backBtn = createImageButton(
            "IMAGE_UI_MAINMENU_BACK_BTN_NORMAL",
            "IMAGE_UI_MAINMENU_BACK_BTN_PRESSED",
            game.textureBank
        );
        unreadBadge = new Image(game.textureBank.region("IMAGE_UI_CLAIM_SMALL"));
        mainTable = new Table();
        topBar = new Table();
        centerTable = new Table();
        bottomBar = new Table();
        badgeOverlay = new Table();
        newsStack = new Stack();
    }

    @Override
    protected void buildUI() {
        initFields();
        mainTable.clear();
        mainTable.setFillParent(true);

        buildTopBar();
        buildCenterTable();
        buildBottomBar();

        if (bg != null) {
            mainTable.setBackground(new TextureRegionDrawable(bg));
        }

        mainStack.add(mainTable);
        setListeners();
        setOnlinePopup();
    }

    private void buildTopBar() {
        if (backBtn != null) topBar.add(backBtn).left().top().pad(10);
        if (onlineGameBtn != null) topBar.add(onlineGameBtn).pad(10);
        topBar.add().expandX();
        if (App.getCurrentUser() != null) topBar.add(resourcesTable).padRight(20);

        mainTable.add(topBar).top().growX().row();
    }

    private void buildCenterTable() {
        if (logoImg != null) {
            mainTable.add(logoImg).prefWidth(400).prefHeight(100).padTop(5).row();
        }

        Table welcomeTbl = new Table();
        welcomeTbl.setBackground(new TextureRegionDrawable(game.textureBank.
            region("IMAGE_UI_MAINMENU_MAINMENU_CONTENT_OFFLINE")));
        Label welcome = new Label("Welcome, " + App.getCurrentUser().getNickname(), skin, "big_outline");
        welcome.setColor(Color.RED);
        welcomeTbl.bottom().left().add(welcome).pad(15);

        centerTable.add(welcomeTbl).row();
        centerTable.add(playBtn).width(200).height(60).pad(20).row();
        mainTable.add(centerTable).expandY().center().row();

    }

    private void buildBottomBar() {
        if (newsBtn != null) {
            setUnreadStatus(controller.checkUnreadNews());
            newsStack.add(newsBtn);
            badgeOverlay.top().right();
            badgeOverlay.add(unreadBadge).size(25, 25).padTop(-8).padRight(-5);
            newsStack.add(badgeOverlay);
            unreadBadge.setVisible(hasUnreadNews);
        }

        float btnSize = 70f;

        if (settingsBtn != null) bottomBar.add(settingsBtn).size(btnSize).padLeft(25).pad(10);
        if (newsBtn != null) bottomBar.add(newsStack).size(btnSize).pad(5);
        bottomBar.add().expandX();

        if (muPoint != null) bottomBar.add(muPoint).size(btnSize).pad(10);
        bottomBar.add().expandX();

        if (leaderboardBtn != null) bottomBar.add(leaderboardBtn).size(btnSize).pad(10);
        if (travelLogBtn != null) bottomBar.add(travelLogBtn).size(btnSize).pad(10);
        if (profileBtn != null) bottomBar.add(profileBtn).size(btnSize).padRight(25).pad(10);

        mainTable.add(bottomBar).bottom().growX().pad(10);
    }

    private void setOnlinePopup() {
        NetworkClient.get().onPush("CHALLENGE_INVITE", msg -> {
        });
    }

    private void setListeners() {
        addClickListener(backBtn, () -> controller.exitMenu());
        addClickListener(playBtn, () -> controller.enterMenu("play"));
        addClickListener(onlineGameBtn, () -> controller.enterMenu("online room"));
        addClickListener(muPoint, () -> {
            controller.enterMenu("mu point");
            SFXManager.getInstance().playSound(GameSFX.BUTTON_CLICK);
            AudioManager.getInstance().playMusic(GameMusic.HOUSE, true);
            GameWorld mupointWorld = MuPointLevel.createMuPointLevel();
            App.setCurrentGame(mupointWorld);
            fadeAndSwitchScreen(new GameScreen(game, mupointWorld, Chapter.EGYPT));
        });
        addClickListener(newsBtn, () -> {
            setUnreadStatus(false);
            showScrollablePopup("NEWS", controller.getNews(skin));
            SFXManager.getInstance().playSound(GameSFX.BUTTON_CLICK);
        });

        addPopupListener(settingsBtn, "SETTINGS", () -> new SettingsMenuTable(game, skin), 750, 520, 680, 400);
        addPopupListener(leaderboardBtn, "LEADERBOARD", () -> new LeaderboardMenuTable(game, skin), 950, 650, 800, 480);
        addPopupListener(profileBtn, "PROFILE", () -> new ProfileMenuTable(game, skin), 660, 620, 570, 480);
        addPopupListener(travelLogBtn, "TRAVEL LOG", () -> new TravelLogMenuTable(game, skin), 800, 500, 750, 400);
    }

    private void addClickListener(Button btn, Runnable action) {
        if (btn == null) return;
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
                SFXManager.getInstance().playSound(GameSFX.BUTTON_CLICK);
            }
        });
    }

    private void addPopupListener(Button btn, String title, Supplier<Actor> contentSupplier,
                                  float bw, float bh, float sw, float sh) {
        if (btn == null) return;
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showScrollablePopup(title, contentSupplier.get(), bw, bh, sw, sh);
                SFXManager.getInstance().playSound(GameSFX.BUTTON_CLICK);
            }
        });
    }

    public static ImageButton createImageButton(String normalRegionKey, String selectedRegionKey, TextureBank bank) {
        TextureRegion normalReg = bank.region(normalRegionKey);
        TextureRegion selectedReg = bank.region(selectedRegionKey);

        if (normalReg == null) return null;

        TextureRegionDrawable upDrawable = new TextureRegionDrawable(normalReg);
        TextureRegionDrawable downDrawable = (selectedReg != null)
            ? new TextureRegionDrawable(selectedReg)
            : upDrawable;

        return new ImageButton(upDrawable, downDrawable);
    }

    private void showScrollablePopup(String titleText, Actor contentActor) {
        showScrollablePopup(titleText, contentActor, 600, 500, 450, 320);
    }

    private void showScrollablePopup(String titleText, Actor contentActor,
                                     float boxWidth, float boxHeight,
                                     float scrollWidth, float scrollHeight) {
        Table overlay = new Table();
        overlay.setFillParent(true);
        overlay.setBackground(createSolidColor(new Color(0, 0, 0, 0.65f)));
        overlay.setTouchable(Touchable.enabled);
        overlay.addListener(new ClickListener());

        BorderedTable popupBox = new BorderedTable();
        popupBox.pad(20);

        Table topBar = new Table();

        Label titleLabel = new Label(titleText, skin, "big_outline");

        ImageButton backBtn = createImageButton(
            "IMAGE_UI_MAINMENU_BACK_BTN_NORMAL",
            "IMAGE_UI_MAINMENU_BACK_BTN_PRESSED",
            game.textureBank
        );
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                overlay.remove();
                UserDataManager.saveUser(App.getCurrentUser());
                UserManager.syncCurrentUser();
                SFXManager.getInstance().playSound(GameSFX.BUTTON_CLICK);
            }
        });

        topBar.add(backBtn).size(45, 45).left().expandX();
        topBar.add(titleLabel).center();
        topBar.add().expandX();

        ScrollPane scrollPane = new ScrollPane(contentActor, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        popupBox.add(topBar).growX().pad(10).row();
        popupBox.add(scrollPane).width(scrollWidth).height(scrollHeight).pad(5).grow().row();

        overlay.add(popupBox).width(boxWidth).height(boxHeight);
        stage.addActor(overlay);
    }

    public static Drawable createSolidColor(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    public void setUnreadStatus(boolean hasUnread) {
        this.hasUnreadNews = hasUnread;
        if (unreadBadge != null) {
            unreadBadge.setVisible(hasUnread);
        }
    }

    public ResourcesTable getResourcesTable() {
        return resourcesTable;
    }
}
