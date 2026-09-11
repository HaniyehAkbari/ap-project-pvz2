package com.pvz2.models.zombie.zombiesType;

import com.pvz2.controller.GameMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.Zombies;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.obstacles.OctopusObstacle;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.view.screen.GameScreen;
import com.pvz2.view.util.LawnGrid;

import java.util.List;

public class RangedZombie extends Zombie {
    private final float cooldownMax = 15f;
    private String projectileType;
    private float cooldown;

    private boolean throwing = false;
    private float throwAnimElapsed = 0f;
    private boolean actionFired = false;

    public RangedZombie(int health, double speed, int damage, String projectileType) {
        super(Zombies.RANGED, health, speed, damage);
        this.projectileType = projectileType;
        this.cooldown = 0;
    }

    private float getThrowAnimDuration() {
        if ("OCTOPUS".equals(projectileType)) {
            return 3.08f;
        } else if ("BONE".equals(projectileType)) {
            return 3.0f;
        } else if ("SNOWBALL".equals(projectileType)) {
            return 2.10f;
        }
        return 0.6f;
    }

    public void throwProjectile() {
        GameWorld game = App.getCurrentGame();
        if (game == null) return;
        switch (projectileType) {
            case "SNOWBALL": {
                int row = LawnGrid.getRowFromY(this.y);
                Plant target = game.getNearestPlantInRow(row, this.x - 10);
                if (target != null) {
                    target.increaseFrozenAmount();

                    GameScreen.spawnSnowballSplat(target.getX(), target.getY());

                    System.out.println("❄️ Hunter Zombie threw a snowball at " + target.getType() +
                        " at (" + target.getX() + ", " + target.getY() + ")");
                } else {
                    System.out.println("❄️ Hunter Zombie threw a snowball but no target found in row " + row);
                }
                break;
            }
            case "OCTOPUS": {
                int row = LawnGrid.getRowFromY(this.y);
                Plant target = game.getNearestPlantInRow(row, this.x - 10);
                if (target != null && !target.isDead()) {
                    Cell cell = game.getCellAt(target.getX(), target.getY());
                    if (cell != null && !cell.hasObstacle()) {
                        OctopusObstacle octopus = new OctopusObstacle(target.getX(), target.getY(), target, cell);
                        cell.setObstacle(octopus);
                        game.getActiveObstacles().add(octopus);
                        GameMenuController.updateState(
                            "Octopus thrown at plant at (" + target.getX() + ", " + target.getY() + ")");
                    }
                }
                break;
            }
            case "BONE": {
                List<Cell> targetCells = game.findTwoEmptyCell(false);
                if (!targetCells.isEmpty()) {
                    for (Cell cell : targetCells) {
                        game.createGrave((int) cell.getX(), (int) cell.getY());
                        GameMenuController.updateState(
                            "Tomb Raiser threw a bone at (" +
                                cell.getCol() + ", " + cell.getRow() + ")");
                    }
                } else {
                    GameMenuController.updateState("No empty cell on the board to place grave.");
                }
                break;
            }
        }
    }

    @Override
    public void update(float delta) {
        if (isDead) return;
        super.update(delta);

        if (throwing) {
            throwAnimElapsed += delta;

            if (!actionFired && throwAnimElapsed >= getThrowActionTime()) {
                throwProjectile();
                actionFired = true;
            }

            if (throwAnimElapsed >= getThrowAnimDuration()) {
                throwing = false;
                throwAnimElapsed = 0f;
                actionFired = false;
                cooldown = cooldownMax;
            }
            return;
        }

        if (cooldown <= 0) {
            throwing = true;
            throwAnimElapsed = 0f;
        } else {
            cooldown -= delta;
        }
    }

    private float getThrowActionTime() {
        if ("SNOWBALL".equals(projectileType)) {
            return 1.1f;
        }
        return getThrowAnimDuration();
    }

    public boolean isThrowing() {
        return throwing;
    }

    public String getProjectileType() {
        return projectileType;
    }

    public String getThrowClipName() {
        return switch (projectileType) {
            case "OCTOPUS" -> "toss";
            case "BONE" -> "power";
            case "SNOWBALL" -> "throw";
            default -> "power";
        };
    }
}
