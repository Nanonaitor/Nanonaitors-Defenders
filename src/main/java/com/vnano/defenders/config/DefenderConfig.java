package com.vnano.defenders.config;

import com.vnano.defenders.item.DefenderTier;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

public final class DefenderConfig {
    private static final ForgeConfigSpec.Builder B = new ForgeConfigSpec.Builder();
    private static final Map<DefenderTier, ForgeConfigSpec.DoubleValue> MAIN_DAMAGE = new EnumMap<>(DefenderTier.class);
    private static final Map<DefenderTier, ForgeConfigSpec.DoubleValue> OFFHAND_DAMAGE = new EnumMap<>(DefenderTier.class);
    private static final Map<DefenderTier, ForgeConfigSpec.DoubleValue> OFFHAND_SPEED = new EnumMap<>(DefenderTier.class);

    public static final ForgeConfigSpec.IntValue PARRY_TICKS;
    public static final ForgeConfigSpec.IntValue PARRY_RECOVERY_TICKS;
    public static final ForgeConfigSpec.IntValue DEBUFF_TICKS;
    public static final ForgeConfigSpec.DoubleValue GUARDED_REDUCTION;
    public static final ForgeConfigSpec.DoubleValue MAX_REDUCTION;
    public static final ForgeConfigSpec.DoubleValue VULNERABILITY;
    public static final ForgeConfigSpec.DoubleValue PARRY_KNOCKBACK;
    public static final ForgeConfigSpec.DoubleValue BLOCK_MOVE_PENALTY;
    public static final ForgeConfigSpec.DoubleValue PARRY_SOUND_VOLUME;
    public static final ForgeConfigSpec.BooleanValue ALLOW_ATTACKING_WHILE_BLOCKING;
    public static final ForgeConfigSpec.IntValue PARRY_DURABILITY_COST;
    public static final ForgeConfigSpec.IntValue GUARDED_DURABILITY_COST;
    public static final ForgeConfigSpec.IntValue MAINHAND_DURABILITY_COST;

    public static final ForgeConfigSpec.BooleanValue BLOCK_ALL_DAMAGE;
    public static final ForgeConfigSpec.BooleanValue BLOCK_DIRECT_MELEE;
    public static final ForgeConfigSpec.BooleanValue BLOCK_MAGIC;
    public static final ForgeConfigSpec.BooleanValue BLOCK_EXPLOSIONS;
    public static final ForgeConfigSpec.BooleanValue BLOCK_FIRE;
    public static final ForgeConfigSpec.BooleanValue BLOCK_FALL;
    public static final ForgeConfigSpec.BooleanValue BLOCK_DROWNING;
    public static final ForgeConfigSpec.BooleanValue BLOCK_ENVIRONMENTAL;
    public static final ForgeConfigSpec.BooleanValue BLOCK_ARMOR_BYPASSING;

    public static final ForgeConfigSpec.BooleanValue ENABLE_ALL_ENCHANTMENTS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_FOOTWORK;
    public static final ForgeConfigSpec.BooleanValue ENABLE_REPRISAL;
    public static final ForgeConfigSpec.BooleanValue ENABLE_FINESSE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_REFLEXES;
    public static final ForgeConfigSpec.BooleanValue ENABLE_DEFLECTION;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SIXTH_SENSE;
    public static final ForgeConfigSpec.DoubleValue FINESSE_DAMAGE_PER_LEVEL;
    public static final ForgeConfigSpec.IntValue REFLEXES_TICKS_PER_LEVEL;
    public static final ForgeConfigSpec.DoubleValue DEFLECTION_REDUCTION_PER_LEVEL;
    public static final ForgeConfigSpec.DoubleValue DEFLECTION_MELEE_REDUCTION_PER_LEVEL;
    public static final ForgeConfigSpec.DoubleValue SIXTH_SENSE_CHANCE;
    public static final ForgeConfigSpec.IntValue SIXTH_SENSE_GLOW_TICKS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_ENCHANTMENTS;

    public static final ForgeConfigSpec.DoubleValue WOOD_MOVEMENT_BONUS;
    public static final ForgeConfigSpec.DoubleValue STONE_KNOCKBACK_BONUS;
    public static final ForgeConfigSpec.DoubleValue GOLD_MAINHAND_SPEED_BONUS;
    public static final ForgeConfigSpec.IntValue DIAMOND_DEBUFF_BONUS_TICKS;
    public static final ForgeConfigSpec.DoubleValue STEEL_GUARD_BONUS;
    public static final ForgeConfigSpec.DoubleValue MAINHAND_ATTACK_SPEED;

    public static final ForgeConfigSpec.IntValue LIVING_EVOLUTION;
    public static final ForgeConfigSpec.BooleanValue REQUIRE_PARASITE_KILLS;
    public static final ForgeConfigSpec.BooleanValue SHOW_EVOLUTION_PROGRESS;

    public static final ForgeConfigSpec SPEC;

    static {
        B.push("combat");
        PARRY_TICKS = B.defineInRange("parryWindowTicks", 20, 1, 200);
        PARRY_RECOVERY_TICKS = B.defineInRange("parryRecoveryTicks", 10, 0, 200);
        DEBUFF_TICKS = B.defineInRange("debuffTicks", 40, 1, 72000);
        GUARDED_REDUCTION = B.defineInRange("guardedReduction", .30, 0, .95);
        MAX_REDUCTION = B.defineInRange("maximumReduction", .95, 0, .99);
        VULNERABILITY = B.defineInRange("vulnerabilityMultiplier", 2.0, 1, 20);
        PARRY_KNOCKBACK = B.defineInRange("parryKnockbackStrength", .8, 0, 5);
        BLOCK_MOVE_PENALTY = B.defineInRange("blockingMovementPenalty", .50, 0, .95);
        PARRY_SOUND_VOLUME = B.defineInRange("perfectParrySoundVolume", .8, 0, 4);
        ALLOW_ATTACKING_WHILE_BLOCKING = B.define("allowAttackingWhileBlocking", true);
        B.pop();

        B.push("durability");
        PARRY_DURABILITY_COST = B.defineInRange("perfectParryCost", 2, 0, 100);
        GUARDED_DURABILITY_COST = B.defineInRange("guardedHitCost", 1, 0, 100);
        MAINHAND_DURABILITY_COST = B.defineInRange("mainHandHitCost", 1, 0, 100);
        B.pop();

        B.push("damage_types");
        BLOCK_ALL_DAMAGE = B.comment("Enables base guarding for all supported non-projectile damage.").define("blockAllDamage", false);
        BLOCK_DIRECT_MELEE = B.define("blockDirectMelee", true);
        BLOCK_MAGIC = B.define("blockMagic", false);
        BLOCK_EXPLOSIONS = B.define("blockExplosions", false);
        BLOCK_FIRE = B.define("blockFire", false);
        BLOCK_FALL = B.define("blockFall", false);
        BLOCK_DROWNING = B.define("blockDrowning", false);
        BLOCK_ENVIRONMENTAL = B.define("blockEnvironmental", false);
        BLOCK_ARMOR_BYPASSING = B.define("blockArmorBypassing", false);
        B.pop();

        B.push("enchantments");
        ENABLE_ALL_ENCHANTMENTS = B.define("enableAllDefenderEnchantments", true);
        ENABLE_FOOTWORK = B.define("enableFootwork", true);
        ENABLE_REPRISAL = B.define("enableReprisal", true);
        ENABLE_FINESSE = B.define("enableFinesse", true);
        ENABLE_REFLEXES = B.define("enableReflexes", true);
        ENABLE_DEFLECTION = B.define("enableDeflection", true);
        ENABLE_SIXTH_SENSE = B.define("enableSixthSense", true);
        FINESSE_DAMAGE_PER_LEVEL = B.defineInRange("finesseDamagePerLevel", .5, 0, 1024);
        REFLEXES_TICKS_PER_LEVEL = B.defineInRange("reflexesWindowTicksPerLevel", 2, 0, 100);
        DEFLECTION_REDUCTION_PER_LEVEL = B.defineInRange("deflectionReductionPerLevel", .10, 0, 1);
        DEFLECTION_MELEE_REDUCTION_PER_LEVEL = B.defineInRange("deflectionMeleeReductionPerLevel", .05, 0, 1);
        SIXTH_SENSE_CHANCE = B.defineInRange("sixthSenseAutoParryChance", .10, 0, 1);
        SIXTH_SENSE_GLOW_TICKS = B.defineInRange("sixthSenseGlowTicks", 100, 1, 72000);
        ADDITIONAL_ENCHANTMENTS = B.defineListAllowEmpty(List.of("additionalAllowedEnchantments"), List::of,
            value -> value instanceof String text && ResourceLocation.tryParse(text) != null);
        B.pop();

        B.push("material_traits");
        WOOD_MOVEMENT_BONUS = B.defineInRange("woodMovementPenaltyReduction", .10, 0, .95);
        STONE_KNOCKBACK_BONUS = B.defineInRange("stoneParryKnockbackBonus", .20, 0, 5);
        GOLD_MAINHAND_SPEED_BONUS = B.defineInRange("goldMainHandAttackSpeedBonus", .20, 0, 20);
        DIAMOND_DEBUFF_BONUS_TICKS = B.defineInRange("diamondDebuffDurationBonusTicks", 20, 0, 72000);
        STEEL_GUARD_BONUS = B.defineInRange("steelGuardedReductionBonus", .05, 0, 1);
        B.pop();

        B.push("weapon_stats");
        MAINHAND_ATTACK_SPEED = B.defineInRange("mainHandAttackSpeed", 1.8, .1, 20);
        for (DefenderTier tier : DefenderTier.values()) {
            MAIN_DAMAGE.put(tier, B.defineInRange(tier.id + "MainHandDamage", tier.mainHandDamage, 0, 1024));
            OFFHAND_DAMAGE.put(tier, B.defineInRange(tier.id + "OffhandBonus", tier.offhandBonus, 0, 1024));
            OFFHAND_SPEED.put(tier, B.defineInRange(tier.id + "OffhandAttackSpeed", tier.defaultOffhandAttackSpeed(), 0, 20));
        }
        B.pop();

        B.push("evolution");
        LIVING_EVOLUTION = B.defineInRange("livingEvolutionThreshold", 50000, 1, 100000000);
        REQUIRE_PARASITE_KILLS = B.define("requireParasiteKills", true);
        SHOW_EVOLUTION_PROGRESS = B.define("showEvolutionProgress", true);
        B.pop();

        SPEC = B.build();
    }

    public static double mainHandDamage(DefenderTier tier) { return MAIN_DAMAGE.get(tier).get(); }
    public static double offhandDamage(DefenderTier tier) { return OFFHAND_DAMAGE.get(tier).get(); }
    public static double offhandSpeed(DefenderTier tier) { return OFFHAND_SPEED.get(tier).get(); }
    public static double mainHandSpeed(DefenderTier tier) {
        return MAINHAND_ATTACK_SPEED.get() + (tier == DefenderTier.GOLD ? GOLD_MAINHAND_SPEED_BONUS.get() : 0);
    }
    public static double movementPenalty(DefenderTier tier) {
        return Math.max(0, BLOCK_MOVE_PENALTY.get() - (tier == DefenderTier.WOOD ? WOOD_MOVEMENT_BONUS.get() : 0));
    }
    public static double knockback(DefenderTier tier) {
        return PARRY_KNOCKBACK.get() + (tier == DefenderTier.STONE ? STONE_KNOCKBACK_BONUS.get() : 0);
    }
    public static int debuffTicks(DefenderTier tier) {
        return DEBUFF_TICKS.get() + (tier == DefenderTier.DIAMOND ? DIAMOND_DEBUFF_BONUS_TICKS.get() : 0);
    }
    public static double guardedReduction(DefenderTier tier) {
        return GUARDED_REDUCTION.get() + (tier == DefenderTier.STEEL ? STEEL_GUARD_BONUS.get() : 0);
    }
    public static boolean enchantmentEnabled(String id) {
        if (!ENABLE_ALL_ENCHANTMENTS.get()) return false;
        return switch (id) {
            case "footwork" -> ENABLE_FOOTWORK.get();
            case "reprisal" -> ENABLE_REPRISAL.get();
            case "finesse" -> ENABLE_FINESSE.get();
            case "reflexes" -> ENABLE_REFLEXES.get();
            case "deflection" -> ENABLE_DEFLECTION.get();
            case "sixth_sense" -> ENABLE_SIXTH_SENSE.get();
            default -> false;
        };
    }
    public static boolean additionalEnchantmentAllowed(Enchantment enchantment) {
        ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
        return id != null && ADDITIONAL_ENCHANTMENTS.get().stream().anyMatch(id.toString()::equalsIgnoreCase);
    }

    private DefenderConfig() {}
}
