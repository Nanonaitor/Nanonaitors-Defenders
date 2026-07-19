package com.vnano.defenders.registry;

import com.vnano.defenders.DefendersMod;
import com.vnano.defenders.item.DefenderTier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DefendersMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> DEFENDERS = TABS.register("defenders", () ->
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.defenders"))
            .icon(() -> ModItems.get(DefenderTier.DIAMOND).get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                ModItems.ALL.forEach((tier, item) -> {
                    if (tier.requiredMod == null || ModList.get().isLoaded(tier.requiredMod)) {
                        output.accept(item.get());
                    }
                });
                addBook(output, ModEnchantments.FOOTWORK);
                addBook(output, ModEnchantments.REPRISAL);
                addBook(output, ModEnchantments.FINESSE);
                addBook(output, ModEnchantments.REFLEXES);
                addBook(output, ModEnchantments.DEFLECTION);
                addBook(output, ModEnchantments.SIXTH_SENSE);
            }).build());

    private static void addBook(CreativeModeTab.Output output, RegistryObject<net.minecraft.world.item.enchantment.Enchantment> enchantment) {
        if (enchantment.get() instanceof com.vnano.defenders.enchantment.DefenderEnchantment defender && defender.enabled()) {
            output.accept(EnchantedBookItem.createForEnchantment(
                new EnchantmentInstance(enchantment.get(), enchantment.get().getMaxLevel())));
        }
    }

    private ModTabs() {}
}
