package com.pvz2.view.table;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.pvz2.models.core.App;
import com.pvz2.view.util.LawnGrid;

public class ShovelPlacementManager {
    private boolean isSelected = false;
    private static final float PREVIEW_WIDTH = 70f;
    private static final float PREVIEW_HEIGHT = 70f;

    TextureRegion shovelRegion = App.getGameApp().textureBank.region("IMAGE_UI_HUD_INGAME_SHOVEL_ICON");
    TextureRegion overlay = PlantCardView.createSolidColor(Color.WHITE);

    public void drawPreview(SpriteBatch batch, Vector3 cursorWorldPos) {
        if (!isSelected) return;

        int col = LawnGrid.getColFromX(cursorWorldPos.x);
        int row = LawnGrid.getRowFromY(cursorWorldPos.y);

        if (col >= 0 && row >= 0) {
            float cellX = LawnGrid.getCellX(col) - LawnGrid.CELL_WIDTH/2;
            float cellY = LawnGrid.getCellY(row) - LawnGrid.CELL_HEIGHT/2;

            batch.setColor(1f, 1f, 1f, 0.35f);
            batch.draw(overlay, cellX, cellY, LawnGrid.CELL_WIDTH, LawnGrid.CELL_HEIGHT);
        }

        float drawX = cursorWorldPos.x - (PREVIEW_WIDTH / 2f);
        float drawY = cursorWorldPos.y - (PREVIEW_HEIGHT / 2f);

        batch.setColor(1f, 1f, 1f, 0.85f);
        batch.draw(shovelRegion, drawX, drawY, PREVIEW_WIDTH, PREVIEW_HEIGHT);

        batch.setColor(Color.WHITE);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
