package com.vnano.defenders.client;
import com.vnano.defenders.DefendersMod; import com.vnano.defenders.registry.ModItems; import net.minecraft.client.renderer.item.ItemProperties; import net.minecraft.resources.ResourceLocation; import net.minecraftforge.api.distmarker.Dist; import net.minecraftforge.eventbus.api.SubscribeEvent; import net.minecraftforge.fml.common.Mod; import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
@Mod.EventBusSubscriber(modid=DefendersMod.MOD_ID,bus=Mod.EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class ClientEvents {
 @SubscribeEvent public static void setup(FMLClientSetupEvent e){e.enqueueWork(()->ModItems.ALL.values().forEach(i->ItemProperties.register(i.get(),new ResourceLocation(DefendersMod.MOD_ID,"blocking"),(stack,level,entity,seed)->entity!=null&&entity.isUsingItem()&&entity.getUseItem()==stack?1:0)));}
 private ClientEvents(){}
}
