package com.pvz2.models.zombie.zombiesType;

import com.pvz2.controller.GameMenuController;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.Zombies;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.zombie.state.WalkingState;
import com.pvz2.view.util.LawnGrid;

import java.util.ArrayList;
import java.util.List;

public class WizardZombie extends Zombie {
    private static final float COOLDOWN_MAX = 3.0f;
    private static final float CAST_ANIM_DURATION = 2.30f;

    private List<Plant> transformedPlants;
    private float cooldown;

    private boolean isCasting;
    private float castTimer;

    public WizardZombie(int health, double speed, int damage) {
        super(Zombies.WIZARD, health, speed, damage);
        this.transformedPlants = new ArrayList<>();
        this.cooldown = 0f;
        this.isCasting = false;
        this.castTimer = 0f;
    }

    @Override
    public void update(float delta) {
        if (isDead() || getIceHealth() > 0 || getFreezedTicksRemaining() > 0 || getDisabledTicksRemaining() > 0) {
            super.update(delta);
            return;
        }

        if (isCasting) {
            castTimer += delta;
            if (castTimer >= CAST_ANIM_DURATION) {
                isCasting = false;
            }
        }

        if (cooldown <= 0) {
            boolean transformed = castSpell();
            cooldown = COOLDOWN_MAX;
            if (transformed) {
                isCasting = true;
                castTimer = 0f;
            }
        } else {
            cooldown -= delta;
        }

        super.update(delta);

        if (!isDead() && getCurrentState() != null && !(getCurrentState() instanceof WalkingState)) {
            GameWorld game = App.getCurrentGame(this);
            if (game != null) {
                int row = LawnGrid.getRowFromY(this.y);
                Plant target = game.getNearestPlantInRow(row, this.x + 50);

                if (target != null && target.isSheep()) {
                    float dist = this.x - target.getX();
                    if (dist > -80 && dist < 120) {
                        setState(new WalkingState());
                        move(delta);
                    }
                }
            }
        }
    }

    private boolean castSpell() {
        GameWorld game = App.getCurrentGame(this);
        if (game == null) return false;

        int row = LawnGrid.getRowFromY(this.y);
        Plant target = game.getNearestPlantInRow(row, this.x + 10);

        if (target != null && !target.isDead() && !target.isSheep()) {
            float distance = this.x - target.getX();
            if (distance > 0 && distance <= 200) {
                target.setSheep(true);
                transformedPlants.add(target);
                GameMenuController.updateState("Wizard turned a " + target.getType().name() + " into a sheep!");
                return true;
            }
        }
        return false;
    }

    @Override
    public String getAnimationClip() {
        if (isDead()) return "die";
        if (isCasting) return "sheep";
        return super.getAnimationClip();
    }

    @Override
    public void die() {
        for (Plant p : transformedPlants) {
            if (p != null && !p.isDead()) {
                p.setSheep(false);
            }
        }
        transformedPlants.clear();
        super.die();
    }
}
