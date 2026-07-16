package com.vnano.defenders.recipe;

import com.vnano.defenders.DefendersMod;
import com.vnano.defenders.compat.CompatManager;
import com.vnano.defenders.item.ItemDefender;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/** A JEI-visible shapeless recipe that carries the original Defender's data forward. */
public final class DefenderUpgradeRecipe extends ShapelessOreRecipe {
    private final ItemDefender source;
    private final ItemDefender result;

    public DefenderUpgradeRecipe(ItemDefender source, ItemDefender result, String catalyst) {
        super(new ResourceLocation(DefendersMod.MOD_ID, "compat"), new ItemStack(result),
            new ItemStack(source), CompatManager.itemStack(catalyst));
        this.source = source;
        this.result = result;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ItemStack original = findSource(inventory);
        if (original.isEmpty()) return ItemStack.EMPTY;
        ItemStack upgraded = new ItemStack(result);
        if (original.hasTagCompound()) upgraded.setTagCompound(original.getTagCompound().copy());
        float used = original.getMaxDamage() <= 0 ? 0F
            : (float) original.getItemDamage() / (float) original.getMaxDamage();
        upgraded.setItemDamage(Math.min(upgraded.getMaxDamage() - 1,
            Math.round(used * upgraded.getMaxDamage())));
        return upgraded;
    }

    private ItemStack findSource(InventoryCrafting inventory) {
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() == source) return stack;
        }
        return ItemStack.EMPTY;
    }
}
