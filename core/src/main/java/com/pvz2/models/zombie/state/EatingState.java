package com.pvz2.models.zombie.state;

import com.pvz2.models.core.App;
import com.pvz2.models.miniGame.beghouled.BeghouledMechanics;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.levelSetup.SaveOurSeedsLevelSetup;
import com.pvz2.models.world.loseCondition.SaveOurSeedsLose;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.view.audios.SFXManager;

public class EatingState implements ZombieState {
    private Plant targetPlant;
    private float biteTimer = 0f;
    private static final float BITE_INTERVAL = 0.5f;

    public EatingState(Plant targetPlant) {
        this.targetPlant = targetPlant;
    }

    public Plant getTargetPlant() {
        return targetPlant;
    }

    @Override
    public void handleAction(Zombie zombie, float delta) {
        if (targetPlant != null && !targetPlant.isDead() && Math.abs(targetPlant.getY()-zombie.getY()) < 5) {
            biteTimer -= delta;
            if (biteTimer > 0f) return;
            biteTimer = BITE_INTERVAL;

            targetPlant.takeDamage(zombie.getDamage(), zombie);
            zombie.setHasEatenPlant(true);

            SFXManager.getInstance().playChompSound();

            if (targetPlant == null || targetPlant.isDead() || targetPlant.getCell() == null) {
                zombie.setState(new WalkingState());
                GameWorld world = App.getCurrentGame();
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
                            System.out.println("onProtectedPlantEaten() CALLED!");
                        }
                    }
                }
            }
        } else {
            zombie.setState(new WalkingState());
        }
    }

    @Override
    public String getAnimationClip() {
        return "eat";
    }
}
