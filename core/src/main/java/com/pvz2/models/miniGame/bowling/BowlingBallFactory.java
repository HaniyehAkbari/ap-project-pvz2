package com.pvz2.models.miniGame.bowling;

import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.enums.ProjectileType;
import com.pvz2.models.projectile.Projectile;
import com.pvz2.models.projectile.hitStrategies.CombinedDamageStrategy;
import com.pvz2.models.projectile.hitStrategies.HitStrategy;
import com.pvz2.models.projectile.movementStrategies.BowlingMovementStrategy;
import com.pvz2.models.projectile.movementStrategies.MovementStrategy;
import com.pvz2.models.projectile.movementStrategies.StraightMovementStrategy;
import com.pvz2.models.projectile.strikeStrategies.CheckStraightStrike;
import com.pvz2.models.zombie.ZombieRegistry;

public class BowlingBallFactory {

    private static int normalZombieHealth() {
        return ZombieRegistry.getZombieProperties("ZombieDefault").getObjdata().getHitpoints();
    }

    public static Projectile create(PlantType plantType, float x, float y) {
        Projectile ball = App.getCurrentGame().getProjectilesPool().acquire();

        HitStrategy hitStrategy = createHitStrategy(plantType);
        MovementStrategy movementStrategy = createMovementStrategy(plantType);

        ball.reset(x, y, hitStrategy, movementStrategy, new CheckStraightStrike(), ProjectileType.BOWLING_STRAIGHT);
        ball.setPierce(getPierce(plantType));

        return ball;
    }

    private static HitStrategy createHitStrategy(PlantType plantType) {
        int normalHealth = normalZombieHealth();
        return switch (plantType) {
            case WALL_NUT ->
                    new CombinedDamageStrategy(normalHealth, ProjectileType.BOWLING_STRAIGHT);
            case EXPLODE_O_NUT -> new CombinedDamageStrategy(
                    3000,
                    3000,
                    App.getCellWidth() * 1.5f,
                    ProjectileType.BOWLING_STRAIGHT
            );
            case GIANT_WALLNUT ->
                    new CombinedDamageStrategy(3000, ProjectileType.BOWLING_STRAIGHT);
            default ->
                    throw new IllegalArgumentException("Invalid plant type for bowling: " + plantType);
        };
    }

    private static MovementStrategy createMovementStrategy(PlantType plantType) {
        return switch (plantType) {
            case WALL_NUT -> new BowlingMovementStrategy(5f, 0f);
            case EXPLODE_O_NUT, GIANT_WALLNUT -> new StraightMovementStrategy(5f, 0f, 0f);
            default -> new BowlingMovementStrategy(5f, 0f);
        };
    }

    private static int getPierce(PlantType plantType) {
        return switch (plantType) {
            case WALL_NUT -> 100;
            case EXPLODE_O_NUT -> 1;
            case GIANT_WALLNUT -> 100;
            default -> 100;
        };
    }
}
