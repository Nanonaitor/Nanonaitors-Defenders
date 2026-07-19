package com.vnano.defenders.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.vnano.defenders.config.DefenderConfig;
import com.vnano.defenders.enchantment.DefenderEnchantment;
import com.vnano.defenders.event.CombatEvents;
import com.vnano.defenders.registry.ModEnchantments;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public final class ItemDefender extends Item {
    private static final UUID OFFHAND_DAMAGE_UUID = UUID.fromString("3a9472fd-19f1-48be-a2b0-746796e68f51");
    private static final UUID OFFHAND_SPEED_UUID = UUID.fromString("bf923046-29e0-4a1b-9c48-40d41d52ff25");
    private static final Set<String> STANDARD_ENCHANTMENTS = Set.of(
        "unbreaking", "mending", "sharpness", "smite", "bane_of_arthropods",
        "knockback", "fire_aspect", "looting", "sweeping");
    private final DefenderTier tier;

    public ItemDefender(DefenderTier tier, Properties properties) { super(properties); this.tier = tier; }
    public DefenderTier tier() { return tier; }
    @Override public int getUseDuration(ItemStack stack) { return 72000; }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BLOCK; }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.OFF_HAND) return InteractionResultHolder.fail(stack);
        CombatEvents.beginBlocking(player);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int left) {
        if (entity instanceof Player player) CombatEvents.endBlocking(player);
    }

    @Override public int getEnchantmentValue() { return tier.enchantability; }
    @Override public boolean isEnchantable(ItemStack stack) { return true; }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        int cost = DefenderConfig.MAINHAND_DURABILITY_COST.get();
        if (cost > 0) stack.hurtAndBreak(cost, attacker, entity -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment instanceof DefenderEnchantment defender) return defender.enabled();
        ResourceLocation id = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
        return id != null && (STANDARD_ENCHANTMENTS.contains(id.getPath())
            || "advanced_mending".equals(id.getPath()) || "advancedmending".equals(id.getPath())
            || DefenderConfig.additionalEnchantmentAllowed(enchantment));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributes = ImmutableMultimap.builder();
        if (slot == EquipmentSlot.MAINHAND) {
            attributes.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                DefenderConfig.mainHandDamage(tier) - 1, AttributeModifier.Operation.ADDITION));
            attributes.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                DefenderConfig.mainHandSpeed(tier) - 4, AttributeModifier.Operation.ADDITION));
        } else if (slot == EquipmentSlot.OFFHAND) {
            int finesse = ModEnchantments.level(ModEnchantments.FINESSE, stack);
            attributes.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(OFFHAND_DAMAGE_UUID, "Defender offhand damage",
                DefenderConfig.offhandDamage(tier) + finesse * DefenderConfig.FINESSE_DAMAGE_PER_LEVEL.get(), AttributeModifier.Operation.ADDITION));
            attributes.put(Attributes.ATTACK_SPEED, new AttributeModifier(OFFHAND_SPEED_UUID, "Defender offhand speed",
                DefenderConfig.offhandSpeed(tier), AttributeModifier.Operation.ADDITION));
        }
        return attributes.build();
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.defenders.parry").withStyle(ChatFormatting.AQUA));
        lines.add(Component.translatable("tooltip.defenders.guard").withStyle(ChatFormatting.BLUE));
        lines.add(Component.translatable("tooltip.defenders.ranged_parry").withStyle(ChatFormatting.DARK_GRAY));
        appendMaterialText(lines);
        if (tier == DefenderTier.DRAGONBONE) {
            lines.add(Component.translatable("tooltip.defenders.monkeys_uncle").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
        if (tier == DefenderTier.LIVING && DefenderConfig.SHOW_EVOLUTION_PROGRESS.get()) {
            lines.add(Component.translatable("tooltip.defenders.living_progress",
                stack.getOrCreateTag().getInt("srpkills")).withStyle(ChatFormatting.DARK_GREEN));
        }
    }

    private void appendMaterialText(List<Component> lines) {
        String key = switch (tier) {
            case SILVER -> "tooltip.defenders.silver";
            case DESERT_MYRMEX, JUNGLE_MYRMEX -> "tooltip.defenders.myrmex";
            case DESERT_VENOM, JUNGLE_VENOM -> "tooltip.defenders.stinger";
            case FLAMED_DRAGONBONE -> "tooltip.defenders.flamed";
            case ICED_DRAGONBONE -> "tooltip.defenders.iced";
            case ELECTRIC_DRAGONBONE -> "tooltip.defenders.electric";
            case LIVING -> "tooltip.defenders.living";
            case SENTIENT -> "tooltip.defenders.sentient";
            default -> null;
        };
        if (key != null) lines.add(Component.translatable(key).withStyle(ChatFormatting.DARK_GREEN));
    }
}
