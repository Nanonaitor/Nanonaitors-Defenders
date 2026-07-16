package com.vnano.defenders.event;

import com.vnano.defenders.DefendersMod;
import com.vnano.defenders.compat.CompatEffects;
import com.vnano.defenders.compat.CompatManager;
import com.vnano.defenders.config.DefenderConfig;
import com.vnano.defenders.enchantment.ModEnchantments;
import com.vnano.defenders.item.ItemDefender;
import com.vnano.defenders.item.DefenderTier;
import com.vnano.defenders.registry.ModContent;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = DefendersMod.MOD_ID)
public final class DefenderCombatHandler {
    private static final String NBT_BLOCKING = "DefendersBlocking";
    private static final String NBT_BLOCK_START = "DefendersBlockStart";
    private static final String NBT_PARRY_READY = "DefendersParryReady";
    private static final String NBT_VULNERABLE_BY = "DefendersVulnerableBy";
    private static final String NBT_VULNERABLE_UNTIL = "DefendersVulnerableUntil";
    private static final String NBT_REPRISAL_LEVEL = "DefendersReprisalLevel";
    private static final String NBT_REPRISAL_UNTIL = "DefendersReprisalUntil";
    private static final String NBT_BYPASSING_VANILLA_BLOCK = "DefendersBypassingVanillaBlock";
    private static final UUID MOVEMENT_UUID = UUID.fromString("d37d6237-d2fe-4f73-b970-68d3cb7633d9");
    private static final double VANILLA_ACTIVE_ITEM_MOVEMENT_FACTOR = 0.20D;

    private DefenderCombatHandler() {}

    public static boolean isBlockingWithDefender(EntityPlayer player) {
        return player.isHandActive()
            && player.getActiveHand() == EnumHand.OFF_HAND
            && player.getActiveItemStack().getItem() instanceof ItemDefender;
    }

    public static void beginBlocking(EntityPlayer player) {
        NBTTagCompound data = player.getEntityData();
        if (!data.getBoolean(NBT_BLOCKING)) {
            data.setBoolean(NBT_BLOCKING, true);
            data.setLong(NBT_BLOCK_START, player.world.getTotalWorldTime());
        }
    }

    public static void endBlocking(EntityPlayer player) {
        player.getEntityData().setBoolean(NBT_BLOCKING, false);
    }

    /**
     * EnumAction.BLOCK is required for the vanilla arm poses, but it also invokes
     * vanilla shield mitigation before LivingHurtEvent. Briefly lowering the
     * active hand here lets the Defender's own damage rules remain authoritative.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        ItemStack defenderStack = player.getHeldItemOffhand();
        if (!(defenderStack.getItem() instanceof ItemDefender)) return;

        NBTTagCompound data = player.getEntityData();
        long now = player.world.getTotalWorldTime();
        boolean blocking = isBlockingWithDefender(player);
        int deflection = ModEnchantments.getLevel(ModEnchantments.DEFLECTION, defenderStack);
        int reflexes = ModEnchantments.getLevel(ModEnchantments.REFLEXES, defenderStack);
        int sixthSense = ModEnchantments.getLevel(ModEnchantments.SIXTH_SENSE, defenderStack);
        DefenderTier tier = ((ItemDefender) defenderStack.getItem()).getTier();
        int reprisal = ModEnchantments.getLevel(ModEnchantments.REPRISAL, defenderStack);
        long blockStart = data.getLong(NBT_BLOCK_START);
        long elapsed = now - blockStart;
        int effectiveParryWindow = DefenderConfig.parryWindowTicks
            + reflexes * DefenderConfig.reflexesWindowTicksPerLevel;

        if (blocking
            && isAllowedDamage(event.getSource())
            && elapsed >= 0
            && elapsed <= effectiveParryWindow
            && now >= data.getLong(NBT_PARRY_READY)) {
            Entity sourceEntity = event.getSource().getTrueSource();
            EntityLivingBase attacker = sourceEntity instanceof EntityLivingBase
                && (isDirectMelee(event.getSource()) || DefenderConfig.retaliateAgainstIndirectAttacker)
                ? (EntityLivingBase) sourceEntity : null;
            performPerfectParry(event, player, defenderStack, attacker, tier, reprisal,
                sixthSense > 0, now);
            return;
        }

        if (deflection == 0
            && sixthSense > 0
            && event.getAmount() > 0F
            && isDirectMelee(event.getSource())
            && now >= data.getLong(NBT_PARRY_READY)
            && player.getRNG().nextFloat() < DefenderConfig.sixthSenseAutoParryChance) {
            EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
            performPerfectParry(event, player, defenderStack, attacker, tier, reprisal, true, now);
            return;
        }

        if (!blocking) return;
        data.setBoolean(NBT_BYPASSING_VANILLA_BLOCK, true);
        player.resetActiveHand();

        // resetActiveHand calls ItemDefender#onPlayerStoppedUsing; retain the
        // original guard session so attacks cannot reset the parry window.
        data.setBoolean(NBT_BLOCKING, true);
        data.setLong(NBT_BLOCK_START, blockStart);
    }

    private static void performPerfectParry(LivingAttackEvent event, EntityPlayer player,
                                            ItemStack defenderStack, EntityLivingBase attacker,
                                            DefenderTier tier, int reprisal,
                                            boolean applyGlow, long now) {
        // Cancel before vanilla reaches its hurt feedback and damage pipeline.
        event.setCanceled(true);
        player.getEntityData().setLong(NBT_PARRY_READY, now + DefenderConfig.parryRecoveryTicks);
        if (DefenderConfig.perfectParryDurabilityCost > 0) {
            defenderStack.damageItem(DefenderConfig.perfectParryDurabilityCost, player);
        }
        applyPerfectParry(player, attacker, tier, reprisal, applyGlow, now);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            updateBlockingMovement(event.player);
            return;
        }
        EntityPlayer player = event.player;
        NBTTagCompound data = player.getEntityData();

        if (data.getBoolean(NBT_BYPASSING_VANILLA_BLOCK)) {
            if (player.getHeldItemOffhand().getItem() instanceof ItemDefender && !player.isHandActive()) {
                player.setActiveHand(EnumHand.OFF_HAND);
            }
            data.removeTag(NBT_BYPASSING_VANILLA_BLOCK);
        }

        boolean blocking = isBlockingWithDefender(player);
        if (blocking && !data.getBoolean(NBT_BLOCKING)) beginBlocking(player);
        if (!blocking && data.getBoolean(NBT_BLOCKING)) endBlocking(player);

        updateBlockingMovement(player);
    }

    private static void updateBlockingMovement(EntityPlayer player) {
        IAttributeInstance speed = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        AttributeModifier old = speed.getModifier(MOVEMENT_UUID);
        if (old != null) speed.removeModifier(old);
        if (isBlockingWithDefender(player)) {
            ItemStack defender = player.getHeldItemOffhand();
            DefenderTier tier = ((ItemDefender) defender.getItem()).getTier();
            int footwork = ModEnchantments.getLevel(ModEnchantments.FOOTWORK, defender);
            double penalty = Math.max(0.0D, DefenderConfig.getMovementPenalty(tier) - footwork * 0.10D);
            double intendedFactor = Math.max(0.0D, Math.min(1.0D, 1.0D - penalty));
            double compensation = intendedFactor / VANILLA_ACTIVE_ITEM_MOVEMENT_FACTOR - 1.0D;
            if (Math.abs(compensation) > 0.0001D) {
                speed.applyModifier(new AttributeModifier(MOVEMENT_UUID,
                    "Defender blocking movement compensation", compensation, 2).setSaved(false));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        Entity trueSource = source.getTrueSource();

        if (isDirectMelee(source) && trueSource instanceof EntityPlayer) {
            EntityPlayer attacker = (EntityPlayer) trueSource;
            applyOffensiveEffects(event, attacker);
        }

        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
        EntityPlayer defender = (EntityPlayer) event.getEntityLiving();
        NBTTagCompound data = defender.getEntityData();
        boolean defenderWasActive = isBlockingWithDefender(defender)
            || data.getBoolean(NBT_BYPASSING_VANILLA_BLOCK);
        if (!defenderWasActive || isVoidDamage(source)) return;

        ItemStack stack = defender.getHeldItemOffhand();
        if (!(stack.getItem() instanceof ItemDefender)) return;
        DefenderTier tier = ((ItemDefender) stack.getItem()).getTier();
        boolean baseAllowed = isAllowedDamage(source);
        int deflection = ModEnchantments.getLevel(ModEnchantments.DEFLECTION, stack);
        if (!baseAllowed && deflection <= 0) return;
        float deflectionPerLevel = isDirectMelee(source)
            ? DefenderConfig.deflectionMeleeReductionPerLevel
            : DefenderConfig.deflectionReductionPerLevel;
        float reduction = deflection * deflectionPerLevel;
        if (baseAllowed) {
            reduction += DefenderConfig.getGuardedReduction(tier);
        }
        reduction = Math.min(DefenderConfig.maximumGuardedReduction, reduction);
        event.setAmount(event.getAmount() * (1.0F - reduction));
        if (DefenderConfig.guardedHitDurabilityCost > 0) {
            stack.damageItem(DefenderConfig.guardedHitDurabilityCost, defender);
        }
    }

    private static void applyOffensiveEffects(LivingHurtEvent event, EntityPlayer attacker) {
        EntityLivingBase target = event.getEntityLiving();
        ItemStack main = attacker.getHeldItemMainhand();
        if (main.getItem() instanceof ItemDefender) {
            DefenderTier tier = ((ItemDefender) main.getItem()).getTier();
            event.setAmount(CompatEffects.modifyMainHandDamage(tier, target, event.getAmount()));
        }
        long now = attacker.world.getTotalWorldTime();
        NBTTagCompound targetData = target.getEntityData();
        if (targetData.hasUniqueId(NBT_VULNERABLE_BY)
            && attacker.getUniqueID().equals(targetData.getUniqueId(NBT_VULNERABLE_BY))
            && now <= targetData.getLong(NBT_VULNERABLE_UNTIL)) {
            event.setAmount(event.getAmount() * DefenderConfig.vulnerabilityMultiplier);
        }

        NBTTagCompound attackerData = attacker.getEntityData();
        int reprisal = attackerData.getInteger(NBT_REPRISAL_LEVEL);
        if (reprisal > 0 && now <= attackerData.getLong(NBT_REPRISAL_UNTIL)) {
            event.setAmount(event.getAmount() + reprisal);
            attackerData.removeTag(NBT_REPRISAL_LEVEL);
            attackerData.removeTag(NBT_REPRISAL_UNTIL);
        } else if (reprisal > 0) {
            attackerData.removeTag(NBT_REPRISAL_LEVEL);
            attackerData.removeTag(NBT_REPRISAL_UNTIL);
        }
    }

    private static void applyPerfectParry(EntityPlayer defender, EntityLivingBase attacker,
                                          DefenderTier tier, int reprisal,
                                          boolean applyGlow, long now) {
        if (attacker != null) {
            int debuffDuration = DefenderConfig.getDebuffDurationTicks(tier);
            attacker.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, debuffDuration, 2));
            attacker.addPotionEffect(new PotionEffect(ModContent.VULNERABLE, debuffDuration, 0));
            if (applyGlow) {
                attacker.addPotionEffect(new PotionEffect(MobEffects.GLOWING,
                    DefenderConfig.sixthSenseGlowDurationTicks, 0));
            }
            NBTTagCompound attackerData = attacker.getEntityData();
            attackerData.setUniqueId(NBT_VULNERABLE_BY, defender.getUniqueID());
            attackerData.setLong(NBT_VULNERABLE_UNTIL, now + debuffDuration);
            CompatEffects.applyPerfectParry(tier, attacker, defender);
        }

        if (reprisal > 0) {
            NBTTagCompound defenderData = defender.getEntityData();
            defenderData.setInteger(NBT_REPRISAL_LEVEL, reprisal);
            defenderData.setLong(NBT_REPRISAL_UNTIL, now + DefenderConfig.debuffDurationTicks);
        }

        if (attacker != null) {
            double dx = defender.posX - attacker.posX;
            double dz = defender.posZ - attacker.posZ;
            attacker.knockBack(defender, DefenderConfig.getParryKnockbackStrength(tier), dx, dz);
        }
        defender.world.playSound(null, defender.posX, defender.posY, defender.posZ,
            SoundEvents.BLOCK_NOTE_CHIME, SoundCategory.PLAYERS,
            DefenderConfig.perfectParrySoundVolume, 1.7F);

        if (defender.world instanceof WorldServer) {
            ((WorldServer) defender.world).spawnParticle(EnumParticleTypes.CRIT,
                attacker == null ? defender.posX : attacker.posX,
                attacker == null ? defender.posY + 1D : attacker.posY + attacker.height * 0.65D,
                attacker == null ? defender.posZ : attacker.posZ,
                14, 0.25D, 0.35D, 0.25D, 0.08D);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer) || !isDirectMelee(event.getSource())) return;
        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        EnumHand hand = null;
        ItemStack living = player.getHeldItemOffhand();
        if (isTier(living, DefenderTier.LIVING)) hand = EnumHand.OFF_HAND;
        else {
            living = player.getHeldItemMainhand();
            if (isTier(living, DefenderTier.LIVING)) hand = EnumHand.MAIN_HAND;
        }
        if (hand == null) return;
        if (DefenderConfig.requireParasiteKills && !CompatManager.isSrpParasite(event.getEntityLiving())) return;

        NBTTagCompound tag = living.hasTagCompound() ? living.getTagCompound() : new NBTTagCompound();
        int progress = (int) (tag.getInteger("srpkills") + event.getEntityLiving().getMaxHealth());
        tag.setInteger("srpkills", progress);
        living.setTagCompound(tag);
        if (progress > CompatManager.getSrpEvolutionThreshold()) evolveDefender(player, hand, living);
    }

    private static void evolveDefender(EntityPlayer player, EnumHand hand, ItemStack living) {
        ItemDefender sentientItem = ModContent.DEFENDERS.get(DefenderTier.SENTIENT);
        if (sentientItem == null) return;
        ItemStack sentient = new ItemStack(sentientItem);
        if (living.hasTagCompound()) {
            NBTTagCompound tag = living.getTagCompound().copy();
            tag.removeTag("srpkills");
            sentient.setTagCompound(tag);
        }
        float used = living.getMaxDamage() <= 0 ? 0F : (float) living.getItemDamage() / living.getMaxDamage();
        sentient.setItemDamage(Math.min(sentient.getMaxDamage() - 1, Math.round(used * sentient.getMaxDamage())));
        player.setHeldItem(hand, sentient);
        player.world.playSound(null, player.posX, player.posY, player.posZ,
            SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.PLAYERS, .7F, 1.3F);
        if (player.world instanceof WorldServer) {
            ((WorldServer) player.world).addWeatherEffect(new EntityLightningBolt(player.world,
                player.posX, player.posY, player.posZ, true));
            ((WorldServer) player.world).spawnParticle(EnumParticleTypes.SPELL_MOB,
                player.posX, player.posY + 1D, player.posZ, 35, .35D, .6D, .35D, .05D);
        }
    }

    private static boolean isTier(ItemStack stack, DefenderTier tier) {
        return stack.getItem() instanceof ItemDefender && ((ItemDefender) stack.getItem()).getTier() == tier;
    }

    private static boolean isAllowedDamage(DamageSource source) {
        if (isVoidDamage(source) || source.isProjectile()) return false;
        if (DefenderConfig.blockAllDamage) return true;
        if (source.isUnblockable() && !DefenderConfig.blockArmorBypassing) return false;
        if (isDirectMelee(source)) return DefenderConfig.blockDirectMelee;
        if (source.isExplosion()) return DefenderConfig.blockExplosions;
        if (source.isMagicDamage()) return DefenderConfig.blockMagic;
        if (source.isFireDamage()) return DefenderConfig.blockFire;
        if ("fall".equals(source.getDamageType())) return DefenderConfig.blockFall;
        if ("drown".equals(source.getDamageType())) return DefenderConfig.blockDrowning;
        return DefenderConfig.blockEnvironmental;
    }

    private static boolean isVoidDamage(DamageSource source) {
        return DamageSource.OUT_OF_WORLD == source || "outOfWorld".equals(source.getDamageType());
    }

    private static boolean isDirectMelee(DamageSource source) {
        Entity immediate = source.getImmediateSource();
        Entity trueSource = source.getTrueSource();
        return trueSource instanceof EntityLivingBase
            && immediate == trueSource
            && !source.isProjectile()
            && !source.isExplosion()
            && !source.isMagicDamage();
    }
}
