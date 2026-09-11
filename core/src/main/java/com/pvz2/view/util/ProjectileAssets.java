package com.pvz2.view.util;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pvz2.models.core.App;
import com.pvz2.models.enums.ProjectileType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ProjectileAssets {

    public enum Kind { PAM, TEXTURE }

    public static class VisualInfo {
        public final Kind kind;

        public final String flightPamPath;
        public final String flightClip;
        public final String impactPamPath;
        public final String impactClip;

        public final String textureRegionKey;
        public final String impactTextureRegionKey;
        public final float impactDuration;

        private VisualInfo(Kind kind, String pamPath, String flightClip, String impactPamPath, String impactClip,
                           String textureRegionKey, String impactTextureRegionKey, float impactDuration) {
            this.kind = kind;
            this.flightPamPath = pamPath;
            this.flightClip = flightClip;
            this.impactPamPath = impactPamPath;
            this.impactClip = impactClip;
            this.textureRegionKey = textureRegionKey;
            this.impactTextureRegionKey = impactTextureRegionKey;
            this.impactDuration = impactDuration;
        }

        public static VisualInfo pam(String flightPamPath, String flightClip,
                                     String impactPamPath, String impactClip) {
            return new VisualInfo(Kind.PAM, flightPamPath, flightClip,impactPamPath, impactClip,
                null, null, 0f);
        }

        public static VisualInfo texture(String regionKey) {
            return new VisualInfo(Kind.TEXTURE, null, null, null, null, regionKey, null, 0f);
        }

        public static VisualInfo textureWithImpact(String regionKey, String impactRegionKey, float impactDuration) {
            return new VisualInfo(Kind.TEXTURE, null, null, null, null, regionKey, impactRegionKey,
                impactDuration);
        }
    }

    private static final Map<ProjectileType, VisualInfo> VISUALS = new EnumMap<>(ProjectileType.class);
    private static final Map<String, TextureRegion> REGION_CACHE = new HashMap<>();

    static {
        VISUALS.put(ProjectileType.PEA, VisualInfo.pam("768/INITIAL/EFFECTS/T_PEA_PROJECTILE/T_PEA_PROJECTILE.PAM",
            "animation", "768/INITIAL/EFFECTS/SPLAT_PEA/SPLAT_PEA.PAM", "animation"));
        VISUALS.put(ProjectileType.ICE_PEA, VisualInfo.pam("768/INITIAL/EFFECTS/T_SNOW_PEA/T_SNOW_PEA.PAM",
            "animation", "768/INITIAL/EFFECTS/SPLAT_SNOW_PEA/SPLAT_SNOW_PEA.PAM", "animation"));
        VISUALS.put(ProjectileType.ROTOBAGA_PROJECTILE, VisualInfo.pam("768/FULL/EFFECTS/ROTORUTABAGA_PROJECTILE1" +
                "/ROTORUTABAGA_PROJECTILE1.PAM", "animation",
            "768/FULL/EFFECTS/ROTORUTABAGA_PROJECTILE_HIT/ROTORUTABAGA_PROJECTILE_HIT.PAM","animation"));
        VISUALS.put(ProjectileType.CABBAGE, VisualInfo.pam(
            "768/INITIAL/EFFECTS/T_CABBAGEPULT_PROJECTILE/T_CABBAGEPULT_PROJECTILE.PAM",
            "animation", "768/INITIAL/EFFECTS/SPLAT_CABBAGEPULT/SPLAT_CABBAGEPULT.PAM", "animation"));
        VISUALS.put(ProjectileType.CACTUS, VisualInfo.pam("768/INITIAL/EFFECTS/CACTUS_PROJECTILE/CACTUS_PROJECTILE.PAM",
            "idle", "768/INITIAL/EFFECTS/CACTUS_PROJECTILE_HIT/CACTUS_PROJECTILE_HIT.PAM", "animation"));
        VISUALS.put(ProjectileType.CACTUS_SPECIAL,
            VisualInfo.pam("768/INITIAL/EFFECTS/CACTUS_PROJECTILE_PLANTFOOD/CACTUS_PROJECTILE_PLANTFOOD.PAM",
            "idle", "768/INITIAL/EFFECTS/CACTUS_PROJECTILE_HIT/CACTUS_PROJECTILE_HIT.PAM", "animation"));
        VISUALS.put(ProjectileType.CITRON, VisualInfo.pam("768/FULL/EFFECTS/CITRON_CITRUS_ORB/CITRON_CITRUS_ORB.PAM",
            "Citron_Citrus_Orb", "768/FULL/EFFECTS/CITRON_CITRUS_ORB_HIT/CITRON_CITRUS_ORB_HIT.PAM", "animation"));
        VISUALS.put(ProjectileType.FIRE_PEA, VisualInfo.pam("768/INITIAL/EFFECTS/T_FIRE_PEA/T_FIRE_PEA.PAM",
            "animation", "768/INITIAL/EFFECTS/T_SPLAT_FIRE_PEA/T_SPLAT_FIRE_PEA.PAM", "animation"));
        VISUALS.put(ProjectileType.FUME,VisualInfo.pam("768/INITIAL/EFFECTS/FUMESHROOM_BUBBLES/FUMESHROOM_BUBBLES.PAM",
            "special", null, null));
        VISUALS.put(ProjectileType.FUME_SPECIAL,
            VisualInfo.pam("768/INITIAL/EFFECTS/FUMESHROOM_BUBBLES/FUMESHROOM_BUBBLES.PAM",
            "plantfood", null, null));
        VISUALS.put(ProjectileType.GIANT_PEA,
            VisualInfo.pam("768/INITIAL/EFFECTS/REPEATER_PLANTFOOD_GIANTPEA/REPEATER_PLANTFOOD_GIANTPEA.PAM",
            "animation", "768/INITIAL/EFFECTS/SPLAT_GIANTPEA/SPLAT_GIANTPEA.PAM", "animation"));
        VISUALS.put(ProjectileType.GOO,
            VisualInfo.pam("768/INITIAL/EFFECTS/GOOPEASHOOTER_PROJECTILES/GOOPEASHOOTER_PROJECTILES.PAM",
            "projectile_t1", "768/INITIAL/EFFECTS/GOOPEASHOOTER_PROJECTILES/GOOPEASHOOTER_PROJECTILES.PAM", "hit_t1"));
        VISUALS.put(ProjectileType.GRAPE,
            VisualInfo.pam("768/INITIAL/EFFECTS/GRAPESHOT_PROJECTILE/GRAPESHOT_PROJECTILE.PAM",
                "animation_forward", "768/INITIAL/EFFECTS/GRAPESHOT_HIT/GRAPESHOT_HIT.PAM",
                "animation"));
        VISUALS.put(ProjectileType.GOO_SPECIAL,
            VisualInfo.pam("768/INITIAL/EFFECTS/GOOPEASHOOTER_PLANTFOOD/GOOPEASHOOTER_PLANTFOOD.PAM",
                "animation", "768/INITIAL/EFFECTS/GOOPEASHOOTER_PROJECTILES/GOOPEASHOOTER_PROJECTILES.PAM", "hit_t3"));
        VISUALS.put(ProjectileType.ICE_MELON,
            VisualInfo.pam("768/FULL/EFFECTS/T_WINTERMELON_PROJECTILE/T_WINTERMELON_PROJECTILE.PAM",
                "animation", "768/FULL/EFFECTS/T_SPLAT_WINTERMELON/T_SPLAT_WINTERMELON.PAM",
                "animation"));
        VISUALS.put(ProjectileType.KERNEL,
            VisualInfo.pam("768/INITIAL/EFFECTS/T_KERNALPULT_PROJECTILE/T_KERNALPULT_PROJECTILE.PAM",
                "animation", "768/INITIAL/EFFECTS/SPLAT_KERNALPULT_KERNAL/SPLAT_KERNALPULT_KERNAL.PAM",
                "animation"));
        VISUALS.put(ProjectileType.LARGE_BULB,
            VisualInfo.pam("768/FULL/EFFECTS/BOWLINGBULB_PROJECTILE3/BOWLINGBULB_PROJECTILE3.PAM",
                "animation", null, null));
        VISUALS.put(ProjectileType.MEDIUM_BULB,
            VisualInfo.pam("768/FULL/EFFECTS/BOWLINGBULB_PROJECTILE2/BOWLINGBULB_PROJECTILE2.PAM",
                "animation", null, null));
        VISUALS.put(ProjectileType.SMALL_BULB,
            VisualInfo.pam("768/FULL/EFFECTS/BOWLINGBULB_PROJECTILE1/BOWLINGBULB_PROJECTILE1.PAM",
                "animation", null, null));
        VISUALS.put(ProjectileType.MELON,
            VisualInfo.pam("768/INITIAL/EFFECTS/T_MELON_PROJECTILE/T_MELON_PROJECTILE.PAM",
                "animation", "768/INITIAL/EFFECTS/T_SPLAT_MELONPULT/T_SPLAT_MELONPULT.PAM",
                "animation"));
        VISUALS.put(ProjectileType.PEPPER,
            VisualInfo.pam("768/FULL/EFFECTS/PEPPERPULT_PROJECTILE/PEPPERPULT_PROJECTILE.PAM",
                "animation", "768/FULL/EFFECTS/PEPPERPULT_PROJECTILE_SPLAT/PEPPERPULT_PROJECTILE_SPLAT.PAM",
                "animation"));
        VISUALS.put(ProjectileType.PLASMA,
            VisualInfo.pam("768/FULL/EFFECTS/CITRON_PLANTFOOD_ORB/CITRON_PLANTFOOD_ORB.PAM",
                "Plantfood_Citron_Plasma_Orb", "768/FULL/EFFECTS/CITRON_PLANTFOOD_HIT/CITRON_PLANTFOOD_HIT.PAM",
                "animation"));
        VISUALS.put(ProjectileType.SEA_SHROOM,
            VisualInfo.pam("768/FULL/EFFECTS/SEASHROOM_PROJECTILE/SEASHROOM_PROJECTILE.PAM",
                "animation", "768/FULL/EFFECTS/SEASHOOTER_FX/SEASHOOTER_FX.PAM",
                "animation"));
        VISUALS.put(ProjectileType.SMALL_SHROOM,
            VisualInfo.pam("768/INITIAL/EFFECTS/T_PUFFSHROOM_PROJECTILE/T_PUFFSHROOM_PROJECTILE.PAM",
                "animation", "768/INITIAL/EFFECTS/T_PUFFSHROOM_HIT/T_PUFFSHROOM_HIT.PAM",
                "animation"));
        VISUALS.put(ProjectileType.SPECIAL_BULB,
            VisualInfo.pam("768/FULL/EFFECTS/BOWLINGBULB_PLANTFOOD_PROJECTILE/BOWLINGBULB_PLANTFOOD_PROJECTILE.PAM",
                "animation", "768/FULL/EFFECTS/BOWLINGBULB_PLANTFOOD_PROJECTILE/BOWLINGBULB_PLANTFOOD_PROJECTILE.PAM",
                "explosion"));
        VISUALS.put(ProjectileType.STAR,
            VisualInfo.pam("768/INITIAL/EFFECTS/T_STARFRUIT_PROJECTILE/T_STARFRUIT_PROJECTILE.PAM",
                "animation", "768/INITIAL/EFFECTS/T_STARFRUIT_PROJECTILE_HIT/T_STARFRUIT_PROJECTILE_HIT.PAM",
                "idle"));
        VISUALS.put(ProjectileType.SPECIAL_CABBAGE,
            VisualInfo.pam("768/INITIAL/EFFECTS/CABBAGEPULT_PLANTFOOD_PROJECTILE/CABBAGEPULT_PLANTFOOD_PROJECTILE.PAM",
                "plantfood_cabbage",
                "768/INITIAL/EFFECTS/CABBAGEPULT_PLANTFOOD_PROJECTILE/CABBAGEPULT_PLANTFOOD_PROJECTILE.PAM",
                "plantfood_cabbageExplode"));
        VISUALS.put(ProjectileType.SPECIAL_ICE_MELON,
            VisualInfo.pam("768/FULL/EFFECTS/T_WINTERMELON_PROJECTILE/T_WINTERMELON_PROJECTILE.PAM",
                "animation", "768/FULL/EFFECTS/WINTERMELON_EXPLODE/WINTERMELON_EXPLODE.PAM",
                "plantfood_WintermelonExplode"));
        VISUALS.put(ProjectileType.SPECIAL_MELON,
            VisualInfo.pam("768/INITIAL/EFFECTS/T_MELON_PROJECTILE/T_MELON_PROJECTILE.PAM",
                "animation", "768/INITIAL/EFFECTS/MELON_EXPLODE/MELON_EXPLODE.PAM",
                "plantfood_MelonExplode"));
        VISUALS.put(ProjectileType.SPECIAL_PEPPER,
            VisualInfo.pam("768/FULL/EFFECTS/T_PEPPERPULT_PROJECTILE/T_PEPPERPULT_PROJECTILE.PAM",
                "animation", "768/FULL/EFFECTS/T_PEPPERPULT_PROJECTILE_SPLAT/T_PEPPERPULT_PROJECTILE_SPLAT.PAM",
                "animation"));

        VISUALS.put(ProjectileType.BUTTER, VisualInfo.textureWithImpact(
            "IMAGE_EFFECTS_KERNELPULT_PROJECTILE_BUTTER", "IMAGE_EFFECTS_KERNELPULT_PROJECTILE_BUTTER", 5f));
    }

    public static VisualInfo get(ProjectileType type) {
        return VISUALS.get(type);
    }

    public static TextureRegion region(String key) {
        if (key == null) return null;
        return REGION_CACHE.computeIfAbsent(key, k -> App.getGameApp().textureBank.region(k));
    }

}
