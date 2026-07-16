package com.vnano.defenders.enchantment;

import com.vnano.defenders.item.ItemDefender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public final class EnchantmentDefender extends Enchantment {
    private final int maxLevel;

    public EnchantmentDefender(String id, Rarity rarity, int maxLevel) {
        super(rarity, EnumEnchantmentType.BREAKABLE, new EntityEquipmentSlot[] { EntityEquipmentSlot.OFFHAND });
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

    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof ItemDefender;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return canApply(stack);
    }
}
