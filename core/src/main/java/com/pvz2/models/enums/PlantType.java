package com.pvz2.models.enums;

import java.util.List;

import static com.pvz2.models.enums.PlantFamily.*;

public enum PlantType {
    SUNFLOWER(SUN_PRODUCER, 50, 5, "Day"),
    TWIN_SUNFLOWER(SUN_PRODUCER, 125, 15, "Day"),
    SUN_SHROOM(SUN_PRODUCER, 25, 5, "Shroom", "Wramp-up", "Night"),
    PRIMAL_SUNFLOWER(SUN_PRODUCER, 75, 5),
    GOLD_BLOOM(SUN_PRODUCER, 0, 75),
    PEASHOOTER(SHOOTER, 100, 5, "Pea"),
    REPEATER(SHOOTER, 200, 5, "Pea"),
    THREEPEATER(SHOOTER, 300, 5, "Pea"),
    SNOW_PEA(SHOOTER, 150, 5, "Pea", "Ice"),
    XSHOT(SHOOTER, 150, 5),
    PEA_POD(SHOOTER, 125, 5, "Pea", "Stack"),
    SPLIT_PEA(SHOOTER, 125, 5, "Pea"),
    CITRON(SHOOTER, 350, 5, "Charge"),
    BOWLING_BULB(SHOOTER, 200, 5, "Charge"),
    CACTUS(STRIKE_THROUGH, 175, 5),
    FIRE_PEASHOOTER(SHOOTER, 175, 5, "Fire", "Pea"),
    STARFRUIT(SHOOTER, 150, 5),
    POISON_PEASHOOTER(SHOOTER, 125, 5, "Poison"),
    MEGA_GATLING(SHOOTER, 400, 5, "Pea"),
    SEA_SHROOM(SHOOTER, 0, 15, "Shroom", "Water"),
    PUFF_SHROOM(SHOOTER, 0, 5, "Shroom"),
    FUME_SHROOM(STRIKE_THROUGH, 125, 5, "Shroom"),
    CABBAGE_PULT(LOBBER, 100, 5),
    KERNEL_PULT(LOBBER, 100, 5),
    MELON_PULT(LOBBER, 325, 5, "AoE"),
    WINTER_MELON(LOBBER, 500, 5, "AoE", "Ice"),
    PEPPER_PULT(LOBBER, 200, 5, "AoE", "Fire"),
    POTATO_MINE(EXPLOSIVE, 25, 25, "Trap", "Charge"),
    PRIMAL_POTATO_MINE(EXPLOSIVE, 50, 5, "Trap", "Charge"),
    CHERRY_BOMB(EXPLOSIVE, 150, 35),
    SQUASH(EXPLOSIVE, 50, 20, "Trap"),
    GRAPESHOT(EXPLOSIVE, 150, 35),
    JALAPENO(EXPLOSIVE, 125, 35, "Fire"),
    DOOM_SHROOM(EXPLOSIVE, 125, 15, "Shroom"),
    TANGLE_KELP(EXPLOSIVE, 25, 15, "Trap"),
    ICEBURG(EXPLOSIVE, 0, 20, "Trap", "Ice"),
    BONK_CHOY(MELEE, 150, 5),
    PHAT_BEET(MELEE, 150, 5, "AoE"),
    CHOMPER(MELEE, 150, 5),
    WASABI_WHIP(MELEE, 150, 5, "Fire"),
    KIWIBEAST(MELEE, 175, 5, "AoE", "Wramp-up"),
    WALL_NUT(WALL_NUTS, 50, 20),
    TALL_NUT(WALL_NUTS, 125, 20),
    ENDURIAN(WALL_NUTS, 100, 15),
    GARLIC(WALL_NUTS, 50, 20, "Move Zombies"),
    SWEET_POTATO(WALL_NUTS, 150, 20, "Move Zombies"),
    EXPLODE_O_NUT(WALL_NUTS, 50, 20, "Explosive"),
    GIANT_WALLNUT(WALL_NUTS, 0, 0),
    PUMPKIN(WALL_NUTS, 150, 20, "Stack"),
    SUN_BEAN(WALL_NUTS, 50, 20, "Sun"),
    TORCHWOOD(MODIFIER, 175, 5, "Fire"),
    MAGNET_SHROOM(HOMING, 100, 15, "Shroom", "Magic"),
    IMITATER(MODIFIER, 0, 0),
    ICE_SHROOM(EXPLOSIVE, 75, 50, "Shroom", "Ice"),
    LILY_PAD(MODIFIER, 25, 5, "Water", "Stack"),
    HOT_POTATO(EXPLOSIVE, 0, 5, "Fire"),
    GRAVE_BUSTER(EXPLOSIVE, 0, 10),
    ENLIGHTEN_MINT(SUN_PRODUCER, 0, 85),
    APPEASE_MINT(SHOOTER, 0, 85),
    ARMA_MINT(LOBBER, 0, 85),
    BOMBARD_MINT(EXPLOSIVE, 0, 85),
    ENFORCE_MINT(MELEE, 0, 85),
    REINFORCE_MINT(WALL_NUTS, 0, 85),
    ENCHANT_MINT(MODIFIER, 0, 85),
    SPEAR_MINT(STRIKE_THROUGH, 0, 85),
    CONTAIN_MINT(HOMING, 0, 85),
    MARIGOLD(WALL_NUTS, 0, 0); // گل معمولی گلخانه

    public final PlantFamily family;
    public final int baseSunCost;
    public final int baseCoolDown;
    public final List<String> tags;

    PlantType(PlantFamily family, int baseSunCost, int baseCoolDown, String... tags) {
        this.family = family;
        this.baseSunCost = baseSunCost;
        this.baseCoolDown = baseCoolDown;
        this.tags = List.of(tags);
    }

    public boolean hasAnyTag() {
        return !tags.isEmpty();
    }

    public boolean hasTag(String tag){
        return this.tags.contains(tag);
    }
}
