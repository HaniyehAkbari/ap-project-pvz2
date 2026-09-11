package com.pvz2.models.zombie.zombiesType;

import com.pvz2.controller.GameMenuController;
import com.pvz2.models.enums.Zombies;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.zombie.state.EatingState;
import com.pvz2.models.zombie.state.TacklingState;

public class PhasingZombie extends Zombie {
    private boolean isPhaseChanged;
    private boolean isNewspaper;
    private int shieldHealth;
    private boolean hasKilledPlant;
    private float defeatAnimationTimeRemaining = 0f;

    public PhasingZombie(int health, double speed, int damage, int shieldHealth, boolean isNewspaper) {
        super(Zombies.PHASING, health, speed, damage);
        this.shieldHealth = shieldHealth;
        this.isNewspaper = isNewspaper;
        this.isPhaseChanged = false;
        this.hasKilledPlant = false;

        if (!isNewspaper) {
            this.originalSpeed = this.speed * 2.0;
            this.speed = this.originalSpeed;
            this.damage = 9999;
        } else {
            this.originalSpeed = this.speed;
        }
    }

    @Override
    public void takeDamage(int amount, String damageType) {
        if (isDead) return;

        if (isNewspaper && !isPhaseChanged && shieldHealth > 0) {
            triggerDamageFlash();
            shieldHealth -= amount;
            if (shieldHealth <= 0) {
                triggerPhaseChange();
            }
        } else {
            super.takeDamage(amount, damageType);

            if (!isNewspaper && !hasKilledPlant && this.health <= this.maxHealth / 2) {
                triggerAllStarPhaseChange("All-Star zombie lost more than half its health and slowed down!");
            }
        }
    }

    private void triggerPhaseChange() {
        this.isPhaseChanged = true;
        if (isNewspaper) {
            this.defeatAnimationTimeRemaining = 1.0f;
            this.speed = this.originalSpeed * 2.0;
            this.damage = this.damage * 3;
            GameMenuController.updateState("Newspaper is destroyed! Speed and damage increased.");
        }
    }

    private void triggerAllStarPhaseChange(String message) {
        this.hasKilledPlant = true;
        this.speed = this.originalSpeed * 0.25;
        this.damage = 20;
        GameMenuController.updateState(message);
    }

    @Override
    public void update(float delta) {
        if (isDead) return;

        if (defeatAnimationTimeRemaining > 0) {
            defeatAnimationTimeRemaining -= delta;
            return;
        }

        super.update(delta);
        if (!isNewspaper && !hasKilledPlant && this.currentState instanceof EatingState eatingState) {
            setState(new TacklingState(eatingState.getTargetPlant()));
        }
    }

    @Override
    public String getAnimationClip() {
        if (isDead) return "die";

        if (isNewspaper) {
            if (!isPhaseChanged) {
                if (getFreezedTicksRemaining() > 0 || getIceHealth() > 0 || getDisabledTicksRemaining() > 0)
                    return "idle_newspaper";
                String base = currentState != null ? currentState.getAnimationClip() : "idle";
                if (base.equals("walk")) return "walk_newspaper";
                if (base.equals("eat")) return "eat_newspaper";
                return "idle_newspaper";
            } else {
                if (defeatAnimationTimeRemaining > 0) return "newspaper_defeat";
                if (getFreezedTicksRemaining() > 0 || getIceHealth() > 0 ||
                    getDisabledTicksRemaining() > 0) return "idle";
                return currentState != null ? currentState.getAnimationClip() : "idle";
            }
        } else {
            if (getFreezedTicksRemaining() > 0 || getIceHealth() > 0 ||
                getDisabledTicksRemaining() > 0) return "idle";
            String base = currentState != null ? currentState.getAnimationClip() : "idle";

            if (!hasKilledPlant && base.equals("walk")) return "run";
            return base;
        }
    }
}
