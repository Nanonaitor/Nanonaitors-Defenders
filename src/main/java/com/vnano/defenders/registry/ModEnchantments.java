package com.vnano.defenders.registry;

import com.vnano.defenders.DefendersMod;
import com.vnano.defenders.enchantment.DefenderEnchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, DefendersMod.MOD_ID);
    public static final RegistryObject<Enchantment> FOOTWORK = register("footwork", Enchantment.Rarity.UNCOMMON, 3);
    public static final RegistryObject<Enchantment> REPRISAL = register("reprisal", Enchantment.Rarity.RARE, 3);
    public static final RegistryObject<Enchantment> FINESSE = register("finesse", Enchantment.Rarity.RARE, 3);
    public static final RegistryObject<Enchantment> REFLEXES = register("reflexes", Enchantment.Rarity.UNCOMMON, 3);
    public static final RegistryObject<Enchantment> DEFLECTION = register("deflection", Enchantment.Rarity.RARE, 3);
    public static final RegistryObject<Enchantment> SIXTH_SENSE = register("sixth_sense", Enchantment.Rarity.VERY_RARE, 1);

    private static RegistryObject<Enchantment> register(String id, Enchantment.Rarity rarity, int max) {
        return ENCHANTMENTS.register(id, () -> new DefenderEnchantment(id, rarity, max));
    }

    public static int level(RegistryObject<Enchantment> enchantment, ItemStack stack) {
        Enchantment value = enchantment.get();
        return value instanceof DefenderEnchantment defender && defender.enabled()
            ? EnchantmentHelper.getItemEnchantmentLevel(value, stack) : 0;
    }

    private ModEnchantments() {}
}
