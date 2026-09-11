package com.pvz2.view.table;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.pvz2.Main;
import com.pvz2.models.core.App;
import com.pvz2.models.miniGame.IZombie.OnlineIZombieLevel;
import com.pvz2.models.plant.card.PlantCard;
import com.pvz2.models.world.GameState;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.network.onlineIZombie.ClientGameController;
import com.pvz2.models.network.onlineIZombie.messages.ReactionCategory;
import com.pvz2.models.network.onlineIZombie.messages.ReactionReceived;
import com.pvz2.view.util.ReactionBubbleFactory;
import com.pvz2.view.util.ReactionPanel;
import com.pvz2.view.util.UiUtils;
import com.pvz2.view.audios.GameSFX;
import com.pvz2.view.audios.SFXManager;
import com.pvz2.view.screen.MainMenuScreen;
import com.pvz2.view.screen.OnlineGameScreen;

import java.util.function.Consumer;

public class OnlineGameHud extends Group {
    private static final float MARGIN = 20f;

    private final Table topBar;
    private final GameHUD.SunCounter sunCounter;
    private final BrainCounter brainCounter;
    private final ImageButton shovelBtn;
    private final SelectedPlantsList selectedPlantsList;
    private final SelectedZombiesList selectedZombiesList;
    private final OnlineGameScreen screen;

    private final Label ownNameLabel;
    private final Label opponentNameLabel;

    private final Container<ReactionPanel> reactionPanelContainer;
    private final ImageButton reactionToggleBtn;
    private final Table reactionDock;
    private final Table opponentBubbleLayer;

    private float lastStageWidth;
    private float lastStageHeight;

    public OnlineGameHud(Main game, Skin skin, OnlineGameScreen screen) {
        this.screen = screen;
        sunCounter = new GameHUD.SunCounter(game, skin);
        brainCounter = new BrainCounter(game, skin);
        selectedPlantsList = new SelectedPlantsList(1, 1, false, 150, 100, createSelectingMethod(), game);
        selectedPlantsList.setNoBoost(true);
        selectedPlantsList.activate();
        selectedZombiesList = new SelectedZombiesList(100, 200, createZombieClickMethod());
        if (screen.controller.getWorld() != null) {
            selectedZombiesList.build(screen.controller.getWorld().getZombieCards());}
        shovelBtn = createShovelBtn(game);
        String ownUsername = App.getCurrentUser() != null ? App.getCurrentUser().getUsername() : "";
        ownNameLabel = new Label(ownUsername, skin, "medium_outline");
        opponentNameLabel = new Label(screen.controller.getOpponentUsername(), skin, "medium_outline");
        topBar = new Table();
        Label plantLabel = screen.controller.getSide() == ClientGameController.Side.PLANTS ? ownNameLabel :
            opponentNameLabel;
        Label zombieLabel = screen.controller.getSide() == ClientGameController.Side.PLANTS ? opponentNameLabel :
            ownNameLabel;
        Table leftSide = new Table();
        leftSide.add(plantLabel).left().padLeft(MARGIN).padTop(5).row();
        leftSide.add(sunCounter).pad(MARGIN);
        leftSide.add(shovelBtn).pad(5).row();
        leftSide.add(selectedPlantsList).pad(MARGIN);
        Table rightSide = new Table();
        rightSide.add(zombieLabel).right().padRight(MARGIN).padTop(5).row();
        rightSide.add(selectedZombiesList).top().right().row();
        rightSide.add(brainCounter).top().right().pad(MARGIN);
        topBar.add(leftSide).expandX().left().top();
        topBar.add().expandX();    topBar.add(rightSide).right().top();  topBar.pack();   addActor(topBar);
        ReactionPanel reactionPanel = new ReactionPanel(game, skin, this::onReactionPicked);
        reactionPanelContainer = new Container<>(reactionPanel);
        reactionPanelContainer.size(440, 420);
        reactionPanelContainer.setVisible(false);
        reactionToggleBtn = MainMenuScreen.createImageButton(
            "IMAGE_UI_MAINMENU_EDIT_BTN_PRESSED",
            "IMAGE_UI_MAINMENU_EDIT_BTN_NORMAL",
            game.textureBank);
        reactionToggleBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleReactionPanel();}});
        reactionDock = new Table();
        reactionDock.add(reactionPanelContainer).padBottom(10).row();
        reactionDock.add(reactionToggleBtn);
        addActor(reactionDock);
        opponentBubbleLayer = new Table();
        addActor(opponentBubbleLayer);
        screen.controller.setReactionListener(this::onReactionReceived);
    }

    private void onReactionPicked(ReactionCategory category, int index) {
        screen.controller.sendReaction(category, index);
        toggleReactionPanel();
    }

    private void onReactionReceived(ReactionReceived received) {
        Gdx.app.postRunnable(() -> showOpponentReactionBubble(received.category, received.index));
    }

    private void showOpponentReactionBubble(ReactionCategory category, int index) {
        Actor bubble = ReactionBubbleFactory.build(App.getGameApp(), App.getGameApp().skin, category, index);
        opponentBubbleLayer.clearChildren();
        opponentBubbleLayer.add(bubble);
        bubble.getColor().a = 0f;
        bubble.addAction(Actions.sequence(
            Actions.fadeIn(0.2f),
            Actions.delay(2.5f),
            Actions.fadeOut(0.4f)
        ));
    }

    private void toggleReactionPanel() {
        if (reactionPanelContainer.isVisible()) {
            reactionPanelContainer.addAction(Actions.sequence(
                Actions.fadeOut(0.25f),
                Actions.visible(false)
            ));
        } else {
            reactionPanelContainer.getColor().a = 0f;
            reactionPanelContainer.addAction(Actions.sequence(
                Actions.visible(true),
                Actions.fadeIn(0.25f)
            ));
        }
    }

    public static class BrainCounter extends Table {
        private final Label brainLabel;

        BrainCounter(Main game, Skin skin) {
            TextureRegion bgRegion = game.textureBank.region("IMAGE_UI_HUD_INGAME_BACKGROUND_3SLICE");
            if (bgRegion != null) {
                setBackground(new TextureRegionDrawable(bgRegion));
            } else {
                setBackground(UiUtils.darkChipBackground());
            }

            pad(5f, 10f, 5f, 25f);

            Image sunIcon = new Image(game.textureBank.region("IMAGE_UI_GAMEOVER_FAIL_SCREEN_BRAIN_ONLY"));
            sunIcon.setScaling(Scaling.fit);

            brainLabel = new Label("0", skin, "big_outline");

            add(sunIcon).size(60f).padRight(8f);
            add(brainLabel).left();
        }

        void update(OnlineIZombieLevel world) {
            if (world == null) return;
            brainLabel.setText(String.valueOf(world.getZombieBrains()));
        }
    }

    private Consumer<ZombieCardView> createZombieClickMethod() {
        return new Consumer<ZombieCardView>() {
            @Override
            public void accept(ZombieCardView card) {
                boolean isSelected = screen.controller.selectAndUnselectZombie(card.getZombieName(), screen);
                Gdx.app.postRunnable(() -> {
                    for (ZombieCardView cardView : selectedZombiesList.getZombieCardViewList()){
                        cardView.setSelectedState(false);
                    }
                    if (isSelected){
                        SFXManager.getInstance().playSound(GameSFX.SEED_LIFT);
                        card.setSelectedState(true);
                        screen.getZombiePlacementManager().selectZombie(card.getZombieName());
                    }
                });
            }
        };
    }

    private ImageButton createShovelBtn(Main game) {
        TextureRegion shovelIcon = game.textureBank.region("IMAGE_UI_HUD_INGAME_SHOVEL_BUTTON");
        TextureRegion shovelIconSelected = game.textureBank.region("IMAGE_UI_HUD_INGAME_SHOVEL_BUTTON_DOWN");
        Drawable shovel = new TextureRegionDrawable(shovelIcon);
        Drawable shovelSelected = new TextureRegionDrawable(shovelIconSelected);
        ImageButton imageButton = new ImageButton(shovel, shovel, shovelSelected);

        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (imageButton.isChecked()) {
                    if (screen.controller.selectAndUnselectShovel()) {
                        screen.getShovelPlacementManager().setSelected(true);
                    }
                } else {
                    screen.controller.selectAndUnselectShovel();
                    screen.getShovelPlacementManager().setSelected(false);
                }
            }
        });
        return imageButton;
    }

    private Consumer<PlantCardView> createSelectingMethod(){
        return new Consumer<PlantCardView>() {
            @Override
            public void accept(PlantCardView plantCardView) {
                if (selectedPlantsList.isActive()){
                    boolean isSelected = screen.controller.selectAndUnselectPlant(plantCardView.getType(), screen);
                    Gdx.app.postRunnable(() -> {
                        for (PlantCardView cardView : selectedPlantsList.getPlantCardViewList()){
                            cardView.setSelectedState(false);
                        }
                        if (isSelected){
                            SFXManager.getInstance().playSound(GameSFX.SEED_LIFT);
                            plantCardView.setSelectedState(true);
                            screen.getPlantPlacementManager().selectPlant(plantCardView.getType(), null);
                        }
                    });
                }
            }
        };
    }

    public void resize(float stageWidth, float stageHeight) {
        this.lastStageWidth = stageWidth;
        this.lastStageHeight = stageHeight;
        reposition();
    }

    @Override
    protected void setStage(com.badlogic.gdx.scenes.scene2d.Stage stage) {
        super.setStage(stage);
        if (stage != null) {
            reposition();
        }
    }

    private void reposition() {
        float width = lastStageWidth;
        float height = lastStageHeight;

        if ((width == 0 || height == 0) && getStage() != null) {
            width = getStage().getWidth();
            height = getStage().getHeight();
        }
        if (width == 0 || height == 0) return;
        topBar.setWidth(lastStageWidth);
        topBar.pack();
        topBar.setWidth(lastStageWidth);
        topBar.setPosition(0, lastStageHeight - topBar.getHeight());

        reactionDock.pack();
        reactionDock.setPosition(lastStageWidth - reactionDock.getWidth() - MARGIN, MARGIN);

        opponentBubbleLayer.setSize(320, 140);
        opponentBubbleLayer.setPosition(
            lastStageWidth - opponentBubbleLayer.getWidth() - MARGIN,
            MARGIN
        );
    }

    public void update(GameWorld gameWorld, float delta) {
        if (gameWorld.getState() != GameState.PLAYING) return;
        OnlineIZombieLevel world = (OnlineIZombieLevel) gameWorld;
        sunCounter.update(world);
        brainCounter.update(world);
        selectedPlantsList.update(gameWorld);
        selectedZombiesList.update((OnlineIZombieLevel) gameWorld);

        boolean needsReposition = false;

        if (selectedPlantsList.getSlots()[0] == null){
            int i = 0;
            for (PlantCard plantCard : world.getPlantLists()){
                selectedPlantsList.getSlots()[i] = plantCard.getType();
                i++;
            }
            selectedPlantsList.build();
            needsReposition = true;
        }

        if (selectedZombiesList.getZombieCardViewList().isEmpty() && world != null &&
            !world.getZombieCards().isEmpty()) {
            selectedZombiesList.build(world.getZombieCards());
            needsReposition = true;
        }

        if (needsReposition) {
            reposition();
        }
    }

    public SelectedZombiesList getSelectedZombiesList() {
        return selectedZombiesList;
    }

    public ImageButton getShovelBtn() {
        return shovelBtn;
    }

    public SelectedPlantsList getSelectedPlantsList() {
        return selectedPlantsList;
    }
}
