package com.pvz2.models.zombie.zombiesType;

import com.pvz2.controller.GameMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.Zombies;
import com.pvz2.models.projectile.Projectile;
import com.pvz2.models.projectile.hitStrategies.HitStrategy;
import com.pvz2.models.projectile.hitStrategies.PlantDamageStrategy;
import com.pvz2.models.projectile.movementStrategies.StraightMovementStrategy;
import com.pvz2.models.projectile.strikeStrategies.CheckPlantStrike;
import com.pvz2.models.projectile.strikeStrategies.CheckStrike;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.zombie.Zombie;

public class DeflectorZombie extends Zombie {
    private static final double SPIN_SPEED_MULTIPLIER = 1.8;
    private static final float SPIN_UP_DURATION = 0.3f;
    private static final float SPIN_DOWN_DURATION = 0.3f;
    private static final float SPIN_ACTIVE_DURATION = 3.0f;
    private static final float DEFLECTED_PROJECTILE_SPEED = 500f;

    private enum SpinPhase { NONE, SPIN_UP, SPINNING, SPIN_DOWN }

    private final boolean isJuggler;
    private SpinPhase spinPhase = SpinPhase.NONE;
    private float spinPhaseTime = 0f;
    private float spinTime = 0f;

    public DeflectorZombie(int health, double speed, int damage, boolean isJuggler) {
        super(Zombies.DEFLECTOR, health, speed, damage);
        this.isJuggler = isJuggler;
        this.originalSpeed = this.speed;
    }

    @Override
    public void update(float delta) {
        if (isDead) return;

        if (isJuggler) {
            boolean approaching = isProjectileApproaching();

            if (approaching) {
                if (spinPhase == SpinPhase.NONE) {
                    startSpinning();
                } else if (spinPhase == SpinPhase.SPINNING) {
                    spinTime = SPIN_ACTIVE_DURATION;
                } else if (spinPhase == SpinPhase.SPIN_DOWN) {
                    spinPhase = SpinPhase.SPINNING;
                    spinTime = SPIN_ACTIVE_DURATION;
                }
            }

            updateSpinPhase(delta);

            this.speed = (spinPhase != SpinPhase.NONE)
                ? originalSpeed * SPIN_SPEED_MULTIPLIER
                : originalSpeed;
        }

        super.update(delta);
    }

    private boolean isProjectileApproaching() {
        GameWorld game = App.getCurrentGame();
        if (game == null) return false;

        for (Projectile p : game.getActiveProjectiles()) {
            if (p.getType() != null &&
                    "STRAIGHT".equals(p.getType().movement) &&
                    !(p.getHitStrategy() instanceof PlantDamageStrategy)) {
                if (Math.abs(p.getY() - this.y) < 50 && p.getX() < this.x && p.getX() > this.x - 250) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateSpinPhase(float delta) {
        switch (spinPhase) {
            case SPIN_UP -> {
                spinPhaseTime -= delta;
                if (spinPhaseTime <= 0) {
                    spinPhase = SpinPhase.SPINNING;
                    spinTime = SPIN_ACTIVE_DURATION;
                }
            }
            case SPINNING -> {
                spinTime -= delta;
                if (spinTime <= 0) {
                    spinPhase = SpinPhase.SPIN_DOWN;
                    spinPhaseTime = SPIN_DOWN_DURATION;
                    System.out.println("Juggler stops spinning.");
                    GameMenuController.updateState("Juggler stops spinning.");
                }
            }
            case SPIN_DOWN -> {
                spinPhaseTime -= delta;
                if (spinPhaseTime <= 0) {
                    spinPhase = SpinPhase.NONE;
                    if (!isSlowed()) {
                        this.speed = originalSpeed;
                    }
                }
            }
            case NONE -> { }
        }
    }

    public boolean tryDeflect(Projectile projectile) {
        if (isDead) return false;

        if (isJuggler) {
            if ("STRAIGHT".equals(projectile.getType().movement)) {
                if (spinPhase == SpinPhase.NONE) {
                    startSpinning();
                }
                spinPhase = SpinPhase.SPINNING;
                spinTime = SPIN_ACTIVE_DURATION;
                deflectProjectile(projectile);
                return true;
            }
        } else {
            if ("LOBBED".equals(projectile.getType().movement)) {
                System.out.println("Parasol deflected a lobbed projectile!");
                return true;
            }
        }
        return false;
    }

    private void startSpinning() {
        spinPhase = SpinPhase.SPIN_UP;
        spinPhaseTime = SPIN_UP_DURATION;
        System.out.println("Juggler starts spinning!");
        GameMenuController.updateState("Juggler starts spinning!");
    }

    private void deflectProjectile(Projectile original) {
        GameWorld game = App.getCurrentGame();
        if (game == null) return;

        Projectile deflected = game.getProjectilesPool().acquire();
        StraightMovementStrategy movement = new StraightMovementStrategy(-1f * DEFLECTED_PROJECTILE_SPEED, 0, 0);

        String element = "NORMAL";
        int dmg = 20;
        if (original.getHitStrategy() != null) {
            element = original.getHitStrategy().getElement();
            dmg = original.getHitStrategy().getDamage();
        }

        HitStrategy hitStrategy = new PlantDamageStrategy(dmg, element);
        CheckStrike strike = new CheckPlantStrike();

        deflected.reset(original.getX(), original.getY(), hitStrategy, movement, strike, original.getType());
        deflected.setPlantType(original.getPlantType());

        game.getActiveProjectiles().add(deflected);
        GameMenuController.updateState("Juggler deflected a projectile back to plants!");
    }

    @Override
    public String getAnimationClip() {
        if (isDead) return "die";

        if (isJuggler) {
            switch (spinPhase) {
                case SPIN_UP:
                    return "spinup";
                case SPINNING:
                    return "spin_walk";
                case SPIN_DOWN:
                    return "spindown";
                case NONE:
                default:
                    break;
            }
        }

        return super.getAnimationClip();
    }
}
