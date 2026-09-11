package com.pvz2.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.pvz2.Main;
import com.pvz2.controller.GameMenuController;
import com.pvz2.models.enums.Chapter;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.miniGame.beghouled.BeghouledMechanics;
import com.pvz2.models.miniGame.beghouled.GridPosition;
import com.pvz2.models.miniGame.beghouled.PlantUpgrade;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.view.util.LawnGrid;
import com.pvz2.view.table.PlantCardView;
import com.pvz2.view.table.SelectedPlantsList;

public class BeghouledScreen extends GameScreen {

    private final BeghouledMechanics mechanics;
    private final ShapeRenderer shapeRenderer;

    private GridPosition dragStartPos = null;
    private boolean isDragging = false;
    private Plant draggedPlant = null;

    private Table topUIBar;
    private Label progressLabel;

    private final Vector3 initialTouchPoint = new Vector3();

    public BeghouledScreen(Main game, GameWorld world, Chapter chapter) {
        super(game, world, chapter);
        this.mechanics = world.getMechanic(BeghouledMechanics.class);
        this.shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void show() {
        super.show();
        if (topUIBar == null) {
            buildTopUIBar();
            initHUDUpgrades();
        }

        if (mechanics != null && isGridEmpty()) {
            mechanics.fillRandomPlants(world);
        }

        initIcyPlantGraphics();
    }

    private boolean isGridEmpty() {
        for (int r = 0; r < world.getRows(); r++) {
            for (int c = 0; c < world.getCols(); c++) {
                if (world.getGrid()[r][c].getPlant() != null) return false;
            }
        }
        return true;
    }

    @Override
    public void initIcyPlantGraphics() {
        if (world != null && world.getGrid() != null) {
            world.getActivePlants().clear();
            world.getActivePlants().clear();
            for (int r = 0; r < world.getRows(); r++) {
                for (int c = 0; c < world.getCols(); c++) {
                    Cell cell = world.getGrid()[r][c];
                    if (cell != null && cell.getPlant() != null) {
                        Plant plant = cell.getPlant();
                        if (!plant.isCombining()) {
                            plant.setX(LawnGrid.getCellX(c));
                            plant.setY(LawnGrid.getCellY(r));
                        }
                        if (!world.getActivePlants().contains(plant)) {
                            world.getActivePlants().add(plant);
                        }
                    }
                }
            }
        }
        super.initIcyPlantGraphics();
    }

    private void buildTopUIBar() {
        if (mechanics == null) return;

        topUIBar = new Table();
        topUIBar.setFillParent(true);
        topUIBar.top().padTop(10);

        progressLabel = new Label("Matches: 0 / 500", skin, "big_outline");
        topUIBar.add(progressLabel).padBottom(8).row();

        mainStack.addActor(topUIBar);
    }

    private void initHUDUpgrades() {
        if (mechanics == null || getHud() == null) return;

        SelectedPlantsList selectedList = getHud().getSelectedPlantsList();

        PlantType[] slots = selectedList.getSlots();
        for (int i = 0; i < slots.length; i++) {
            slots[i] = null;
        }

        for (PlantUpgrade upgrade : mechanics.getUpgrades()) {
            selectedList.addPlant(upgrade.getTo());
        }

        selectedList.build();

        for (Actor child : selectedList.getChildren()) {
            if (child instanceof Table) {
                Table cardTable = (Table) child;
                for (Actor inner : cardTable.getChildren()) {
                    if (inner instanceof PlantCardView) {
                        PlantCardView cardView = (PlantCardView) inner;

                        for (PlantUpgrade upgrade : mechanics.getUpgrades()) {
                            if (upgrade.getTo() == cardView.getType()) {
                                cardView.setClickMethod(view -> {
                                    String error = mechanics.upgradePlant(world, upgrade.getFrom());
                                    if (error == null) {
                                        announce("Upgraded " + upgrade.getFrom().name() + "!");
                                        initIcyPlantGraphics();
                                    } else {
                                        announce(error);
                                    }
                                });
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        if (mechanics != null && mechanics.consumeNeedsViewUpdate()) {
            initIcyPlantGraphics();
        }

        resumeCameraToMain();

        if (progressLabel != null && mechanics != null) {
            int currentMatches = mechanics.getMatchCount();
            int targetMatches = mechanics.getTargetMatches();
            progressLabel.setText("Matches: " + currentMatches + " / " + (targetMatches > 0 ? targetMatches : 500));
        }

        super.render(delta);
        drawSelectionHighlight();
    }

    @Override
    public void handleInput() {
        Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        worldViewport.unproject(touchPoint);
        int col = LawnGrid.getColFromX(touchPoint.x);
        int row = LawnGrid.getRowFromY(touchPoint.y);
        if (Gdx.input.justTouched()) {
            if (row >= 0 && row < world.getRows() && col >= 0 && col < world.getCols()) {
                Cell cell = world.getGrid()[row][col];
                if (cell != null && cell.getPlant() != null) {
                    dragStartPos = new GridPosition(row, col);
                    draggedPlant = cell.getPlant();
                    initialTouchPoint.set(touchPoint);
                    isDragging = true;
                }
            } else {
                GameMenuController.collectSun(touchPoint.x, touchPoint.y);
                resetDragState();
            }
        }
        else if (!Gdx.input.isTouched() && isDragging) {
            if (dragStartPos != null && draggedPlant != null) {
                float dx = touchPoint.x - initialTouchPoint.x;
                float dy = touchPoint.y - initialTouchPoint.y;
                int targetCol = dragStartPos.col();
                int targetRow = dragStartPos.row();
                if (Math.abs(dx) > Math.abs(dy)) {
                    if (dx > LawnGrid.CELL_WIDTH / 3f) targetCol++;
                    else if (dx < -LawnGrid.CELL_WIDTH / 3f) targetCol--;
                } else {
                    if (dy > LawnGrid.CELL_HEIGHT / 3f) targetRow++;
                    else if (dy < -LawnGrid.CELL_HEIGHT / 3f) targetRow--;
                }
                GridPosition targetPos = new GridPosition(targetRow, targetCol);
                if ((targetRow != dragStartPos.row() || targetCol != dragStartPos.col())
                    && targetRow >= 0 && targetRow < world.getRows()
                    && targetCol >= 0 && targetCol < world.getCols()) {
                    String error = mechanics.trySwap(world, dragStartPos, targetPos);
                    if (error != null) {
                        announce(error);
                    } else {
                        initIcyPlantGraphics();
                    }
                }
            }
            resetDragState();
        }
    }

    private void resetDragState() {
        isDragging = false;
        dragStartPos = null;
        draggedPlant = null;
    }

    @Override
    public void restartLevel() {
        GameMenuController.restartBeghouled(world);
    }

    private void drawSelectionHighlight() {
        if (dragStartPos == null) return;

        float x = LawnGrid.getCellX(dragStartPos.col()) - LawnGrid.CELL_WIDTH/2;
        float y = LawnGrid.getCellY(dragStartPos.row()) - LawnGrid.CELL_HEIGHT/2;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(worldCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(x, y, LawnGrid.CELL_WIDTH, LawnGrid.CELL_HEIGHT);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
