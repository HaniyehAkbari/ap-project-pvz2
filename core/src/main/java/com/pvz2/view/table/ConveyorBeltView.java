package com.pvz2.view.table;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.pvz2.models.core.App;
import com.pvz2.models.plant.card.PlantCard;
import com.pvz2.models.world.GameState;
import com.pvz2.models.world.GameWorld;
import com.pvz2.view.screen.GameScreen;

import java.util.ArrayList;
import java.util.List;

public class ConveyorBeltView extends WidgetGroup {

    private static class CardHolder {
        PlantCard card;
        final PlantCardView view;
        float currentX;

        CardHolder(PlantCard card, PlantCardView view, float startX) {
            this.card = card;
            this.view = view;
            this.currentX = startX;
        }
    }

    private final List<CardHolder> cardHolders = new ArrayList<>();
    private final GameScreen screen;
    private PlantCardView selectedCardView = null;

    private final TextureRegion bgRegion;
    private float bgOffsetX = 0f;

    private static final float CARD_WIDTH = 54f;
    private static final float CARD_HEIGHT = 74f;
    private static final float CARD_SPACING = 6f;
    private static final float PADDING_LEFT = 15f;

    private static final float BG_SCROLL_SPEED = 35f;
    private static final float CARD_MOVE_SPEED = 260f;

    public ConveyorBeltView(GameScreen screen) {
        this.screen = screen;
        this.bgRegion = App.getGameApp().textureBank.region("IMAGE_UI_HUD_LOD_LOD_FALLFESTIVAL_BG");

        setSize(550f, 95f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        GameWorld world = App.getCurrentGame();
        if (world != null && world.getState() != GameState.PLAYING) delta = 0;
        if (world == null || !world.isConveyorMode()) {
            setVisible(false);
            return;
        }
        setVisible(true);

        if (getStage() != null) {
            float stageWidth = getStage().getWidth();
            float stageHeight = getStage().getHeight();

            float x = (stageWidth - getWidth()) / 2f;
            float y = stageHeight - getHeight() - 10f;

            setPosition(x, y);
        }

        if (bgRegion != null) {
            bgOffsetX += BG_SCROLL_SPEED * delta;
        }

        updateCardPositions(delta);
    }

    private void updateCardPositions(float delta) {
        float startX = PADDING_LEFT;
        float centerY = (getHeight() - CARD_HEIGHT) / 2f - 2f;

        for (int i = 0; i < cardHolders.size(); i++) {
            CardHolder holder = cardHolders.get(i);
            float targetX = startX + i * (CARD_WIDTH + CARD_SPACING);

            float minX = targetX;
            if (i > 0) {
                CardHolder prevHolder = cardHolders.get(i - 1);
                minX = Math.max(targetX, prevHolder.currentX + CARD_WIDTH + CARD_SPACING);
            }

            if (holder.currentX > minX) {
                holder.currentX = Math.max(minX, holder.currentX - CARD_MOVE_SPEED * delta);
            } else if (holder.currentX < targetX) {
                holder.currentX = Math.min(targetX, holder.currentX + CARD_MOVE_SPEED * delta);
            }

            holder.view.setPosition(holder.currentX, centerY);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        GameWorld world = App.getCurrentGame();

        if (world == null || !world.isConveyorMode()) {
            return;
        }
        if (bgRegion != null) {
            float width = getWidth();
            float height = getHeight();
            if (width > 0 && height > 0) {
                int regX = bgRegion.getRegionX();
                int regY = bgRegion.getRegionY();
                int regW = bgRegion.getRegionWidth();
                int regH = bgRegion.getRegionHeight();

                if (regW > 0 && regH > 0) {
                    float offset = bgOffsetX % regW;
                    float currentX = getX();
                    float currentY = getY();
                    float remainingWidth = width;

                    boolean first = true;
                    while (remainingWidth > 0) {
                        float srcXOffset = first ? offset : 0;
                        float chunkWidth = Math.min(regW - srcXOffset, remainingWidth);

                        batch.draw(
                            bgRegion.getTexture(),
                            currentX, currentY,
                            chunkWidth, height,
                            (int)(regX + srcXOffset), regY,
                            (int)chunkWidth, regH,
                            false, false
                        );

                        currentX += chunkWidth;
                        remainingWidth -= chunkWidth;
                        first = false;
                    }
                }
            }
        }

        super.draw(batch, parentAlpha);
    }

    public void update(GameWorld world) {
        if (world == null) return;
        List<PlantCard> beltCards = world.getConveyorBelt();
        if (beltCards == null) return;

        if (needsRebuild(beltCards)) {
            rebuild(beltCards);
        } else {
            for (CardHolder holder : cardHolders) {
                holder.view.update();
            }
        }
    }

    private boolean needsRebuild(List<PlantCard> beltCards) {
        if (beltCards.size() != cardHolders.size()) return true;
        for (int i = 0; i < beltCards.size(); i++) {
            if (cardHolders.get(i).card != beltCards.get(i)) return true;
        }
        return false;
    }

    private void rebuild(List<PlantCard> beltCards) {
        List<CardHolder> oldHolders = new ArrayList<>(cardHolders);
        this.clearChildren();
        cardHolders.clear();

        float spawnX = getWidth() > 0 ? getWidth() + 20f : 550f;

        for (PlantCard card : beltCards) {
            CardHolder matchedHolder = null;

            for (CardHolder old : oldHolders) {
                if (old.card == card) {
                    matchedHolder = old;
                    break;
                }
            }

            if (matchedHolder == null) {
                for (CardHolder old : oldHolders) {
                    if (old.card.getType() == card.getType()) {
                        matchedHolder = old;
                        matchedHolder.card = card;
                        matchedHolder.view.setCard(card);
                        break;
                    }
                }
            }

            CardHolder finalHolder;
            if (matchedHolder != null) {
                oldHolders.remove(matchedHolder);
                finalHolder = matchedHolder;
            } else {
                PlantCardView cardView = createCardView(card);
                finalHolder = new CardHolder(card, cardView, spawnX);
            }

            cardHolders.add(finalHolder);
            addActor(finalHolder.view);
        }
    }
    private PlantCardView createCardView(PlantCard card) {
        PlantCardView cardView = new PlantCardView(
            true, false, false, 1, 0, card.getType()
        );
        cardView.setCard(card);
        cardView.setSize(CARD_WIDTH, CARD_HEIGHT);

        cardView.setClickMethod(clickedView -> {
            if (selectedCardView == clickedView) {
                selectedCardView.setSelectedState(false);
                selectedCardView = null;
                screen.getPlantPlacementManager().cancelSelection();
            } else {
                if (selectedCardView != null) {
                    selectedCardView.setSelectedState(false);
                }
                selectedCardView = clickedView;
                selectedCardView.setSelectedState(true);

                screen.getPlantPlacementManager().selectPlant(clickedView.getType(), () -> {
                    GameWorld world = App.getCurrentGame();
                    if (world != null && world.getConveyorBelt() != null) {
                        world.getConveyorBelt().remove(card);
                    }
                });
            }
        });

        if (selectedCardView != null && selectedCardView.getType() == card.getType()) {
            cardView.setSelectedState(true);
        }

        return cardView;
    }
}
