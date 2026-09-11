package com.pvz2.view.graphic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.PlantType;
import com.pvz2.models.plant.AnimationDurations;
import com.pvz2.models.plant.Plant;
import com.pvz2.models.plant.PlantAnimationClips;
import com.pvz2.models.plant.components.ArmorComponent;
import com.pvz2.models.world.GameState;
import com.pvz2.models.world.GameWorld;
import com.pvz2.view.util.DamageFlashShader;
import com.pvz2.view.util.LawnGrid;
import com.pvz2.view.table.ZombiesTable;
import com.pvz2.view.screen.PlantsCollectionMenuScreen;
import pvz.libpvz.pam.PamPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PlantGraphic {

    private Plant plant;
    private float worldX;
    private float worldY;

    private String normalPamPath;
    private String imitatorPamPath;
    private String sheepPamPath;
    private String burnPamPath = "768/INITIAL/EFFECTS/PLANT_BURNT/PLANT_BURNT.PAM";
    private float burnAnimTime = 0f;

    private String pamPath;
    private String initialClip;
    private String currentClip;
    private float animTime = 0f;
    private boolean isLoop = true;

    private final String plantFoodBgPamPath;
    private float plantFoodBgAnimTime = 0f;
    private boolean inPlantFoodBg = false;

    private boolean isSheepState = false;
    private boolean isRevertingSheep = false;
    private final Random random = new Random();

    private static final Map<String, TextureRegion> FROST_REGION_CACHE = new HashMap<>();

    private static final Map<PlantType, String[]> ARMOR_KEYS_MAP = new HashMap<>();
    private static final Map<PlantType, float[]> ARMORS_SCALE = new HashMap<>();

    private static final String FROST_33_KEY = "IMAGE_EFFECTS_FROSTBITE_CHILL_PLANT_FROSTBITE_CHILL_PLANT_153X62";
    private static final String FROST_66_KEY = "IMAGE_EFFECTS_FROSTBITE_CHILL_PLANT_FROSTBITE_CHILL_PLANT_153X79";

    private static final String[] ICE_HEALTH_KEYS = {
        "IMAGE_EFFECTS_FROSTBITE_ICE_BLOCK_PLANT_FROSTBITE_ICE_BLOCK_PLANT_164X169",
        "IMAGE_EFFECTS_FROSTBITE_ICE_BLOCK_PLANT_FROSTBITE_ICE_BLOCK_PLANT_167X172_5",
        "IMAGE_EFFECTS_FROSTBITE_ICE_BLOCK_PLANT_FROSTBITE_ICE_BLOCK_PLANT_167X172_4",
        "IMAGE_EFFECTS_FROSTBITE_ICE_BLOCK_PLANT_FROSTBITE_ICE_BLOCK_PLANT_167X172_3",
        "IMAGE_EFFECTS_FROSTBITE_ICE_BLOCK_PLANT_FROSTBITE_ICE_BLOCK_PLANT_167X172_2",
        "IMAGE_EFFECTS_FROSTBITE_ICE_BLOCK_PLANT_FROSTBITE_ICE_BLOCK_PLANT_167X172_1"
    };

    static {
        ARMOR_KEYS_MAP.put(PlantType.WALL_NUT, new String[]{
            "IMAGE_PLANT_WALLNUT_WALLNUT_120X139",
            "IMAGE_PLANT_WALLNUT_WALLNUT_120X100",
            "IMAGE_PLANT_WALLNUT_WALLNUT_110X51"
        });
        ARMOR_KEYS_MAP.put(PlantType.TALL_NUT, new String[]{
            "IMAGE_PLANT_TALLNUT_TALLNUT_138X157",
            "IMAGE_PLANT_TALLNUT_TALLNUT_137X141",
            "IMAGE_PLANT_TALLNUT_TALLNUT_138X123"
        });
        ARMOR_KEYS_MAP.put(PlantType.ENDURIAN, new String[]{
            "IMAGE_PLANT_ENDURIAN_ENDURIAN_172X182",
            "IMAGE_PLANT_ENDURIAN_ENDURIAN_172X129",
            "IMAGE_PLANT_ENDURIAN_ENDURIAN_164X76"
        });
        ARMOR_KEYS_MAP.put(PlantType.EXPLODE_O_NUT, new String[]{
            "IMAGE_PLANT_EXPLODEONUT_EXPLODEONUT_131X166",
            "IMAGE_PLANT_EXPLODEONUT_EXPLODEONUT_131X100",
            "IMAGE_PLANT_EXPLODEONUT_EXPLODEONUT_124X48"
        });
        ARMOR_KEYS_MAP.put(PlantType.SUN_BEAN, new String[]{
            "IMAGE_PLANT_SUNBEAN_SUNBEAN_109X66"
        });
        ARMOR_KEYS_MAP.put(PlantType.PUMPKIN, new String[]{
            "IMAGE_PLANT_PUMPKIN_PUMPKIN_187X79",
            "IMAGE_PLANT_PUMPKIN_PUMPKIN_187X78",
            "IMAGE_PLANT_PUMPKIN_PUMPKIN_165X77",
            "IMAGE_PLANT_PUMPKIN_PUMPKIN_165X39"
        });

        ARMORS_SCALE.put(PlantType.WALL_NUT, new float[]{0.75f, 0.93f, 15});
        ARMORS_SCALE.put(PlantType.TALL_NUT, new float[]{0.8f, 0.8f, 70});
        ARMORS_SCALE.put(PlantType.ENDURIAN, new float[]{0.95f, 0.95f, 23});
        ARMORS_SCALE.put(PlantType.EXPLODE_O_NUT, new float[]{0.85f, 1.05f, 27});
        ARMORS_SCALE.put(PlantType.PUMPKIN, new float[]{1.1f, 0.6f, -20});
        ARMORS_SCALE.put(PlantType.SUN_BEAN, new float[]{0.5f, 0.5f, 70});
    }

    private Plant.State lastState = Plant.State.IDLE;

    public PlantGraphic(Plant plant, PamPlayer pamPlayer) {
        this.plant = plant;

        this.normalPamPath = PlantsCollectionMenuScreen.getPlantAnimAddress(plant.getType());
        this.imitatorPamPath = PlantsCollectionMenuScreen.getPlantAnimAddress(PlantType.IMITATER);
        this.sheepPamPath = ZombiesTable.getZombiesAnimAddress().
            getOrDefault("Sheep", "768/FULL/ZOMBIE/SHEEP/SHEEP.PAM");

        this.pamPath = plant.isImitate() ? imitatorPamPath : normalPamPath;
        this.currentClip = plant.isImitate() ? "idle" : PlantsCollectionMenuScreen.getPlantInitialClip(plant.getType());
        this.initialClip = currentClip;

        if (normalPamPath != null && pamPlayer != null) pamPlayer.loadAsync(normalPamPath, null);
        if (imitatorPamPath != null && pamPlayer != null) pamPlayer.loadAsync(imitatorPamPath, null);
        if (sheepPamPath != null && pamPlayer != null) pamPlayer.loadAsync(sheepPamPath, null);
        if (burnPamPath != null && pamPlayer != null) pamPlayer.loadAsync(burnPamPath, null);

        this.plantFoodBgPamPath = PlantAnimationClips.getPlantFoodBackgroundPamPath();
        if (plantFoodBgPamPath != null && pamPlayer != null) {
            pamPlayer.loadAsync(plantFoodBgPamPath, null);
        }
    }

    public void update(float delta) {
        if (plant.isBurnt()) {
            burnAnimTime += delta;
            return;
        }

        GameWorld world = App.getCurrentGame();
        if (world != null && world.getState() != GameState.PLAYING) delta = 0;
        if (!plant.isFreeze()) animTime += delta;

        worldX = plant.getX();
        worldY = plant.getY();

        if (handleSheepUpdate()) return;

        pamPath = plant.isImitate() ? imitatorPamPath : normalPamPath;
        initialClip = PlantsCollectionMenuScreen.getPlantInitialClip(plant.getType());

        Plant.State state = plant.getState();
        updatePlantFoodBg(state, delta);

        if (handleStateChange(state)) return;

        if (!currentClip.equals(initialClip)) {
            float duration = AnimationDurations.getDuration(plant.getType(), currentClip, 0.5f);
            if (isLoop || animTime >= duration) playClip(initialClip, true);
        }
    }

    private boolean handleSheepUpdate() {
        if (plant.isSheep() && !isSheepState) {
            isSheepState = true;
            isRevertingSheep = false;
            pamPath = sheepPamPath;
            playClip("animation", false);
            return true;
        } else if (!plant.isSheep() && isSheepState && !isRevertingSheep) {
            isRevertingSheep = true;
            pamPath = sheepPamPath;
            playClip("animation2", false);
            return true;
        }

        if (isSheepState && !isRevertingSheep) {
            if (currentClip.equals("animation") && animTime >= 1.5f) {
                playClip("idle", true);
            } else if (currentClip.startsWith("idle") && animTime >= 2.0f) {
                String[] idles = {"idle", "idle2", "idle3"};
                playClip(idles[random.nextInt(idles.length)], true);
            }
            return true;
        }

        if (isRevertingSheep) {
            if (animTime >= 1.5f) {
                isSheepState = false;
                isRevertingSheep = false;
                pamPath = plant.isImitate() ? imitatorPamPath : normalPamPath;
                playClip(initialClip, true);
                lastState = Plant.State.IDLE;
            }
            return true;
        }
        return false;
    }

    private void updatePlantFoodBg(Plant.State state, float delta) {
        boolean isPlantFoodState = (state == Plant.State.PLANT_FOOD || state == Plant.State.PLANT_FOOD2);
        if (isPlantFoodState) {
            if (!inPlantFoodBg) {
                inPlantFoodBg = true;
                plantFoodBgAnimTime = 0f;
            } else {
                plantFoodBgAnimTime += delta;
            }
        } else {
            inPlantFoodBg = false;
        }
    }

    private boolean handleStateChange(Plant.State state) {
        boolean stateJustEntered = state != lastState;
        lastState = state;

        if (state != Plant.State.IDLE) {
            if (stateJustEntered) {
                ClipInfo info = resolveClipFor(state);
                if (info != null) playClip(info.clipName, info.loop);
            }
            return true;
        }
        return false;
    }

    private static TextureRegion cachedRegion(String key) {
        if (key == null) return null;
        return FROST_REGION_CACHE.computeIfAbsent(key,
            k -> App.getGameApp().textureBank.region(k));
    }

    private TextureRegion resolveArmorOverlay() {
        ArmorComponent armorComp = plant.getComponent(ArmorComponent.class);
        if (armorComp == null || armorComp.getArmorHp() <= 0) {
            return null;
        }

        String[] keys = ARMOR_KEYS_MAP.get(plant.getType());
        if (keys == null || keys.length == 0) {
            return null;
        }

        float remainingFraction = armorComp.getArmorHp() / (float) armorComp.getInitHp();
        int stageCount = keys.length;
        int stageIndex = Math.min(stageCount - 1, (int) ((1f - remainingFraction) * stageCount));

        return cachedRegion(keys[stageIndex]);
    }

    private TextureRegion resolveFrostOverlay() {
        if (plant.isFreeze()) {
            int stageCount = ICE_HEALTH_KEYS.length;
            float remainingFraction = plant.getIceHealth() / (float) Plant.MAX_ICE_HEALTH;
            int stageIndex = Math.min(stageCount - 1,
                (int) ((1f - remainingFraction) * stageCount));
            return cachedRegion(ICE_HEALTH_KEYS[stageIndex]);
        }
        int frozenAmount = plant.getFrozenAmount();
        if (frozenAmount >= 66) return cachedRegion(FROST_66_KEY);
        if (frozenAmount >= 33) return cachedRegion(FROST_33_KEY);
        return null;
    }

    private ClipInfo resolveClipFor(Plant.State state) {
        if (state == Plant.State.IMITATE_IDLE) return new ClipInfo("idle", true);
        if (state == Plant.State.IMITATE_ATTACK) return new ClipInfo("attack", true);
        if (state == Plant.State.SPECIAL || plant.getType() == PlantType.PUFF_SHROOM ||
            plant.getType() == PlantType.FUME_SHROOM) {
            return new ClipInfo(PlantsCollectionMenuScreen.getSpecialClip(plant.getType()), false);
        }
        if (state == Plant.State.ATTACK) {
            if (plant.getType() == PlantType.KIWIBEAST) return new ClipInfo("attack_stage3", false);
            if (plant.getType() == PlantType.FUME_SHROOM) return new ClipInfo("special", false);
            return new ClipInfo("attack", false);
        }
        if (state == Plant.State.TRIGGERED) return new
            ClipInfo(PlantAnimationClips.getTriggerClip(plant.getType()), false);
        if (state == Plant.State.UNARMED) return new
            ClipInfo(PlantAnimationClips.getUnarmedClip(plant.getType()), true);
        if(state == Plant.State.HIT_LEFT || state == Plant.State.HIT_RIGHT || state == Plant.State.HIT_RIGHT_AND_LEFT){
            return new ClipInfo(PlantAnimationClips.getHitClip(state), false);
        }
        if (state == Plant.State.SPECIAL_IDLE) return new ClipInfo("special_idle", true);
        if (state == Plant.State.INTRO) return new ClipInfo("intro", false);
        if (state == Plant.State.DAMAGE || state == Plant.State.DAMAGE2 || state == Plant.State.DAMAGE3){
            return new ClipInfo(PlantAnimationClips.getDamagedClip(plant.getType(), state), true);
        }
        if (state == Plant.State.BUSY) return new ClipInfo("busy", true);
        if (state == Plant.State.PLANT_FOOD) {
            boolean loop = PlantAnimationClips.isPlantFoodLooping(plant.getType());
            return new ClipInfo(PlantAnimationClips.getPlantFoodClip(plant.getType()), loop);
        }
        if (state == Plant.State.PLANT_FOOD2) {
            boolean loop = PlantAnimationClips.isPlantFoodLooping(plant.getType());
            return new ClipInfo(PlantAnimationClips.getPlantFood2Clip(plant.getType()), loop);
        }
        if (state == Plant.State.BUSY){
            return new ClipInfo("busy", true);}
        if (state == Plant.State.JUMP_UP_LEFT)
            return new ClipInfo(PlantAnimationClips.getJumpUpLeftClip(plant.getType()), false);
        if (state == Plant.State.JUMP_UP_RIGHT)
            return new ClipInfo(PlantAnimationClips.getJumpUpRightClip(plant.getType()), false);
        if (state == Plant.State.JUMP_DOWN_LEFT)
            return new ClipInfo(PlantAnimationClips.getJumpDownLeftClip(plant.getType()), false);
        if (state == Plant.State.JUMP_DOWN_RIGHT)
            return new ClipInfo(PlantAnimationClips.getJumpDownRightClip(plant.getType()), false);
        if (state == Plant.State.PLANT_FOOD_IDLE){
            return new ClipInfo(PlantAnimationClips.getPlantFoodIdleClip(), true);}
        if (state == Plant.State.PLANT_FOOD_INTRO) {
            return new ClipInfo(PlantAnimationClips.getPlantFoodIntroClip(plant.getType()), false);}
        if (state == Plant.State.PLANT_FOOD_OUTRO) {
            return new ClipInfo(PlantAnimationClips.getPlantFoodOutroClip(plant.getType()), false);}
        return null;
    }

    private static class ClipInfo {
        final String clipName;
        final boolean loop;
        ClipInfo(String clipName, boolean loop) {
            this.clipName = clipName;
            this.loop = loop;
        }
    }

    public void draw(SpriteBatch batch, PamPlayer pamPlayer) {
        if (plant == null || plant.isDead() || pamPath == null || pamPlayer == null) return;

        if (inPlantFoodBg && plantFoodBgPamPath != null) {
            String bgClip = PlantAnimationClips.getPlantFoodBackgroundClip();
            pamPlayer.draw(batch, plantFoodBgPamPath, bgClip, plantFoodBgAnimTime*App.getSpeed(),
                worldX+10, worldY+80, 0.8f, 0.8f,
                true);
        }

        float flashAmount = plant.getDamageFlashProgress();
        if (flashAmount > 0f && !plant.isFreeze()) {
            ShaderProgram shader = DamageFlashShader.get();
            batch.setShader(shader);
            shader.setUniformf("u_flashColor", 1f, 1f, 1f);
            shader.setUniformf("u_flashAmount", flashAmount);
        }
        pamPlayer.draw(batch, pamPath, currentClip, animTime, worldX, worldY, 0.8f, 0.8f, isLoop);
        if (flashAmount > 0f) {
            batch.setShader(null);
        }

        TextureRegion armorOverlay = resolveArmorOverlay();
        if (armorOverlay != null) {
            float w = LawnGrid.CELL_WIDTH * ARMORS_SCALE.get(plant.getType())[0];
            float h = LawnGrid.CELL_HEIGHT * ARMORS_SCALE.get(plant.getType())[1];
            batch.draw(armorOverlay, worldX - w / 2f, worldY - h / 2f + ARMORS_SCALE.get(plant.getType())[2], w, h);
        }

        TextureRegion overlay = resolveFrostOverlay();
        if (overlay != null) {
            float freezeFlashAmount = plant.getDamageFlashProgress();
            if (plant.isFreeze()){
                if (freezeFlashAmount > 0f) {
                    ShaderProgram shader = DamageFlashShader.get();
                    batch.setShader(shader);
                    shader.setUniformf("u_flashColor", 1f, 1f, 1f);
                    shader.setUniformf("u_flashAmount", freezeFlashAmount);
                }
            }
            float w = LawnGrid.CELL_WIDTH * 0.8f;
            float h = LawnGrid.CELL_HEIGHT * 0.8f;
            batch.draw(overlay, worldX - w / 2f, worldY - h / 2f, w, h);
            if (freezeFlashAmount > 0f) {
                batch.setShader(null);
            }
        }
    }

    public void playClip(String clipName, boolean loop) {
        this.currentClip = clipName;
        this.isLoop = loop;
        this.animTime = 0f;
    }

    public boolean isReadyToRemoveAfterDeath() {
        if (plant.isBurnt()) {
            return burnAnimTime >= 1.5f; // local timer, unaffected by a frozen model — safe as-is
        }
        return true; // no other death/despawn animation exists for plants — safe to remove the instant it's gone
    }

    public Plant getPlant() { return plant; }
    public int getRow() { return (int) plant.getY(); }
    public int getCol() { return (int) plant.getX(); }
    public PlantType getPlantType() { return plant.getType(); }

    public boolean isDead() {
        if (plant.isBurnt()) {
            return burnAnimTime >= 1.5f;
        }
        return plant.isDead();
    }
    public void updateModel(Plant newPlant) {
        this.plant = newPlant;
    }
    public float getWorldX() { return worldX; }
    public float getWorldY() { return worldY; }
}
