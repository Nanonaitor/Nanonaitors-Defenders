package com.vnano.defenders.enchantment;

import com.vnano.defenders.DefendersMod;
import com.vnano.defenders.config.DefenderConfig;
import com.vnano.defenders.item.ItemDefender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class EnchantmentDefender extends Enchantment {
    private final int maxLevel;
    private final String id;

    public EnchantmentDefender(String id, Rarity rarity, int maxLevel) {
        super(rarity, EnumEnchantmentType.BREAKABLE, new EntityEquipmentSlot[] { EntityEquipmentSlot.OFFHAND });
        this.id = id;
        this.maxLevel = maxLevel;
        setRegistryName(id);
        setName("defenders." + id);
    }

    @Override
    public int getMinEnchantability(int level) {
        return 8 + (level - 1) * 10;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return getMinEnchantability(level) + 20;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    public boolean isEnabled() {
        return DefenderConfig.isDefenderEnchantmentEnabled(id);
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return isEnabled() && stack.getItem() instanceof ItemDefender;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return canApply(stack);
    }

    @Override
    public boolean isAllowedOnBooks() {
        return isEnabled() && super.isAllowedOnBooks();
    }

    @Override
    public boolean canApplyTogether(Enchantment other) {
        ResourceLocation otherId = other.getRegistryName();
        boolean thisNeedsParries = "reflexes".equals(id) || "sixth_sense".equals(id);
        boolean otherNeedsParries = otherId != null
            && ("reflexes".equals(otherId.getResourcePath()) || "sixth_sense".equals(otherId.getResourcePath()));
        boolean opposite = otherId != null && DefendersMod.MOD_ID.equals(otherId.getResourceDomain())
            && ((thisNeedsParries && "deflection".equals(otherId.getResourcePath()))
                || ("deflection".equals(id) && otherNeedsParries));
        return !opposite && super.canApplyTogether(other);
    }
}
