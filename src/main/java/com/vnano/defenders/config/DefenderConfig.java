package com.vnano.defenders.config;

import com.vnano.defenders.item.DefenderTier;
import java.io.File;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.config.Configuration;

public final class DefenderConfig {
    private static final Map<DefenderTier, Double> MAIN_DAMAGE = new EnumMap<>(DefenderTier.class);
    private static final Map<DefenderTier, Double> OFFHAND_DAMAGE = new EnumMap<>(DefenderTier.class);

    public static int parryWindowTicks = 20, parryRecoveryTicks = 10, debuffDurationTicks = 40;
    public static int perfectParryDurabilityCost = 2, guardedHitDurabilityCost = 1, mainHandHitDurabilityCost = 1;
    public static float guardedReduction = .30F, maximumGuardedReduction = .95F;
    public static float movementPenalty = .50F;
    public static float vulnerabilityMultiplier = 2F, parryKnockbackStrength = .8F;
    public static float finesseDamagePerLevel = .5F;
    public static float deflectionReductionPerLevel = .10F, deflectionMeleeReductionPerLevel = .05F;
    public static float perfectParrySoundVolume = .8F;
    public static float woodMovementPenaltyReduction = .10F;
    public static float stoneParryKnockbackBonus = .20F;
    public static float steelGuardedReductionBonus = .05F;
    public static float goldAttackSpeedBonus = .20F;
    public static int diamondDebuffDurationBonusTicks = 20;
    public static float sixthSenseAutoParryChance = .10F;
    public static int reflexesWindowTicksPerLevel = 2;
    public static int sixthSenseGlowDurationTicks = 100;
    public static double mainHandAttackSpeed = 1.8D;
    public static boolean allowAttackingWhileBlocking = true;
    public static boolean enableAllDefenderEnchantments = true;
    public static boolean enableFootwork = true;
    public static boolean enableReprisal = true, enableFinesse = true;
    public static boolean enableReflexes = true, enableDeflection = true, enableSixthSense = true;

    public static boolean blockAllDamage, blockMagic, blockExplosions;
    public static boolean blockFire, blockFall, blockDrowning, blockEnvironmental, blockArmorBypassing;
    public static boolean blockDirectMelee = true, retaliateAgainstIndirectAttacker;

    public static boolean enableSpartanMaterials = true, enableDefiledLands = true;
    public static boolean enableIceAndFire = true, enableSRParasites = true;
    public static boolean enableSilverUndeadDamage = true, enableMyrmexCreatureDamage = true;
    public static boolean enableVenomParryPoison = true, enableElementalMainHandEffects = true;
    public static boolean enableElementalParryEffects = true, enableLivingParryEffects = true;
    public static boolean followSrpEvolutionThreshold = true, requireParasiteKills = true;
    public static boolean showEvolutionProgress = true;
    public static int evolutionHealthOverride = 50000;

    public static float silverUndeadMainHandBonus = 2F, silverUndeadParryDamage = 1F;
    public static float myrmexNonArthropodBonus = 4F, myrmexDeathWormBonus = 4F;
    public static int venomDurationTicks = 200, venomAmplifier = 2;
    public static int flameDurationSeconds = 5, frozenDurationTicks = 200;
    public static int iceDebuffDurationTicks = 100, iceDebuffAmplifier = 2;
    public static float dragonOpposedElementBonus = 13.5F, lightningDragonBonus = 6.75F;
    public static float elementalMainHandKnockback = 1F;
    public static int livingEffectDurationTicks = 100, immalleableAmplifier = 0, viralAmplifier = 0;
    public static String[] additionalAllowedEnchantments = new String[0];

    private DefenderConfig() {}

    public static double getMainHandDamage(DefenderTier tier) {
        return MAIN_DAMAGE.containsKey(tier) ? MAIN_DAMAGE.get(tier) : tier.defaultMainHandDamage();
    }

    public static double getOffhandDamage(DefenderTier tier) {
        return OFFHAND_DAMAGE.containsKey(tier) ? OFFHAND_DAMAGE.get(tier) : tier.offhandBonus;
    }

    public static double getMainHandAttackSpeed(DefenderTier tier) {
        return mainHandAttackSpeed + (tier == DefenderTier.GOLD ? goldAttackSpeedBonus : 0D);
    }

    public static float getMovementPenalty(DefenderTier tier) {
        return Math.max(0F, movementPenalty
            - (tier == DefenderTier.WOOD ? woodMovementPenaltyReduction : 0F));
    }

    public static float getParryKnockbackStrength(DefenderTier tier) {
        return parryKnockbackStrength
            + (tier == DefenderTier.STONE ? stoneParryKnockbackBonus : 0F);
    }

    public static float getGuardedReduction(DefenderTier tier) {
        return guardedReduction
            + (tier == DefenderTier.STEEL ? steelGuardedReductionBonus : 0F);
    }

    public static int getDebuffDurationTicks(DefenderTier tier) {
        return debuffDurationTicks
            + (tier == DefenderTier.DIAMOND ? diamondDebuffDurationBonusTicks : 0);
    }

    public static boolean isAdditionalEnchantmentAllowed(Enchantment enchantment) {
        if (enchantment == null || enchantment.getRegistryName() == null) return false;
        String id = enchantment.getRegistryName().toString();
        for (String configured : additionalAllowedEnchantments) {
            if (id.equalsIgnoreCase(configured.trim())) return true;
        }
        return false;
    }

    public static boolean isDefenderEnchantmentEnabled(String id) {
        if (!enableAllDefenderEnchantments) return false;
        switch (id) {
            case "footwork": return enableFootwork;
            case "reprisal": return enableReprisal;
            case "finesse": return enableFinesse;
            case "reflexes": return enableReflexes;
            case "deflection": return enableDeflection;
            case "sixth_sense": return enableSixthSense;
            default: return false;
        }
    }

    public static void load(File file) {
        Configuration c = new Configuration(file);
        c.load();
        parryWindowTicks = c.getInt("parryWindowTicks", "combat", 20, 1, 200, "Perfect-parry window in ticks.");
        parryRecoveryTicks = c.getInt("parryRecoveryTicks", "combat", 10, 0, 200, "Cooldown before another perfect parry.");
        debuffDurationTicks = c.getInt("debuffDurationTicks", "combat", 40, 1, 1200, "Slowness and Vulnerable duration.");
        guardedReduction = c.getFloat("guardedDamageReduction", "combat", .30F, 0F, 1F, "Base reduction after the parry window.");
        maximumGuardedReduction = c.getFloat("maximumGuardedReduction", "combat", .95F, 0F, 1F, "Maximum guarded reduction.");
        movementPenalty = c.getFloat("movementSpeedPenalty", "combat", .50F, 0F, .95F, "Movement penalty while blocking.");
        vulnerabilityMultiplier = c.getFloat("vulnerabilityMultiplier", "combat", 2F, 1F, 20F, "Personal Vulnerable damage multiplier.");
        parryKnockbackStrength = c.getFloat("parryKnockbackStrength", "combat", .8F, 0F, 5F, "Perfect-parry knockback.");
        perfectParrySoundVolume = c.getFloat("perfectParrySoundVolume", "combat", .8F, 0F, 4F,
            "Volume of the perfect-parry confirmation sound; 0 disables it.");
        reflexesWindowTicksPerLevel = c.getInt("reflexesWindowTicksPerLevel", "enchantments", 2, 0, 100,
            "Ticks added to the perfect-parry window per Reflexes level.");
        deflectionReductionPerLevel = c.getFloat("deflectionReductionPerLevel", "enchantments", .10F, 0F, 1F,
            "Guarded reduction per Deflection level for damage not using the normal melee guard.");
        deflectionMeleeReductionPerLevel = c.getFloat("deflectionMeleeReductionPerLevel", "enchantments", .05F, 0F, 1F,
            "Reduction per Deflection level when stacked with normal direct-melee guarding.");
        sixthSenseAutoParryChance = c.getFloat("sixthSenseAutoParryChance", "enchantments", .10F, 0F, 1F,
            "Chance for 6th Sense to auto-parry an otherwise damaging direct-melee attack.");
        sixthSenseGlowDurationTicks = c.getInt("sixthSenseGlowDurationTicks", "enchantments", 100, 1, 72000,
            "Glowing duration applied to an attacker on any parry while 6th Sense is equipped.");
        additionalAllowedEnchantments = c.getStringList("additionalAllowedEnchantments", "enchantments",
            new String[0], "Optional enchantment registry IDs allowed on Defenders, for example modid:enchantment.");
        enableAllDefenderEnchantments = c.getBoolean("enableAllDefenderEnchantments", "enchantments", true,
            "Master switch for every Defender enchantment. Disabled enchants do not generate or provide effects.");
        enableFootwork = c.getBoolean("enableFootwork", "enchantments", true, "Enable Footwork generation and effects.");
        enableReprisal = c.getBoolean("enableReprisal", "enchantments", true, "Enable Reprisal generation and effects.");
        enableFinesse = c.getBoolean("enableFinesse", "enchantments", true, "Enable Finesse generation and effects.");
        enableReflexes = c.getBoolean("enableReflexes", "enchantments", true, "Enable Reflexes generation and effects.");
        enableDeflection = c.getBoolean("enableDeflection", "enchantments", true, "Enable Deflection generation and effects.");
        enableSixthSense = c.getBoolean("enableSixthSense", "enchantments", true, "Enable 6th Sense generation and effects.");
        allowAttackingWhileBlocking = c.getBoolean("allowAttackingWhileBlocking", "combat", true, "Allow attacks while blocking.");
        finesseDamagePerLevel = c.getFloat("finesseDamagePerLevel", "combat", .5F, 0F, 1024F,
            "Flat melee damage added per Finesse level while its Defender is equipped off hand.");
        mainHandAttackSpeed = Math.round(c.get("weapon_stats", "mainHandAttackSpeed", 1.8D,
            "Main-hand Defender attack speed.", .1D, 20D).getDouble() * 100D) / 100D;

        woodMovementPenaltyReduction = c.getFloat("woodMovementPenaltyReduction", "material_traits", .10F,
            0F, .95F, "Reduction to Wood's normal blocking movement penalty.");
        stoneParryKnockbackBonus = c.getFloat("stoneParryKnockbackBonus", "material_traits", .20F,
            0F, 5F, "Knockback strength added to Stone perfect parries.");
        goldAttackSpeedBonus = c.getFloat("goldAttackSpeedBonus", "material_traits", .20F,
            0F, 20F, "Main-hand attack speed added to Gold.");
        diamondDebuffDurationBonusTicks = c.getInt("diamondDebuffDurationBonusTicks", "material_traits", 20,
            0, 72000, "Ticks added to Diamond's Slowness and Vulnerable duration.");
        steelGuardedReductionBonus = c.getFloat("steelGuardedReductionBonus", "material_traits", .05F,
            0F, 1F, "Sustained guarded reduction added to Steel when its base guard applies.");

        perfectParryDurabilityCost = c.getInt("perfectParryCost", "durability", 2, 0, 100, "Perfect-parry durability cost.");
        guardedHitDurabilityCost = c.getInt("guardedHitCost", "durability", 1, 0, 100, "Guarded-hit durability cost.");
        mainHandHitDurabilityCost = c.getInt("mainHandHitCost", "durability", 1, 0, 100, "Main-hand hit durability cost.");

        blockAllDamage = c.getBoolean("blockAllDamage", "damage_types", false,
            "Enable base Defender logic for everything except void and projectiles.");
        blockDirectMelee = c.getBoolean("blockDirectMelee", "damage_types", true, "Enable direct melee.");
        blockMagic = c.getBoolean("blockMagic", "damage_types", false, "Enable magic.");
        blockExplosions = c.getBoolean("blockExplosions", "damage_types", false, "Enable explosions.");
        blockFire = c.getBoolean("blockFire", "damage_types", false, "Enable fire and lava.");
        blockFall = c.getBoolean("blockFall", "damage_types", false, "Enable fall damage.");
        blockDrowning = c.getBoolean("blockDrowning", "damage_types", false, "Enable drowning.");
        blockEnvironmental = c.getBoolean("blockEnvironmental", "damage_types", false, "Enable other environmental damage.");
        blockArmorBypassing = c.getBoolean("blockArmorBypassing", "damage_types", false, "Enable armor-bypassing sources.");
        retaliateAgainstIndirectAttacker = c.getBoolean("retaliateAgainstIndirectAttacker", "damage_types", false, "Debuff a projectile owner's living source.");

        enableSpartanMaterials = c.getBoolean("enableSpartanMaterials", "compatibility", true, "Enable Bronze, Silver and Steel when available.");
        enableDefiledLands = c.getBoolean("enableDefiledLands", "compatibility", true, "Enable Umbrium when available.");
        enableIceAndFire = c.getBoolean("enableIceAndFire", "compatibility", true, "Enable Dragonbone and Myrmex when available.");
        enableSRParasites = c.getBoolean("enableSRParasites", "compatibility", true, "Enable Living and Sentient when available.");

        enableSilverUndeadDamage = c.getBoolean("silverUndeadDamage", "special_effects", true, "Silver adds 2 main-hand damage and deals 1 perfect-parry damage to undead.");
        enableMyrmexCreatureDamage = c.getBoolean("myrmexCreatureDamage", "special_effects", true, "Use the native Myrmex creature bonus.");
        enableVenomParryPoison = c.getBoolean("venomParryPoison", "special_effects", true, "Myrmex Stinger hits and perfect parries apply Poison III for ten seconds.");
        enableElementalMainHandEffects = c.getBoolean("elementalMainHandEffects", "special_effects", true, "Native dragon-blood effects on hits.");
        enableElementalParryEffects = c.getBoolean("elementalPerfectParryEffects", "special_effects", true, "Native dragon-blood effects on parries.");
        enableLivingParryEffects = c.getBoolean("livingSentientParryEffects", "special_effects", true, "Immalleable and Viral parry effects.");

        silverUndeadMainHandBonus = c.getFloat("silverUndeadMainHandBonus", "special_effect_values", 2F, 0F, 1024F, "Flat main-hand bonus against undead.");
        silverUndeadParryDamage = c.getFloat("silverUndeadParryDamage", "special_effect_values", 1F, 0F, 1024F, "Silver perfect-parry damage against undead.");
        myrmexNonArthropodBonus = c.getFloat("myrmexNonArthropodBonus", "special_effect_values", 4F, 0F, 1024F, "Flat Myrmex bonus against non-arthropods.");
        myrmexDeathWormBonus = c.getFloat("myrmexDeathWormBonus", "special_effect_values", 4F, 0F, 1024F, "Separate native Myrmex bonus against Death Worms.");
        venomDurationTicks = c.getInt("venomDurationTicks", "special_effect_values", 200, 1, 72000, "Myrmex Stinger poison duration.");
        venomAmplifier = c.getInt("venomAmplifier", "special_effect_values", 2, 0, 255, "Zero-based poison amplifier; 2 is Poison III.");
        flameDurationSeconds = c.getInt("flameDurationSeconds", "special_effect_values", 5, 0, 3600, "Flamed Defender burn duration.");
        frozenDurationTicks = c.getInt("frozenDurationTicks", "special_effect_values", 200, 0, 72000, "Iced Defender native frozen duration.");
        iceDebuffDurationTicks = c.getInt("iceDebuffDurationTicks", "special_effect_values", 100, 0, 72000, "Iced Defender Slowness and Mining Fatigue duration.");
        iceDebuffAmplifier = c.getInt("iceDebuffAmplifier", "special_effect_values", 2, 0, 255, "Zero-based ice debuff amplifier; 2 is level III.");
        dragonOpposedElementBonus = c.getFloat("dragonOpposedElementBonus", "special_effect_values", 13.5F, 0F, 1024F, "Flamed/Iced bonus against the opposed dragon.");
        lightningDragonBonus = c.getFloat("lightningDragonBonus", "special_effect_values", 6.75F, 0F, 1024F, "Electric bonus against fire and ice dragons.");
        elementalMainHandKnockback = c.getFloat("elementalMainHandKnockback", "special_effect_values", 1F, 0F, 20F, "Native elemental main-hand knockback strength.");
        livingEffectDurationTicks = c.getInt("livingEffectDurationTicks", "special_effect_values", 100, 1, 72000, "Immalleable and Viral duration.");
        immalleableAmplifier = c.getInt("immalleableAmplifier", "special_effect_values", 0, 0, 255, "Zero-based Immalleable amplifier.");
        viralAmplifier = c.getInt("viralAmplifier", "special_effect_values", 0, 0, 255, "Zero-based Viral amplifier.");

        followSrpEvolutionThreshold = c.getBoolean("followSRPThreshold", "evolution", true, "Read SRP's active HP Evolve threshold.");
        evolutionHealthOverride = c.getInt("evolutionHealthOverride", "evolution", 50000, 1, Integer.MAX_VALUE, "Fallback/override evolution threshold.");
        requireParasiteKills = c.getBoolean("requireParasiteKills", "evolution", true,
            "True grants Living Defender evolution points only for SRP parasite kills; false allows all living creatures.");
        showEvolutionProgress = c.getBoolean("showEvolutionProgress", "evolution", true, "Show Living Defender progress.");

        MAIN_DAMAGE.clear();
        OFFHAND_DAMAGE.clear();
        for (DefenderTier tier : DefenderTier.values()) {
            MAIN_DAMAGE.put(tier, c.get("weapon_stats", tier.id + "MainHandDamage", tier.defaultMainHandDamage(),
                "Total main-hand damage.", 0D, 1024D).getDouble());
            OFFHAND_DAMAGE.put(tier, c.get("weapon_stats", tier.id + "OffhandBonus", tier.offhandBonus,
                "Flat offhand melee bonus.", 0D, 1024D).getDouble());
        }
        boolean removedLegacyOptions = false;
        if (c.hasKey("damage_types", "blockProjectiles")) {
            c.getCategory("damage_types").remove("blockProjectiles");
            removedLegacyOptions = true;
        }
        if (c.hasKey("combat", "fortificationReductionPerLevel")) {
            c.getCategory("combat").remove("fortificationReductionPerLevel");
            removedLegacyOptions = true;
        }
        if (c.hasKey("enchantments", "enableFortification")) {
            c.getCategory("enchantments").remove("enableFortification");
            removedLegacyOptions = true;
        }
        if (removedLegacyOptions || c.hasChanged()) c.save();
    }
}
