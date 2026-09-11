package com.pvz2.models.world;

import com.badlogic.gdx.math.Rectangle;
import com.pvz2.controller.GameMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.plant.components.SunProducerComponent;
import com.pvz2.models.pool.Resettable;
import com.pvz2.view.graphic.SunGraphic;

public class Sun implements Resettable {
    private float x, y;
    private float finalX, finalY;
    private float fallSpeed = 200f;
    private float groundedTimer = 0f;
    private static final float DESPAWN_TIME = 10f;

    // --- rise-then-arc-down animation (used when a sun is produced beside a plant) ---
    private enum FallPhase { SKY_FALL, RISING, ARC_DOWN, GROUNDED }
    private FallPhase fallPhase = FallPhase.SKY_FALL;
    private float arcStartX;
    private float arcPeakY;
    private float riseSpeed = 260f;
    private static final float RISE_HEIGHT_BASE = 65f;
    private static final float SIDE_OFFSET_BASE = 55f;

    private boolean isCollected;
    private boolean isExploded;
    private boolean isExpired;
    private transient SunProducerComponent producer;
    private int size;
    private transient GameWorld game;
    private SunType type;
    private float animTime = 0f;
    private String id = java.util.UUID.randomUUID().toString(); // not final — regenerated on every real reuse
    private transient GameWorld currentWorld;

    public String getId() { return id; }

    public void setup(int row, int col, SunType type) {
        if (game == null) {
            game = App.getCurrentGame(this);
        }
        this.id = java.util.UUID.randomUUID().toString();
        this.finalX = App.getCellCenterX(col);
        this.finalY = App.getCellCenterY(row);

        this.x = finalX;
        this.y = 1050f;

        this.type = type;
        this.size = type.amount;
        this.isCollected = false;
        this.isExploded = false;
        this.isExpired = false;
        this.groundedTimer = 0f;
        this.animTime = 0f;
        this.producer = null;
        this.fallPhase = FallPhase.SKY_FALL;

        GameMenuController.updateState("New " + type + " sun dropping at (" + finalX + ", " + finalY + ")");
    }

    private void beginRiseAndLandBeside(float startX, float startY, int produceIndex) {
        float side = (produceIndex % 2 == 0) ? -1f : 1f;
        float offsetX = side * (SIDE_OFFSET_BASE + (produceIndex % 3) * 12f);
        float riseHeight = RISE_HEIGHT_BASE + (produceIndex % 3) * 15f;

        this.x = startX;
        this.y = startY;
        this.finalX = startX + offsetX;
        this.finalY = startY;

        this.arcStartX = startX;
        this.arcPeakY = startY + riseHeight;
        this.fallPhase = FallPhase.RISING;
    }

    public void update(float delta, GameWorld world){
        if (currentWorld == null) currentWorld = world;
        update(delta);
    }

    public void update(float delta) {
        if (isCollected || isExploded || isExpired) return;

        animTime += delta;

        switch (fallPhase) {
            case SKY_FALL -> {
                if (y > finalY) {
                    y -= fallSpeed * delta;
                    if (y <= finalY) {
                        y = finalY;
                        land();
                    }
                } else {
                    tickDespawn(delta);
                }
            }
            case RISING -> {
                y += riseSpeed * delta;
                if (y >= arcPeakY) {
                    y = arcPeakY;
                    fallPhase = FallPhase.ARC_DOWN;
                }
            }
            case ARC_DOWN -> {
                y -= fallSpeed * delta;
                float totalDrop = arcPeakY - finalY;
                float fallen = arcPeakY - y;
                float t = totalDrop > 0 ? Math.min(1f, fallen / totalDrop) : 1f;
                x = arcStartX + (finalX - arcStartX) * t;
                if (y <= finalY) {
                    y = finalY;
                    x = finalX;
                    land();
                }
            }
            case GROUNDED -> tickDespawn(delta);
        }
    }

    private void tickDespawn(float delta) {
        groundedTimer += delta;
        if (groundedTimer >= DESPAWN_TIME) {
            isExpired = true;
        }
    }

    private void land() {
        fallPhase = FallPhase.GROUNDED;
        groundedTimer = 0f;
        onLand();
    }

    private void onLand() {
        GameMenuController.updateState("Sun landed at (" + finalX + ", " + finalY + ")");
        if (type == SunType.RADIOACTIVE) {
            this.type = SunType.NORMAL;
            this.size = SunType.NORMAL.amount;
        }
    }

    public void explode() {
        if (isExploded || isExpired) return;
        this.isExploded = true;
        GameMenuController.updateState("Radioactive sun exploded at (" + x + ", " + y + ")");

        Cell[][] grid = App.getCurrentGame(this).getGrid();
        Cell sunCell = Cell.findCell(x, y, grid);
        if (sunCell != null) {
            var zombieCells = Cell.getNeighborCells(sunCell, grid, 2);
            var zombies = Cell.getZombiesInCells(zombieCells);
            for (var zombie : zombies) {
                zombie.takeDamage(150, "NORMAL");
            }

            var plantCells = Cell.getNeighborCells(sunCell, grid, 1);
            for (Cell cell : plantCells) {
                if (cell.getPlant(com.pvz2.models.enums.PlantLayer.BASE) != null)
                    cell.getPlant(com.pvz2.models.enums.PlantLayer.BASE).takeDamage(80);
                if (cell.getPlant(com.pvz2.models.enums.PlantLayer.MAIN) != null)
                    cell.getPlant(com.pvz2.models.enums.PlantLayer.MAIN).takeDamage(80);
                if (cell.getPlant(com.pvz2.models.enums.PlantLayer.SHIELD) != null)
                    cell.getPlant(com.pvz2.models.enums.PlantLayer.SHIELD).takeDamage(80);
            }
        }
    }

    public void collect() {
        if (type == SunType.RADIOACTIVE && !isExploded) {
            explode();
            return;
        }
        if (producer != null){
            producer.getComponentSuns().remove(this);
        }
        this.isCollected = true;
        this.isExpired = true;
    }

    public boolean isExpired() { return isExpired || isCollected; }
    public float getX() { return x; }
    public float getY() { return y; }
    public int getSize() { return size; }
    public boolean isCollected() { return isCollected; }
    public boolean isExploded() { return isExploded; }
    public void setExpired(boolean expired) { this.isExpired = expired; }
    public SunProducerComponent getProducer() { return producer; }
    public SunType getType() { return type; }
    public float getAnimTime() { return animTime; }

    @Override
    public void reset(float x, float y, int size, SunProducerComponent component) {
        this.id = java.util.UUID.randomUUID().toString();
        this.size = size;
        this.producer = component;
        this.game = App.getCurrentGame(this);
        this.type = SunType.NORMAL;
        this.isCollected = false;
        this.isExploded = false;
        this.isExpired = false;
        this.groundedTimer = 0f;
        this.animTime = 0f;

        int produceIndex = (component != null && component.getComponentSuns() != null)
            ? component.getComponentSuns().size() : 0;
        beginRiseAndLandBeside(x, y, produceIndex);
    }

    @Override
    public void reset(float x, float y) {
        this.isCollected = false;
        this.isExploded = false;
        this.isExpired = false;
    }

    @Override
    public void reset(float x, float y, com.pvz2.models.projectile.hitStrategies.HitStrategy hitStrategy,
                      com.pvz2.models.projectile.movementStrategies.MovementStrategy movementStrategy,
                      com.pvz2.models.projectile.strikeStrategies.CheckStrike checkStrike,
                      com.pvz2.models.enums.ProjectileType type) {
        this.isCollected = false;
        this.isExploded = false;
        this.isExpired = false;
    }

    public Rectangle getBounds() {
        float size = 80f * SunGraphic.getScale(type);
        return new Rectangle(x - size / 2f, y - size / 2f, size, size);
    }

    public void setX(float x) {
        this.x = x;
    }
    public GameWorld getCurrentWorld() {
        return currentWorld;
    }
    public void setY(float y) {
        this.y = y;
    }
}
