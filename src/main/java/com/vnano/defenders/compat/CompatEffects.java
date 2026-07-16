package com.vnano.defenders.compat;

import com.vnano.defenders.config.DefenderConfig;
import com.vnano.defenders.item.DefenderTier;
import java.lang.reflect.Method;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class CompatEffects {
    private CompatEffects() {}

    public static float modifyMainHandDamage(DefenderTier tier, EntityLivingBase target, float damage) {
        if (tier == DefenderTier.SILVER && DefenderConfig.enableSilverUndeadDamage
            && target.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
            damage += DefenderConfig.silverUndeadMainHandBonus;
        }
        if (tier.isMyrmex() && DefenderConfig.enableMyrmexCreatureDamage) {
            if (target.getCreatureAttribute() != EnumCreatureAttribute.ARTHROPOD) damage += DefenderConfig.myrmexNonArthropodBonus;
            if (isEntity(target, "deathworm")) damage += DefenderConfig.myrmexDeathWormBonus;
        }
        if (tier == DefenderTier.FLAMED_DRAGONBONE && isEntity(target, "icedragon")) damage += DefenderConfig.dragonOpposedElementBonus;
        if (tier == DefenderTier.ICED_DRAGONBONE && isEntity(target, "firedragon")) damage += DefenderConfig.dragonOpposedElementBonus;
        if (tier == DefenderTier.ELECTRIC_DRAGONBONE
            && (isEntity(target, "firedragon") || isEntity(target, "icedragon"))) damage += DefenderConfig.lightningDragonBonus;
        return damage;
    }

    public static void applyMainHandHit(DefenderTier tier, EntityLivingBase target, EntityLivingBase attacker) {
        if (tier.isVenom() && DefenderConfig.enableVenomParryPoison) {
            target.addPotionEffect(new PotionEffect(MobEffects.POISON, DefenderConfig.venomDurationTicks, DefenderConfig.venomAmplifier));
        }
        if (!DefenderConfig.enableElementalMainHandEffects) return;
        applyElement(tier, target, attacker, false);
    }

    public static void applyPerfectParry(DefenderTier tier, EntityLivingBase attacker, EntityLivingBase defender) {
        if (tier == DefenderTier.SILVER && DefenderConfig.enableSilverUndeadDamage
            && attacker.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
            attacker.attackEntityFrom(DamageSource.GENERIC, DefenderConfig.silverUndeadParryDamage);
        }
        if (tier.isVenom() && DefenderConfig.enableVenomParryPoison) {
            attacker.addPotionEffect(new PotionEffect(MobEffects.POISON, DefenderConfig.venomDurationTicks, DefenderConfig.venomAmplifier));
        }
        if (DefenderConfig.enableElementalParryEffects) applyElement(tier, attacker, defender, true);
        if (DefenderConfig.enableLivingParryEffects) {
            if (tier == DefenderTier.LIVING || tier == DefenderTier.SENTIENT) {
                addOptionalPotion(attacker, "srparasites:antimall", DefenderConfig.livingEffectDurationTicks, DefenderConfig.immalleableAmplifier);
            }
            if (tier == DefenderTier.SENTIENT) addOptionalPotion(attacker, "srparasites:viral", DefenderConfig.livingEffectDurationTicks, DefenderConfig.viralAmplifier);
        }
    }

    private static void applyElement(DefenderTier tier, EntityLivingBase target,
                                     EntityLivingBase source, boolean parry) {
        if (tier == DefenderTier.FLAMED_DRAGONBONE) {
            target.setFire(DefenderConfig.flameDurationSeconds);
            if (!parry) knockBack(target, source);
        } else if (tier == DefenderTier.ICED_DRAGONBONE) {
            setFrozen(target, DefenderConfig.frozenDurationTicks);
            target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, DefenderConfig.iceDebuffDurationTicks, DefenderConfig.iceDebuffAmplifier));
            target.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, DefenderConfig.iceDebuffDurationTicks, DefenderConfig.iceDebuffAmplifier));
            if (!parry) knockBack(target, source);
        } else if (tier == DefenderTier.ELECTRIC_DRAGONBONE) {
            createChainLightning(target, source);
            if (!parry) knockBack(target, source);
        }
    }

    private static void knockBack(EntityLivingBase target, EntityLivingBase source) {
        target.knockBack(source, DefenderConfig.elementalMainHandKnockback, source.posX - target.posX, source.posZ - target.posZ);
    }

    private static void setFrozen(EntityLivingBase target, int ticks) {
        try {
            Class<?> api = Class.forName("com.github.alexthe666.iceandfire.api.InFCapabilities");
            Object capability = api.getMethod("getEntityEffectCapability", EntityLivingBase.class).invoke(null, target);
            Method setter = capability.getClass().getMethod("setFrozen", int.class);
            setter.invoke(capability, ticks);
        } catch (ReflectiveOperationException | LinkageError ignored) {
            // Older Ice and Fire versions still receive the vanilla debuffs.
        }
    }

    private static void createChainLightning(EntityLivingBase target, EntityLivingBase source) {
        try {
            Class<?> api = Class.forName("com.github.alexthe666.iceandfire.api.ChainLightningUtils");
            api.getMethod("createChainLightningFromTarget", net.minecraft.world.World.class,
                EntityLivingBase.class, EntityLivingBase.class).invoke(null, target.world, target, source);
        } catch (ReflectiveOperationException | LinkageError ignored) {
            // Electric Defenders are hidden unless their Dregora ingredient exists.
        }
    }

    private static void addOptionalPotion(EntityLivingBase target, String id, int ticks, int amplifier) {
        Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(id));
        if (potion != null) target.addPotionEffect(new PotionEffect(potion, ticks, amplifier));
    }

    private static boolean isEntity(EntityLivingBase entity, String path) {
        ResourceLocation id = net.minecraft.entity.EntityList.getKey(entity);
        return id != null && "iceandfire".equals(id.getResourceDomain()) && path.equals(id.getResourcePath());
    }
}
