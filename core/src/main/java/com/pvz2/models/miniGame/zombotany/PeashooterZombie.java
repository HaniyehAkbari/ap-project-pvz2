package com.pvz2.models.miniGame.zombotany;

import com.pvz2.models.core.App;
import com.pvz2.models.enums.ProjectileType;
import com.pvz2.models.enums.Zombies;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.projectile.Projectile;
import com.pvz2.models.projectile.hitStrategies.PlantDamageStrategy;
import com.pvz2.models.projectile.movementStrategies.StraightMovementStrategy;
import com.pvz2.models.projectile.strikeStrategies.CheckPlantStrike;
import com.pvz2.models.projectile.strikeStrategies.CheckStrike;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.view.util.LawnGrid;

public class PeashooterZombie extends Zombie {
    private static final float COOLDOWN_TICKS = 1.5f;
    private float shootCooldown = 0f;

    public PeashooterZombie(Zombies name, int health, double speed, int damage) {
        super(name, health, speed, damage);
    }

    private void shoot() {
        GameWorld world = App.getCurrentGame();
        if (world == null) return;

        int row = LawnGrid.getRowFromY(this.y);
        Plant target = world.getNearestPlantInRow(row, this.x);
        if (target == null) return;
        System.out.println("🎯 PeashooterZombie saw " + target.getClass().getSimpleName() +
                " at row " + row + "! Shooting...");

        Projectile pea = world.getProjectilesPool().acquire();

        StraightMovementStrategy movement = new StraightMovementStrategy(-5f, 0, 0);

        CheckStrike checkStrike = new CheckPlantStrike();
        pea.reset(this.x - 20,
                this.y,
                new PlantDamageStrategy(20, "NORMAL"),
                movement,
                checkStrike,
                ProjectileType.PEA);

        world.addProjectile(pea);
        System.out.println("🚀 Zombie Pea spawned at X: " + (this.x - 20) + ", Y: " + this.y);
    }

    @Override
    public void update(float delta) {
        if (isDead) return;
        super.update(delta);
        if (isDead) return;

        if (shootCooldown <= 0) {
            shoot();
            shootCooldown = COOLDOWN_TICKS;
        } else {
            shootCooldown-= delta;
        }
    }
}
