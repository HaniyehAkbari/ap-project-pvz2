package com.pvz2.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.pvz2.Main;
import com.pvz2.controller.OnlineRoomMenuController;
import com.pvz2.models.core.App;
import com.pvz2.view.util.PamActor;
import com.pvz2.view.table.ResourcesTable;
import pvz.skin.BorderedTable;

public class OnlineRoomMenuScreen extends MenuScreen{
    private Table mainTable = new Table();
    private Table loadingTable = new Table();
    private Label loadingLabel;
    private BorderedTable leftTable;
    private BorderedTable rightTable;
    private Table centerTable = new Table();
    private TextureRegion bg = game.textureBank.region("IMAGE_MAINMENU_BACKGROUND");
    private ResourcesTable resourcesTable = new ResourcesTable(App.getCurrentUser(), game);
    private TextButton challengeBtn, playBtn;
    private OnlineRoomMenuController controller = new OnlineRoomMenuController(this);
    private TextField usernameField;
    private TextButton cancelBtn;

    public OnlineRoomMenuScreen(Main game) {
        super(game);
    }

    @Override
    protected void buildUI() {
        leftTable = new BorderedTable();
        rightTable = new BorderedTable();
        mainTable.center().defaults().pad(10);
        centerTable.add(leftTable).height(500).width(500);
        centerTable.add(rightTable).height(500).width(500);
        mainTable.setBackground(new TextureRegionDrawable(bg));
        Label leftTitle = new Label("CHALLENGE A FRIEND", skin, "big_outline");
        Label leftDescription = new Label("enter a username to send request", skin, "medium");
        leftDescription.setColor(Color.BLACK);
        usernameField = new TextField("", skin);
        usernameField.setMessageText("username");
        challengeBtn = new TextButton("CHALLENGE", skin);
        Label rightTitle = new Label("RANDOM MATCH", skin, "big_outline");
        Label rightDescription = new Label("play against random opponents", skin, "medium");
        rightDescription.setColor(Color.BLACK);
        playBtn = new TextButton("PLAY", skin);
        leftTable.center().top().add(leftTitle).row();
        leftTable.add(leftDescription).row();
        leftTable.add(usernameField).pad(30).row();
        leftTable.add().expandY().row();
        leftTable.add(challengeBtn);
        rightTable.center().top().add(rightTitle).row();
        rightTable.add(rightDescription).row();
        rightTable.add().expandY().row();
        rightTable.add(playBtn);
        mainStack.add(mainTable);
        mainTable.add(centerTable);
        createTopBar();
        createLoadingPart();
        setListeners();
    }

    private void createLoadingPart() {
        loadingLabel = new Label("", skin, "big_outline");
        PamActor back = new PamActor(game.pamPlayer, "768/INITIAL/EFFECTS/LOAD_ICON_BACK/LOAD_ICON_BACK.PAM",
            "animation", 1, null);
        PamActor front = new PamActor(game.pamPlayer, "768/INITIAL/EFFECTS/LOAD_ICON_FRONT/LOAD_ICON_FRONT.PAM",
            "animation", 1, null);
        loadingTable.top().add(loadingLabel).top().padBottom(200).row();
        Stack loading = new Stack();
        loading.add(back);
        loading.add(front);
        Table loadingWrapper = new Table();
        loadingWrapper.top().add(loadingTable).growY().top();
        loadingTable.center().add(loading).center().row();
        cancelBtn = new TextButton("CANCEL", skin);
        cancelBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.cancelRandomMatch();
            }
        });
        loadingTable.add(cancelBtn).padTop(200);
        cancelBtn.setVisible(false);

        mainStack.add(loadingWrapper);
        loadingTable.setVisible(false);
    }

    private void setListeners() {
        challengeBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new Thread(() -> {
                    Gdx.app.postRunnable(() -> {
                        controller.challenge(usernameField.getText());
                    });
                }).start();
            }
        });
        playBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.play();
            }
        });
    }

    private void createTopBar() {
        Table main = new Table();
        Table topBar = new Table();
        ImageButton backBtn = MainMenuScreen.createImageButton("IMAGE_UI_MAINMENU_BACK_BTN_NORMAL",
            "IMAGE_UI_MAINMENU_BACK_BTN_PRESSED",
            game.textureBank
        );
        main.top().add(topBar).growX();
        topBar.add(backBtn).pad(10);
        topBar.add().expandX();
        topBar.add(resourcesTable).padRight(20);
        backBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (loadingTable.isVisible()){
                    if (!controller.cancelRandomMatch()) return;
                }
                controller.exitMenu();
            }
        });
        mainStack.add(main);
    }


    public void toggleToLoading(String type) {
        if (loadingTable.isVisible()) return;
        if (type.equals("challenge")){
            loadingLabel.setText("waiting for a response from your friend...");
        }
        else if (type.equals("play")){
            loadingLabel.setText("finding an opponent...");
        }
        centerTable.addAction(Actions.sequence(
            Actions.fadeOut(0.5f),
            Actions.visible(false)
        ));
        loadingTable.addAction(Actions.sequence(
            Actions.visible(true),
            Actions.fadeIn(0.5f)
        ));
        if (type.equals("play")){
            cancelBtn.addAction(Actions.sequence(
                Actions.visible(true),
                Actions.fadeIn(0.5f)
            ));
        }
    }

    public void toggleToRequestSection() {
        if (centerTable.isVisible()) return;
        usernameField.clearSelection();
        loadingTable.addAction(Actions.sequence(
            Actions.fadeOut(0.5f),
            Actions.visible(false)
        ));
        centerTable.addAction(Actions.sequence(
            Actions.visible(true),
            Actions.fadeIn(0.5f)
        ));
        if (cancelBtn.isVisible()){
            cancelBtn.addAction(Actions.sequence(
                Actions.fadeOut(0.5f),
                Actions.visible(false)
            ));
        }
    }

}
