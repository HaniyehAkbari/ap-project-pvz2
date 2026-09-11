package com.pvz2.models.miniGame.beghouled;

import com.badlogic.gdx.Gdx;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantLayer;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.factory.PlantFactory;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.mechanics.Mechanic;

import java.util.*;

public class BeghouledMechanics implements Mechanic {
    private final List<PlantType> availablePlantTypes;
    private final List<PlantUpgrade> upgrades;
    private final Set<GridPosition> craters = new HashSet<>();
    private final int targetScore;
    private int score = 0;
    private boolean needsViewUpdate = false;

    private float matchDelayTimer = 0f;
    private List<List<GridPosition>> pendingMatches = null;

    public BeghouledMechanics(List<PlantType> availablePlantTypes, List<PlantUpgrade> upgrades, int targetScore) {
        this.availablePlantTypes = availablePlantTypes;
        this.upgrades = upgrades;
        this.targetScore = targetScore;
    }

    @Override
    public void applyMechanic(GameWorld world) {
        if (pendingMatches != null) {
            matchDelayTimer -= Gdx.graphics.getDeltaTime();
            if (matchDelayTimer <= 0) {
                List<List<GridPosition>> matchesToProcess = pendingMatches;
                pendingMatches = null;
                processMatches(world, matchesToProcess, false);
                needsViewUpdate = true;
            }
        }
    }

    public boolean consumeNeedsViewUpdate() {
        if (needsViewUpdate) {
            needsViewUpdate = false;
            return true;
        }
        return false;
    }

    public void fillRandomPlants(GameWorld world) {
        for (int r = 0; r < world.getRows(); r++) {
            for (int c = 0; c < world.getCols(); c++) {
                if (craters.contains(new GridPosition(r, c))) continue;
                PlantType randomType = randomPlantTypeAvoidingMatch(world, r, c);
                placePlant(world, r, c, randomType);
            }
        }
        needsViewUpdate = true;
    }

    private void placePlant(GameWorld world, int row, int col, PlantType type) {
        Cell cell = world.getGrid()[row][col];
        Plant plant = PlantFactory.createPlant(type,(int) App.getCellCenterX(col), (int) App.getCellCenterY(row), cell);
        cell.setPlant(plant, PlantLayer.MAIN);

        if (!world.getActivePlants().contains(plant)) {
            world.getActivePlants().add(plant);
        }
    }

    private void placePlantWithFall(GameWorld world, int row, int col, PlantType type) {
        Cell cell = world.getGrid()[row][col];
        Plant plant = PlantFactory.createPlant(type, (int) App.getCellCenterX(col)
            , (int) App.getCellCenterY(row), cell);
        plant.setX(App.getCellCenterX(col));
        plant.setY(App.getCellCenterY(row - 1.5f));
        plant.slideTo(App.getCellCenterX(col), App.getCellCenterY(row));
        cell.setPlant(plant, PlantLayer.MAIN);

        if (!world.getActivePlants().contains(plant)) {
            world.getActivePlants().add(plant);
        }
    }

    public String trySwap(GameWorld world, GridPosition a, GridPosition b) {
        if (pendingMatches != null) return "busy";
        if (!areAdjacent(a, b)) return "only neighbors";
        if (craters.contains(a) || craters.contains(b)) return "you cant swap craters";

        swapPlants(world, a, b);

        List<List<GridPosition>> matches = findAllMatches(world);
        if (matches.isEmpty()) {
            swapPlants(world, a, b);
            return "no combination";
        }

        this.pendingMatches = matches;
        this.matchDelayTimer = 0.2f;

        return null;
    }

    private boolean areAdjacent(GridPosition a, GridPosition b) {
        int dr = Math.abs(a.row() - b.row());
        int dc = Math.abs(a.col() - b.col());
        return (dr + dc) == 1;
    }

    private void swapPlants(GameWorld world, GridPosition a, GridPosition b) {
        Cell cellA = world.getGrid()[a.row()][a.col()];
        Cell cellB = world.getGrid()[b.row()][b.col()];

        Plant plantA = cellA.getPlant();
        Plant plantB = cellB.getPlant();

        cellA.removePlant();
        cellB.removePlant();

        if (plantB != null) {
            cellA.setPlant(plantB, PlantLayer.MAIN);
            plantB.setCell(cellA);
            plantB.startCombineAnimation(App.getCellCenterX(a.col()), App.getCellCenterY(a.row()));
        }
        if (plantA != null) {
            cellB.setPlant(plantA, PlantLayer.MAIN);
            plantA.setCell(cellB);
            plantA.startCombineAnimation(App.getCellCenterX(b.col()), App.getCellCenterY(b.row()));
        }
    }

    private List<List<GridPosition>> findAllMatches(GameWorld world) {
        List<List<GridPosition>> matches = new ArrayList<>();

        for (int r = 0; r < world.getRows(); r++) {
            int c = 0;
            while (c < world.getCols()) {
                PlantType type = typeAt(world, r, c);
                if (type == null) {
                    c++;
                    continue;
                }
                int start = c;
                while (c < world.getCols() && typeAt(world, r, c) == type) c++;
                if (c - start >= 3) {
                    List<GridPosition> match = new ArrayList<>();
                    for (int k = start; k < c; k++) match.add(new GridPosition(r, k));
                    matches.add(match);
                }
            }
        }

        for (int c = 0; c < world.getCols(); c++) {
            int r = 0;
            while (r < world.getRows()) {
                PlantType type = typeAt(world, r, c);
                if (type == null) {
                    r++;
                    continue;
                }
                int start = r;
                while (r < world.getRows() && typeAt(world, r, c) == type) r++;
                if (r - start >= 3) {
                    List<GridPosition> match = new ArrayList<>();
                    for (int k = start; k < r; k++) match.add(new GridPosition(k, c));
                    matches.add(match);
                }
            }
        }

        return matches;
    }

    private PlantType typeAt(GameWorld world, int row, int col) {
        Cell cell = world.getGrid()[row][col];
        if (cell == null) return null;
        Plant plant = cell.getPlant();
        return (plant == null) ? null : plant.getType();
    }

    private void processMatches(GameWorld world, List<List<GridPosition>> matches, boolean isCascade) {
        Set<GridPosition> toRemove = new HashSet<>();
        int sunUnits = 0;

        for (List<GridPosition> match : matches) {
            toRemove.addAll(match);
            int size = match.size();
            int unitsForThisMatch = (size - 3) + 1;
            if (isCascade) unitsForThisMatch += 1;
            sunUnits += unitsForThisMatch;
        }

        for (GridPosition pos : toRemove) {
            Cell cell = world.getGrid()[pos.row()][pos.col()];
            Plant plant = cell.getPlant();
            if (plant != null) {
                world.getActivePlants().remove(plant);
                cell.findAndRemovePlant();
            }
        }

        world.setSun(world.getSun() + sunUnits * 50);
        score += matches.size();

        applyGravityAndRefill(world);

        List<List<GridPosition>> cascadeMatches = findAllMatches(world);
        if (!cascadeMatches.isEmpty()) {
            processMatches(world, cascadeMatches, true);
        } else if (!hasAnyPossibleMove(world)) {
            resetBoard(world);
        }
    }

    private void applyGravityAndRefill(GameWorld world) {
        for (int c = 0; c < world.getCols(); c++) {
            List<Plant> column = new ArrayList<>();
            for (int r = world.getRows() - 1; r >= 0; r--) {
                if (craters.contains(new GridPosition(r, c))) continue;
                Plant p = world.getGrid()[r][c].getPlant();
                if (p != null) column.add(p);
            }

            int idx = 0;
            for (int r = world.getRows() - 1; r >= 0; r--) {
                if (craters.contains(new GridPosition(r, c))) continue;
                Cell cell = world.getGrid()[r][c];
                cell.removePlant();

                if (idx < column.size()) {
                    Plant p = column.get(idx++);
                    cell.setPlant(p, PlantLayer.MAIN);
                    p.setCell(cell);
                    p.startCombineAnimation(App.getCellCenterX(c), App.getCellCenterY(r));
                } else {
                    PlantType safeType = randomPlantTypeAvoidingMatch(world, r, c);
                    placePlantWithFall(world, r, c, safeType);
                }
            }
        }
    }

    private PlantType randomPlantTypeAvoidingMatch(GameWorld world, int row, int col) {
        List<PlantType> shuffled = new ArrayList<>(availablePlantTypes);
        Collections.shuffle(shuffled);

        for (PlantType candidate : shuffled) {
            if (!wouldFormMatch(world, row, col, candidate)) {
                return candidate;
            }
        }
        return shuffled.get(0);
    }

    private boolean wouldFormMatch(GameWorld world, int row, int col, PlantType type) {
        if (col >= 2) {
            PlantType left1 = typeAt(world, row, col - 1);
            PlantType left2 = typeAt(world, row, col - 2);
            if (type == left1 && type == left2) return true;
        }
        if (row <= world.getRows() - 3) {
            PlantType down1 = typeAt(world, row + 1, col);
            PlantType down2 = typeAt(world, row + 2, col);
            if (type == down1 && type == down2) return true;
        }
        return false;
    }

    public void resetBoard(GameWorld world) {
        for (int r = 0; r < world.getRows(); r++) {
            for (int c = 0; c < world.getCols(); c++) {
                if (craters.contains(new GridPosition(r, c))) continue;
                Cell cell = world.getGrid()[r][c];
                Plant p = cell.getPlant();
                if (p != null) {
                    world.getActivePlants().remove(p);
                    cell.findAndRemovePlant();
                }
            }
        }
        fillRandomPlants(world);
    }

    private boolean hasAnyPossibleMove(GameWorld world) {
        for (int r = 0; r < world.getRows(); r++) {
            for (int c = 0; c < world.getCols(); c++) {
                GridPosition current = new GridPosition(r, c);
                if (craters.contains(current)) continue;

                if (c + 1 < world.getCols() && !craters.contains(new GridPosition(r, c + 1))
                    && wouldCreateMatch(world, current, new GridPosition(r, c + 1))) {
                    return true;
                }
                if (r + 1 < world.getRows() && !craters.contains(new GridPosition(r + 1, c))
                    && wouldCreateMatch(world, current, new GridPosition(r + 1, c))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean wouldCreateMatch(GameWorld world, GridPosition a, GridPosition b) {
        swapPlants(world, a, b);
        boolean result = !findAllMatches(world).isEmpty();
        swapPlants(world, a, b);
        return result;
    }

    public String upgradePlant(GameWorld world, PlantType from) {
        PlantUpgrade upgrade = upgrades.stream()
            .filter(u -> u.getFrom() == from)
            .findFirst()
            .orElse(null);

        if (upgrade == null) return "no upgrade for this plant";
        if (world.getSun() < upgrade.getCost()) return "not enough sun";

        for (Cell[] row : world.getGrid()) {
            for (Cell cell : row) {
                Plant plant = cell.getPlant();
                if (plant != null && plant.getType() == from) {
                    int c = cell.getCol();
                    int r = cell.getRow();
                    Plant upgraded = PlantFactory.createPlant(upgrade.getTo(), (int) App.getCellCenterX(c),
                        (int) App.getCellCenterY(r), cell);

                    world.getActivePlants().remove(plant);
                    cell.removePlant();

                    cell.setPlant(upgraded, PlantLayer.MAIN);
                    world.getActivePlants().add(upgraded);
                }
            }
        }

        availablePlantTypes.remove(from);
        availablePlantTypes.add(upgrade.getTo());
        world.setSun(world.getSun() - upgrade.getCost());
        needsViewUpdate = true;
        return null;
    }

    public void createCrater(GameWorld world, int row, int col) {
        GridPosition pos = new GridPosition(row, col);
        craters.add(pos);
        Cell cell = world.getGrid()[row][col];
        Plant plant = cell.getPlant();
        if (plant != null) {
            world.getActivePlants().remove(plant);
            cell.findAndRemovePlant();
        }
        cell.setPlantable(false);
        needsViewUpdate = true;
    }

    public int getScore() { return score; }
    public int getTargetScore() { return targetScore; }
    public List<PlantUpgrade> getUpgrades() { return upgrades; }
    public int getMatchCount() { return score; }
    public int getTargetMatches() { return targetScore; }
}
