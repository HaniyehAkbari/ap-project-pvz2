package com.pvz2.view.util;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pvz2.models.core.App;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.zombie.ZombieFactory;
import com.pvz2.models.zombie.state.IdleState;
import com.pvz2.models.zombie.wave.WaveManager;
import com.pvz2.view.graphic.ZombieGraphic;
import pvz.libpvz.pam.PamPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ZombiePreviewManager {
    private final List<ZombieGraphic> previewGraphics = new ArrayList<>();

    public ZombiePreviewManager(WaveManager waveManager, int rows, int cols) {
        if (waveManager == null) return;
        Random random = new Random();

        for (String alias : waveManager.getAllZombieAliases()) {
            Zombie zombie = new ZombieFactory().createZombie(alias);
            if (zombie == null) continue;

            zombie.setState(new IdleState());

            float extraCols = 0.3f + random.nextFloat() * 0.8f;
            float jitterY = (random.nextFloat() - 0.5f) * App.getCellHeight() * 0.5f;

            float x = App.getFirstCellX() + (cols + extraCols) * App.getCellWidth();
            int row = random.nextInt(rows);
            float y = App.getCellCenterY(row) + jitterY;

            zombie.setX(x);
            zombie.setY(y);

            previewGraphics.add(new ZombieGraphic(zombie));
        }
    }

    public void update(float delta, PamPlayer pamPlayer) {
        for (ZombieGraphic zg : previewGraphics) {
            zg.update(delta, pamPlayer);
        }
    }

    public void draw(SpriteBatch batch, PamPlayer pamPlayer) {
        for (ZombieGraphic zg : previewGraphics) {
            zg.draw(batch, pamPlayer);
        }
    }
}
