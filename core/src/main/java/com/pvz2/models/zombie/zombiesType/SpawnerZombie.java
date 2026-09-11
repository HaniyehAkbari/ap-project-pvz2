package com.pvz2.models.zombie.zombiesType;

import com.pvz2.controller.GameMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.Zombies;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.zombie.ZombieFactory;
import com.pvz2.view.util.LawnGrid;

public class SpawnerZombie extends Zombie {
    private boolean isGargantuar;
    private float spawnCooldown;
    private float currentCooldown;
    private boolean hasThrownImp;

    private boolean isThrowing;
    private float throwTimer;
    private int throwStage;

    private boolean isIntro;
    private float introTimer;
    private boolean isSpecial;
    private float specialTimer;
    private boolean useIdle2;
    private float idleTimer;

    private Plant smashTarget;
    private boolean isSmashing;
    private float smashTimer;
    private boolean hasAppliedSmashDamage;
    private boolean hasShakenSmashLeft;

    private static final float THROW_FIRE_DURATION = 0.97f;
    private static final float THROW_CANNON_FIRE_DURATION = 0.57f;
    private static final float SMASH_EAT_DURATION = 1.27f;
    private static final float SMASH_LEFT_DURATION = 1.77f;
    private static final float SMASH_TOTAL_DURATION = SMASH_EAT_DURATION + SMASH_LEFT_DURATION;
    private static final float SMASH_RANGE = 200f;

    public SpawnerZombie(int health, double speed, int damage, boolean isGargantuar) {
        super(Zombies.SPAWNER, health, speed, damage);
        this.isGargantuar = isGargantuar;
        this.spawnCooldown = 5.0f;
        this.currentCooldown = spawnCooldown;
        this.hasThrownImp = false;
        this.isThrowing = false;
        this.throwTimer = 0f;

        if (!isGargantuar) {
            this.speed = 0;
            this.originalSpeed = 0;
            this.isIntro = true;
            this.introTimer = 0f;
            this.isSpecial = false;
            this.specialTimer = 0f;
            this.useIdle2 = false;
            this.idleTimer = 0f;
        } else {
            this.damage = damage;
        }
    }

    @Override
    public void setX(float x) {
        if (!isGargantuar) {
            super.setX(x - App.getCellWidth());
        } else {
            super.setX(x);
        }
    }

    @Override
    public void update(float delta) {
        if (isDead) return;
        super.update(delta);

        if (isGargantuar) {
            updateGargantuar(delta);
        } else {
            updateNonGargantuar(delta);
        }
    }

    private void updateGargantuar(float delta) {
        if (isThrowing) {
            updateThrowing(delta);
            return;
        }

        if (!hasThrownImp && this.health <= this.maxHealth / 2) {
            startThrowing();
            return;
        }

        if (isSmashing) {
            updateSmashing(delta);
            return;
        }

        checkForSmashTarget();
    }

    private void updateThrowing(float delta) {
        throwTimer += delta;
        this.speed = 0;

        if (throwStage == 0) {
            if (throwTimer >= THROW_FIRE_DURATION) {
                throwStage = 1;
                throwTimer = 0f;
                throwImp();
            }
        } else {
            if (throwTimer >= THROW_CANNON_FIRE_DURATION) {
                isThrowing = false;
                this.speed = this.originalSpeed;
            }
        }
    }

    private void startThrowing() {
        isThrowing = true;
        hasThrownImp = true;
        throwStage = 0;
        throwTimer = 0f;
        this.speed = 0;
    }

    private void updateSmashing(float delta) {
        smashTimer += delta;
        this.speed = 0;

        if (!hasAppliedSmashDamage && smashTimer >= SMASH_EAT_DURATION) {
            hasAppliedSmashDamage = true;
            if (smashTarget != null && !smashTarget.isDead()) {
                smashTarget.die();
                GameMenuController.updateState("Gargantuar smashed a plant at (" +
                    (int) smashTarget.getX() + ", " + (int) smashTarget.getY() + ")");
            }
            requestScreenShake();
        }

        if (!hasShakenSmashLeft && smashTimer >= SMASH_EAT_DURATION) {
            hasShakenSmashLeft = true;
            requestScreenShake();
        }

        if (smashTimer >= SMASH_TOTAL_DURATION) {
            isSmashing = false;
            smashTarget = null;
            this.speed = this.originalSpeed;
        }
    }

    private void checkForSmashTarget() {
        GameWorld game = App.getCurrentGame(this);
        if (game == null) return;

        int row = LawnGrid.getRowFromY(this.y);
        Plant target = game.getNearestPlantInRow(row, this.x - 10);

        if (target != null && !target.isDead()) {
            float distance = this.x - target.getX();
            if (distance >= 0 && distance <= SMASH_RANGE) {
                smashTarget = target;
                isSmashing = true;
                smashTimer = 0f;
                hasAppliedSmashDamage = false;
                hasShakenSmashLeft = false;
            }
        }
    }

    private void updateNonGargantuar(float delta) {
        if (isIntro) {
            updateIntro(delta);
            return;
        }

        if (isSpecial) {
            updateSpecial(delta);
            return;
        }

        updateIdle(delta);
        updateCooldown(delta);
    }

    private void updateIntro(float delta) {
        introTimer += delta;
        if (introTimer >= 2.0f) {
            isIntro = false;
        }
    }

    private void updateSpecial(float delta) {
        specialTimer += delta;
        if (specialTimer >= 1.5f) {
            isSpecial = false;
        }
    }

    private void updateIdle(float delta) {
        idleTimer += delta;
        if (idleTimer >= 3.0f) {
            useIdle2 = !useIdle2;
            idleTimer = 0f;
        }
    }

    private void updateCooldown(float delta) {
        if (currentCooldown <= 0 && !isSpecial) {
            if (knightNearbyZombie()) {
                isSpecial = true;
                specialTimer = 0f;
                currentCooldown = spawnCooldown;
            }
        } else if (!isSpecial) {
            currentCooldown -= delta;
        }
    }


    @Override
    public String getAnimationClip() {
        if (isDead) return "die";

        if (isGargantuar) {
            if (isThrowing) {
                return throwStage == 0 ? "fire" : "cannon_fire";
            }
            if (isSmashing) {
                return smashTimer < SMASH_EAT_DURATION ? "eat" : "smash_left";
            }
        } else {
            if (isIntro) return "intro";
            if (isSpecial) return "special";
            return useIdle2 ? "idle2" : "idle";
        }

        return super.getAnimationClip();
    }

    private void throwImp() {
        GameWorld game = App.getCurrentGame(this);
        if (game == null) return;

        ImpZombie imp = (ImpZombie) new ZombieFactory().createZombie("ZombieImp");
        float targetX = App.getCellCenterX(2);
        float targetY = this.y;

        imp.throwImp(targetX, targetY);
        game.getActiveZombies().add(imp);
        GameMenuController.updateState("Gargantuar threw an Imp to column 3 at (" + targetX + ", " + targetY + ")");
    }

    private boolean knightNearbyZombie() {
        GameWorld game = App.getCurrentGame(this);
        if (game == null) return false;

        for (Zombie z : game.getActiveZombies()) {
            if (z == this) continue;

            boolean isBasic = z.getName() == Zombies.ZOMBIE &&
                !(z instanceof ArmoredZombie) &&
                Math.abs(z.getY() - this.y) < 10 &&
                Math.abs(z.getX() - this.getX()) < 150;

            if (isBasic) {
                double actualSpeed = z.getSpeed();
                int actualDamage = z.getDamage();

                ArmoredZombie knight = new ArmoredZombie(
                    (int) z.getHealth(),
                    0,
                    0,
                    1600,
                    true
                );
                knight.setX(z.getX());
                knight.setY(z.getY());
                knight.setSpecificName("ZombieDarkArmor3");
                knight.setSpeed(actualSpeed);
                knight.setDamage(actualDamage);

                z.die();
                game.getActiveZombies().remove(z);
                game.getActiveZombies().add(knight);

                GameMenuController.updateState(
                    "King turned a zombie into a knight at (" + knight.getX() + ", " + knight.getY() + ")");
                return true;
            }
        }
        return false;
    }

    @Override
    public void move(float delta) {
        if (!isGargantuar) {
            return;
        }
        super.move(delta);
    }
}
