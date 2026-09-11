package com.pvz2.view.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.pvz2.models.core.App;
import com.pvz2.view.table.PlantCardView;
import com.pvz2.view.table.ZombiesTable;
import pvz.libpvz.pam.PamPlayer;

import java.util.HashMap;

public class ZombiePlacementManager {
    private String selectedZombie;
    private float stateTime = 0;
    TextureRegion overlay = PlantCardView.createSolidColor(Color.WHITE);

    public void selectZombie(String type) {
        this.selectedZombie = type;
    }

    public boolean isZombieSelected() {
        return selectedZombie != null;
    }

    public String getSelectedZombie() {
        return selectedZombie;
    }

    public void cancelSelection() {
        this.selectedZombie = null;
    }

    public void drawPreview(PamPlayer pamPlayer, SpriteBatch batch, Vector3 cursorWorldPos, float delta) {
        if (!isZombieSelected()) return;
        String zombieName = App.getArmoredZombieName(selectedZombie);

        String zombiePam = ZombiesTable.getZombiesAnimAddress().get(zombieName);
        String clip = zombieName.equals("ZombieNewspaper") ? "idle_newspaper" : "idle";

        HashMap<String, Boolean> visibility =
            ZombiesTable.getZombiesVisibilities().getOrDefault(zombieName, null);

        if (zombiePam == null) return;

        int col = LawnGrid.getColFromX(cursorWorldPos.x);
        int row = LawnGrid.getRowFromY(cursorWorldPos.y);

        if (col >= 0 && row >= 0) {
            float cellX = LawnGrid.getCellX(0) - LawnGrid.CELL_WIDTH/2;
            float cellY = LawnGrid.getCellY(row) - LawnGrid.CELL_HEIGHT/2;

            batch.setColor(1f, 1f, 1f, 0.35f);
            batch.draw(overlay, cellX, cellY, LawnGrid.CELL_WIDTH*9, LawnGrid.CELL_HEIGHT);
        }

        float drawX = cursorWorldPos.x;
        float drawY = cursorWorldPos.y;

        stateTime += delta;
        batch.setColor(1f, 1f, 1f, 0.85f);

        pamPlayer.draw(batch, zombiePam, clip, stateTime, drawX, drawY, 0.8f, 0.8f, true, visibility);

        batch.setColor(Color.WHITE);
    }

}
