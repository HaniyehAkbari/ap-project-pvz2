package com.pvz2.view.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.pvz2.Main;
import com.pvz2.controller.CollectionMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantFamily;
import com.pvz2.view.table.PlantCardView;
import com.pvz2.view.table.PlantsTable;
import com.pvz2.view.table.ResourcesTable;
import com.pvz2.view.table.ZombiesTable;
import com.ray3k.tenpatch.TenPatchDrawable;

import java.util.function.Consumer;

public class CollectionMenuScreen extends MenuScreen {
    private CollectionMenuController controller;
    private ScrollPane pane;
    private PlantsTable plantsTable;
    private Table zombiesTable;
    private Table currentTable;
    private Table mainTable;
    private ResourcesTable resourcesTable = new ResourcesTable(App.getCurrentUser(), game);
    private boolean plantsTabActive = true;

    public CollectionMenuScreen(Main game, MenuScreen lastScreen) {
        super(game);
        this.controller = new CollectionMenuController(lastScreen, this);
    }

    @Override
    public void show() {
        super.show();
        resetMainTable();
    }

    @Override
    protected void buildUI() {
        plantsTable = new PlantsTable(8, 30, true, 150, 100, createCollectionMenuCardsMethod());
        plantsTable.build();
        zombiesTable = new ZombiesTable(createCollectionMenuZombieCardsMethod(), 6,
            App.getCurrentUser().getShowedZombies().keySet(), false);
        currentTable = plantsTabActive ? plantsTable : zombiesTable;

        pane = new ScrollPane(currentTable, skin);
        pane.setFadeScrollBars(true);
        pane.setScrollingDisabled(true, false);

        mainTable = new Table();
        Table rootTable = new Table();

        resetMainTable();

        TenPatchDrawable tenPatchDrawable = new TenPatchDrawable((TenPatchDrawable)
            skin.getDrawable("image_ui_quests_panel_edge_to_edge_ten"));
        Table headerTable = buildHeaderTable();

        Table containingTable = new Table();
        containingTable.setBackground(tenPatchDrawable);

        rootTable.bottom();
        containingTable.top().add(headerTable).growX().padLeft(10).padRight(10).padTop(-80);
        containingTable.row();
        containingTable.add(mainTable).bottom().growX();
        rootTable.add(containingTable).bottom().height(900).growX();

        Table topBar = new Table();
        Table topBarWrapper = new Table();
        topBar.add(resourcesTable).padRight(100);
        topBarWrapper.top().right().add(topBar).pad(5);
        mainStack.add(rootTable);
        mainStack.add(topBarWrapper);
    }

    public void resetMainTable() {
        mainTable.clearChildren();
        mainTable.add(pane).pad(20).row();
        if (currentTable instanceof PlantsTable) mainTable.add(createFilterBar(plantsTable)).center();
    }

    private Consumer<PlantCardView> createCollectionMenuCardsMethod() {
        return new Consumer<PlantCardView>() {
            @Override
            public void accept(PlantCardView plantCardView) {
                PlantsCollectionMenuScreen plantsCollectionMenuScreen =
                    new PlantsCollectionMenuScreen(game, plantCardView.getType(),
                        CollectionMenuScreen.this);
                fadeAndSwitchScreen(plantsCollectionMenuScreen);
                controller.setPlantsCollectionMenuScreen(plantsCollectionMenuScreen);
            }
        };
    }

    private Consumer<String> createCollectionMenuZombieCardsMethod() {
        return new Consumer<String>() {
            @Override
            public void accept(String name) {
                fadeAndSwitchScreen(new ZombiesCollectionMenuScreen(game, name,
                    CollectionMenuScreen.this));
            }
        };
    }

    private Table buildHeaderTable() {
        Table headerTable = new Table();

        ImageButton plantsTab = createTabButton("IMAGE_UI_ALMANAC_TABS_PLANTS_DOWN",
            "IMAGE_UI_ALMANAC_TABS_PLANTS_ACTIVE");
        Stack plantsTabStack = createTabStack(plantsTab, "IMAGE_UI_STORE_TABICONS_PLANTS");

        ImageButton zombiesTab = createTabButton("IMAGE_UI_ALMANAC_TABS_ZOMBIES_DOWN",
            "IMAGE_UI_ALMANAC_TABS_ZOMBIES_ACTIVE");
        Stack zombiesTabStack = createTabStack(zombiesTab, "IMAGE_UI_STORE_TABICONS_ZOMBIES");

        ButtonGroup<ImageButton> tabGroup = new ButtonGroup<>(plantsTab, zombiesTab);
        tabGroup.setMinCheckCount(1);
        tabGroup.setMaxCheckCount(1);
        if (currentTable instanceof PlantsTable) plantsTab.setChecked(true);
        else zombiesTab.setChecked(true);

        setupTabListeners(plantsTab, zombiesTab);

        Table tabsTable = new Table();
        tabsTable.left().top();
        tabsTable.add(plantsTabStack).top().padLeft(10).padRight(15);
        tabsTable.add(zombiesTabStack).top();

        ImageButton exitButton = MainMenuScreen.createImageButton(
            "IMAGE_UI_ALMANAC_TABS_CLOSE_TAB", "IMAGE_UI_ALMANAC_TABS_CLOSE_TAB_DOWN",
            game.textureBank);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.exitMenu();
            }
        });

        headerTable.add(tabsTable).left().expandX();
        headerTable.add().expandX();
        headerTable.add(exitButton).right().padBottom(-12).size(50, 50);

        return headerTable;
    }

    private ImageButton createTabButton(String upRegion, String checkedRegion) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new Image(game.textureBank.region(upRegion)).getDrawable();
        style.imageChecked = new Image(game.textureBank.region(checkedRegion)).getDrawable();
        return new ImageButton(style);
    }

    private Stack createTabStack(ImageButton tabButton, String iconRegion) {
        Image icon = new Image(game.textureBank.region(iconRegion));
        icon.setTouchable(Touchable.disabled);
        Table iconWrapper = new Table();
        iconWrapper.add(icon);

        Stack tabStack = new Stack();
        tabStack.add(tabButton);
        tabStack.add(iconWrapper);
        return tabStack;
    }

    private void setupTabListeners(ImageButton plantsTab, ImageButton zombiesTab) {
        plantsTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (plantsTab.isChecked()) {
                    currentTable = plantsTable;
                    plantsTabActive = true;
                    pane.setActor(plantsTable);
                    resetMainTable();
                }
            }
        });

        zombiesTab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (zombiesTab.isChecked()) {
                    plantsTabActive = false;
                    currentTable = zombiesTable;
                    pane.setActor(zombiesTable);
                    resetMainTable();
                }
            }
        });
    }

    private Table createFilterBar(PlantsTable plantsTable) {
        Table filterTable = new Table();
        filterTable.defaults().pad(5);

        SelectBox<String> familySelect = new SelectBox<>(skin);
        Array<String> familyOptions = new Array<>();
        familyOptions.add("All Families");
        for (PlantFamily family : PlantFamily.values()) {
            familyOptions.add(family.name());
        }
        familySelect.setItems(familyOptions);

        SelectBox<PlantsTable.LockFilter> lockSelect = new SelectBox<>(skin);
        lockSelect.setItems(PlantsTable.LockFilter.ALL,
            PlantsTable.LockFilter.UNLOCKED_ONLY, PlantsTable.LockFilter.LOCKED_ONLY);

        CheckBox upgradeableCheck = new CheckBox("Upgradeable", skin);

        ChangeListener filterChangeListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selectedFamStr = familySelect.getSelected();
                PlantFamily family = selectedFamStr.equals("All Families") ? null : PlantFamily.valueOf(selectedFamStr);

                PlantsTable.LockFilter lockFilter = lockSelect.getSelected();
                boolean upgradeable = upgradeableCheck.isChecked();

                plantsTable.applyFilters(family, lockFilter, upgradeable);
            }
        };

        familySelect.addListener(filterChangeListener);
        lockSelect.addListener(filterChangeListener);
        upgradeableCheck.addListener(filterChangeListener);

        filterTable.add(new Label("Family:", skin)).left();
        filterTable.add(familySelect);
        filterTable.add(new Label("Status:", skin)).padLeft(15);
        filterTable.add(lockSelect);
        filterTable.add(upgradeableCheck).padLeft(15);

        return filterTable;
    }

    public CollectionMenuController getController() {
        return controller;
    }

    public ResourcesTable getResourcesTable() {
        return resourcesTable;
    }

    public PlantsTable getPlantsTable() {
        return plantsTable;
    }
}
