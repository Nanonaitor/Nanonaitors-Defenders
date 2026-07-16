package com.vnano.defenders.client;

import com.vnano.defenders.DefendersMod;
import com.vnano.defenders.enchantment.ModEnchantments;
import com.vnano.defenders.enchantment.EnchantmentDefender;
import java.util.Map;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = DefendersMod.MOD_ID, value = Side.CLIENT)
public final class EnchantmentTooltipHandler {
    private EnchantmentTooltipHandler() {}

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        // Enchantment Descriptions reads the same localization keys itself.
        // Keep this handler only as the fallback when that mod is absent.
        if (Loader.isModLoaded("enchdesc")) return;
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(event.getItemStack());
        addDescription(event, enchantments, ModEnchantments.FOOTWORK, "enchantment.defenders.footwork.desc");
        addDescription(event, enchantments, ModEnchantments.FORTIFICATION, "enchantment.defenders.fortification.desc");
        addDescription(event, enchantments, ModEnchantments.REPRISAL, "enchantment.defenders.reprisal.desc");
        addDescription(event, enchantments, ModEnchantments.FINESSE, "enchantment.defenders.finesse.desc");
        addDescription(event, enchantments, ModEnchantments.REFLEXES, "enchantment.defenders.reflexes.desc");
        addDescription(event, enchantments, ModEnchantments.DEFLECTION, "enchantment.defenders.deflection.desc");
        addDescription(event, enchantments, ModEnchantments.SIXTH_SENSE, "enchantment.defenders.sixth_sense.desc");
    }

    private static void addDescription(ItemTooltipEvent event, Map<Enchantment, Integer> enchantments,
                                       Enchantment enchantment, String key) {
        if (enchantment != null && enchantments.containsKey(enchantment)
            && (!(enchantment instanceof EnchantmentDefender)
                || ((EnchantmentDefender) enchantment).isEnabled())) {
            event.getToolTip().add(TextFormatting.DARK_GRAY + I18n.format(key));
        }
    }
}
