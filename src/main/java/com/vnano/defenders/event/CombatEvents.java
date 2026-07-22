package com.vnano.defenders.event;

import com.vnano.defenders.config.DefenderConfig;
import com.vnano.defenders.item.*;
import com.vnano.defenders.registry.ModEffects;
import com.vnano.defenders.registry.ModEnchantments;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.UseEffects;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;

public final class CombatEvents {
    private static final String BLOCKING="DefendersBlocking",START="DefendersBlockStart",
        READY="DefendersParryReady",VULN_BY="DefendersVulnerableBy",
        VULN_UNTIL="DefendersVulnerableUntil",REPRISAL="DefendersReprisal",
        REPRISAL_UNTIL="DefendersReprisalUntil";
    public static void beginBlocking(Player player){
        CompoundTag data=player.getPersistentData();
        if(!data.getBooleanOr(BLOCKING,false)){data.putBoolean(BLOCKING,true);data.putLong(START,player.level().getGameTime());}
    }
    public static void endBlocking(Player player){player.getPersistentData().putBoolean(BLOCKING,false);}
    public static boolean isBlocking(Player player){return player.isUsingItem()
        && player.getUsedItemHand()==InteractionHand.OFF_HAND
        && player.getOffhandItem().getItem() instanceof ItemDefender;}

    public static boolean onAttack(LivingAttackEvent event){
        if(!(event.getEntity() instanceof Player player))return false;
        ItemStack stack=player.getOffhandItem();
        if(!(stack.getItem() instanceof ItemDefender defender))return false;
        long now=player.level().getGameTime(); CompoundTag data=player.getPersistentData();
        boolean blocking=isBlocking(player),melee=directMelee(event.getSource());
        int reflexes=ModEnchantments.level(ModEnchantments.REFLEXES,stack);
        int deflection=ModEnchantments.level(ModEnchantments.DEFLECTION,stack);
        int sixth=ModEnchantments.level(ModEnchantments.SIXTH_SENSE,stack);
        long elapsed=now-data.getLongOr(START,now);
        if(blocking&&allowedDamage(event.getSource())&&elapsed>=0
            &&elapsed<=DefenderConfig.PARRY_TICKS.get()+reflexes*2
            &&now>=data.getLongOr(READY,0)){perfectParry(player,stack,defender.tier(),
                event.getSource().getEntity() instanceof LivingEntity living&&melee?living:null,sixth>0,now);return true;}
        if(deflection==0&&sixth>0&&melee&&event.getAmount()>0&&now>=data.getLongOr(READY,0)
            &&player.getRandom().nextDouble()<.10){perfectParry(player,stack,defender.tier(),
                (LivingEntity)event.getSource().getEntity(),true,now);return true;}
        return false;
    }

    private static void perfectParry(Player player,ItemStack stack,
            DefenderTier tier,LivingEntity attacker,boolean glow,long now){
        player.getPersistentData().putLong(READY,now+DefenderConfig.PARRY_RECOVERY_TICKS.get());
        damage(stack,player,DefenderConfig.PARRY_COST.get());
        if(attacker!=null){int duration=DefenderConfig.DEBUFF_TICKS.get()+(tier==DefenderTier.DIAMOND?20:0);
            attacker.addEffect(new MobEffectInstance(MobEffects.SLOWNESS,duration,2));
            attacker.addEffect(new MobEffectInstance(ModEffects.VULNERABLE.getHolder().orElseThrow(),duration,0));
            if(glow)attacker.addEffect(new MobEffectInstance(MobEffects.GLOWING,100,0));
            attacker.getPersistentData().putString(VULN_BY,player.getUUID().toString());
            attacker.getPersistentData().putLong(VULN_UNTIL,now+duration);
            double kb=DefenderConfig.KNOCKBACK.get()+(tier==DefenderTier.STONE?.2:0);
            attacker.knockback(kb,player.getX()-attacker.getX(),player.getZ()-attacker.getZ());}
        int reprisal=ModEnchantments.level(ModEnchantments.REPRISAL,stack);
        if(reprisal>0){player.getPersistentData().putInt(REPRISAL,reprisal);player.getPersistentData().putLong(REPRISAL_UNTIL,now+40);}
        float volume=DefenderConfig.SOUND_VOLUME.get().floatValue();
        if(volume>0)player.level().playSound(null,player.blockPosition(),SoundEvents.NOTE_BLOCK_CHIME.value(),SoundSource.PLAYERS,volume,1.7f);
        if(player.level() instanceof ServerLevel server)server.sendParticles(ParticleTypes.CRIT,
            attacker==null?player.getX():attacker.getX(),attacker==null?player.getY()+1:attacker.getY()+attacker.getBbHeight()*.65,
            attacker==null?player.getZ():attacker.getZ(),14,.25,.35,.25,.08);
    }

    public static void onHurt(LivingHurtEvent event){
        DamageSource source=event.getSource();
        if(directMelee(source)&&source.getEntity() instanceof Player attacker)applyOffense(event,attacker);
        if(!(event.getEntity() instanceof Player player)||!isBlocking(player)||voidDamage(source))return;
        ItemStack stack=player.getOffhandItem(); if(!(stack.getItem() instanceof ItemDefender defender))return;
        boolean base=allowedDamage(source); int deflection=ModEnchantments.level(ModEnchantments.DEFLECTION,stack);
        if(!base&&deflection<=0)return;
        double reduction=deflection*.10+(directMelee(source)?deflection*.05:0);
        if(base)reduction+=DefenderConfig.GUARDED_REDUCTION.get();
        reduction=Math.min(DefenderConfig.MAX_REDUCTION.get(),reduction);
        event.setAmount((float)(event.getAmount()*(1-reduction)));
        damage(stack,player,DefenderConfig.GUARD_COST.get());
    }
    private static void applyOffense(LivingHurtEvent event,Player attacker){
        ItemStack offhand=attacker.getOffhandItem();
        if(offhand.getItem() instanceof ItemDefender)event.setAmount(event.getAmount()
            +ModEnchantments.level(ModEnchantments.FINESSE,offhand)*.5f);
        LivingEntity target=event.getEntity();long now=attacker.level().getGameTime();CompoundTag td=target.getPersistentData();
        if(attacker.getUUID().toString().equals(td.getStringOr(VULN_BY,""))&&now<=td.getLongOr(VULN_UNTIL,0))
            event.setAmount((float)(event.getAmount()*DefenderConfig.VULNERABILITY.get()));
        CompoundTag ad=attacker.getPersistentData();int reprisal=ad.getIntOr(REPRISAL,0);
        if(reprisal>0&&now<=ad.getLongOr(REPRISAL_UNTIL,0))event.setAmount(event.getAmount()+reprisal);
        if(reprisal>0){ad.remove(REPRISAL);ad.remove(REPRISAL_UNTIL);}
    }

    public static void onTick(TickEvent.PlayerTickEvent.Post event){Player player=event.player();
        CompoundTag data=player.getPersistentData();if(isBlocking(player)&&!data.getBooleanOr(BLOCKING,false))beginBlocking(player);
        if(!isBlocking(player)&&data.getBooleanOr(BLOCKING,false))endBlocking(player);updateMovement(player);}
    private static void updateMovement(Player player){ItemStack stack=player.getOffhandItem();
        if(!(stack.getItem() instanceof ItemDefender defender))return;
        int footwork=ModEnchantments.level(ModEnchantments.FOOTWORK,stack);
        double base=DefenderConfig.MOVE_PENALTY.get()-(defender.tier()==DefenderTier.WOOD?.10:0);
        float multiplier=(float)Math.max(0,Math.min(1,1-Math.max(0,base-footwork*.10)));
        UseEffects current=stack.getOrDefault(DataComponents.USE_EFFECTS,UseEffects.DEFAULT);
        UseEffects desired=new UseEffects(false,true,multiplier);
        if(!desired.equals(current))stack.set(DataComponents.USE_EFFECTS,desired);}
    private static void damage(ItemStack stack,Player player,int amount){if(amount>0)stack.hurtAndBreak(amount,player,InteractionHand.OFF_HAND);}
    private static boolean allowedDamage(DamageSource source){if(voidDamage(source)||source.is(DamageTypeTags.IS_PROJECTILE))return false;
        if(DefenderConfig.BLOCK_ALL.get())return true;if(source.is(DamageTypeTags.BYPASSES_ARMOR)&&!DefenderConfig.BLOCK_ARMOR_BYPASSING.get())return false;
        if(directMelee(source))return DefenderConfig.BLOCK_MELEE.get();if(source.is(DamageTypeTags.IS_EXPLOSION))return DefenderConfig.BLOCK_EXPLOSIONS.get();
        if(source.is(DamageTypeTags.IS_FIRE))return DefenderConfig.BLOCK_FIRE.get();String type=source.getMsgId();
        if("fall".equals(type))return DefenderConfig.BLOCK_FALL.get();if("drown".equals(type))return DefenderConfig.BLOCK_DROWNING.get();
        if(type.toLowerCase().contains("magic")||"thorns".equals(type))return DefenderConfig.BLOCK_MAGIC.get();return DefenderConfig.BLOCK_ENVIRONMENTAL.get();}
    private static boolean voidDamage(DamageSource s){return "outOfWorld".equals(s.getMsgId())||"genericKill".equals(s.getMsgId());}
    private static boolean directMelee(DamageSource source){Entity attacker=source.getEntity();return attacker instanceof LivingEntity
        &&source.getDirectEntity()==attacker&&!source.is(DamageTypeTags.IS_PROJECTILE)&&!source.is(DamageTypeTags.IS_EXPLOSION);}
    private CombatEvents(){}
}
