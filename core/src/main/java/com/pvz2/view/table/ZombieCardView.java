package com.pvz2.view.table;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pvz2.models.core.App;
import com.pvz2.models.network.onlineIZombie.ZombieCard;

import java.util.function.Consumer;

public class ZombieCardView extends Stack {
    private boolean active;
    private int brainCost;
    private String zombieName;
    private Consumer<ZombieCardView> onClick;
    private CooldownOverlay overlay;
    private ZombieCard card;
    private Image selectedImg;

    public ZombieCardView(boolean active, int brainCost, String zombieName) {
        this.active = active;
        this.brainCost = brainCost;
        this.zombieName = zombieName;
        this.overlay = new CooldownOverlay(PlantCardView.createSolidColor(Color.WHITE));
        this.setTouchable(Touchable.enabled);
        this.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (onClick != null) onClick.accept(ZombieCardView.this);
            }
        });
        build();
    }

    public void build() {
        Image back = new Image(App.getGameApp().textureBank.region("IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_READY"));
        add(back);
        Table imageWrapper = new Table();
        Image image = new Image(App.getGameApp().textureBank.region(
            ZombiesTable.getZombiesPicAddress().get(App.getArmoredZombieName(zombieName))));
        imageWrapper.add(image).bottom().pad(5);
        add(imageWrapper);

        Table costWrapper = new Table();
        costWrapper.bottom().right();
        costWrapper.add(new Label(Integer.toString(brainCost), App.getGameApp().skin,
            "medium_outline")).padBottom(5).padRight(10);
        this.add(costWrapper);

        overlay.setProgress(active ? 0 : 1);
        this.add(overlay);

        selectedImg = new Image(App.getGameApp().textureBank.region("IMAGE_UI_PACKETS_SELECT"));
        this.add(selectedImg);
        selectedImg.setVisible(false);
    }

    public void update() {
        if (card == null) return;
        if (card.isReady()) {
            this.active = true;
            overlay.setProgress(0);
        } else {
            this.active = false;
            float remainingFraction = (card.getMaxCooldownTicks() - card.getCurrentCooldownTicks())
                / card.getMaxCooldownTicks();
            overlay.setProgress(remainingFraction);
        }
    }

    public String getZombieName() { return zombieName; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public void setSelectedState(boolean state) { selectedImg.setVisible(state); }
    public void setClickMethod(Consumer<ZombieCardView> onClick) { this.onClick = onClick; }
    public void setCard(ZombieCard card) { this.card = card; }
    public ZombieCard getCard() { return card; }
}
