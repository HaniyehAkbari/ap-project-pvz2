package com.pvz2.models.miniGame.IZombie;

import com.pvz2.models.core.App;
import com.pvz2.models.enums.Zombies;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.zombie.Zombie;

public class SunProducer extends Zombie {
    private int baseSunRate;
    private float sunSpawnTime;
    private float lastSunProduceTime;
    private boolean initialized = false;

    public SunProducer(Zombies name, int health, double speed, int damage) {
        super(name, health, speed, damage);
        this.baseSunRate = 50;
        this.sunSpawnTime = 0f;
        this.lastSunProduceTime = 0f;
    }

    public void initSpawnTick(float currentTime) {
        this.sunSpawnTime = currentTime;
        this.lastSunProduceTime = currentTime;
        this.initialized = true;
    }

    public int calculateSunAmount(float currentTime) {
        float elapsedTicks = currentTime - sunSpawnTime;
        return 15 + (int) (elapsedTicks / 100) * 5;
    }

    public void updateSunGeneration(IZombieLevel level) {
        float currentTime = level.getElapsedTime();
        if (currentTime - lastSunProduceTime >= baseSunRate) {
            int sunAmount = calculateSunAmount(currentTime);
            System.out.println("SunProducer generated " + sunAmount + " suns! ☀️");
            level.addSunToPlayer(sunAmount);
            lastSunProduceTime = currentTime;
        }
    }

    @Override
    public void takeDamage(int amount, String damageType) {
        super.takeDamage(amount, damageType);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        GameWorld currentGame = App.getCurrentGame();
        if (currentGame instanceof IZombieLevel level) {
            if (!initialized) {
                initSpawnTick(level.getElapsedTime());
            }

            updateSunGeneration(level);
        }
    }
}
