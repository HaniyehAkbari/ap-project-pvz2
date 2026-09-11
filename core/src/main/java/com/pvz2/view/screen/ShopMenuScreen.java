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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.pvz2.Main;
import com.pvz2.controller.ShopMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.shop.DailyOffer;
import com.pvz2.models.shop.ShopItem;
import com.pvz2.models.shop.ShopList;
import com.pvz2.view.util.PamActor;
import com.pvz2.view.table.ResourcesTable;
import pvz.skin.BorderedTable;

import java.util.List;

public class ShopMenuScreen extends MenuScreen {

    private final ShopMenuController controller;
    private final ShopList shopList;

    private Table mainTable;
    private Table itemsGrid;
    private ResourcesTable resourcesTable;

    public ShopMenuScreen(Main game) {
        super(game);
        this.controller = new ShopMenuController();
        this.shopList = new ShopList();
    }

    @Override
    protected void buildUI() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();

        TextureRegion bgRegion = game.textureBank.region("IMAGE_MAINMENU_BACKGROUND");
        if (bgRegion != null) {
            mainTable.setBackground(new TextureRegionDrawable(bgRegion));
        }

        Table topBar = new Table();
        ImageButton backBtn = MainMenuScreen.createImageButton(
            "IMAGE_UI_MAINMENU_BACK_BTN_NORMAL",
            "IMAGE_UI_MAINMENU_BACK_BTN_PRESSED",
            game.textureBank);

        if (backBtn != null) {
            backBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    fadeAndSwitchScreen(new MainMenuScreen(game));
                }
            });
            topBar.add(backBtn).size(60, 60).left().pad(10);
        }

        Label titleLabel = new Label("STORE", skin, "big_outline");
        titleLabel.setFontScale(1.1f);
        topBar.add(titleLabel).expandX().center();

        if (App.getCurrentUser() != null) {
            resourcesTable = new ResourcesTable(App.getCurrentUser(), game);
            topBar.add(resourcesTable).right().pad(10);
        }

        mainTable.add(topBar).growX().padTop(10).padBottom(10).row();
        itemsGrid = new Table();
        itemsGrid.top();
        refreshShopItems();
        ScrollPane scrollPane = new ScrollPane(itemsGrid, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFlickScroll(true);
        scrollPane.setOverscroll(false, false);
        stage.setScrollFocus(scrollPane);
        mainTable.add(scrollPane).grow().pad(10).row();
        mainStack.add(mainTable);
    }

    public void refreshShopItems() {
        itemsGrid.clear();
        int cols = 0;
        int maxCols = 3;

        DailyOffer dailyOffer = shopList.getDailyOffer();
        if (dailyOffer != null && dailyOffer.isAvailableToday()) {
            Table dailyCard = createDailyOfferCard(dailyOffer);
            itemsGrid.add(dailyCard).width(210).height(270).pad(10);
            cols++;
            if (cols % maxCols == 0) itemsGrid.row();
        }

        List<ShopItem> permanentItems = shopList.getPermanentItems();
        for (ShopItem item : permanentItems) {
            Table itemCard = createPermanentItemCard(item);
            itemsGrid.add(itemCard).width(210).height(270).pad(10);
            cols++;
            if (cols % maxCols == 0) itemsGrid.row();
        }
    }

    private Table createDailyOfferCard(DailyOffer offer) {
        BorderedTable card = new BorderedTable();
        card.pad(10);
        card.top();

        Label badge = createDailyOfferBadge();
        Label titleLabel = createDailyOfferTitle(offer.getName());
        Container<Actor> animContainer = createDailyOfferAnimation(offer);
        Table timerBox = createDailyOfferTimer();
        TextButton buyBtn = createDailyOfferBuyButton(offer);

        card.add(badge).center().padTop(5).row();
        card.add(titleLabel).width(170).height(40).center().padBottom(5).row();
        card.add(animContainer).size(100, 85).center().row();
        card.add(timerBox).center().padTop(5).padBottom(5).row();
        card.add().expandY().row();
        card.add(buyBtn).width(150).height(42).padBottom(15).bottom();

        return card;
    }

    private Label createDailyOfferBadge() {
        Label badge = new Label("DAILY OFFER", skin, "big_outline");
        badge.setFontScale(0.48f);
        badge.setColor(Color.YELLOW);
        return badge;
    }

    private Label createDailyOfferTitle(String name) {
        Label titleLabel = new Label(name, skin, "big_outline");
        titleLabel.setFontScale(0.5f);
        titleLabel.setWrap(true);
        titleLabel.setAlignment(com.badlogic.gdx.utils.Align.center);
        return titleLabel;
    }

    private Container<Actor> createDailyOfferAnimation(DailyOffer offer) {
        Actor plantDisplay;
        try {
            PlantType plant = offer.getPlantType();
            String path = PlantsCollectionMenuScreen.getPlantAnimAddress(plant);
            String clip = PlantsCollectionMenuScreen.getPlantInitialClip(plant);
            plantDisplay = new PamActor(game.pamPlayer, path, clip, 0.55f, null);
        } catch (Exception e) {
            plantDisplay = getItemImage(offer.getPlantType().name());
        }

        Container<Actor> animContainer = new Container<>(plantDisplay);
        animContainer.center();
        animContainer.padLeft(18).padTop(10);
        return animContainer;
    }

    private Table createDailyOfferTimer() {
        Label timerTitle = new Label("Resets in: ", skin);
        timerTitle.setFontScale(0.6f);
        timerTitle.setColor(Color.BLACK);

        Label timerLabel = new Label("", skin);
        timerLabel.setFontScale(0.7f);
        timerLabel.setColor(Color.RED);

        timerLabel.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.forever(
            com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence(
                com.badlogic.gdx.scenes.scene2d.actions.Actions.run(() -> {
                    java.time.LocalDateTime now = java.time.LocalDateTime.now();
                    java.time.LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
                    java.time.Duration duration = java.time.Duration.between(now, midnight);
                    long totalSeconds = Math.max(0, duration.getSeconds());
                    long hours = totalSeconds / 3600;
                    long minutes = (totalSeconds % 3600) / 60;
                    long seconds = totalSeconds % 60;
                    timerLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                }),
                com.badlogic.gdx.scenes.scene2d.actions.Actions.delay(1f)
            )
        ));

        Table timerBox = new Table();
        timerBox.add(timerTitle);
        timerBox.add(timerLabel);
        return timerBox;
    }

    private TextButton createDailyOfferBuyButton(DailyOffer offer) {
        TextButton buyBtn = new TextButton(offer.getCoinCost() + " Coins", skin, "purple");
        buyBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showPurchaseConfirmation(offer.getName(), offer.getCoinCost() + " Coins", () -> {
                    String result = controller.buyItem(offer.getId(), 1, offer.getPlantType().name());
                    handlePurchaseResult(result);
                });
            }
        });
        return buyBtn;
    }


    private Table createPermanentItemCard(ShopItem item) {
        BorderedTable card = new BorderedTable();
        card.pad(10);
        card.top();

        Label titleLabel = new Label(item.getName(), skin, "big_outline");
        titleLabel.setFontScale(0.5f);
        titleLabel.setWrap(true);
        titleLabel.setAlignment(com.badlogic.gdx.utils.Align.center);

        Image itemImg = getItemImage(item.getName());

        String priceText = item.getCoinCost() > 0 ? item.getCoinCost() + " Coins" : item.getDiamondCost() + " Gems";
        TextButton buyBtn = new TextButton(priceText, skin, "green");

        buyBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String cleanName = item.getName().toLowerCase();
                boolean isSpecificSeed = cleanName.contains("specific") || cleanName.contains("choose") ||
                    cleanName.contains("select");

                if (isSpecificSeed) {
                    showAllPlantsCollectionDialog(item, priceText);
                } else {
                    showPurchaseConfirmation(item.getName(), priceText, () -> {
                        String result = controller.buyItem(item.getId(), 1, null);
                        handlePurchaseResult(result);
                    });
                }
            }
        });

        card.add(titleLabel).width(170).height(45).center().padBottom(30).row();
        card.add(itemImg).size(80, 80).center().padBottom(10).row();
        card.add().expandY().row();
        card.add(buyBtn).width(150).height(42).padBottom(15).bottom();

        return card;
    }

    private void showAllPlantsCollectionDialog(ShopItem item, String priceText) {
        Table overlay = createOverlayTable();
        BorderedTable popup = new BorderedTable();
        popup.pad(20);

        Label title = new Label("Select Plant to Purchase", skin, "big_outline");
        title.setFontScale(0.85f);

        Table plantGrid = new Table();
        plantGrid.top();
        int col = 0;

        for (PlantType plant : PlantType.values()) {
            Table plantCell = createDirectPlantCell(plant, item, priceText, overlay);
            plantGrid.add(plantCell).width(130).height(140).pad(10);

            col++;
            if (col % 4 == 0) plantGrid.row();
        }

        ScrollPane scroll = new ScrollPane(plantGrid, skin);
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);
        stage.setScrollFocus(scroll);

        TextButton cancelBtn = new TextButton("Cancel", skin, "purple");
        cancelBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                overlay.remove();
            }
        });

        popup.add(title).padBottom(15).row();
        popup.add(scroll).width(650).height(380).padBottom(15).row();
        popup.add(cancelBtn).width(140).height(45);

        overlay.add(popup);
        stage.addActor(overlay);
    }

    private Table createDirectPlantCell(PlantType plant, ShopItem item, String priceText, Table overlay) {
        Table cell = new Table();
        cell.pad(5);
        cell.top();

        Actor plantDisplay;
        try {
            String path = PlantsCollectionMenuScreen.getPlantAnimAddress(plant);
            String clip = PlantsCollectionMenuScreen.getPlantInitialClip(plant);
            plantDisplay = new PamActor(game.pamPlayer, path, clip, 0.45f, null);
        } catch (Exception e) {
            plantDisplay = getItemImage(plant.name());
        }

        Label plantName = new Label(plant.name().replace("_", " "), skin);
        plantName.setFontScale(0.35f);
        plantName.setWrap(true);
        plantName.setAlignment(com.badlogic.gdx.utils.Align.center);
        plantName.setColor(Color.BLACK);

        cell.add(plantDisplay).size(85, 85).center().row();
        cell.add(plantName).width(110).padTop(6).center().row();

        cell.setTouchable(Touchable.enabled);
        cell.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                overlay.remove();
                showPurchaseConfirmation(item.getName() + " (" + plant.name() + ")", priceText, () -> {
                    String result = controller.buyItem(item.getId(), 1, plant.name());
                    handlePurchaseResult(result);
                });
            }
        });

        return cell;
    }

    private Table createOverlayTable() {
        Table overlay = new Table();
        overlay.setFillParent(true);
        overlay.setBackground(createSolidColor(new Color(0, 0, 0, 0.75f)));
        overlay.setTouchable(Touchable.enabled);
        return overlay;
    }

    private void showPurchaseConfirmation(String itemName, String priceText, Runnable onConfirm) {
        Table overlay = createOverlayTable();

        BorderedTable popup = new BorderedTable();
        popup.pad(20);

        Label title = new Label("Purchase Confirmation", skin, "big_outline");
        title.setFontScale(0.85f);

        Label message = new Label("Would you like to purchase\n" + itemName + " for " + priceText + "?", skin);
        message.setWrap(true);
        message.setAlignment(com.badlogic.gdx.utils.Align.center);
        message.setColor(Color.BLACK);

        TextButton yesBtn = new TextButton("Yes", skin, "green");
        TextButton noBtn = new TextButton("Cancel", skin, "purple");

        yesBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                overlay.remove();
                onConfirm.run();
            }
        });

        noBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                overlay.remove();
            }
        });

        popup.add(title).padBottom(15).row();
        popup.add(message).width(320).padBottom(20).row();

        Table buttons = new Table();
        buttons.add(noBtn).width(120).padRight(15);
        buttons.add(yesBtn).width(120);
        popup.add(buttons);

        overlay.add(popup).width(400);
        stage.addActor(overlay);
    }

    private void handlePurchaseResult(String result) {
        boolean isError = result.toLowerCase().startsWith("error");
        showResultDialog(isError ? "Error" : "Success", result);
        if (!isError) {
            refreshShopItems();
            if (resourcesTable != null) {
                resourcesTable.update();
            }
        }
    }

    private void showResultDialog(String titleStr, String messageStr) {
        Table overlay = createOverlayTable();

        BorderedTable popup = new BorderedTable();
        popup.pad(20);

        Label title = new Label(titleStr, skin, "big_outline");
        Label msg = new Label(messageStr, skin);
        msg.setWrap(true);
        msg.setAlignment(com.badlogic.gdx.utils.Align.center);
        msg.setColor(Color.BLACK);

        TextButton okBtn = new TextButton("OK", skin);
        okBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                overlay.remove();
            }
        });

        popup.add(title).padBottom(10).row();
        popup.add(msg).width(320).padBottom(15).row();
        popup.add(okBtn).width(100);

        overlay.add(popup);
        stage.addActor(overlay);
    }

    private Image getItemImage(String name) {
        TextureRegion reg = null;
        String cleanName = name.toLowerCase();

        if (cleanName.contains("food")) {
            reg = game.textureBank.region("IMAGE_UI_DANGERROOM_PLANTFOOD_ICON");
        } else if (cleanName.contains("specific")) {
            reg = game.textureBank.region("IMAGE_UI_PACKETS_PEASHOOTER");
        } else if (cleanName.contains("pot") || cleanName.contains("sprout")) {
            reg = game.textureBank.region(
                "IMAGE_ZEN_GARDEN_GROWING_PLANT_SLOT_GROWING_PLANT_SLOT_184X161");
        } else if (cleanName.contains("random")) {
            reg = game.textureBank.region("IMAGE_UI_STOREMULTI_SEEDPACKETICON");
        } else if(cleanName.contains("currency")) {
            reg = game.textureBank.region("IMAGE_EFFECTS_COIN_GOLD_COIN_GOLD_98X95");
        }
        else {
            reg = game.textureBank.region("IMAGE_UI_PACKETS_" + name.toUpperCase());
        }

        if (reg == null) {
            reg = game.textureBank.region("IMAGE_UI_CLAIM_SMALL");
        }

        return (reg != null) ? new Image(reg) : new Image();
    }

    private TextureRegionDrawable createSolidColor(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }
}
