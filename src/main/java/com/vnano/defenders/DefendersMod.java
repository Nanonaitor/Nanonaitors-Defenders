package com.vnano.defenders;

import com.vnano.defenders.config.DefenderConfig;
import com.vnano.defenders.item.DefenderCreativeTab;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
    modid = DefendersMod.MOD_ID,
    name = DefendersMod.NAME,
    version = DefendersMod.VERSION,
    acceptedMinecraftVersions = "[1.12.2]"
)
public final class DefendersMod {
    public static final String MOD_ID = "defenders";
    public static final String NAME = "Defenders";
    public static final String VERSION = "1.1.2";
    public static final CreativeTabs CREATIVE_TAB = new DefenderCreativeTab();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        DefenderConfig.load(event.getSuggestedConfigurationFile());
    }
}
