package com.pvz2.models.zombie.zombiesType;

import com.pvz2.controller.GameMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantLayer;
import com.pvz2.models.enums.Zombies;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.zombie.Zombie;

public class FishermanZombie extends Zombie {
    private static final float HOOK_INTERVAL = 4.5f;
    private float hookCooldown;

    public static final String HOOK_EFFECT_PAM = "768/FULL/EFFECTS/ZOMBIE_FISHERMAN_HOOK/ZOMBIE_FISHERMAN_HOOK.PAM";

    public enum FishermanState {
        INTRO("intro"),
        IDLE("idle"),
        CAST("cast"),
        CAST_LOOP("cast_loop"),
        REEL("reel"),
        TOSS("toss"),
        DIE("die");

        private final String animName;
        FishermanState(String animName) { this.animName = animName; }
        public String getAnimName() { return animName; }
    }

    private FishermanState currentState = FishermanState.INTRO;

    public FishermanZombie(int health, int damage) {
        super(Zombies.FISHERMAN, health, 0, damage);
        this.hookCooldown = 0f;
    }

    @Override
    public void update(float delta) {
        if (isDead) {
            currentState = FishermanState.DIE;
            return;
        }

        GameWorld game = App.getCurrentGame();
        if (game != null) {
            float targetX = App.getCellCenterX(game.getCols() - 1) + (App.getCellWidth() / 2f);

            if (this.x > targetX + 5f) {
                this.x -= 40 * delta;
                currentState = FishermanState.INTRO;
                return;
            } else {
                this.x = targetX;
                if (currentState == FishermanState.INTRO) {
                    currentState = FishermanState.IDLE;
                }
            }
        }

        if (hookCooldown <= 0) {
            if (currentState == FishermanState.IDLE) {
                tryHook();
                hookCooldown = HOOK_INTERVAL;
            }
        } else {
            hookCooldown -= delta;
            if (hookCooldown < HOOK_INTERVAL - 0.5f && currentState == FishermanState.CAST) {
                currentState = FishermanState.CAST_LOOP;
            } else if (hookCooldown < HOOK_INTERVAL - 1.5f &&
                (currentState == FishermanState.CAST_LOOP || currentState == FishermanState.REEL ||
                    currentState == FishermanState.TOSS)) {
                currentState = FishermanState.IDLE;
            }
        }
    }

    private void tryHook() {
        GameWorld game = App.getCurrentGame();
        if (game == null) return;
        Cell zombieCell = Cell.findZombieCell(game.getGrid(), this);
        if (zombieCell == null) return;
        int row = zombieCell.getRow();
        Plant target = game.getNearestPlantInRow(row, this.x);
        if (target == null) return;
        Cell currentCell = Cell.findCell(target.getX(), target.getY(), game.getGrid());
        if (currentCell == null) return;
        int zombieCol = game.getCols() - 1;
        currentState = FishermanState.CAST;
        if (currentCell.getCol() >= zombieCol - 1) {
            target.die();
            currentState = FishermanState.TOSS;
            GameMenuController.updateState("Fisherman threw and destroyed plant at (" + target.getX() +
                ", " + target.getY() + ")");
            return;
        }
        int targetCol = currentCell.getCol() + 1;
        if (targetCol >= game.getCols()) {
            currentState = FishermanState.IDLE;
            return;
        }
        Cell targetCell = game.getGrid()[row][targetCol];

        if (targetCell == null || !targetCell.isEmpty()) {
            currentState = FishermanState.IDLE;
            return;
        }
        PlantLayer layer = null;
        for (PlantLayer l : PlantLayer.values()) {
            if (currentCell.getPlant(l) == target) {
                layer = l;
                break;
            }
        }
        if (layer == null) return;
        currentCell.setPlant(null, layer);
        targetCell.setPlant(target, layer);
        target.setX((int) targetCell.getX());
        target.setY((int) targetCell.getY());
        target.setCell(targetCell);

        currentState = FishermanState.REEL;

        GameMenuController.updateState("Fisherman pulled plant from (" +
            currentCell.getX() + ", " + currentCell.getY() +
            ") to (" + targetCell.getX() + ", " + targetCell.getY() + ")");
    }
    public FishermanState getFishermanState() {
        return currentState;
    }
}
