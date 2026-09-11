package com.pvz2.view.graphic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.pvz2.Main;
import com.pvz2.models.core.App;
import com.pvz2.models.world.ChapterWorld.DarkAgesWorld;
import com.pvz2.models.world.GameState;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.world.obstacles.Grave;
import com.pvz2.view.util.DamageFlashShader;
import pvz.libpvz.pam.PamPlayer;

public class GraveGraphic {
    private final Grave grave;
    private float animTime = 0f;
    private boolean breakStarted = false;
    private boolean breakFinished = false;

    private static final String EGYPT_PAM_PATH = "768/INITIAL/GRAVESTONES/EGYPT_HIEROGLYPH/EGYPT_HIEROGLYPH.PAM";

    private static final String DARK_NORMAL_PAM_PATH = "768/FULL/GRAVESTONES/DARK_NOOP/DARK_NOOP.PAM";
    private static final String DARK_SUN_PAM_PATH = "768/FULL/GRAVESTONES/DARK_SUN/DARK_SUN.PAM";
    private static final String DARK_PLANT_FOOD_PAM_PATH = "768/FULL/GRAVESTONES/DARK_PLANTFOOD/DARK_PLANTFOOD.PAM";

    private static final String DIRT_SPAWN_FUTURE_PAM_PATH = "768/FULL/EFFECTS/DIRT_SPAWN_FUTURE/DIRT_SPAWN_FUTURE.PAM";
    private static final String DIRT_SPAWN_ANIM_STATE = "tomb_dirt_anim";
    private static final float SPAWN_ANIM_DURATION = 0.6f;

    private final String pamPath;
    private boolean spawning;
    private float spawnAnimTime = 0f;

    public GraveGraphic(Grave grave) {
        this(grave, App.getCurrentGame(), false);
    }

    public GraveGraphic(Grave grave, GameWorld world) {
        this(grave, world, false);
    }

    public GraveGraphic(Grave grave, GameWorld world, boolean playSpawnAnimation) {
        this.grave = grave;
        this.pamPath = resolvePamPath(grave, world);
        this.spawning = playSpawnAnimation;
    }

    private String resolvePamPath(Grave grave, GameWorld world) {
        if (world instanceof DarkAgesWorld) {
            Grave.GraveType type = grave.getType();

            if (type == Grave.GraveType.SUN) {
                return DARK_SUN_PAM_PATH;
            } else if (type == Grave.GraveType.PLANT_FOOD) {
                return DARK_PLANT_FOOD_PAM_PATH;
            } else {
                return DARK_NORMAL_PAM_PATH;
            }
        }

        return EGYPT_PAM_PATH;
    }

    public void  update(float delta) {
        if (spawning) {
            spawnAnimTime += delta;
            if (spawnAnimTime >= SPAWN_ANIM_DURATION) {
                spawning = false;
                animTime = 0f;
            }
            return;
        }

        GameWorld world = App.getCurrentGame();
        if (world != null && world.getState() != GameState.PLAYING) delta = 0;
        animTime += delta;

        if (grave.isDying() && !breakStarted) {
            breakStarted = true;
            animTime = 0f;
        }

        if (breakStarted && animTime >= 0.5f) {
            breakFinished = true;
            grave.markDestroyed();
        }
    }

    public void draw(SpriteBatch batch, PamPlayer pamPlayer, Main game){
        if (breakFinished) return;

        if (spawning) {
            try {
                pamPlayer.draw(
                    batch,
                    DIRT_SPAWN_FUTURE_PAM_PATH,
                    DIRT_SPAWN_ANIM_STATE,
                    spawnAnimTime,
                    grave.getX(), grave.getY(), 1.0f, 1.0f, true
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        String animState;
        if (breakStarted) {
            animState = "damage1";
        } else {
            switch (grave.getDamageStage()) {
                case 1:  animState = "damage2"; break;
                case 2:  animState = "damage3"; break;
                default: animState = "undamaged"; break;
            }
        }
        float flashAmount = grave.getDamageFlashProgress();
        if (flashAmount > 0f) {
            ShaderProgram shader = DamageFlashShader.get();
            batch.setShader(shader);
            shader.setUniformf("u_flashColor", 1f, 1f, 1f);
            shader.setUniformf("u_flashAmount", flashAmount);
        }
        try {
            pamPlayer.draw(
                batch,
                pamPath,
                animState,
                animTime*App.getSpeed(),
                grave.getX(), grave.getY(), 1.0f, 1.0f, true
            );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (flashAmount > 0f) {
                batch.setShader(null);
            }
        }
    }

    public boolean isBreakFinished() { return breakFinished; }
    public Grave getGrave() { return grave; }
}
