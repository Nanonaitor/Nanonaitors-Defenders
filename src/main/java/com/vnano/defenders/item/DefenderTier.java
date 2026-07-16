package com.vnano.defenders.item;

public enum DefenderTier {
    WOOD("wood", 96, 1.0D, 4.0D, 15, CompatFamily.VANILLA, "ore:plankWood"),
    STONE("stone", 160, 1.25D, 5.0D, 5, CompatFamily.VANILLA, "ore:cobblestone"),
    GOLD("gold", 64, 1.25D, 4.0D, 22, CompatFamily.VANILLA, "ore:ingotGold"),
    IRON("iron", 320, 2.0D, 6.0D, 14, CompatFamily.VANILLA, "ore:ingotIron"),
    DIAMOND("diamond", 960, 3.0D, 7.0D, 10, CompatFamily.VANILLA, "ore:gemDiamond"),

    SILVER("silver", 460, 2.25D, 5.5D, 16, CompatFamily.SPARTAN, "ore:ingotSilver"),
    BRONZE("bronze", 200, 1.5D, 6.0D, 12, CompatFamily.SPARTAN, "ore:ingotBronze"),
    STEEL("steel", 480, 2.5D, 6.5D, 14, CompatFamily.SPARTAN, "ore:ingotSteel"),
    UMBRIUM("umbrium", 320, 2.0D, 6.0D, 14, CompatFamily.DEFILED, "defiledlands:umbrium_ingot"),

    DRAGONBONE("dragonbone", 1660, 3.25D, 8.0D, 22, CompatFamily.ICE_AND_FIRE, "iceandfire:dragonbone"),
    FLAMED_DRAGONBONE("flamed_dragonbone", 2000, 3.5D, 9.5D, 22, CompatFamily.FIRE_DRAGONBONE, "iceandfire:dragonbone"),
    ICED_DRAGONBONE("iced_dragonbone", 2000, 3.5D, 9.5D, 22, CompatFamily.ICE_DRAGONBONE, "iceandfire:dragonbone"),
    ELECTRIC_DRAGONBONE("electric_dragonbone", 2000, 3.5D, 9.5D, 22, CompatFamily.LIGHTNING_DRAGONBONE, "iceandfire:dragonbone"),

    DESERT_MYRMEX("desert_myrmex", 600, 2.5D, 5.0D, 8, CompatFamily.DESERT_MYRMEX, "iceandfire:myrmex_desert_chitin"),
    JUNGLE_MYRMEX("jungle_myrmex", 600, 2.5D, 5.0D, 8, CompatFamily.JUNGLE_MYRMEX, "iceandfire:myrmex_jungle_chitin"),
    DESERT_VENOM("desert_venom", 600, 2.5D, 5.0D, 8, CompatFamily.DESERT_MYRMEX, "iceandfire:myrmex_desert_chitin"),
    JUNGLE_VENOM("jungle_venom", 600, 2.5D, 5.0D, 8, CompatFamily.JUNGLE_MYRMEX, "iceandfire:myrmex_jungle_chitin"),

    LIVING("living", 1000, 4.0D, 15.0D, 1, CompatFamily.SRP, "srparasites:infectious_blade_fragment"),
    SENTIENT("sentient", 1000, 4.5D, 20.0D, 1, CompatFamily.SRP, "srparasites:infectious_blade_fragment");

    public enum CompatFamily {
        VANILLA, SPARTAN, DEFILED, ICE_AND_FIRE,
        FIRE_DRAGONBONE, ICE_DRAGONBONE, LIGHTNING_DRAGONBONE,
        DESERT_MYRMEX, JUNGLE_MYRMEX, SRP
    }

    public final String id;
    public final int durability;
    public final double offhandBonus;
    public final double swordDamage;
    public final int enchantability;
    public final CompatFamily family;
    public final String repairIngredient;

    DefenderTier(String id, int durability, double offhandBonus, double swordDamage,
                 int enchantability, CompatFamily family, String repairIngredient) {
        this.id = id;
        this.durability = durability;
        this.offhandBonus = offhandBonus;
        this.swordDamage = swordDamage;
        this.enchantability = enchantability;
        this.family = family;
        this.repairIngredient = repairIngredient;
    }

    public double defaultMainHandDamage() {
        return Math.floor(swordDamage * 0.80D * 2.0D) / 2.0D;
    }

    public boolean isMyrmex() {
        return this == DESERT_MYRMEX || this == JUNGLE_MYRMEX
            || this == DESERT_VENOM || this == JUNGLE_VENOM;
    }

    public boolean isVenom() {
        return this == DESERT_VENOM || this == JUNGLE_VENOM;
    }

    public boolean isDragonBlooded() {
        return this == FLAMED_DRAGONBONE || this == ICED_DRAGONBONE
            || this == ELECTRIC_DRAGONBONE;
    }
}
