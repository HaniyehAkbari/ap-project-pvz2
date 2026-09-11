package com.pvz2.view.graphic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.pvz2.models.core.App;
import com.pvz2.models.world.GameState;
import com.pvz2.models.world.GameWorld;
import com.pvz2.models.zombie.Zombie;
import com.pvz2.models.zombie.zombiesType.ArmoredZombie;
import com.pvz2.models.zombie.zombiesType.BarrelRollerZombie;
import com.pvz2.models.zombie.zombiesType.FishermanZombie;
import com.pvz2.models.zombie.zombiesType.RangedZombie;
import com.pvz2.models.zombie.zombiesType.SnorkelZombie;
import com.pvz2.models.zombie.zombiesType.SunStealerZombie;
import com.pvz2.view.util.DamageFlashShader;
import com.pvz2.view.table.ZombiesTable;
import pvz.libpvz.pam.PamPlayer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class ZombieGraphic {
    private static final Set<String> NON_LOOPING_CLIPS = Set.of(
        "spinup", "spindown", "fly_start", "fly_end", "fire", "cannon_fire", "sheep",
        "power", "toss", "power_up", "power_down", "die", "cast", "reel", "intro"
    );

    private static final String[] OCTOPUS_IDLE_VARIANTS = {"idle", "idle2", "idle3", "idle4", "idle5"};
    private static final Random RANDOM = new Random();

    private static final String SNORKEL_VISIBLE_PART_WHEN_SUBMERGED = "zombie_snorkeler_skull_01";
    private static final float SNORKEL_SUBMERGED_Y_OFFSET = 50f;

    /** How fast the displayed position chases the latest server-confirmed position.
     *  Higher = snappier but more visible pop on each snapshot; lower = smoother but laggier. */
    private static final float NETWORK_LERP_SPEED = 15f;

    private Zombie zombie;
    private final String pamPath;
    private final HashMap<String, Boolean> visibilities;

    private float animTime = 0f;
    private String currentClip;
    private boolean isLoop = true;

    private float idleVariantTimer = 0f;
    private float nextIdleSwitchTime = randomIdleInterval();

    private boolean armorVisualHidden = false;


    // Interpolated render position. Offline (updateModel() never called): always equals the
    // live Zombie's real position, zero lag, identical to the old behavior. Online (updateModel()
    // called once per GAME_STATE snapshot): smoothly chases toward each new snapshot's position
    // instead of snapping, since snapshots arrive at ~20Hz but rendering happens up to ~60Hz.
    private float displayX, displayY;
    private boolean networked = false;

    public ZombieGraphic(Zombie zombie) {
        this.zombie = zombie;

        String lookupName = (zombie.getSpecificName() != null)
            ? zombie.getSpecificName()
            : zombie.getName().name();

        lookupName = App.getArmoredZombieName(lookupName);
        this.pamPath = ZombiesTable.getZombiesAnimAddress().get(lookupName);

        HashMap<String, Boolean> sharedVisibilities = ZombiesTable.getZombiesVisibilities().get(lookupName);
        this.visibilities = (sharedVisibilities != null) ? new HashMap<>(sharedVisibilities) : null;

        if (zombie instanceof FishermanZombie) {
            this.currentClip = ((FishermanZombie) zombie).getFishermanState().getAnimName();
        } else {
            this.currentClip = zombie.getAnimationClip() != null ? resolveClip(zombie.getAnimationClip()) : "walk";
        }

        this.displayX = zombie.getX();
        this.displayY = zombie.getY();
    }

    public void update(float delta, PamPlayer pamPlayer) {
        checkArmorBroken();

        String dieClip = resolveClip("die");

        if (zombie.isDead() && !currentClip.equals(dieClip)) {
            playClip(dieClip, false);
        }

        GameWorld world = App.getCurrentGame();
        if (world != null && world.getState() != GameState.PLAYING) delta = 0;
        animTime += delta;

        updateDisplayPosition(delta);

        if (!zombie.isDead() && !currentClip.equals("die")) {
            if (zombie instanceof RangedZombie rangedZombie && rangedZombie.isThrowing()) {
                String throwClip = resolveClip(rangedZombie.getThrowClipName());
                if (!throwClip.equals(currentClip)) {
                    playClip(throwClip, false);
                }
            } else {
                String targetClip;

                if (zombie instanceof FishermanZombie fishermanZombie) {
                    targetClip = fishermanZombie.getFishermanState().getAnimName();
                } else {
                    targetClip = resolveClip(zombie.getAnimationClip());
                }

                if (isOctopusThrower() && "idle".equals(targetClip)) {
                    updateOctopusIdle(delta);
                } else {
                    if (!targetClip.equals(currentClip)) {
                        boolean loop = !NON_LOOPING_CLIPS.contains(targetClip);
                        playClip(targetClip, loop);
                    }
                }
            }
        }

        if (pamPath != null) {
            pamPlayer.loadAsync(pamPath, null);
        }
    }

    private void updateDisplayPosition(float delta) {
        if (!networked) {
            // offline: same live object every frame, already exact — no smoothing needed or wanted
            displayX = zombie.getX();
            displayY = zombie.getY();
            return;
        }

        float t = Math.min(1f, NETWORK_LERP_SPEED * delta);
        displayX = MathUtils.lerp(displayX, zombie.getX(), t);
        displayY = MathUtils.lerp(displayY, zombie.getY(), t);
    }

    private void checkArmorBroken() {
        if (armorVisualHidden || visibilities == null) return;

        if (zombie instanceof ArmoredZombie armoredZombie && armoredZombie.getArmorHealth() <= 0) {
            for (String key : visibilities.keySet()) {
                visibilities.put(key, false);
            }
            armorVisualHidden = true;
        }
    }

    private void updateOctopusIdle(float delta) {
        boolean alreadyInIdleFamily = Arrays.asList(OCTOPUS_IDLE_VARIANTS).contains(currentClip);

        if (!alreadyInIdleFamily) {
            switchToRandomIdleVariant();
            return;
        }

        idleVariantTimer += delta;
        if (idleVariantTimer >= nextIdleSwitchTime) {
            switchToRandomIdleVariant();
        }
    }

    private void switchToRandomIdleVariant() {
        String variant = OCTOPUS_IDLE_VARIANTS[RANDOM.nextInt(OCTOPUS_IDLE_VARIANTS.length)];
        playClip(variant, true);
        idleVariantTimer = 0f;
        nextIdleSwitchTime = randomIdleInterval();
    }

    private static float randomIdleInterval() {
        return 2f + RANDOM.nextFloat() * 2f;
    }

    private boolean isOctopusThrower() {
        return zombie instanceof RangedZombie rangedZombie && "OCTOPUS".equals(rangedZombie.getProjectileType());
    }

    private String resolveClip(String baseClip) {
        String specificName = zombie.getSpecificName();
        if ("ZombieNewspaper".equals(specificName) && baseClip.equals("idle")) {
            return "idle_newspaper";
        }
        if (zombie instanceof BarrelRollerZombie barrelZombie && !barrelZombie.isBarrelIntact()) {
            return baseClip + "2";
        }
        return baseClip;
    }

    public void draw(SpriteBatch batch, PamPlayer pamPlayer) {
        if (pamPath == null) return;
        float renderX = displayX;
        float renderY = displayY;
        float flashAmount = zombie.getDamageFlashProgress();
        float[] flashColor = {1f, 1f, 1f};
        if (flashAmount <= 0f && zombie.isNearEndLine() && !zombie.isDead()) {
            long t = System.currentTimeMillis();
            boolean blinkOn = (t / 200) % 2 == 0;
            if (blinkOn) {
                flashAmount = 0.3f;
                flashColor = new float[]{1f, 0f, 0f};
            }
        }
        if (flashAmount > 0f) {
            ShaderProgram shader = DamageFlashShader.get();
            batch.setShader(shader);
            shader.setUniformf("u_flashColor", flashColor[0], flashColor[1], flashColor[2]);
            shader.setUniformf("u_flashAmount", flashAmount);
        }
        float scaleX = zombie.getSpeed() < 0 ? -0.8f : 0.8f;
        if (zombie instanceof SnorkelZombie snorkel && snorkel.isUnderwater()) {
            pamPlayer.drawPart(batch, pamPath, currentClip, animTime, renderX,
                renderY - SNORKEL_SUBMERGED_Y_OFFSET, SNORKEL_VISIBLE_PART_WHEN_SUBMERGED);
        } else if (visibilities != null) {
            pamPlayer.draw(batch, pamPath, currentClip, animTime, renderX,
                renderY, scaleX, 0.8f, isLoop, visibilities);
        } else {
            pamPlayer.draw(batch, pamPath, currentClip, animTime, renderX,
                renderY, scaleX, 0.8f, isLoop);
        }
        if (flashAmount > 0f) {
            batch.setShader(null);
        }
        if (zombie instanceof SunStealerZombie stealer && !stealer.isRa()) {
            if ("power_down".equals(stealer.getTurquoiseAnimState())) {
                try {
                    pamPlayer.draw(
                        batch,
                        SunStealerZombie.LASER_PAM_PATH,
                        SunStealerZombie.LASER_CLIP,
                        animTime,
                        renderX - 250f,
                        renderY,
                        1.2f, 1.0f, false);
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void playClip(String clipName, boolean loop) {
        if (!this.currentClip.equals(clipName)) {
            this.currentClip = clipName;
            this.isLoop = loop;
            this.animTime = 0f;
        }
    }

    public String getCurrentClip() {
        return currentClip;
    }

    public boolean isDeathAnimationFinished() {
        return zombie.isDead() && currentClip.equals(resolveClip("die")) && animTime >= 1.5f;
    }

    public boolean isNetworkedDeathAnimationFinished() {
        return currentClip.equals("die") && animTime >= 1.5f;
    }

    public void updateModel(Zombie newZombie) {
        this.zombie = newZombie;
        this.networked = true;
    }

    public Zombie getZombie() {
        return zombie;
    }
}
