package com.vnano.defenders.compat;

import com.vnano.defenders.config.DefenderConfig;
import com.vnano.defenders.item.DefenderTier;
import java.lang.reflect.Field;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public final class CompatManager {
    private CompatManager() {}

    public static boolean isTierAvailable(DefenderTier tier) {
        switch (tier.family) {
            case VANILLA:
                return true;
            case SPARTAN:
                return DefenderConfig.enableSpartanMaterials && hasOre(tier.repairIngredient.substring(4));
            case DEFILED:
                return DefenderConfig.enableDefiledLands && hasItem("defiledlands:umbrium_ingot");
            case ICE_AND_FIRE:
                return DefenderConfig.enableIceAndFire && hasItem("iceandfire:dragonbone");
            case FIRE_DRAGONBONE:
                return DefenderConfig.enableIceAndFire && hasItem("iceandfire:fire_dragon_blood");
            case ICE_DRAGONBONE:
                return DefenderConfig.enableIceAndFire && hasItem("iceandfire:ice_dragon_blood");
            case LIGHTNING_DRAGONBONE:
                return DefenderConfig.enableIceAndFire && hasItem("iceandfire:lightning_dragon_blood");
            case DESERT_MYRMEX:
                return DefenderConfig.enableIceAndFire && hasItem("iceandfire:myrmex_desert_chitin");
            case JUNGLE_MYRMEX:
                return DefenderConfig.enableIceAndFire && hasItem("iceandfire:myrmex_jungle_chitin");
            case SRP:
                return DefenderConfig.enableSRParasites && Loader.isModLoaded("srparasites")
                    && hasItem("srparasites:living_core");
            default:
                return false;
        }
    }

    public static boolean hasItem(String id) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        return item != null && item != net.minecraft.init.Items.AIR;
    }

    public static ItemStack itemStack(String id) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        return item == null || item == net.minecraft.init.Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
    }

    public static boolean hasOre(String ore) {
        return OreDictionary.doesOreNameExist(ore) && !OreDictionary.getOres(ore, false).isEmpty();
    }

    /** Detects Ice and Fire versions that provide a native Dragon Forge block. */
    public static boolean hasDragonForge() {
        if (!Loader.isModLoaded("iceandfire")) return false;
        for (ResourceLocation id : ForgeRegistries.BLOCKS.getKeys()) {
            if (!"iceandfire".equals(id.getResourceDomain())) continue;
            String path = id.getResourcePath().replace("_", "").toLowerCase(java.util.Locale.ROOT);
            if (path.contains("dragonforge")) return true;
        }
        return false;
    }

    public static boolean matchesIngredient(String descriptor, ItemStack candidate) {
        if (candidate.isEmpty()) return false;
        if (descriptor.startsWith("ore:")) {
            for (ItemStack ore : OreDictionary.getOres(descriptor.substring(4), false)) {
                if (OreDictionary.itemMatches(ore, candidate, false)) return true;
            }
            return false;
        }
        ItemStack expected = itemStack(descriptor);
        return !expected.isEmpty() && OreDictionary.itemMatches(expected, candidate, false);
    }

    public static int getSrpEvolutionThreshold() {
        if (DefenderConfig.followSrpEvolutionThreshold && Loader.isModLoaded("srparasites")) {
            try {
                Class<?> config = Class.forName("com.dhanantry.scapeandrunparasites.util.config.SRPConfig");
                Field field = config.getField("weapon_livingSentient_HP_needed");
                return Math.max(1, field.getInt(null));
            } catch (ReflectiveOperationException | LinkageError ignored) {
                // Use the documented Defender fallback below.
            }
        }
        return DefenderConfig.evolutionHealthOverride;
    }

    /** Uses SRP's real parasite base class without making SRP a hard dependency. */
    public static boolean isSrpParasite(EntityLivingBase entity) {
        if (entity == null || !Loader.isModLoaded("srparasites")) return false;
        try {
            Class<?> parasiteBase = Class.forName(
                "com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase");
            return parasiteBase.isInstance(entity);
        } catch (ReflectiveOperationException | LinkageError ignored) {
            ResourceLocation id = EntityList.getKey(entity);
            return id != null && "srparasites".equals(id.getResourceDomain());
        }
    }
}
