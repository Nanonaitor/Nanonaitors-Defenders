package com.vnano.defenders;
import com.vnano.defenders.client.ClientInputEvents;
import com.vnano.defenders.config.DefenderConfig;
import com.vnano.defenders.event.CombatEvents;
import com.vnano.defenders.registry.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
@Mod(DefendersMod.MOD_ID)
public final class DefendersMod {
    public static final String MOD_ID="defenders";
    public DefendersMod(FMLJavaModLoadingContext context){
        var group=context.getModBusGroup();
        ModItems.ITEMS.register(group); ModEffects.EFFECTS.register(group); ModTabs.TABS.register(group);
        context.registerConfig(ModConfig.Type.COMMON,DefenderConfig.SPEC);
        LivingAttackEvent.BUS.addListener(CombatEvents::onAttack);
        LivingHurtEvent.BUS.addListener(CombatEvents::onHurt);
        TickEvent.PlayerTickEvent.Post.BUS.addListener(CombatEvents::onTick);
        if(FMLEnvironment.dist==Dist.CLIENT)ClientInputEvents.register();
    }
}
