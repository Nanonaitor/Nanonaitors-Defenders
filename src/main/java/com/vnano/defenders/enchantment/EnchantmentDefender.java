package com.vnano.defenders.enchantment;

import com.vnano.defenders.DefendersMod;
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

    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof ItemDefender;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return canApply(stack);
    }

    @Override
    public boolean canApplyTogether(Enchantment other) {
        ResourceLocation otherId = other.getRegistryName();
        boolean opposite = otherId != null && DefendersMod.MOD_ID.equals(otherId.getResourceDomain())
            && (("reflexes".equals(id) && "deflection".equals(otherId.getResourcePath()))
                || ("deflection".equals(id) && "reflexes".equals(otherId.getResourcePath())));
        return !opposite && super.canApplyTogether(other);
    }
}
