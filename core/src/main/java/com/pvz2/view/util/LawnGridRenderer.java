package com.pvz2.view.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class LawnGridRenderer {

    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    public void draw(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);

        float gridWidth = LawnGrid.COLS * LawnGrid.CELL_WIDTH;
        float gridHeight = LawnGrid.ROWS * LawnGrid.CELL_HEIGHT;
        float left = LawnGrid.ORIGIN_X - LawnGrid.CELL_WIDTH / 2f;
        float bottom = LawnGrid.ORIGIN_Y - LawnGrid.CELL_HEIGHT / 2f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        for (int c = 0; c <= LawnGrid.COLS; c++) {
            float x = left + c * LawnGrid.CELL_WIDTH;
            shapeRenderer.line(x, bottom, x, bottom + gridHeight);
        }
        for (int r = 0; r <= LawnGrid.ROWS; r++) {
            float y = bottom + r * LawnGrid.CELL_HEIGHT;
            shapeRenderer.line(left, y, left + gridWidth, y);
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        float s = 6f;
        for (int col = 0; col < LawnGrid.COLS; col++) {
            for (int row = 0; row < LawnGrid.ROWS; row++) {
                float cx = LawnGrid.getCellX(col);
                float cy = LawnGrid.getCellY(row);
                shapeRenderer.line(cx - s, cy, cx + s, cy);
                shapeRenderer.line(cx, cy - s, cx, cy + s);
            }
        }
        shapeRenderer.end();
    }

    public void drawLine(Color color, float x, OrthographicCamera camera){
        shapeRenderer.setProjectionMatrix(camera.combined);
        float gridHeight = LawnGrid.ROWS * LawnGrid.CELL_HEIGHT;
        float down = LawnGrid.ORIGIN_Y-LawnGrid.CELL_HEIGHT/2;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);

        shapeRenderer.line(x, down,
            x, down+gridHeight);
        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
