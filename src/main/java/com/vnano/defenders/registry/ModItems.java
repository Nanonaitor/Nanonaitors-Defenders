package com.vnano.defenders.registry;
import com.vnano.defenders.DefendersMod; import com.vnano.defenders.item.*; import java.util.*; import net.minecraft.world.item.Item; import net.minecraftforge.registries.*;
public final class ModItems {
 public static final DeferredRegister<Item> ITEMS=DeferredRegister.create(ForgeRegistries.ITEMS,DefendersMod.MOD_ID);
 public static final Map<DefenderTier,RegistryObject<Item>> ALL=new EnumMap<>(DefenderTier.class);
 static{for(DefenderTier t:DefenderTier.values())ALL.put(t,ITEMS.register("defender_"+t.id,()->new ItemDefender(t,new Item.Properties().durability(t.durability))));}
 public static RegistryObject<Item> get(DefenderTier t){return ALL.get(t);} private ModItems(){}
}
