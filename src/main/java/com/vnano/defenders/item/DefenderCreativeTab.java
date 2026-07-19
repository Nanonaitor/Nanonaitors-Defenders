package com.vnano.defenders.item;

import com.vnano.defenders.config.DefenderConfig;
import com.vnano.defenders.enchantment.EnchantmentDefender;
import com.vnano.defenders.enchantment.ModEnchantments;
import com.vnano.defenders.registry.ModContent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.util.NonNullList;

public final class DefenderCreativeTab extends CreativeTabs {
    public DefenderCreativeTab() {
        super("defenders");
    }

    @Override
    public ItemStack getTabIconItem() {
        ItemDefender diamond = ModContent.DEFENDERS.get(DefenderTier.DIAMOND);
        return diamond == null ? new ItemStack(Items.SHIELD) : new ItemStack(diamond);
    }

    @Override
    public void displayAllRelevantItems(NonNullList<ItemStack> items) {
        super.displayAllRelevantItems(items);
        addBook(items, ModEnchantments.FOOTWORK);
        addBook(items, ModEnchantments.REPRISAL);
        addBook(items, ModEnchantments.FINESSE);
        addBook(items, ModEnchantments.REFLEXES);
        addBook(items, ModEnchantments.DEFLECTION);
        addBook(items, ModEnchantments.SIXTH_SENSE);
    }

    private static void addBook(NonNullList<ItemStack> items, EnchantmentDefender enchantment) {
        if (enchantment != null && DefenderConfig.isDefenderEnchantmentEnabled(
            enchantment.getRegistryName().getResourcePath())) {
            items.add(ItemEnchantedBook.getEnchantedItemStack(
                new EnchantmentData(enchantment, enchantment.getMaxLevel())));
        }
    }
}
