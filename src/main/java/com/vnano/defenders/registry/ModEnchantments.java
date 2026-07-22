package com.vnano.defenders.registry;
import com.vnano.defenders.DefendersMod;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
public final class ModEnchantments {
    public static final ResourceKey<Enchantment> FOOTWORK=key("footwork"), REPRISAL=key("reprisal"),
        FINESSE=key("finesse"), REFLEXES=key("reflexes"), DEFLECTION=key("deflection"),
        SIXTH_SENSE=key("sixth_sense");
    private static ResourceKey<Enchantment> key(String id){return ResourceKey.create(Registries.ENCHANTMENT,Identifier.fromNamespaceAndPath(DefendersMod.MOD_ID,id));}
    public static int level(ResourceKey<Enchantment> key,ItemStack stack){
        ItemEnchantments all=stack.getOrDefault(DataComponents.ENCHANTMENTS,ItemEnchantments.EMPTY);
        for(Holder<Enchantment> holder:all.keySet()) if(holder.unwrapKey().filter(key::equals).isPresent()) return all.getLevel(holder);
        return 0;
    }
    private ModEnchantments(){}
}
