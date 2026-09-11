package com.pvz2.models.zombie.zombiesType;

import com.pvz2.models.core.App;
import com.pvz2.models.enums.Zombies;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.obstacles.BarrelObstacle;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.zombie.ZombieFactory;

public class BarrelRollerZombie extends Zombie {
    private int barrelHealth;
    private boolean barrelIntact;
    private Cell currentCell;
    private boolean hasSpawnedImps;

    public BarrelRollerZombie(int health, double speed, int damage, int barrelHealth) {
        super(Zombies.PUSHER, health, speed, damage);
        this.barrelHealth = barrelHealth;
        this.barrelIntact = true;
        this.hasSpawnedImps = false;
    }

    @Override
    public void update(float delta) {
        if (isDead) return;

        GameWorld game = App.getCurrentGame();
        if (game == null) {
            super.update(delta);
            return;
        }

        Cell zombieCell = Cell.findZombieCell(game.getGrid(), this);
        if (zombieCell == null) {
            super.update(delta);
            return;
        }
        this.currentCell = zombieCell;

        if (barrelIntact && barrelHealth > 0) {
            crushPlants(zombieCell, game);
            super.update(delta);
        } else {
            super.update(delta);
            if (!barrelIntact && !hasSpawnedImps) {
                spawnImps(zombieCell, game);
                hasSpawnedImps = true;
            }
        }
    }

    private void crushPlants(Cell zombieCell, GameWorld game) {
        Plant plantHere = zombieCell.getPlant();
        if (plantHere != null && !plantHere.isDead()) {
            plantHere.die();
            System.out.println("🛢️ Barrel crushed plant at (" + plantHere.getX() + ", " + plantHere.getY() + ")");
        }

        Cell frontCell = Cell.previousCell(zombieCell, game.getGrid());
        if (frontCell != null) {
            Plant plantFront = frontCell.getPlant();
            if (plantFront != null && !plantFront.isDead()) {
                plantFront.die();
                System.out.println("🛢️ Barrel crushed plant at (" + plantFront.getX() + ", " + plantFront.getY() + ")");
            }
        }
    }

    private void spawnImps(Cell zombieCell, GameWorld game) {
        ZombieFactory factory = new ZombieFactory();
        for (int i = 0; i < 2; i++) {
            ImpZombie imp = (ImpZombie) factory.createZombie("ZombieImp");
            if (imp == null) continue;
            float impX = zombieCell.getX() - (i * 20);
            float impY = zombieCell.getY();
            imp.setX(impX);
            imp.setY(impY);
            game.getActiveZombies().add(imp);
            System.out.println("👾 Barrel released Imp #" + (i+1) + " at (" + impX + ", " + impY + ")");
        }
        if (zombieCell.getObstacle() instanceof BarrelObstacle) {
            zombieCell.removeObstacle();
        }
    }

    @Override
    public void takeDamage(int amount, String damageType) {
        if (isDead) return;
        if (barrelIntact && barrelHealth > 0) {
            triggerDamageFlash();
            int excess = amount - barrelHealth;
            if (excess > 0) {
                barrelHealth = 0;
                barrelIntact = false;
                System.out.println("💥 Barrel destroyed! Imps will be released.");
                super.takeDamage(excess, damageType);
            } else {
                barrelHealth -= amount;
                if (barrelHealth <= 0) {
                    barrelHealth = 0;
                    barrelIntact = false;
                    System.out.println("💥 Barrel destroyed! Imps will be released.");
                }
            }
        } else {
            super.takeDamage(amount, damageType);
        }
    }

    @Override
    public void die() {
        if (barrelIntact && barrelHealth > 0 && currentCell != null) {
            BarrelObstacle barrelObstacle = new BarrelObstacle(
                currentCell.getX(), currentCell.getY(), barrelHealth
            );
            currentCell.setObstacle(barrelObstacle);
            App.getCurrentGame().getActiveObstacles().add(barrelObstacle);
            System.out.println("🛢️ Barrel left behind as obstacle at (" +
                currentCell.getX() + ", " + currentCell.getY() + ")");
        }
        super.die();
    }

    public boolean isBarrelIntact() {
        return barrelIntact;
    }

}
