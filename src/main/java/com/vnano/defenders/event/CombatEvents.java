package com.vnano.defenders.event;

import com.vnano.defenders.DefendersMod;
import com.vnano.defenders.config.DefenderConfig;
import com.vnano.defenders.item.DefenderTier;
import com.vnano.defenders.item.ItemDefender;
import com.vnano.defenders.registry.ModEffects;
import com.vnano.defenders.registry.ModEnchantments;
import com.vnano.defenders.registry.ModItems;
import java.util.UUID;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = DefendersMod.MOD_ID)
public final class CombatEvents {
    private static final String BLOCKING = "DefendersBlocking";
    private static final String START = "DefendersBlockStart";
    private static final String PARRY_READY = "DefendersParryReady";
    private static final String VULN_BY = "DefendersVulnerableBy";
    private static final String VULN_UNTIL = "DefendersVulnerableUntil";
    private static final String REPRISAL = "DefendersReprisal";
    private static final String REPRISAL_UNTIL = "DefendersReprisalUntil";
    private static final String BYPASSING_VANILLA_BLOCK = "DefendersBypassingVanillaBlock";
    private static final UUID MOVE_UUID = UUID.fromString("d37d6237-d2fe-4f73-b970-68d3cb7633d9");
    private static final double VANILLA_USE_MOVEMENT_FACTOR = .20;

    public static void beginBlocking(Player player) {
        CompoundTag data = player.getPersistentData();
        if (!data.getBoolean(BLOCKING)) {
            data.putBoolean(BLOCKING, true);
            data.putLong(START, player.level().getGameTime());
        }
    }

    public static void endBlocking(Player player) { player.getPersistentData().putBoolean(BLOCKING, false); }

    public static boolean isBlocking(Player player) {
        return player.isUsingItem() && player.getUsedItemHand() == InteractionHand.OFF_HAND
            && player.getUseItem().getItem() instanceof ItemDefender;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack defender = player.getOffhandItem();
        if (!(defender.getItem() instanceof ItemDefender item)) return;

        CompoundTag data = player.getPersistentData();
        long now = player.level().getGameTime();
        boolean blocking = isBlocking(player);
        boolean melee = directMelee(event.getSource());
        int reflexes = ModEnchantments.level(ModEnchantments.REFLEXES, defender);
        int deflection = ModEnchantments.level(ModEnchantments.DEFLECTION, defender);
        int sixthSense = ModEnchantments.level(ModEnchantments.SIXTH_SENSE, defender);

        long elapsed = now - data.getLong(START);
        int parryWindow = DefenderConfig.PARRY_TICKS.get() + reflexes * DefenderConfig.REFLEXES_TICKS_PER_LEVEL.get();
        if (blocking && allowedDamage(event.getSource()) && elapsed >= 0 && elapsed <= parryWindow
            && now >= data.getLong(PARRY_READY)) {
            LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity living && melee ? living : null;
            perfectParry(event, player, defender, item.tier(), attacker, sixthSense > 0, now);
            return;
        }

        if (deflection == 0 && sixthSense > 0 && melee && event.getAmount() > 0
            && now >= data.getLong(PARRY_READY) && player.getRandom().nextDouble() < DefenderConfig.SIXTH_SENSE_CHANCE.get()) {
            perfectParry(event, player, defender, item.tier(),
                (LivingEntity) event.getSource().getEntity(), true, now);
            return;
        }

        if (!blocking) return;
        long blockStart = data.getLong(START);
        data.putBoolean(BYPASSING_VANILLA_BLOCK, true);
        player.stopUsingItem();
        data.putBoolean(BLOCKING, true);
        data.putLong(START, blockStart);
    }

    private static void perfectParry(LivingAttackEvent event, Player player, ItemStack defender,
                                     DefenderTier tier, LivingEntity attacker, boolean glow, long now) {
        event.setCanceled(true);
        player.getPersistentData().putLong(PARRY_READY, now + DefenderConfig.PARRY_RECOVERY_TICKS.get());
        damageDefender(defender, player, DefenderConfig.PARRY_DURABILITY_COST.get());
        if (attacker != null) {
            int duration = DefenderConfig.debuffTicks(tier);
            attacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 2));
            attacker.addEffect(new MobEffectInstance(ModEffects.VULNERABLE.get(), duration, 0));
            if (glow) attacker.addEffect(new MobEffectInstance(MobEffects.GLOWING, DefenderConfig.SIXTH_SENSE_GLOW_TICKS.get(), 0));
            CompoundTag targetData = attacker.getPersistentData();
            targetData.putUUID(VULN_BY, player.getUUID());
            targetData.putLong(VULN_UNTIL, now + duration);
            attacker.knockback(DefenderConfig.knockback(tier), player.getX() - attacker.getX(), player.getZ() - attacker.getZ());
            applyMaterialParry(tier, attacker, player);
        }
        int reprisal = ModEnchantments.level(ModEnchantments.REPRISAL, defender);
        if (reprisal > 0) {
            CompoundTag data = player.getPersistentData();
            data.putInt(REPRISAL, reprisal);
            data.putLong(REPRISAL_UNTIL, now + DefenderConfig.DEBUFF_TICKS.get());
        }
        float volume = DefenderConfig.PARRY_SOUND_VOLUME.get().floatValue();
        if (volume > 0) player.level().playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_CHIME.value(),
            SoundSource.PLAYERS, volume, 1.7f);
        if (player.level() instanceof ServerLevel server) {
            double x = attacker == null ? player.getX() : attacker.getX();
            double y = attacker == null ? player.getY() + 1 : attacker.getY() + attacker.getBbHeight() * .65;
            double z = attacker == null ? player.getZ() : attacker.getZ();
            server.sendParticles(ParticleTypes.CRIT, x, y, z, 14, .25, .35, .25, .08);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (directMelee(source) && source.getEntity() instanceof Player attacker) applyOffense(event, attacker);
        if (!(event.getEntity() instanceof Player player) || voidDamage(source)) return;
        CompoundTag data = player.getPersistentData();
        if (!isBlocking(player) && !data.getBoolean(BYPASSING_VANILLA_BLOCK)) return;
        ItemStack defender = player.getOffhandItem();
        if (!(defender.getItem() instanceof ItemDefender item)) return;
        boolean baseAllowed = allowedDamage(source);
        int deflection = ModEnchantments.level(ModEnchantments.DEFLECTION, defender);
        if (!baseAllowed && deflection <= 0) return;
        double reduction = deflection * (directMelee(source)
            ? DefenderConfig.DEFLECTION_MELEE_REDUCTION_PER_LEVEL.get()
            : DefenderConfig.DEFLECTION_REDUCTION_PER_LEVEL.get());
        if (baseAllowed) reduction += DefenderConfig.guardedReduction(item.tier());
        reduction = Math.min(DefenderConfig.MAX_REDUCTION.get(), reduction);
        event.setAmount((float) (event.getAmount() * (1 - reduction)));
        damageDefender(defender, player, DefenderConfig.GUARDED_DURABILITY_COST.get());
    }

    private static void applyOffense(LivingHurtEvent event, Player attacker) {
        LivingEntity target = event.getEntity();
        if (attacker.getMainHandItem().getItem() instanceof ItemDefender defender) {
            event.setAmount(event.getAmount() + materialDamage(defender.tier(), target));
            applyMaterialHit(defender.tier(), target, attacker);
        }
        long now = attacker.level().getGameTime();
        CompoundTag targetData = target.getPersistentData();
        if (targetData.hasUUID(VULN_BY) && attacker.getUUID().equals(targetData.getUUID(VULN_BY))
            && now <= targetData.getLong(VULN_UNTIL)) {
            event.setAmount((float) (event.getAmount() * DefenderConfig.VULNERABILITY.get()));
        }
        CompoundTag attackerData = attacker.getPersistentData();
        int reprisal = attackerData.getInt(REPRISAL);
        if (reprisal > 0 && now <= attackerData.getLong(REPRISAL_UNTIL)) event.setAmount(event.getAmount() + reprisal);
        if (reprisal > 0) { attackerData.remove(REPRISAL); attackerData.remove(REPRISAL_UNTIL); }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase == TickEvent.Phase.END) {
            CompoundTag data = player.getPersistentData();
            if (data.getBoolean(BYPASSING_VANILLA_BLOCK)) {
                if (player.getOffhandItem().getItem() instanceof ItemDefender && !player.isUsingItem()) {
                    player.startUsingItem(InteractionHand.OFF_HAND);
                }
                data.remove(BYPASSING_VANILLA_BLOCK);
            }
            if (isBlocking(player) && !data.getBoolean(BLOCKING)) beginBlocking(player);
            if (!isBlocking(player) && data.getBoolean(BLOCKING)) endBlocking(player);
            grantMonkeysUncle(player);
        }
        updateMovement(player);
    }

    private static void updateMovement(Player player) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;
        AttributeModifier old = speed.getModifier(MOVE_UUID);
        if (old != null) speed.removeModifier(old);
        if (isBlocking(player)) {
            ItemStack stack = player.getOffhandItem();
            DefenderTier tier = ((ItemDefender) stack.getItem()).tier();
            int footwork = ModEnchantments.level(ModEnchantments.FOOTWORK, stack);
            double penalty = Math.max(0, DefenderConfig.movementPenalty(tier) - footwork * .10);
            double intended = Math.max(0, Math.min(1, 1 - penalty));
            speed.addTransientModifier(new AttributeModifier(MOVE_UUID, "Defender guard movement",
                intended / VANILLA_USE_MOVEMENT_FACTOR - 1, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player) || !directMelee(event.getSource())) return;
        InteractionHand hand = holdsTier(player.getOffhandItem(), DefenderTier.LIVING)
            ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack living = player.getItemInHand(hand);
        if (!holdsTier(living, DefenderTier.LIVING)) return;
        if (DefenderConfig.REQUIRE_PARASITE_KILLS.get() && !isParasite(event.getEntity())) return;
        CompoundTag tag = living.getOrCreateTag();
        int progress = tag.getInt("srpkills") + Math.max(1, (int) event.getEntity().getMaxHealth());
        tag.putInt("srpkills", progress);
        if (progress >= DefenderConfig.LIVING_EVOLUTION.get()) evolve(player, hand, living);
    }

    private static void evolve(Player player, InteractionHand hand, ItemStack living) {
        ItemStack sentient = new ItemStack(ModItems.get(DefenderTier.SENTIENT).get());
        if (living.hasTag()) { CompoundTag tag = living.getTag().copy(); tag.remove("srpkills"); sentient.setTag(tag); }
        float used = living.getMaxDamage() == 0 ? 0 : (float) living.getDamageValue() / living.getMaxDamage();
        sentient.setDamageValue(Math.min(sentient.getMaxDamage() - 1, Math.round(used * sentient.getMaxDamage())));
        player.setItemInHand(hand, sentient);
        player.level().playSound(null, player.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER,
            SoundSource.PLAYERS, .7f, 1.3f);
    }

    private static void applyMaterialHit(DefenderTier tier, LivingEntity target, Player attacker) {
        if (tier.isVenom()) target.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 2));
        if (tier == DefenderTier.FLAMED_DRAGONBONE) target.setSecondsOnFire(5);
        if (tier == DefenderTier.ICED_DRAGONBONE) {
            target.setTicksFrozen(Math.max(target.getTicksFrozen(), 200));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
            target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 2));
        }
        if (tier == DefenderTier.ELECTRIC_DRAGONBONE && !target.level().isClientSide) {
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(target.level());
            if (bolt != null) { bolt.moveTo(target.position()); bolt.setVisualOnly(true); target.level().addFreshEntity(bolt); }
        }
    }

    private static void applyMaterialParry(DefenderTier tier, LivingEntity target, Player defender) {
        applyMaterialHit(tier, target, defender);
        if (tier == DefenderTier.SILVER && target.getMobType() == MobType.UNDEAD) {
            target.hurt(defender.damageSources().playerAttack(defender), 1);
        }
        if (tier == DefenderTier.LIVING || tier == DefenderTier.SENTIENT) {
            addOptionalEffect(target, "scapeandrunparasites:immalleable", "srparasites:immalleable");
        }
        if (tier == DefenderTier.SENTIENT) {
            addOptionalEffect(target, "scapeandrunparasites:viral", "srparasites:viral", "srp_spartans:virulent");
        }
    }

    private static void addOptionalEffect(LivingEntity target, String... ids) {
        for (String text : ids) {
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(text));
            if (effect != null) { target.addEffect(new MobEffectInstance(effect, 100, 0)); return; }
        }
    }

    private static float materialDamage(DefenderTier tier, LivingEntity target) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
        String path = id.getPath();
        if (tier == DefenderTier.SILVER && target.getMobType() == MobType.UNDEAD) return 2;
        if (tier.isMyrmex() && (target.getMobType() != MobType.ARTHROPOD || path.contains("death_worm"))) return 4;
        if (tier == DefenderTier.FLAMED_DRAGONBONE && path.contains("ice_dragon")) return 13.5f;
        if (tier == DefenderTier.ICED_DRAGONBONE && path.contains("fire_dragon")) return 13.5f;
        if (tier == DefenderTier.ELECTRIC_DRAGONBONE && (path.contains("fire_dragon") || path.contains("ice_dragon"))) return 6.75f;
        return 0;
    }

    private static void damageDefender(ItemStack stack, Player player, int amount) {
        if (amount > 0) stack.hurtAndBreak(amount, player, entity -> entity.broadcastBreakEvent(InteractionHand.OFF_HAND));
    }

    private static boolean allowedDamage(DamageSource source) {
        if (voidDamage(source) || source.is(DamageTypeTags.IS_PROJECTILE)) return false;
        if (DefenderConfig.BLOCK_ALL_DAMAGE.get()) return true;
        if (source.is(DamageTypeTags.BYPASSES_ARMOR) && !DefenderConfig.BLOCK_ARMOR_BYPASSING.get()) return false;
        if (directMelee(source)) return DefenderConfig.BLOCK_DIRECT_MELEE.get();
        if (source.is(DamageTypeTags.IS_EXPLOSION)) return DefenderConfig.BLOCK_EXPLOSIONS.get();
        if (source.is(DamageTypeTags.IS_FIRE)) return DefenderConfig.BLOCK_FIRE.get();
        String type = source.getMsgId();
        if ("fall".equals(type)) return DefenderConfig.BLOCK_FALL.get();
        if ("drown".equals(type)) return DefenderConfig.BLOCK_DROWNING.get();
        if (type.toLowerCase().contains("magic") || "thorns".equals(type)) return DefenderConfig.BLOCK_MAGIC.get();
        return DefenderConfig.BLOCK_ENVIRONMENTAL.get();
    }

    private static boolean voidDamage(DamageSource source) { return "outOfWorld".equals(source.getMsgId()) || "genericKill".equals(source.getMsgId()); }
    private static boolean directMelee(DamageSource source) {
        Entity attacker = source.getEntity();
        return attacker instanceof LivingEntity && source.getDirectEntity() == attacker
            && !source.is(DamageTypeTags.IS_PROJECTILE) && !source.is(DamageTypeTags.IS_EXPLOSION);
    }
    private static boolean isParasite(LivingEntity entity) {
        String namespace = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getNamespace().toLowerCase();
        return namespace.contains("parasite") || namespace.startsWith("srp") || namespace.contains("scapeandrun");
    }
    private static boolean holdsTier(ItemStack stack, DefenderTier tier) {
        return stack.getItem() instanceof ItemDefender defender && defender.tier() == tier;
    }
    private static void grantMonkeysUncle(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || !holdsTier(player.getMainHandItem(), DefenderTier.DRAGONBONE)
            && !holdsTier(player.getOffhandItem(), DefenderTier.DRAGONBONE) || serverPlayer.getServer() == null) return;
        Advancement advancement = serverPlayer.getServer().getAdvancements().getAdvancement(new ResourceLocation(DefendersMod.MOD_ID, "a_monkeys_uncle"));
        if (advancement != null) serverPlayer.getAdvancements().award(advancement, "holding_dragonbone_defender");
    }

    private CombatEvents() {}
}
