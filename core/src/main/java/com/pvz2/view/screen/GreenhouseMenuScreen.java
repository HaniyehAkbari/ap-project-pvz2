package com.pvz2.view.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.pvz2.Main;
import com.pvz2.controller.GreenhouseMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.models.greenhouse.GreenHouse;
import com.pvz2.models.greenhouse.Pot;
import com.pvz2.view.util.PamActor;
import com.pvz2.view.table.ResourcesTable;

public class GreenhouseMenuScreen extends MenuScreen {

    private final GreenhouseMenuController controller = new GreenhouseMenuController();
    private Table gridTable;
    private Label statusLabel;
    private ResourcesTable resourcesTable;
    private Table seedBoxContainer;

    private static final String TEX_BG = "IMAGE_BACKGROUNDS_ZEN_GARDEN";
    private static final String TEX_POT_EMPTY = "IMAGE_ZEN_GARDEN_GROWING_PLANT_SLOT_GROWING_PLANT_SLOT_184X161";
    private static final String TEX_POT_LOCKED = "IMAGE_ZEN_GARDEN_LOCKED_POT_ICON";

    private static final Color READY_COLOR = Color.WHITE;
    private static final Color GROWING_TINT = new Color(0.6f, 0.6f, 0.6f, 1f);

    public GreenhouseMenuScreen(Main game) {
        super(game);
    }

    @Override
    protected void buildUI() {
        stage.setViewport(new FillViewport(1800, 1000));

        Image backgroundImage = new Image(game.textureBank.region(TEX_BG));
        backgroundImage.setFillParent(true);
        mainStack.add(backgroundImage);

        Table topTable = new Table();
        topTable.top().setFillParent(true);
        topTable.pad(60f, 70f, 0f, 70f);

        Button backBtn = createBackButton();
        Button shopBtn = createShopButton();

        topTable.add(backBtn).size(65, 65).padRight(15).padLeft(120);
        topTable.add(shopBtn).size(65, 65);
        topTable.add().expandX();

        User currentUser = App.getCurrentUser();
        resourcesTable = new ResourcesTable(currentUser, game);

        seedBoxContainer = new Table();
        updateSeedBox();

        Table rightContainer = new Table();
        rightContainer.add(resourcesTable).padRight(15);
        rightContainer.add(seedBoxContainer).padRight(120);

        topTable.add(rightContainer).right();
        mainStack.add(topTable);

        gridTable = new Table();
        gridTable.setFillParent(true);
        gridTable.top().padTop(280);
        mainStack.add(gridTable);

        statusLabel = new Label("", skin);
        statusLabel.setColor(Color.YELLOW);
        statusLabel.setAlignment(Align.center);

        Table statusTable = new Table();
        statusTable.bottom().setFillParent(true);
        statusTable.add(statusLabel).padBottom(25);
        toastStack.add(statusTable);

        refreshGrid();
    }

    private void updateSeedBox() {
        seedBoxContainer.clearChildren();

        Table box = new Table();
        TextureRegion bgRegion = game.textureBank.region("IMAGE_UI_GENERIC_BUTTON_GENERIC_LTECURRENCY");
        if (bgRegion != null) {
            box.setBackground(new TextureRegionDrawable(bgRegion));
        }

        User currentUser = App.getCurrentUser();
        int totalSeeds = 0;
        if (currentUser != null && currentUser.getSeedPackets() != null) {
            totalSeeds = currentUser.getSeedPackets().values().stream()
                .mapToInt(Integer::intValue)
                .sum();
        }

        TextureRegion iconTex = game.textureBank.region(
            "IMAGE_ZEN_GARDEN_GROWING_PLANT_SLOT_GROWING_PLANT_SLOT_122X161");
        Image icon = (iconTex != null) ? new Image(iconTex) : new Image();
        icon.setScaling(Scaling.fit);

        Label label = new Label("x" + totalSeeds, skin);

        box.add(icon).height(60).padLeft(-8);
        box.add().expandX();
        box.add(label);
        box.add().expandX();

        seedBoxContainer.add(box);
    }

    private Button createShopButton() {
        TextureRegion shopNorm = game.textureBank.region("IMAGE_UI_HUD_WORLDMAP_BUTTONS_HUD_STORE_NORMAL");
        TextureRegion shopPress = game.textureBank.region("IMAGE_UI_HUD_WORLDMAP_BUTTONS_HUD_STORE_SELECTED");

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(shopNorm);
        if (shopPress != null) style.down = new TextureRegionDrawable(shopPress);

        ImageButton btn = new ImageButton(style);
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fadeAndSwitchScreen(new ShopMenuScreen(game));
            }
        });
        return btn;
    }

    private void refreshGrid() {
        if (resourcesTable != null) resourcesTable.update();
        updateSeedBox();

        gridTable.clearChildren();

        GreenHouse greenHouse = (App.getCurrentUser() != null) ? App.getCurrentUser().getGreenhouse() : null;
        if (greenHouse == null) {
            gridTable.add(new Label("No user logged in.", skin));
            return;
        }

        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 4; c++) {
                Pot pot = greenHouse.getPot(c, r);
                Table potWidget = createPotWidget(pot, c, r);

                gridTable.add(potWidget)
                    .width(100)
                    .height(100)
                    .pad(50f, 40f, 50f, 40f);
            }
            gridTable.row();
        }
    }

    private Table createPotWidget(final Pot pot, final int x, final int y) {
        Table cell = new Table();
        if (pot == null) return cell;

        final Stack potStack = new Stack();

        if (pot.isLocked()) {
            buildLockedPot(potStack);
        } else if (pot.isEmpty()) {
            buildEmptyPot(potStack, x, y);
        } else {
            buildOccupiedPot(potStack, pot, x, y);
        }

        cell.add(potStack).size(95, 95);
        return cell;
    }

    private void buildLockedPot(Stack potStack) {
        Image lockImage = new Image(game.textureBank.region(TEX_POT_LOCKED));
        potStack.add(lockImage);

        Table buyOverlay = new Table();
        buyOverlay.setFillParent(true);

        TextButton buyBtn = new TextButton("Unlock", skin);
        buyBtn.getLabel().setFontScale(0.45f);
        buyBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float cx, float cy) {
                refreshGrid();
                game.setScreen(new ShopMenuScreen(game));
            }
        });

        buyOverlay.bottom().add(buyBtn).width(70).height(24).padBottom(-5);
        potStack.add(buyOverlay);
    }

    private void buildEmptyPot(Stack potStack, final int x, final int y) {
        Image emptyImage = new Image(game.textureBank.region(TEX_POT_EMPTY));
        potStack.add(emptyImage);

        potStack.setTouchable(Touchable.enabled);
        potStack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float cx, float cy) {
                String resultMessage = controller.plantPot(x, y);
                showToast(resultMessage);
                refreshGrid();
            }
        });
    }

    private void buildOccupiedPot(Stack potStack, final Pot pot, final int x, final int y) {
        Image potBg = new Image(game.textureBank.region(TEX_POT_EMPTY));
        potStack.add(potBg);

        String pamPath = PlantsCollectionMenuScreen.getPlantAnimAddress(pot.getPlantType());
        String clip = PlantsCollectionMenuScreen.getPlantInitialClip(pot.getPlantType());
        PamActor plantPamActor = new PamActor(game.pamPlayer, pamPath, clip, 0.6f, null);

        plantPamActor.setColor(pot.isReady() ? READY_COLOR : GROWING_TINT);

        Table plantContainer = new Table();
        plantContainer.setFillParent(true);
        plantContainer.add(plantPamActor).center().padBottom(40f);
        potStack.add(plantContainer);

        if (!pot.isReady()) {
            addTimerOverlay(potStack, pot);
        }

        potStack.setTouchable(Touchable.enabled);
        potStack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float cx, float cy) {
                String resultMessage = pot.isReady() ? controller.collect(x, y) : controller.grow(x, y);
                showToast(resultMessage);
                refreshGrid();
            }
        });
    }

    private void addTimerOverlay(Stack potStack, Pot pot) {
        Table overlayTable = new Table();
        overlayTable.setFillParent(true);

        Table timerTable = new Table();
        TextureRegion timerRegion = game.textureBank.region("finish_timer_background");
        if (timerRegion != null) timerTable.background(new TextureRegionDrawable(timerRegion));

        long remainingHours = pot.getRemainingHours();
        Label timeLabel = new Label(remainingHours + "h", skin);
        timeLabel.setFontScale(0.55f);
        timeLabel.setColor(Color.WHITE);
        timerTable.add(timeLabel).pad(2, 6, 2, 6);

        Table growButtonTable = new Table();
        TextureRegion btnRegion = game.textureBank.region("IMAGE_ZEN_GARDEN_BUTTON_UNLOCK_ACTIVE");
        if (btnRegion != null) growButtonTable.background(new TextureRegionDrawable(btnRegion));

        int gemCost = (int) Math.ceil(remainingHours);
        Label gemLabel = new Label(String.valueOf(gemCost), skin);
        gemLabel.setFontScale(0.55f);
        gemLabel.setColor(Color.WHITE);

        TextureRegion gemRegion = game.textureBank.region("GEM_LARGE");
        Image gemImage =
            (gemRegion != null) ? new Image(gemRegion) : new Image(game.textureBank.region(TEX_POT_EMPTY));

        growButtonTable.add(gemLabel).padRight(2);
        growButtonTable.add(gemImage).size(14, 14);

        overlayTable.top().add(timerTable).padTop(-5).row();
        overlayTable.bottom().add(growButtonTable).padBottom(-2);

        potStack.add(overlayTable);
    }

    private Button createBackButton() {
        TextureRegion backNorm = game.textureBank.region("IMAGE_UI_MAINMENU_BACK_BTN_NORMAL");
        TextureRegion backPress = game.textureBank.region("IMAGE_UI_MAINMENU_BACK_BTN_PRESSED");

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(backNorm);
        if (backPress != null) style.down = new TextureRegionDrawable(backPress);

        ImageButton btn = new ImageButton(style);
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fadeAndSwitchScreen(new MainMenuScreen(game));
            }
        });
        return btn;
    }

    private void showToast(String message) {
        statusLabel.setText(message);
        statusLabel.clearActions();
        statusLabel.getColor().a = 1f;
        statusLabel.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence(
            com.badlogic.gdx.scenes.scene2d.actions.Actions.delay(3f),
            com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut(1f)
        ));
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
    }
}
