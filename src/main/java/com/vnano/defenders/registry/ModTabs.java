package com.vnano.defenders.registry;
import com.vnano.defenders.DefendersMod;
import com.vnano.defenders.item.DefenderTier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.registries.*;
public final class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS=DeferredRegister.create(Registries.CREATIVE_MODE_TAB,DefendersMod.MOD_ID);
    public static final RegistryObject<CreativeModeTab> MAIN=TABS.register("defenders",()->CreativeModeTab.builder()
        .withTabsBefore(CreativeModeTabs.COMBAT).title(Component.translatable("itemGroup.defenders"))
        .icon(()->ModItems.get(DefenderTier.DIAMOND).get().getDefaultInstance())
        .displayItems((parameters,output)->ModItems.ALL.values().forEach(item->output.accept(item.get()))).build());
    private ModTabs(){}
}
