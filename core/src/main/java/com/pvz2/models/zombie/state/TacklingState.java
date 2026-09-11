package com.pvz2.models.zombie.state;

import com.pvz2.models.core.App;
import com.pvz2.models.miniGame.beghouled.BeghouledMechanics;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.levelSetup.SaveOurSeedsLevelSetup;
import com.pvz2.models.world.loseCondition.SaveOurSeedsLose;
import com.pvz2.models.zombie.Zombie;

public class TacklingState implements ZombieState {
    private static final float TACKLE_DURATION = 1.30f;
    private static final float KICK_DURATION = 1.60f;

    private enum Phase { TACKLE, KICK }

    private final Plant targetPlant;
    private Phase phase = Phase.TACKLE;
    private float phaseTimer = TACKLE_DURATION;
    private boolean plantDestroyed = false;

    public TacklingState(Plant targetPlant) {
        this.targetPlant = targetPlant;
    }

    @Override
    public void handleAction(Zombie zombie, float delta) {
        phaseTimer -= delta;

        if (phase == Phase.TACKLE) {
            if (phaseTimer <= 0f) {
                phase = Phase.KICK;
                phaseTimer = KICK_DURATION;
            }
            return;
        }

        // Phase.KICK
        if (!plantDestroyed && phaseTimer <= 0f) {
            destroyPlant(zombie);
            plantDestroyed = true;
        }

        if (plantDestroyed && phaseTimer <= 0f) {
            zombie.setState(new WalkingState());
        }
    }

    private void destroyPlant(Zombie zombie) {
        if (targetPlant == null || targetPlant.isDead()) return;

        targetPlant.takeDamage(zombie.getDamage(), zombie);
        zombie.setHasEatenPlant(true);

        GameWorld world = App.getCurrentGame();
        if (world == null) return;

        BeghouledMechanics beghouled = world.getMechanic(BeghouledMechanics.class);
        if (beghouled != null && targetPlant.getCell() != null) {
            beghouled.createCrater(world, targetPlant.getCell().getRow(), targetPlant.getCell().getCol());
        }

        if (world.getLevelSetup() instanceof SaveOurSeedsLevelSetup setup) {
            boolean isProtected = setup.isProtectedPlant(targetPlant);
            if (isProtected) {
                SaveOurSeedsLose loseCondition = world.getLoseCondition(SaveOurSeedsLose.class);
                if (loseCondition != null) {
                    loseCondition.onProtectedPlantEaten();
                }
            }
        }
    }

    @Override
    public String getAnimationClip() {
        return phase == Phase.TACKLE ? "tackle" : "kick";
    }
}
