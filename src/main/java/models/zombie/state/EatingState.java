package models.zombie.state;

import models.core.App;
import models.miniGame.beghouled.BeghouledMechanics;
import models.plant.Plant;
import models.world.Cell;
import models.world.GameWorld;
import models.world.levelSetup.SaveOurSeedsLevelSetup;
import models.world.loseCondition.SaveOurSeedsLose;
import models.zombie.Zombie;

public class EatingState implements ZombieState {
    private Plant targetPlant;

    public EatingState(Plant targetPlant) {
        this.targetPlant = targetPlant;
    }

    @Override
    public void handleAction(Zombie zombie) {
        if (targetPlant != null && !targetPlant.isDead()) {
            Cell targetCell = targetPlant.getCell();
            targetPlant.takeDamage(zombie.getDamage(), zombie);
            zombie.setHasEatenPlant(true);

            if (targetPlant.isDead()) {
                zombie.setState(new WalkingState());
                GameWorld world = App.getCurrentGame();
                BeghouledMechanics beghouled = world.getMechanic(BeghouledMechanics.class);

                if (beghouled != null && targetCell != null) {
                    beghouled.createCrater(world, targetCell.getRow(), targetCell.getCol());
                }

                if(world.getLevelSetup() instanceof SaveOurSeedsLevelSetup setup){
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
}
