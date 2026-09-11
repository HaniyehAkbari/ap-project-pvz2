package com.pvz2.models.world.mechanics;

import com.pvz2.controller.GameMenuController;
import com.pvz2.models.world.Cell;
import com.pvz2.models.world.ChapterWorld.DarkAgesWorld;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.zombie.ZombieFactory;
import com.pvz2.models.zombie.wave.Wave;
import com.pvz2.models.zombie.wave.WaveManager;

public class DarkAgesMechanic implements Mechanic {
    private int lastWaveNumber = -1;

    @Override
    public void applyMechanic(GameWorld world) {
        if (!(world instanceof DarkAgesWorld)) return;
        DarkAgesWorld darkWorld = (DarkAgesWorld) world;

        NormalMechanic normalMechanic = world.getMechanic(NormalMechanic.class);
        if (normalMechanic == null) return;
        WaveManager waveManager = normalMechanic.getWaveManager();
        if (waveManager == null) return;

        Wave currentWave = waveManager.getCurrentWave();
        if (currentWave == null) return;

        int currentWaveNumber = currentWave.getWaveNumber();

        if (currentWaveNumber != lastWaveNumber) {
            lastWaveNumber = currentWaveNumber;

            darkWorld.spawnWaveGraves();

            GameMenuController.showAnnouncement("Necromancy stirs the graves...");
            darkWorld.triggerNecromancy();

            spawnZombiesFromNecromancy(darkWorld);
        }
    }

    private void spawnZombiesFromNecromancy(DarkAgesWorld world) {
        Cell[][] grid = world.getGrid();
        for (int r = 0; r < world.getRows(); r++) {
            for (int c = 0; c < world.getCols(); c++) {
                Cell cell = grid[r][c];
                if (cell.isNecromancyTriggered()) {
                    Zombie zombie = new ZombieFactory().createZombie("ZombieDefault");
                    if (zombie != null) {
                        zombie.setX(cell.getX());
                        zombie.setY(cell.getY());
                        world.addZombie(zombie);
                        world.registerNecromancyZombie(zombie);
                    }
                    cell.setNecromancyTriggered(false);
                }
            }
        }
    }
}
