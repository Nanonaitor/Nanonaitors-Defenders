package com.vnano.defenders.enchantment;

import com.vnano.defenders.config.DefenderConfig;
import com.vnano.defenders.item.ItemDefender;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public final class DefenderEnchantment extends Enchantment {
    private static final EnchantmentCategory TYPE = EnchantmentCategory.create("defender", item -> item instanceof ItemDefender);
    private final String id;
    private final int max;

    public DefenderEnchantment(String id, Rarity rarity, int max) {
        super(rarity, TYPE, new EquipmentSlot[]{EquipmentSlot.OFFHAND});
        this.id = id;
        this.max = max;
    }

    public String id() { return id; }
    public boolean enabled() { return DefenderConfig.enchantmentEnabled(id); }
    @Override public int getMaxLevel() { return max; }
    @Override public int getMinCost(int level) { return 8 + (level - 1) * 10; }
    @Override public int getMaxCost(int level) { return getMinCost(level) + 20; }
    @Override public boolean isDiscoverable() { return enabled(); }
    @Override public boolean isTradeable() { return enabled(); }
    @Override public boolean isAllowedOnBooks() { return enabled(); }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        if (other instanceof DefenderEnchantment defender) {
            if ("deflection".equals(id)
                && ("reflexes".equals(defender.id) || "sixth_sense".equals(defender.id))) return false;
            if (("reflexes".equals(id) || "sixth_sense".equals(id))
                && "deflection".equals(defender.id)) return false;
        }
        return super.checkCompatibility(other);
    }
}
