package com.vnano.defenders.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.vnano.defenders.enchantment.ModEnchantments;
import com.vnano.defenders.enchantment.EnchantmentDefender;
import com.vnano.defenders.compat.CompatEffects;
import com.vnano.defenders.compat.CompatManager;
import com.vnano.defenders.config.DefenderConfig;
import com.vnano.defenders.event.DefenderCombatHandler;
import java.util.List;
import java.util.UUID;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.creativetab.CreativeTabs;
import com.vnano.defenders.DefendersMod;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public final class ItemDefender extends Item {
    private static final DecimalFormat VALUE_FORMAT = new DecimalFormat("0.##",
        DecimalFormatSymbols.getInstance(Locale.ROOT));
    private static final UUID ATTACK_BONUS_UUID = UUID.fromString("3a9472fd-19f1-48be-a2b0-746796e68f51");
    private static final UUID ATTACK_SPEED_BONUS_UUID = UUID.fromString("bf923046-29e0-4a1b-9c48-40d41d52ff25");
    private static final ResourceLocation ADVANCED_MENDING =
        new ResourceLocation("somanyenchantments", "advancedmending");
    private final DefenderTier tier;

    public ItemDefender(DefenderTier tier) {
        this.tier = tier;
        setRegistryName("defender_" + tier.id);
        setUnlocalizedName("defenders.defender_" + tier.id);
        setMaxStackSize(1);
        setMaxDamage(tier.durability);
        setCreativeTab(DefendersMod.CREATIVE_TAB);
        addPropertyOverride(new ResourceLocation("blocking"), (stack, world, entity) ->
            entity != null && entity.isHandActive() && entity.getActiveItemStack() == stack ? 1.0F : 0.0F);
    }

    public DefenderTier getTier() {
        return tier;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab) && CompatManager.isTierAvailable(tier)) items.add(new ItemStack(this));
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BLOCK;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (hand != EnumHand.OFF_HAND) return new ActionResult<>(EnumActionResult.FAIL, stack);
        DefenderCombatHandler.beginBlocking(player);
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
        if (entity instanceof EntityPlayer) DefenderCombatHandler.endBlocking((EntityPlayer) entity);
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> modifiers = super.getItemAttributeModifiers(slot);
        if (slot == EntityEquipmentSlot.OFFHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                new AttributeModifier(ATTACK_BONUS_UUID, "Defender melee bonus", DefenderConfig.getOffhandDamage(tier), 0));
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                new AttributeModifier(ATTACK_SPEED_BONUS_UUID, "Defender attack speed bonus",
                    DefenderConfig.getOffhandAttackSpeed(tier), 0));
        } else if (slot == EntityEquipmentSlot.MAINHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", DefenderConfig.getMainHandDamage(tier) - 1.0D, 0));
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier",
                    DefenderConfig.getMainHandAttackSpeed(tier) - 4.0D, 0));
        }
        return modifiers;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create(super.getAttributeModifiers(slot, stack));
        if (slot == EntityEquipmentSlot.OFFHAND) {
            String attackDamage = SharedMonsterAttributes.ATTACK_DAMAGE.getName();
            modifiers.get(attackDamage).removeIf(modifier -> ATTACK_BONUS_UUID.equals(modifier.getID()));
            int finesse = ModEnchantments.getLevel(ModEnchantments.FINESSE, stack);
            double total = DefenderConfig.getOffhandDamage(tier) + finesse * DefenderConfig.finesseDamagePerLevel;
            modifiers.put(attackDamage,
                new AttributeModifier(ATTACK_BONUS_UUID, "Defender melee bonus", total, 0));
        }
        return modifiers;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (DefenderConfig.mainHandHitDurabilityCost > 0) {
            stack.damageItem(DefenderConfig.mainHandHitDurabilityCost, attacker);
        }
        CompatEffects.applyMainHandHit(tier, target, attacker);
        return true;
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return tier.enchantability;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.UNBREAKING
            || enchantment == Enchantments.MENDING
            || ADVANCED_MENDING.equals(enchantment.getRegistryName())
            || enchantment == Enchantments.SHARPNESS
            || enchantment == Enchantments.SMITE
            || enchantment == Enchantments.BANE_OF_ARTHROPODS
            || enchantment == Enchantments.KNOCKBACK
            || enchantment == Enchantments.FIRE_ASPECT
            || enchantment == Enchantments.LOOTING
            || enchantment == Enchantments.SWEEPING
            || (enchantment instanceof EnchantmentDefender
                && ((EnchantmentDefender) enchantment).isEnabled())
            || DefenderConfig.isAdditionalEnchantmentAllowed(enchantment);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return CompatManager.matchesIngredient(tier.repairIngredient, repair) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity) {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(TextFormatting.AQUA + "Parry within 1 second of blocking.");
        tooltip.add(TextFormatting.BLUE + "Reduce Melee Damage When Blocking");
        tooltip.add(TextFormatting.DARK_GRAY + "Cannot parry ranged damage");
        addMaterialEffectTooltips(tooltip);
        if (tier == DefenderTier.LIVING && DefenderConfig.showEvolutionProgress && stack.hasTagCompound()) {
            tooltip.add(TextFormatting.BLUE + "--->" + stack.getTagCompound().getInteger("srpkills"));
            tooltip.add(TextFormatting.BLUE.toString());
        }
    }

    private void addMaterialEffectTooltips(List<String> tooltip) {
        if (tier == DefenderTier.SILVER && DefenderConfig.enableSilverUndeadDamage) {
            addEffect(tooltip, "tooltip.defenders.silver", value(DefenderConfig.silverUndeadMainHandBonus),
                value(DefenderConfig.silverUndeadParryDamage));
        }
        if (tier.isMyrmex() && DefenderConfig.enableMyrmexCreatureDamage) {
            addEffect(tooltip, "tooltip.defenders.myrmex", value(DefenderConfig.myrmexNonArthropodBonus));
        }
        if (tier.isVenom() && DefenderConfig.enableVenomParryPoison) {
            addEffect(tooltip, "tooltip.defenders.stinger", level(DefenderConfig.venomAmplifier),
                seconds(DefenderConfig.venomDurationTicks));
        }
        if ((DefenderConfig.enableElementalMainHandEffects || DefenderConfig.enableElementalParryEffects)
            && tier == DefenderTier.FLAMED_DRAGONBONE) {
            addEffect(tooltip, "tooltip.defenders.flamed", value(DefenderConfig.flameDurationSeconds),
                value(DefenderConfig.dragonOpposedElementBonus));
        }
        if ((DefenderConfig.enableElementalMainHandEffects || DefenderConfig.enableElementalParryEffects)
            && tier == DefenderTier.ICED_DRAGONBONE) {
            addEffect(tooltip, "tooltip.defenders.iced", seconds(DefenderConfig.frozenDurationTicks),
                level(DefenderConfig.iceDebuffAmplifier), seconds(DefenderConfig.iceDebuffDurationTicks),
                value(DefenderConfig.dragonOpposedElementBonus));
        }
        if ((DefenderConfig.enableElementalMainHandEffects || DefenderConfig.enableElementalParryEffects)
            && tier == DefenderTier.ELECTRIC_DRAGONBONE) {
            addEffect(tooltip, "tooltip.defenders.electric", value(DefenderConfig.lightningDragonBonus));
        }
        if (DefenderConfig.enableLivingParryEffects && tier == DefenderTier.LIVING) {
            addEffect(tooltip, "tooltip.defenders.living", level(DefenderConfig.immalleableAmplifier),
                seconds(DefenderConfig.livingEffectDurationTicks));
        }
        if (DefenderConfig.enableLivingParryEffects && tier == DefenderTier.SENTIENT) {
            addEffect(tooltip, "tooltip.defenders.sentient", level(DefenderConfig.immalleableAmplifier),
                level(DefenderConfig.viralAmplifier), seconds(DefenderConfig.livingEffectDurationTicks));
        }
    }

    private static void addEffect(List<String> tooltip, String key, Object... values) {
        tooltip.add(TextFormatting.DARK_GREEN
            + net.minecraft.util.text.translation.I18n.translateToLocalFormatted(key, values));
    }

    private static String seconds(int ticks) {
        return value(ticks / 20.0D);
    }

    private static String value(double number) {
        synchronized (VALUE_FORMAT) {
            return VALUE_FORMAT.format(number);
        }
    }

    private static String level(int zeroBasedAmplifier) {
        int level = zeroBasedAmplifier + 1;
        String[] roman = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return level <= roman.length ? roman[level - 1] : Integer.toString(level);
    }

}
