package com.pvz2.models.plant.components.explosiveBehaviors;

import com.pvz2.models.core.App;
import com.pvz2.models.enums.ProjectileType;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.projectile.Projectile;
import com.pvz2.models.projectile.hitStrategies.CombinedDamageStrategy;
import com.pvz2.models.projectile.hitStrategies.HitStrategy;
import com.pvz2.models.projectile.movementStrategies.BouncingStrategy;
import com.pvz2.models.projectile.strikeStrategies.CheckStraightStrike;

public class GrapeshotBehavior implements ExplosiveBehavior {
    private static final float GRAPE_SPEED = 800f;
    private final int bounceMax;

    public GrapeshotBehavior(int bounceMax) {
        this.bounceMax = bounceMax;
    }

    @Override
    public void execute(Plant owner) {
        float startX = owner.getCell().getX();
        float startY = owner.getCell().getY();
        HitStrategy hitStrategy = new CombinedDamageStrategy(20, 0, 0, ProjectileType.GRAPE);

        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            float vx = (float) (GRAPE_SPEED * Math.cos(angle));
            float vy = (float) (GRAPE_SPEED * Math.sin(angle));

            Projectile grape = App.getCurrentGame().getProjectilesPool().acquire();

            grape.reset(
                    startX,
                    startY,
                    hitStrategy,
                    new BouncingStrategy(vx, vy, bounceMax),
                    new CheckStraightStrike(),
                    ProjectileType.GRAPE
            );

            grape.setPierce(99);

            App.getCurrentGame().getActiveProjectiles().add(grape);
        }
    }
}
