package com.pvz2.view.table;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pvz2.Main;
import com.pvz2.controller.CollectionMenuController;
import com.pvz2.models.core.User;
import com.pvz2.models.enums.PlantType;
import com.pvz2.view.screen.GameScreen;
import com.pvz2.view.screen.PlantsCollectionMenuScreen;
import com.ray3k.tenpatch.TenPatchDrawable;

public class PlantDetailsTable extends Table {
    private PlantType type;
    private final User user;
    private final Main game;
    private GameScreen screen;

    public PlantDetailsTable(PlantType type, User user, Main game, GameScreen screen) {
        this.type = type;
        this.user = user;
        this.game = game;
        this.screen = screen;
        build();
    }

    private void build(){
        this.clear();
        TenPatchDrawable tenPatchDrawable =
            new TenPatchDrawable((TenPatchDrawable) game.skin.getDrawable(
                "image_ui_dialog_asset_inner_bkgd_10"));
        this.setBackground(tenPatchDrawable);
        this.add(new Label(type.name(), game.skin, "secondary")).center().row();
        Table leftColumn = new Table();
        leftColumn.add(PlantsCollectionMenuScreen.createCardAppearanceTable(type, game, 0.5f)).row();
        int cardLevel = user.getUnlockedPlantsLevels().getOrDefault(type, 0);
        if (cardLevel < 4){
            leftColumn.add(PlantsTable.createProgressStack(user, type, Math.max(1, cardLevel),
                "medium"));
        }
        this.add(leftColumn).left().pad(5);
        Table rightColumn = new Table();
        rightColumn.add(PlantsCollectionMenuScreen.createCardFieldsTable(type, game, "secondary", false
            )).row();
        if (cardLevel > 0){
            rightColumn.bottom();
            rightColumn.add(btnStack("UPGRADE", "IMAGE_UI_QUESTS_COIN_ICON", cardLevel*1000,
                "purple",
                () -> {
                    if (CollectionMenuController.upgradePlant(type, screen)){
                        build();
                        screen.buildStreetTable();
                        screen.getHud().getResourcesTable().update();
                    }
                }
                )).pad(5);
            rightColumn.add(btnStack("BOOST", "IMAGE_UI_QUESTS_GEM_ICON", 15, "default",
                () -> {
                    if (screen.getPlantMenuController().boostPlant(type.name(), screen)){
                        screen.buildStreetTable();
                        screen.getHud().getResourcesTable().update();
                        screen.getHud().getSelectedPlantsList().build();
                    }
                }
                )).pad(5);
        }
        this.add(rightColumn);

    }

    private Stack btnStack(String name, String imgAddress, int amount, String style, Runnable runnable){
        Stack stack = new Stack();
        TextButton textButton = new TextButton("", game.skin, style);
        stack.add(textButton);
        textButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                runnable.run();
            }
        });
        Table details = new Table();
        details.add(new Label(name, game.skin, "medium"));
        details.add(new Image(game.textureBank.region(imgAddress)));
        details.add(new Label(Integer.toString(amount), game.skin, "medium"));
        Table wrapper = new Table();
        wrapper.add(details).pad(7);
        stack.add(wrapper);
        details.setTouchable(Touchable.disabled);
        return stack;
    }

    public void reset(PlantType type){
        this.type = type;
        build();
    }
}
