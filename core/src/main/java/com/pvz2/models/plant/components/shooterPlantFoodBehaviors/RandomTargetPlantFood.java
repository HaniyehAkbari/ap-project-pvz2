package com.pvz2.models.plant.components.shooterPlantFoodBehaviors;

import com.pvz2.models.core.App;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.components.ShooterComponent;
import com.pvz2.models.projectile.Projectile;
import com.pvz2.models.zombie.Zombie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomTargetPlantFood implements PlantFoodBehavior {
    private final int targetCount;
    private final float intervalBetweenShots;

    private final transient List<Zombie> pendingTargets = new ArrayList<>();
    private float timer = 0f;
    private boolean started = false;

    public RandomTargetPlantFood(int targetCount, float intervalBetweenShots) {
        this.targetCount = targetCount;
        this.intervalBetweenShots = intervalBetweenShots;
    }

    public RandomTargetPlantFood(int targetCount){
        this(targetCount, 0.1f);
    }

    public RandomTargetPlantFood(){
        this(Integer.MAX_VALUE);
    }

    @Override
    public void activate(Plant owner, ShooterComponent shooterComponent) {
        pendingTargets.clear();
        timer = 0f;
        started = true;

        List<Zombie> allZombies = App.getCurrentGame().getActiveZombies();
        if (allZombies.isEmpty()) return;

        List<Zombie> zombieCopy = new ArrayList<>(allZombies);
        Collections.shuffle(zombieCopy);
        int finalCount = Math.min(targetCount, zombieCopy.size());
        pendingTargets.addAll(zombieCopy.subList(0, finalCount));
    }

    @Override
    public void update(Plant owner, ShooterComponent shooterComponent, float delta) {
        if (!started || pendingTargets.isEmpty()) return;

        timer += delta;
        if (timer >= intervalBetweenShots) {
            timer -= intervalBetweenShots;
            fireAt(owner, shooterComponent, pendingTargets.remove(0));
        }
    }

    @Override
    public boolean isFinished() {
        return started && pendingTargets.isEmpty();
    }

    private void fireAt(Plant owner, ShooterComponent shooterComponent, Zombie zombie) {
        Projectile p = App.getCurrentGame().getProjectilesPool().acquire();
        p.reset(owner.getX(), owner.getY(), shooterComponent.getPlantFoodStrategy(),
            shooterComponent.getMovementStrategies().getFirst().get(),
            shooterComponent.getStrikeStrategy(), shooterComponent.getGiantType());
        p.setTarget(zombie);
        App.getCurrentGame().getActiveProjectiles().add(p);
    }
}
