package com.vnano.defenders;

import com.vnano.defenders.config.DefenderConfig;
import com.vnano.defenders.registry.ModEnchantments;
import com.vnano.defenders.registry.ModItems;
import com.vnano.defenders.registry.ModEffects;
import com.vnano.defenders.registry.ModTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DefendersMod.MOD_ID)
public final class DefendersMod {
    public static final String MOD_ID = "defenders";

    public DefendersMod(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        ModItems.ITEMS.register(modBus);
        ModEnchantments.ENCHANTMENTS.register(modBus);
        ModEffects.EFFECTS.register(modBus);
        ModTabs.TABS.register(modBus);
        context.registerConfig(ModConfig.Type.COMMON, DefenderConfig.SPEC);
        MinecraftForge.EVENT_BUS.register(this);
    }

}
