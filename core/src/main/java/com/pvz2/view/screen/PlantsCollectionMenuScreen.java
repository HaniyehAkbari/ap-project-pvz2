package com.pvz2.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.pvz2.Main;
import com.pvz2.controller.CollectionMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.PlantAnimationClips;
import com.pvz2.view.util.PamActor;
import com.pvz2.view.table.PlantsTable;
import com.pvz2.view.table.ResourcesTable;

public class PlantsCollectionMenuScreen extends MenuScreen{
    private PlantType plantType;
    private CollectionMenuScreen collectionMenuScreen;
    private ResourcesTable resourcesTable = new ResourcesTable(App.getCurrentUser(), game);


    public PlantsCollectionMenuScreen(Main game, PlantType plantType, CollectionMenuScreen collectionMenuScreen) {
        super(game);
        this.plantType = plantType;
        this.collectionMenuScreen = collectionMenuScreen;
    }

    @Override
    protected void buildUI() {
        if (resourcesTable == null && App.getCurrentUser() != null) {
            resourcesTable = new ResourcesTable(App.getCurrentUser(), game);}
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        Table topBar = new Table();
        ImageButton backBtn = MainMenuScreen.createImageButton(
            "IMAGE_UI_ALMANAC_BUTTONS_HUD_BACK_NORMAL",
            "IMAGE_UI_ALMANAC_BUTTONS_HUD_BACK_SELECTED",
            game.textureBank);
        if (backBtn != null) {
            topBar.add(backBtn).left().top().pad(15);}
        topBar.add().expandX().fillX();
        if (resourcesTable != null) {
            resourcesTable.update();
            topBar.add(resourcesTable).right().top().pad(15);}
        mainTable.add(topBar).top().growX().row();
        Table centerTable = new Table();
        centerTable.add(new Label(plantType.name(), skin, "big_outline"))
            .colspan(2)
            .padBottom(80)
            .row();
        Table leftColumn = new Table();
        leftColumn.add(createCardAppearanceTable(plantType, game, 0.8f)).size(350).row();
        int cardLevel =
            (App.getCurrentUser() != null && App.getCurrentUser().getUnlockedPlantsLevels().get(plantType) != null)
            ? App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(plantType, 1) : 1;
        Label levelLbl = new Label("Level " + cardLevel, skin, "medium");
        leftColumn.add(levelLbl).padTop(-40).padBottom(15).row();
        if (cardLevel < 4){
            leftColumn.add(PlantsTable.createProgressStack(App.getCurrentUser(), plantType, cardLevel, "medium"))
                .growX()
                .row();}
        if (App.getCurrentUser().getUnlockedPlantsLevels().getOrDefault(plantType, 0) == 0){
            leftColumn.add(createBuyBtn()).pad(10).growX();}
        else{
            leftColumn.add(createUpgradeBtn()).pad(10).growX();}
        centerTable.add(leftColumn).top().padRight(80);
        centerTable.add(createCardFieldsTable(plantType, game, "medium", true)).top().size(350).row();
        mainTable.add(centerTable).expand().center().row();
        mainTable.setBackground(new TextureRegionDrawable(
            game.textureBank.region("IMAGE_MAINMENU_BACKGROUND")));
        backBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fadeAndSwitchScreen(collectionMenuScreen);}});
        mainStack.add(mainTable);
    }

    private Button createBuyBtn() {
        TextButton textButton = new TextButton("BUY", skin, "purple");
        textButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (collectionMenuScreen.getController().purchasePlant(plantType)){
                    buildUI();
                    collectionMenuScreen.getResourcesTable().update();
                    collectionMenuScreen.resetMainTable();
                }
            }
        });
        return textButton;
    }

    private Button createUpgradeBtn() {
        TextButton textButton = new TextButton("UPGRADE", skin, "purple");
        textButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (collectionMenuScreen.getController().upgradePlant(plantType)){
                    buildUI();
                    collectionMenuScreen.getResourcesTable().update();
                }
            }
        });
        return textButton;
    }

    public static Table createCardFieldsTable(PlantType plantType, Main game, String style,
                                              boolean showFamily) {
        int[] result = CollectionMenuController.showPlant(plantType);
        Table table = new Table();
        table.top().left();
        table.add(createFieldTable("IMAGE_UI_ALMANAC_ALMANAC_STAT_ICON_SUNCOST", "SUN COST",
            result, 0, game, style)).left().pad(20);
        table.add(createFieldTable("IMAGE_UI_ALMANAC_PLANTS_RECHARGE_ICON", "RECHARGE",
            result, 1, game, style)).pad(20).row();
        table.add(createFieldTable("IMAGE_UI_ALMANAC_PLANTS_TOUGHNESS_ICON", "TOUGHNESS",
            result, 2, game, style)).left().pad(20);
        table.add(createFieldTable("IMAGE_UI_ALMANAC_PLANTS_DAMAGE_ICON", "DAMAGE",
            result, 3, game, style)).pad(20).row();
        if (showFamily) {
            Image familyImg =
                new Image(game.textureBank.region(PlantsTable.getPlantsFamilyMap().get(plantType.family)));
            Label familyLbl = new Label(plantType.family.name(), game.skin, "medium");
            table.add(familyImg).pad(5);
            table.add(familyLbl).row();
            if (plantType.hasAnyTag()) {
                String tags = String.join(", ", plantType.tags);
                Label tagsLbl = new Label("TAGS: " + tags, game.skin, "medium");
                table.add(tagsLbl).colspan(2).pad(10);
            }
        }
        return table;
    }

    private static Table createFieldTable(String address, String text, int[] result, int index,
                                          Main game, String style) {
        Table table = new Table();
        table.add(new Image(game.textureBank.region(address))).pad(5);
        Table details = new Table();
        details.left().add(new Label(text, game.skin, style)).expandX().row();
        details.add(new Label(Integer.toString(result[index]), game.skin, style)).expandX();
        table.add(details);
        return table;
    }

    public static Table createCardAppearanceTable(PlantType plantType, Main game, float scale) {
        String path = getPlantAnimAddress(plantType);
        String clip = getPlantInitialClip(plantType);
        PamActor plantPam = new PamActor(game.pamPlayer, path, clip, scale, null);


        Table table = new Table();
        if (plantType.hasTag("Water")){
            table.setBackground(new TextureRegionDrawable(
                game.textureBank.region("IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_BEACH_WATER")));
        }
        else if (plantType.hasTag("Shroom")){
            table.setBackground(new TextureRegionDrawable(
                game.textureBank.region("IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_DARK")));
        }
        else {
            table.setBackground(new TextureRegionDrawable(
                game.textureBank.region("IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_MODERN")));
        }
        table.add(plantPam);
        return table;
    }

    public static String getPlantAnimAddress(PlantType type){
        String name = type.name().toUpperCase().replaceAll("_", "");
        String finalPath;

        switch (type){
            case TWIN_SUNFLOWER -> name = "SUNFLOWER_TWIN";
            case PRIMAL_POTATO_MINE -> name = "PRIMAL_POTATOMINE";
            case PRIMAL_SUNFLOWER -> name = "PRIMAL_SUNFLOWER";
            case XSHOT -> name = "ROTORUTABAGA";
            case KERNEL_PULT -> name = "KERNALPULT";
            case PHAT_BEET -> name = "PHATBEETS";
            case POISON_PEASHOOTER -> name = "GOOPEASHOOTER";
        }

        String initialPath = "768/INITIAL/PLANT/" + name + "/" + name + ".PAM";
        String fullPath = "768/FULL/PLANT/" + name + "/" + name + ".PAM";

        finalPath = Gdx.files.internal("IMAGES/" + initialPath).exists() ? initialPath :
            fullPath;

        if (type.name().contains("MINT")){
            finalPath = "768/INITIAL/EMPOWERMINTS/PLANT/" + name + "/" + name + ".PAM";
        }

        return finalPath;
    }

    public static String getPlantInitialClip(PlantType type){
        String clip = "idle";
        if (type.hasTag("Wramp-up") || type == PlantType.PUFF_SHROOM){
            clip = type == PlantType.KIWIBEAST ? "idle_stage3_" : "idle_stage3";
        }
        else if (type.name().contains("MINT")){
            clip = "loop";
        }
        else if (type == PlantType.DOOM_SHROOM){
            clip = "stage2_idle";
        }
        else if (type == PlantType.GRAVE_BUSTER){
            clip = "attack";
        }

        return clip;
    }

    public static String getSpecialClip(PlantType type){
        return PlantAnimationClips.getSpecialClip(type);
    }


}
