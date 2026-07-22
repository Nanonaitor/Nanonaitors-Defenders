package com.vnano.defenders.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class DefenderConfig {
    private static final ForgeConfigSpec.Builder B=new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.IntValue PARRY_TICKS, PARRY_RECOVERY_TICKS,
        DEBUFF_TICKS, PARRY_COST, GUARD_COST;
    public static final ForgeConfigSpec.DoubleValue GUARDED_REDUCTION, MAX_REDUCTION,
        VULNERABILITY, KNOCKBACK, MOVE_PENALTY, SOUND_VOLUME;
    public static final ForgeConfigSpec.BooleanValue ALLOW_ATTACKING, BLOCK_ALL,
        BLOCK_MELEE, BLOCK_MAGIC, BLOCK_EXPLOSIONS, BLOCK_FIRE, BLOCK_FALL,
        BLOCK_DROWNING, BLOCK_ENVIRONMENTAL, BLOCK_ARMOR_BYPASSING;
    public static final ForgeConfigSpec SPEC;
    static {
        B.push("combat");
        PARRY_TICKS=B.defineInRange("parryWindowTicks",20,1,200);
        PARRY_RECOVERY_TICKS=B.defineInRange("parryRecoveryTicks",10,0,200);
        DEBUFF_TICKS=B.defineInRange("debuffTicks",40,1,72000);
        GUARDED_REDUCTION=B.defineInRange("guardedReduction",.30,0,.95);
        MAX_REDUCTION=B.defineInRange("maximumReduction",.95,0,.99);
        VULNERABILITY=B.defineInRange("vulnerabilityMultiplier",2.0,1,20);
        KNOCKBACK=B.defineInRange("parryKnockbackStrength",.8,0,5);
        MOVE_PENALTY=B.defineInRange("blockingMovementPenalty",.50,0,.95);
        SOUND_VOLUME=B.defineInRange("perfectParrySoundVolume",.8,0,4);
        ALLOW_ATTACKING=B.define("allowAttackingWhileBlocking",true);
        B.pop();
        B.push("durability");
        PARRY_COST=B.defineInRange("perfectParryCost",2,0,100);
        GUARD_COST=B.defineInRange("guardedHitCost",1,0,100);
        B.pop();
        B.push("damage_types");
        BLOCK_ALL=B.define("blockAllDamage",false);
        BLOCK_MELEE=B.define("blockDirectMelee",true);
        BLOCK_MAGIC=B.define("blockMagic",false);
        BLOCK_EXPLOSIONS=B.define("blockExplosions",false);
        BLOCK_FIRE=B.define("blockFire",false);
        BLOCK_FALL=B.define("blockFall",false);
        BLOCK_DROWNING=B.define("blockDrowning",false);
        BLOCK_ENVIRONMENTAL=B.define("blockEnvironmental",false);
        BLOCK_ARMOR_BYPASSING=B.define("blockArmorBypassing",false);
        B.pop();
        SPEC=B.build();
    }
    private DefenderConfig() {}
}
