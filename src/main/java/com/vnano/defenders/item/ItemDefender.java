package com.vnano.defenders.item;

import com.vnano.defenders.DefendersMod;
import com.vnano.defenders.event.CombatEvents;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

public final class ItemDefender extends Item {
    private final DefenderTier tier;
    public ItemDefender(DefenderTier tier,Properties properties){super(properties);this.tier=tier;}
    public DefenderTier tier(){return tier;}

    public static ItemAttributeModifiers attributes(DefenderTier tier){
        return ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,new AttributeModifier(BASE_ATTACK_DAMAGE_ID,
                tier.mainHandDamage-1,AttributeModifier.Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,new AttributeModifier(BASE_ATTACK_SPEED_ID,
                (tier==DefenderTier.GOLD?2.0:1.8)-4,AttributeModifier.Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_DAMAGE,new AttributeModifier(
                Identifier.fromNamespaceAndPath(DefendersMod.MOD_ID,"offhand_damage"),tier.offhandBonus,
                AttributeModifier.Operation.ADD_VALUE),EquipmentSlotGroup.OFFHAND)
            .add(Attributes.ATTACK_SPEED,new AttributeModifier(
                Identifier.fromNamespaceAndPath(DefendersMod.MOD_ID,"offhand_speed"),tier.offhandSpeed(),
                AttributeModifier.Operation.ADD_VALUE),EquipmentSlotGroup.OFFHAND).build();
    }

    @Override public InteractionResult use(Level level,Player player,InteractionHand hand){
        if(hand!=InteractionHand.OFF_HAND)return InteractionResult.FAIL;
        CombatEvents.beginBlocking(player); player.startUsingItem(hand); return InteractionResult.CONSUME;
    }
    @Override public ItemUseAnimation getUseAnimation(ItemStack stack){return ItemUseAnimation.BLOCK;}
    @Override public int getUseDuration(ItemStack stack,LivingEntity user){return 72000;}
    @Override public boolean releaseUsing(ItemStack stack,Level level,LivingEntity entity,int left){
        if(entity instanceof Player player)CombatEvents.endBlocking(player); return false;
    }
    @Override public void hurtEnemy(ItemStack stack,LivingEntity target,LivingEntity attacker){
        stack.hurtAndBreak(1,attacker,net.minecraft.world.entity.EquipmentSlot.MAINHAND);
    }
    @Override public void appendHoverText(ItemStack stack,Item.TooltipContext context,
            TooltipDisplay display,Consumer<Component> lines,TooltipFlag flag){
        lines.accept(Component.translatable("tooltip.defenders.parry").withStyle(ChatFormatting.AQUA));
        lines.accept(Component.translatable("tooltip.defenders.guard").withStyle(ChatFormatting.BLUE));
        lines.accept(Component.translatable("tooltip.defenders.ranged_parry").withStyle(ChatFormatting.DARK_GRAY));
    }
}
