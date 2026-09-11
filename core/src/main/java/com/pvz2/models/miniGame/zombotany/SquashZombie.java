package com.pvz2.models.miniGame.zombotany;

import com.pvz2.models.core.App;
import com.pvz2.models.enums.Zombies;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.zombie.Zombie;

public class SquashZombie extends Zombie {

    public SquashZombie(Zombies name, int health, double speed, int damage) {
        super(name, health, speed, damage);
    }

    @Override
    public void update(float delta) {
        if (isDead) return;

        GameWorld game = App.getCurrentGame();
        if (game != null) {
            Plant plant = game.getPlantAtPosition(this.x, this.y);
            if (plant != null && !plant.isDead()) {
                plant.takeDamage(9999);
                this.die();
                return;
            }
        }

        super.update(delta);
    }
}
