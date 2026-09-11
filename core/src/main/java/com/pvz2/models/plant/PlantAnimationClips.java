package com.pvz2.models.plant;

import com.pvz2.models.enums.PlantFamily;
import com.pvz2.models.enums.PlantType;

public class PlantAnimationClips {
    public static String getSpecialClip(PlantType type) {
        if (type.hasTag("Wramp-up") || type == PlantType.PUFF_SHROOM) {
            return type == PlantType.KIWIBEAST ? "attack_stage3" : "special_stage3";
        }
        if (type == PlantType.GOLD_BLOOM) return "attack";
        return "special";
    }

    public static String getUnarmedClip(PlantType type) {
        return "plant_idle";
    }

    public static String getTriggerClip(PlantType type) {
        if (type == PlantType.SQUASH) return "jump_down_right";
        if (type == PlantType.DOOM_SHROOM) return "stage2_explode";
        return "attack";
    }

    public static boolean isPlantFoodLooping(PlantType type) {
        return type.family != PlantFamily.LOBBER;
    }

    public static String getExplosionPamPath(PlantType type) {
        if (type == PlantType.POTATO_MINE) return "768/INITIAL/EFFECTS/POTATOMINE_EXPLOSION/POTATOMINE_EXPLOSION.PAM";
        if (type == PlantType.PRIMAL_POTATO_MINE) return
        "768/INITIAL/EFFECTS/PRIMAL_POTATOMINE_EXPLOSION/PRIMAL_POTATOMINE_EXPLOSION.PAM";
        if (type == PlantType.CHERRY_BOMB) return "768/FULL/EFFECTS/CHERRYBOMB_EXPLOSION_TOP" +
            "/CHERRYBOMB_EXPLOSION_TOP.PAM";
        if (type == PlantType.JALAPENO) return "768/INITIAL/EFFECTS/JALAPENO_FIRE/JALAPENO_FIRE.PAM";
        return null;
    }

    public static String getHitClip(Plant.State plantState){
        return switch (plantState){
            case HIT_RIGHT -> "attack";
            case HIT_LEFT -> "attack2";
            case HIT_RIGHT_AND_LEFT -> "attack3";
            default -> "idle";
        };
    }

    public static String getJumpUpLeftClip(PlantType type) { return "jump_up_left"; }
    public static String getJumpUpRightClip(PlantType type) { return "jump_up_right"; }
    public static String getJumpDownLeftClip(PlantType type) { return "jump_down_left"; }
    public static String getJumpDownRightClip(PlantType type) { return "jump_down_right"; }

    public static String getExplosionClip(PlantType type) {
        if (type == PlantType.POTATO_MINE || type == PlantType.PRIMAL_POTATO_MINE) return "animation";
        if (type == PlantType.CHERRY_BOMB) return "explosion";
        if (type == PlantType.JALAPENO) return "idle";
        return null;
    }

    public static String getPlantFoodClip(PlantType type) {
        if (type.family == PlantFamily.WALL_NUTS) return "idle";
        if (type == PlantType.KIWIBEAST) return "plantfood_stage3";
        if (type == PlantType.XSHOT) return "plantfood_on";
        if (type == PlantType.BOWLING_BULB) return "plantfood_idle";
        if (type == PlantType.SEA_SHROOM) return "pf";
        return "plantfood";
    }
    public static String getPlantFood2Clip(PlantType type) {
        if (type == PlantType.PEA_POD || type == PlantType.CITRON ||
            type == PlantType.POISON_PEASHOOTER || type == PlantType.MEGA_GATLING
        ||  type == PlantType.FUME_SHROOM) return "plantfood";
        if (type == PlantType.CACTUS) return "attack_plantfood";
        return "plantfood2";
    }

    public static String getPlantFoodIntroClip(PlantType type) { return "plantfood_on"; }
    public static String getPlantFoodOutroClip(PlantType type) { return "plantfood_off"; }

    public static String getPlantFoodBackgroundPamPath() {
        return "768/INITIAL/EFFECTS/PLANTFOOD_FX/PLANTFOOD_FX.PAM";
    }

    public static String getPlantFoodBackgroundClip() {
        return "plantfood";
    }

    public static String getPlantFoodIdleClip() {
        return "plantfood";
    }

    public static String getDamagedClip(PlantType type, Plant.State state) {
        String num = switch (state){
            case DAMAGE2 -> "2";
            case DAMAGE3 -> "3";
            default -> "";
        };
        if (type == PlantType.PUMPKIN) return "idle" + num;
        if (type == PlantType.GARLIC || type == PlantType.SWEET_POTATO) return "idle_damage" + num;
        return "damage" + num;
    }
}
