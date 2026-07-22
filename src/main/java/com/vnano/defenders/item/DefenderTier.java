package com.vnano.defenders.item;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public enum DefenderTier {
    WOOD("wood",96,1.0,3.0,15,ItemTags.PLANKS),
    STONE("stone",160,1.25,4.0,5,ItemTags.STONE_TOOL_MATERIALS),
    COPPER("copper",224,1.5,4.5,12,ItemTags.COPPER_TOOL_MATERIALS),
    GOLD("gold",64,1.25,3.0,22,ItemTags.GOLD_TOOL_MATERIALS),
    IRON("iron",320,2.0,4.5,14,ItemTags.IRON_TOOL_MATERIALS),
    DIAMOND("diamond",960,3.0,5.5,10,ItemTags.DIAMOND_TOOL_MATERIALS),
    NETHERITE("netherite",1280,3.5,6.5,15,ItemTags.NETHERITE_TOOL_MATERIALS);

    public final String id;
    public final int durability, enchantability;
    public final double offhandBonus, mainHandDamage;
    public final TagKey<Item> repairItems;
    DefenderTier(String id,int durability,double offhandBonus,double mainHandDamage,
                 int enchantability,TagKey<Item> repairItems) {
        this.id=id; this.durability=durability; this.offhandBonus=offhandBonus;
        this.mainHandDamage=mainHandDamage; this.enchantability=enchantability;
        this.repairItems=repairItems;
    }
    public double offhandSpeed() {
        return switch(this) {
            case WOOD, DIAMOND -> .15;
            case STONE -> .05;
            case GOLD -> .25;
            default -> .10;
        };
    }
}
