package com.pvz2.view.graphic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.ProjectileType;
import com.pvz2.models.projectile.Projectile;
import com.pvz2.models.world.GameState;
import com.pvz2.models.world.GameWorld;
import com.pvz2.view.util.ProjectileAssets;
import com.pvz2.view.audios.GameSFX;
import com.pvz2.view.audios.SFXManager;
import pvz.libpvz.pam.PamPlayer;

public class ProjectileGraphic {
    /** How fast the displayed position chases the latest server-confirmed position.
     *  Higher = snappier but more visible pop on each snapshot; lower = smoother but laggier.
     *  Same value as ZombieGraphic — keep them matching so zombies and their own projectiles
     *  don't visibly drift apart from each other under interpolation. */
    private static final float NETWORK_LERP_SPEED = 15f;

    private Projectile projectile;
    private ProjectileType type;
    private float animTime = 0f;
    private final int generation;
    private int lastKnownPierce = -1;

    private float lastX;
    private float lastY;
    private boolean networked = false;

    public ProjectileGraphic(Projectile projectile, int generation) {
        this.projectile = projectile;
        this.type = projectile.getType();
        this.lastX = projectile.getX();
        this.lastY = projectile.getY();
        this.generation = generation;
    }

    public void update(float delta) {
        GameWorld world = App.getCurrentGame();
        if (world != null && world.getState() != GameState.PLAYING) delta = 0;
        animTime += delta;
        updateDisplayPosition(delta);
    }

    private void updateDisplayPosition(float delta) {
        if (!networked) {
            // offline: same live object every frame, already exact — no smoothing needed or wanted
            lastX = projectile.getX();
            lastY = projectile.getY();
            return;
        }

        float t = Math.min(1f, NETWORK_LERP_SPEED * delta);
        lastX = MathUtils.lerp(lastX, projectile.getX(), t);
        lastY = MathUtils.lerp(lastY, projectile.getY(), t);
    }

    public void draw(SpriteBatch batch, PamPlayer pamPlayer) {
        if (projectile.getType() != type) type = projectile.getType();
        ProjectileAssets.VisualInfo info = ProjectileAssets.get(type);
        if (info == null) return;

        float drawX, drawY;

        if (type == ProjectileType.STAR){
            drawX = lastX;
            drawY = lastY;
        }
        else if (type == ProjectileType.ROTOBAGA_PROJECTILE){
            drawX = lastX-40;
            drawY = lastY+30;
        }
        else{
            drawY = type.movement.equals("STRAIGHT") ? lastY + 20 : lastY + 15;
            drawX = type.movement.equals("STRAIGHT") ? lastX : lastX - 30;
        }
        if (type == ProjectileType.CACTUS || type == ProjectileType.CACTUS_SPECIAL) drawY += 15;
        if (type == ProjectileType.SMALL_SHROOM) drawY -= 15;
        if (type == ProjectileType.FUME || type == ProjectileType.FUME_SPECIAL){
            drawX += 60;
            drawY -= 20;
        }
        if (info.kind == ProjectileAssets.Kind.PAM) {
            pamPlayer.draw(batch, info.flightPamPath, info.flightClip,
                animTime* App.getSpeed(), drawX, drawY, true);
        } else {
            TextureRegion region = ProjectileAssets.region(info.textureRegionKey);
            if (region == null) return;
            float w = region.getRegionWidth();
            float h = region.getRegionHeight();
            batch.draw(region, drawX - w / 2f, drawY - h / 2f, w, h);
        }
    }

    public float getLastX() { return lastX; }
    public float getLastY() { return lastY; }
    public ProjectileType getType() { return type; }

    public void updateModel(Projectile newProjectile) {
        if (lastKnownPierce >= 0 && newProjectile.getPierce() < lastKnownPierce) {
            SFXManager.getInstance().playSound(GameSFX.SPLAT);
        }
        lastKnownPierce = newProjectile.getPierce();
        this.projectile = newProjectile;
        this.networked = true;
    }

    public int getGeneration() {
        return generation;
    }
}
