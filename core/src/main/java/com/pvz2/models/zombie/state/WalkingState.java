package com.pvz2.models.zombie.state;

import com.pvz2.models.core.App;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.zombie.Zombie;

public class WalkingState implements ZombieState {
    @Override
    public void handleAction(Zombie zombie, float delta) {
        zombie.move(delta);

        GameWorld game = App.getCurrentGame();
        if (game == null){
            if (zombie.getCurrentWorld() != null) game = zombie.getCurrentWorld();
            else return;
        }

        Plant targetPlant = game.getPlantAtPosition(zombie.getX(), zombie.getY());
        if (targetPlant != null && !targetPlant.isDead()) {
            zombie.setState(new EatingState(targetPlant));
        }
    }
    @Override
    public String getAnimationClip() {
        return "walk";
    }
}
