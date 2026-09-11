package com.pvz2.view.screen;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.pvz2.Main;
import com.pvz2.models.core.App;
import com.pvz2.view.util.PamActor;
import com.pvz2.view.table.ResourcesTable;
import com.pvz2.view.table.ZombiesTable;

import java.util.HashMap;

public class ZombiesCollectionMenuScreen extends MenuScreen{
    private String zombieName;
    private CollectionMenuScreen collectionMenuScreen;
    private ResourcesTable resourcesTable = new ResourcesTable(App.getCurrentUser(), game);

    public ZombiesCollectionMenuScreen(Main game, String zombieName,
                                       CollectionMenuScreen collectionMenuScreen) {
        super(game);
        this.zombieName = zombieName;
        this.collectionMenuScreen = collectionMenuScreen;
    }

    @Override
    protected void buildUI() {
        if (resourcesTable == null && App.getCurrentUser() != null) {
            resourcesTable = new ResourcesTable(App.getCurrentUser(), game);
        }
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        Table topBar = new Table();
        ImageButton backBtn = MainMenuScreen.createImageButton(
            "IMAGE_UI_ALMANAC_BUTTONS_HUD_BACK_NORMAL",
            "IMAGE_UI_ALMANAC_BUTTONS_HUD_BACK_SELECTED",
            game.textureBank
        );
        if (backBtn != null) {
            topBar.add(backBtn).left().top().pad(15);
        }
        topBar.add().expandX().fillX();
        if (resourcesTable != null) {
            resourcesTable.update();
            topBar.add(resourcesTable).right().top().pad(15);
        }
        mainTable.add(topBar).top().growX().row();
        Table centerTable = new Table();
        centerTable.add(new Label(zombieName, skin, "big_outline"))
            .colspan(2)
            .padBottom(80)
            .row();
        Table leftColumn = new Table();
        leftColumn.add(createCardAppearanceTable()).size(450).row();
        centerTable.add(leftColumn).top().padRight(80);
        centerTable.add(createCardFieldsTable()).top().size(350).row();
        mainTable.add(centerTable).expand().center().row();
        mainTable.setBackground(new TextureRegionDrawable(
            game.textureBank.region("IMAGE_MAINMENU_BACKGROUND")
        ));
        backBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fadeAndSwitchScreen(collectionMenuScreen);
            }
        });
        mainStack.add(mainTable);
    }

    private Table createCardFieldsTable() {
        float[] result = collectionMenuScreen.getController().showZombie(App.getZombieId(zombieName));
        Table table = new Table();
        table.top().left();

        Table toughnessTable = new Table();
        toughnessTable.add(new Image(game.textureBank.region("IMAGE_UI_ALMANAC_ZOMBIES_ZOMBIETOUGHNESS_ICON"))).pad(5);
        Table details = new Table();
        details.left().add(new Label("TOUGHNESS", skin, "medium")).expandX().row();
        details.add(new Label(getToughnessString((int) result[0]), skin, "medium")).expandX();
        toughnessTable.add(details);

        table.add(toughnessTable).left().pad(20).row();

        Table speedTable = new Table();
        speedTable.add(new Image(game.textureBank.region("IMAGE_UI_ALMANAC_ZOMBIES_ZOMBIESPEED_ICON"))).pad(5);
        Table speedDetails = new Table();
        speedDetails.left().add(new Label("SPEED", skin, "medium")).expandX().row();
        speedDetails.add(new Label(getSpeedString(result[1]), skin, "medium")).expandX();
        speedTable.add(speedDetails);

        table.add(speedTable).left().pad(20).row();

        Label damage = new Label("DAMAGE: " +(int) result[2], skin, "medium");
        table.add(damage).pad(20).left();
        return table;
    }

    private String getToughnessString(int i) {
        if (i <= 250){
            return "LOW";
        }
        else if (i <= 600){
            return "MEDIUM";
        }
        else if (i <= 1000){
            return "HIGH";
        }
        else{
            return "VERY HIGH";
        }
    }

    private String getSpeedString(float i) {
        if (i <= 1){
            return "SLOW";
        }
        else if (i <= 1.9){
            return "NORMAL";
        }
        else{
            return "FAST";
        }
    }

    private Table createCardAppearanceTable() {
        String path = ZombiesTable.getZombiesAnimAddress().get(zombieName);
        String clip = zombieName.equals("ZombieNewspaper") ? "idle_newspaper" : "idle";
        HashMap<String, Boolean> visibility =
            ZombiesTable.getZombiesVisibilities().getOrDefault(zombieName, null);
        PamActor zombiePam = new PamActor(game.pamPlayer, path, clip, 0.7f, visibility);

        Table table = new Table();
        if (zombieName.contains("Beach")){
            table.setBackground(new TextureRegionDrawable(
                game.textureBank.region("IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_BEACH_WATER")));
        }
        else{
            table.setBackground(new TextureRegionDrawable(
                game.textureBank.region("IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_MODERN")));
        }
        if (zombieName.equals("ZombiePiano")){
            PamActor pianoPam = new PamActor(game.pamPlayer,
                ZombiesTable.getZombiesAnimAddress().get("Piano"), "idle", 0.7f, null);
            Stack stack = new Stack();
            Table pianoTbl = new Table();
            pianoTbl.add(pianoPam).padLeft(-1);
            Table zombieTbl = new Table();
            zombieTbl.add(zombiePam);
            stack.add(pianoTbl);
            stack.add(zombieTbl);
            table.add(stack);
        }
        else{
            table.add(zombiePam);
        }
        return table;
    }
}
