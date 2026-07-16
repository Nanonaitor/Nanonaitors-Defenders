package com.vnano.defenders.recipe;

import com.vnano.defenders.DefendersMod;
import com.vnano.defenders.compat.CompatManager;
import com.vnano.defenders.item.DefenderTier;
import com.vnano.defenders.item.ItemDefender;
import com.vnano.defenders.registry.ModContent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod.EventBusSubscriber(modid = DefendersMod.MOD_ID)
public final class ModRecipes {
    private ModRecipes() {}

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        registerBase(event, DefenderTier.SILVER);
        registerBase(event, DefenderTier.BRONZE);
        registerBase(event, DefenderTier.STEEL);
        registerBase(event, DefenderTier.UMBRIUM);
        registerBase(event, DefenderTier.DRAGONBONE);
        registerBase(event, DefenderTier.DESERT_MYRMEX);
        registerBase(event, DefenderTier.JUNGLE_MYRMEX);

        if (!CompatManager.hasDragonForge()) {
            registerUpgrade(event, DefenderTier.DRAGONBONE, DefenderTier.FLAMED_DRAGONBONE,
                "iceandfire:fire_dragon_blood");
            registerUpgrade(event, DefenderTier.DRAGONBONE, DefenderTier.ICED_DRAGONBONE,
                "iceandfire:ice_dragon_blood");
            registerUpgrade(event, DefenderTier.DRAGONBONE, DefenderTier.ELECTRIC_DRAGONBONE,
                "iceandfire:lightning_dragon_blood");
        }
        registerMyrmexStinger(event, DefenderTier.DESERT_VENOM);
        registerMyrmexStinger(event, DefenderTier.JUNGLE_VENOM);

        if (CompatManager.isTierAvailable(DefenderTier.LIVING)
            && CompatManager.hasItem("srparasites:infectious_blade_fragment")
            && CompatManager.hasItem("srparasites:hardened_bone_handle")) {
            ItemStack output = new ItemStack(ModContent.DEFENDERS.get(DefenderTier.LIVING));
            ShapedOreRecipe recipe = new ShapedOreRecipe(new ResourceLocation(DefendersMod.MOD_ID, "compat"),
                output, " F", "HC", " F",
                'F', CompatManager.itemStack("srparasites:infectious_blade_fragment"),
                'C', CompatManager.itemStack("srparasites:living_core"),
                'H', CompatManager.itemStack("srparasites:hardened_bone_handle"));
            recipe.setRegistryName(DefendersMod.MOD_ID, "defender_living");
            event.getRegistry().register(recipe);
        }
    }

    private static void registerMyrmexStinger(RegistryEvent.Register<IRecipe> event, DefenderTier tier) {
        if (!CompatManager.isTierAvailable(tier) || !CompatManager.hasItem("iceandfire:myrmex_stinger")) return;
        Object chitin = ingredient(tier.repairIngredient);
        if (chitin == null) return;
        ShapedOreRecipe recipe = new ShapedOreRecipe(new ResourceLocation(DefendersMod.MOD_ID, "compat"),
            new ItemStack(ModContent.DEFENDERS.get(tier)), " V", "SM", " M",
            'V', CompatManager.itemStack("iceandfire:myrmex_stinger"),
            'M', chitin, 'S', new ItemStack(Items.STICK));
        recipe.setRegistryName(DefendersMod.MOD_ID, "defender_" + tier.id);
        event.getRegistry().register(recipe);
    }

    private static void registerBase(RegistryEvent.Register<IRecipe> event, DefenderTier tier) {
        if (!CompatManager.isTierAvailable(tier)) return;
        Object material = ingredient(tier.repairIngredient);
        if (material == null) return;
        ShapedOreRecipe recipe = new ShapedOreRecipe(new ResourceLocation(DefendersMod.MOD_ID, "compat"),
            new ItemStack(ModContent.DEFENDERS.get(tier)), " M", "SM", " M",
            'M', material, 'S', new ItemStack(Items.STICK));
        recipe.setRegistryName(DefendersMod.MOD_ID, "defender_" + tier.id);
        event.getRegistry().register(recipe);
    }

    private static void registerUpgrade(RegistryEvent.Register<IRecipe> event, DefenderTier source,
                                        DefenderTier target, String catalyst) {
        if (!CompatManager.isTierAvailable(target) || !CompatManager.hasItem(catalyst)) return;
        DefenderUpgradeRecipe recipe = new DefenderUpgradeRecipe(
            ModContent.DEFENDERS.get(source), ModContent.DEFENDERS.get(target), catalyst);
        recipe.setRegistryName(DefendersMod.MOD_ID, "defender_" + target.id);
        event.getRegistry().register(recipe);
    }

    private static Object ingredient(String descriptor) {
        if (descriptor.startsWith("ore:")) return descriptor.substring(4);
        ItemStack stack = CompatManager.itemStack(descriptor);
        return stack.isEmpty() ? null : stack;
    }
}
