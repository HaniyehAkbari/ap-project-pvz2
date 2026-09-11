package com.pvz2.models.world;

import com.pvz2.controller.GameMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.core.User;
import com.pvz2.models.enums.PlantFamily;
import com.pvz2.models.enums.PlantLayer;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.PlacementBehaviorComponent;
import com.pvz2.models.plant.components.ShooterComponent;
import com.pvz2.models.plant.factory.PlantFactory;
import com.pvz2.models.quest.QuestStats;
import com.pvz2.models.world.cellTerrains.CellTerrain;
import com.pvz2.models.world.obstacles.Grave;
import com.pvz2.models.world.obstacles.Obstacle;
import com.pvz2.models.zombie.Zombie;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Cell {
    private int row;
    private int col;
    private float x;
    private float y;
    private Plant basePlant;
    private Plant mainPlant;
    private Plant shieldPlant;
    private Obstacle obstacle;
    private CellTerrain terrain;
    private boolean lowLyingCoast;
    private int slippingDir = 0;
    private int craterTime = 0;

    private boolean plantable = true;
    private boolean necromancyPotential = false;
    private boolean necromancyTriggered = false;
    private static GameWorld currentWorld;


    public Cell(int row, int col, CellTerrain initialTerrain) {
        this.row = row;
        this.col = col;
        x = App.getCellCenterX(col);
        y = App.getCellCenterY(row);
        this.terrain = initialTerrain;
    }

    public void update(float delta, GameWorld world){
        if (currentWorld == null) currentWorld = world;
        update(delta);
    }

    public void update(float delta){
        if (craterTime > 0){
            craterTime--;
        }
    }

    public static Cell findCell(float x, float y, Cell[][] grid) {
        for (Cell[] cellRows : grid) {
            for (Cell cell : cellRows) {
                if (cell.containsX(x) && cell.containsY(y)) {
                    return cell;
                }
            }
        }
        return null;

    }

    public static List<Cell> getNeighborCells(Cell inputCell, Cell[][] grid, int radius) {
        List<Cell> neighbors = new ArrayList<>();
        if (inputCell == null) return neighbors;

        int centerCol = inputCell.getCol();
        int centerLane = inputCell.getRow();

        int totalLanes = grid.length;
        int totalCols = grid[0].length;

        int minLane = Math.max(0, centerLane - radius);
        int maxLane = Math.min(totalLanes - 1, centerLane + radius);

        int minCol = Math.max(0, centerCol - radius);
        int maxCol = Math.min(totalCols - 1, centerCol + radius);

        for (int l = minLane; l <= maxLane; l++) {
            neighbors.addAll(Arrays.asList(grid[l]).subList(minCol, maxCol + 1));
        }

        return neighbors;
    }

    public static List<Cell> getCellsInRow(Cell inputCell, Cell[][] grid) {
        return new ArrayList<>(Arrays.asList(grid[inputCell.getRow()]).subList(0, grid[0].length));
    }

    public static List<Zombie> getZombiesInCells(List<Cell> affectedCells) {
        List<Zombie> activeZombies = App.getCurrentGame() == null ? currentWorld.activeZombies :
            App.getCurrentGame().getActiveZombies();

        return activeZombies.stream()
                .filter(zombie -> affectedCells.stream().anyMatch(cell ->
                        Math.abs(zombie.getY() - cell.getY()) < 5 &&
                                cell.containsX(zombie.getX())
                ))
                .toList();
    }

    public static List<Zombie> getZombiesInCell(Cell affectedCell) {
        List<Zombie> activeZombies = App.getCurrentGame() == null ? currentWorld.activeZombies :
        App.getCurrentGame().getActiveZombies();

        return activeZombies.stream()
                .filter(zombie ->
                        zombie.getY() == affectedCell.getY() &&
                                affectedCell.containsX(zombie.getX())
                )
                .toList();
    }

    public static Cell findZombieCell(Cell[][] grid, Zombie zombie) {
        for (Cell[] cellRows : grid) {
            for (Cell cell : cellRows) {
                if (cell.containsX(zombie.getX()) && Math.abs(cell.getY() - zombie.getY()) < 5) {
                    return cell;
                }
            }
        }
        return null;
    }

    public static Cell nextCell(Cell origin, Cell[][] grid) {
        int row = origin.getRow();
        if (origin.getCol() >= grid[0].length - 1) {
            return null;
        }
        return grid[row][origin.getCol() + 1];
    }

    public static Cell previousCell(Cell origin, Cell[][] grid) {
        int row = origin.getRow();
        if (origin.getCol() <= 0) {
            return null;
        }
        return grid[row][origin.getCol() - 1];
    }

    public boolean isLayerEmpty(PlantLayer layer) {
        return switch (layer) {
            case BASE -> basePlant == null;
            case MAIN -> mainPlant == null;
            case SHIELD -> shieldPlant == null;
        };
    }

    public void setPlant(Plant plant, PlantLayer layer) {
        switch (layer) {
            case BASE -> this.basePlant = plant;
            case MAIN -> this.mainPlant = plant;
            case SHIELD -> this.shieldPlant = plant;
        }
    }

    public Plant getPlant(PlantLayer layer) {
        switch (layer) {
            case BASE -> {
                return basePlant;
            }
            case MAIN -> {
                return mainPlant;
            }
            case SHIELD -> {
                return shieldPlant;
            }
        }
        return null;
    }

    public Plant getPlant() {
        if (shieldPlant != null) {
            return shieldPlant;
        } else if (mainPlant != null) {
            return mainPlant;
        } else {
            return basePlant;
        }
    }

    public boolean isEmpty() {
        return basePlant == null && mainPlant == null && shieldPlant == null;
    }

    private Plant checkPlantable(PlantType type, boolean boost){
        if (!this.isPlantable()) {
            if (!(this.obstacle instanceof Grave && type == PlantType.GRAVE_BUSTER))
                return null;
        }
        if (craterTime > 0) return null;
        Plant newPlant;
        newPlant = PlantFactory.createPlant(type, (int) x, (int) y, this);
        if (boost) newPlant.setPlantFoodInStart(true);
        if (((this.obstacle instanceof Grave) != (type == PlantType.GRAVE_BUSTER))
                || !(this.terrain.canPlant(newPlant, this))
                || (this.hasIcyZombie())) {
            return null;
        }
        System.out.println("plant x: " + x);
        System.out.println("plant y: " + y);
        return newPlant;
    }

    public Plant handlePlanting(PlantType type, boolean boost) {
        Plant newPlant = checkPlantable(type, boost);
        if (newPlant == null){
            GameMenuController.updateState("Error", "you cannot plant in that place!");
            return null;
        }
        PlacementBehaviorComponent behavior = newPlant.getComponent(PlacementBehaviorComponent.class);
        PlantLayer layer = (behavior != null) ? behavior.getTargetLayer() : PlantLayer.MAIN;
        if (behavior != null && behavior.isStackable() && !isLayerEmpty(layer)) {
            Plant existingPlant = getPlant(layer);
            if (existingPlant.getType() == type) {
                PlacementBehaviorComponent existingBehavior = existingPlant.getComponent(
                        PlacementBehaviorComponent.class);
                if (existingBehavior.tryIncrementStack()) {
                    ShooterComponent shooterComp = existingPlant.getComponent(ShooterComponent.class);
                    shooterComp.setBurstProjectileNumber(existingBehavior.getCurrentStack());
                    shooterComp.setBurstProjectileNumberOnPlantFood(existingBehavior.getCurrentStack());
                    shooterComp.setGiantCount(existingBehavior.getCurrentStack());
                    return newPlant;
                }
            }
        }

        if (isLayerEmpty(layer) || type == PlantType.HOT_POTATO) {
            if (type != PlantType.HOT_POTATO) setPlant(newPlant, layer);
            GameWorld world = App.getCurrentGame() == null ? currentWorld :
                App.getCurrentGame();
            world.getActivePlants().add(newPlant);
            User user = App.getCurrentUser();
            if (user != null) {
                QuestStats stats = user.getQuestStats();
                stats.addFamilyUsedInLevel(newPlant.getType().family);
                stats.incrementTotalPlantsUsed();
                if (Plant.isMushroom(newPlant.getType())) {
                    stats.incrementMushroomPlantsUsed();
                }
                if (newPlant.getType().family == PlantFamily.EXPLOSIVE) {
                    stats.incrementExplosivePlantsUsed();
                }
                if (newPlant.getType().family == PlantFamily.SUN_PRODUCER) {
                    stats.incrementSunProducerPlantsInLevel();
                }
            }
            return newPlant;
        }
        GameMenuController.updateState("Error", "that place isn't empty!");
        return null;
    }

    public Plant handlePlanting(PlantType type) {
        return handlePlanting(type, false);
    }

    public void removePlant() {
        this.mainPlant = null;
    }

    public Plant findPlant() {
        if (this.shieldPlant != null) {
            return this.shieldPlant;
        }
        if (this.mainPlant != null) {
            return this.mainPlant;
        }
        if (this.basePlant != null) {
            return this.basePlant;
        }
        return null;
    }

    public boolean findAndRemovePlant() {
        if (this.shieldPlant != null) {
            this.shieldPlant.die();
            this.shieldPlant = null;
            return true;
        }
        if (this.mainPlant != null) {
            this.mainPlant.die();
            this.mainPlant = null;
            return true;
        }
        if (this.basePlant != null) {
            this.basePlant.die();
            this.basePlant = null;
            return true;
        }
        return false;
    }

    public boolean containsX(float x) {
        return (x > this.x - App.getCellWidth() / 2 && x <= this.x + App.getCellWidth() / 2);
    }

    public boolean containsY(float y) {
        return (y > this.y - App.getCellHeight() / 2 && y <= this.y + App.getCellHeight() / 2);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean hasObstacle() {
        return obstacle != null;
    }

    public Obstacle getObstacle() {
        return obstacle;
    }

    public void setObstacle(Obstacle obstacle) {
        this.obstacle = obstacle;
    }

    public void removeObstacle() {
        if (this.obstacle == null) return;
        if (obstacle instanceof Grave grave){
            grave.releaseContent();
        }
        obstacle.die();
        this.obstacle = null;
        this.plantable = true;
    }

    public boolean isWater() {
        return terrain.isWater();
    }

    public boolean isPlantable() {
        return plantable;
    }

    public void setPlantable(boolean plantable) {
        this.plantable = plantable;
    }

    public CellTerrain getTerrain() {
        return terrain;
    }

    public void setTerrain(CellTerrain terrain) {
        this.terrain = terrain;
    }

    public boolean isNecromancyPotential() {
        return necromancyPotential;
    }

    public void setNecromancyPotential(boolean value) {
        this.necromancyPotential = value;
    }

    public boolean isNecromancyTriggered() {
        return necromancyTriggered;
    }

    public void setNecromancyTriggered(boolean value) {
        this.necromancyTriggered = value;
    }


    public boolean isLowLyingCoast() {
        return lowLyingCoast;
    }

    public void setLowLyingCoast(boolean lowLyingCoast) {
        this.lowLyingCoast = lowLyingCoast;
    }

    public int getSlippingDir() {
        return slippingDir;
    }

    public void setSlippingDir(int slippingDir) {
        this.slippingDir = slippingDir;
    }

    public void setCraterTime(int craterTime) {
        this.craterTime = craterTime;
    }

    public boolean hasIcyZombie() {
        for (Zombie zombie : Cell.getZombiesInCell(this)) {
            if (zombie.getIceHealth() > 0) return true;
        }
        return false;
    }

    public static void setCurrentWorld(GameWorld currentWorld) {
        Cell.currentWorld = currentWorld;
    }
}
