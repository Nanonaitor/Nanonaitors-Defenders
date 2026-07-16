package com.vnano.defenders.registry;

import com.vnano.defenders.DefendersMod;
import com.vnano.defenders.enchantment.EnchantmentDefender;
import com.vnano.defenders.enchantment.ModEnchantments;
import com.vnano.defenders.item.DefenderTier;
import com.vnano.defenders.item.ItemDefender;
import com.vnano.defenders.potion.PotionVulnerable;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = DefendersMod.MOD_ID)
public final class ModContent {
    public static final Map<DefenderTier, ItemDefender> DEFENDERS = new EnumMap<>(DefenderTier.class);
    public static PotionVulnerable VULNERABLE;

    private ModContent() {}

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (DefenderTier tier : DefenderTier.values()) {
            ItemDefender item = new ItemDefender(tier);
            DEFENDERS.put(tier, item);
            event.getRegistry().register(item);
        }
    }

    @SubscribeEvent
    public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        ModEnchantments.FOOTWORK = new EnchantmentDefender("footwork", Enchantment.Rarity.UNCOMMON, 3);
        ModEnchantments.FORTIFICATION = new EnchantmentDefender("fortification", Enchantment.Rarity.UNCOMMON, 3);
        ModEnchantments.REPRISAL = new EnchantmentDefender("reprisal", Enchantment.Rarity.RARE, 3);
        ModEnchantments.FINESSE = new EnchantmentDefender("finesse", Enchantment.Rarity.RARE, 3);
        ModEnchantments.REFLEXES = new EnchantmentDefender("reflexes", Enchantment.Rarity.UNCOMMON, 3);
        ModEnchantments.DEFLECTION = new EnchantmentDefender("deflection", Enchantment.Rarity.RARE, 3);
        ModEnchantments.SIXTH_SENSE = new EnchantmentDefender("sixth_sense", Enchantment.Rarity.VERY_RARE, 1);
        event.getRegistry().registerAll(
            ModEnchantments.FOOTWORK,
            ModEnchantments.FORTIFICATION,
            ModEnchantments.REPRISAL,
            ModEnchantments.FINESSE,
            ModEnchantments.REFLEXES,
            ModEnchantments.DEFLECTION,
            ModEnchantments.SIXTH_SENSE
        );
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        VULNERABLE = new PotionVulnerable();
        event.getRegistry().register(VULNERABLE);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModels(ModelRegistryEvent event) {
        for (ItemDefender item : DEFENDERS.values()) {
            ModelLoader.setCustomModelResourceLocation(item, 0,
                new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }
}
