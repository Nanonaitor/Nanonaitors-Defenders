package com.vnano.defenders.item;

public enum DefenderTier {
    WOOD("wood",96,1,3,15,null), STONE("stone",160,1.25,4,5,null),
    COPPER("copper",224,1.5,4.5,12,null), GOLD("gold",64,1.25,3,22,null),
    IRON("iron",320,2,4.5,14,null), DIAMOND("diamond",960,3,5.5,10,null),
    NETHERITE("netherite",1280,3.5,6.5,15,null),
    SILVER("silver",460,2.25,4,16,"iceandfire"),
    BRONZE("bronze",200,1.5,4.5,12,"spartanweaponry"),
    STEEL("steel",480,2.5,5,14,"spartanweaponry"),
    UMBRIUM("umbrium",320,2,4.5,20,"defiled_lands_preborn"),
    DRAGONBONE("dragonbone",1660,3.25,6,22,"iceandfire"),
    FLAMED_DRAGONBONE("flamed_dragonbone",2000,3.5,7.5,22,"iceandfire"),
    ICED_DRAGONBONE("iced_dragonbone",2000,3.5,7.5,22,"iceandfire"),
    ELECTRIC_DRAGONBONE("electric_dragonbone",2000,3.5,7.5,22,"iceandfire"),
    DESERT_MYRMEX("desert_myrmex",600,2.5,4,8,"iceandfire"),
    JUNGLE_MYRMEX("jungle_myrmex",600,2.5,4,8,"iceandfire"),
    DESERT_VENOM("desert_venom",600,2.5,4,8,"iceandfire"),
    JUNGLE_VENOM("jungle_venom",600,2.5,4,8,"iceandfire"),
    LIVING("living",1000,4,12,1,"srp_spartans"),
    SENTIENT("sentient",1000,4.5,16,1,"srp_spartans");
    public final String id, requiredMod; public final int durability, enchantability;
    public final double offhandBonus, mainHandDamage;
    DefenderTier(String id,int durability,double offhandBonus,double mainHandDamage,int enchantability,String requiredMod){
        this.id=id;this.durability=durability;this.offhandBonus=offhandBonus;this.mainHandDamage=mainHandDamage;this.enchantability=enchantability;this.requiredMod=requiredMod;
    }
    public boolean isMyrmex(){return name().contains("MYRMEX")||name().contains("VENOM");}
    public boolean isVenom(){return this==DESERT_VENOM||this==JUNGLE_VENOM;}
    public boolean isDragonBlooded(){return this==FLAMED_DRAGONBONE||this==ICED_DRAGONBONE||this==ELECTRIC_DRAGONBONE;}
    public double defaultOffhandAttackSpeed(){
        return switch(this){
            case WOOD, UMBRIUM, DIAMOND, LIVING -> .15;
            case STONE, STEEL -> .05;
            case GOLD -> .25;
            case SILVER, DESERT_MYRMEX, JUNGLE_MYRMEX, DESERT_VENOM, JUNGLE_VENOM, SENTIENT -> .20;
            default -> .10;
        };
    }
}
