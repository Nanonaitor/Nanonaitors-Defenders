package com.vnano.defenders.enchantment;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public final class ModEnchantments {
    public static EnchantmentDefender FOOTWORK;
    public static EnchantmentDefender REPRISAL;
    public static EnchantmentDefender FINESSE;
    public static EnchantmentDefender REFLEXES;
    public static EnchantmentDefender DEFLECTION;
    public static EnchantmentDefender SIXTH_SENSE;

    public static int getLevel(EnchantmentDefender enchantment, ItemStack stack) {
        return enchantment != null && enchantment.isEnabled()
            ? EnchantmentHelper.getEnchantmentLevel(enchantment, stack) : 0;
    }

    private ModEnchantments() {}
}
