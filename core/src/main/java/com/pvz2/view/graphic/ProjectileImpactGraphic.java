package com.pvz2.view.graphic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.ProjectileType;
import com.pvz2.models.world.GameState;
import com.pvz2.models.world.GameWorld;
import com.pvz2.view.util.ProjectileAssets;
import pvz.libpvz.pam.PamPlayer;

public class ProjectileImpactGraphic {
    private final ProjectileType type;
    private final float x, y;
    private float animTime = 0f;

    public ProjectileImpactGraphic(ProjectileType type, float x, float y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public void update(float delta) {
        GameWorld world = App.getCurrentGame();
        if (world != null && world.getState() != GameState.PLAYING) delta = 0;
        animTime += delta;
    }

    public void draw(SpriteBatch batch, PamPlayer pamPlayer) {
        ProjectileAssets.VisualInfo info = ProjectileAssets.get(type);
        if (info == null) return;

        if (info.kind == ProjectileAssets.Kind.PAM) {
            if (info.impactClip == null) return; // no impact animation configured
            pamPlayer.draw(batch, info.impactPamPath, info.impactClip,
                animTime* App.getSpeed(), x - 20, y, false);
        } else {
            if (info.impactTextureRegionKey == null) return; // no impact art — nothing to draw
            TextureRegion region = ProjectileAssets.region(info.impactTextureRegionKey);
            if (region == null) return;
            float w = region.getRegionWidth();
            float h = region.getRegionHeight();
            batch.draw(region, x - w / 2f, y - h / 2f, w, h);
        }
    }

    public boolean isFinished(PamPlayer pamPlayer) {
        ProjectileAssets.VisualInfo info = ProjectileAssets.get(type);
        if (info == null) return true;

        if (info.kind == ProjectileAssets.Kind.PAM) {
            if (info.impactClip == null) return true; // nothing was ever drawn — finish immediately
            return pamPlayer == null || animTime >= pamPlayer.clipDurationSeconds(info.impactPamPath, info.impactClip);
        } else {
            if (info.impactTextureRegionKey == null) return true;
            return animTime >= info.impactDuration;
        }
    }
}
