package com.vnano.defenders.registry;
import com.vnano.defenders.DefendersMod;
import com.vnano.defenders.item.*;
import java.util.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.UseEffects;
import net.minecraftforge.registries.*;
public final class ModItems {
    public static final DeferredRegister<Item> ITEMS=DeferredRegister.create(ForgeRegistries.ITEMS,DefendersMod.MOD_ID);
    public static final Map<DefenderTier,RegistryObject<Item>> ALL=new EnumMap<>(DefenderTier.class);
    static { for(DefenderTier tier:DefenderTier.values()) {
        String id="defender_"+tier.id;
        ALL.put(tier,ITEMS.register(id,()->new ItemDefender(tier,new Item.Properties()
            .setId(ITEMS.key(id)).durability(tier.durability).repairable(tier.repairItems)
            .enchantable(tier.enchantability).attributes(ItemDefender.attributes(tier))
            .component(DataComponents.USE_EFFECTS,new UseEffects(false,true,
                tier==DefenderTier.WOOD?.6f:.5f)))));
    }}
    public static RegistryObject<Item> get(DefenderTier tier){return ALL.get(tier);}
    private ModItems(){}
}
