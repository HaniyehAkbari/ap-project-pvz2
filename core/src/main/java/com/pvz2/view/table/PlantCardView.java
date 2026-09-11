package com.pvz2.view.table;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.card.PlantCard;

import java.util.function.Consumer;

public class PlantCardView extends Stack {
    private boolean active, boost, lock;
    private int level, costAmount;
    private Consumer<PlantCardView> onClick;
    private CooldownOverlay overlay;
    private PlantType type;
    private PlantCard card;
    private Image selectedImg;


    public PlantCardView(boolean active, boolean boost, boolean lock,
                         int level, int costAmount, PlantType type) {
        this.active = active;
        this.boost = boost;
        this.lock = lock;
        this.level = level;
        this.costAmount = costAmount;
        this.type = type;
        this.overlay = new CooldownOverlay(createSolidColor(Color.WHITE));
        this.setTouchable(Touchable.enabled);
        this.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (onClick != null) onClick.accept(PlantCardView.this);
            }
        });
        build();
    }

    public void build(){
        add(new Image(App.getGameApp().textureBank.region(
            boost ? "IMAGE_UI_PACKETS_BOOST" : "IMAGE_UI_PACKETS_MODERNDAY")));
        Table plantTable = new Table();
        Table detailsTable = new Table();
        plantTable.bottom().left().padLeft(5).padBottom(10);
        plantTable.add(new Image(App.getGameApp().textureBank.region(PlantsTable.getPlantsMap().get(type))))
            .size(80/150f*getWidth(), 55/100f*getHeight());
        detailsTable.left().top();
        Stack familyStack = new Stack();
        Image familyBg = new Image(App.getGameApp().textureBank.region(
            "IMAGE_UI_PACKETS_MINTFAM_BANNER"));
        familyBg.setColor(Color.BROWN);
        familyStack.add(familyBg);
        familyStack.add(new Image(App.getGameApp().textureBank.region(
            PlantsTable.getPlantsFamilyMap().get(type.family))));
        detailsTable.add(familyStack).padLeft(-10).padTop(-15).size(30,30);

        this.add(plantTable);

        if (!lock) {
            Table levelWrapper = new Table();
            levelWrapper.top().right();
            Label levelLabel = new Label("LVL "+ level, App.getGameApp().skin, "medium_outline");
            levelLabel.setFontScale(0.6f);
            levelWrapper.add(levelLabel).padRight(10).padTop(5);
            Table sunCostWrapper = new Table();
            sunCostWrapper.bottom().right();
            sunCostWrapper.add(new Label(Integer.toString(costAmount), App.getGameApp().skin,
                "medium_outline")).padBottom(5).padRight(10);
            this.add(levelWrapper);
            this.add(sunCostWrapper);
        }

        overlay.setProgress(active ? 0 : 1);
        this.add(overlay);

        if (lock){
            Table lockTable = new Table();
            lockTable.center().right();
            lockTable.add(new Image(App.getGameApp().textureBank.region(
                "IMAGE_UI_CARDS_LOCK_MEDIUM"))).size(30, 40).pad(10);
            this.add(lockTable);
        }
        this.add(detailsTable);
        selectedImg = new Image(App.getGameApp().textureBank.region("IMAGE_UI_PACKETS_SELECT"));
        this.add(selectedImg);
        selectedImg.setVisible(false);
    }

    public void update(){
        if (card == null) return;
        if (card.isReady()){
            this.active = true;
            overlay.setProgress(0);
        }
        else{
            this.active = false;
            float remainingFraction = (card.getMaxCooldownTicks() - card.getCurrentCooldownTicks())
                / card.getMaxCooldownTicks();
            overlay.setProgress(remainingFraction);
        }
    }

    public static TextureRegion createSolidColor(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegion(texture);
    }


    public PlantType getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isBoost() {
        return boost;
    }

    public int getLevel() {
        return level;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setSelectedState(boolean state){
        selectedImg.setVisible(state);
    }

    public void setClickMethod(Consumer<PlantCardView> onClick) {
        this.onClick = onClick;
    }

    public void setCard(PlantCard card) {
        this.card = card;
    }

    public PlantCard getCard() {
        return card;
    }
}
