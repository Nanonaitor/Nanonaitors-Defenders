package com.vnano.defenders.client;

import com.vnano.defenders.DefendersMod;
import com.vnano.defenders.enchantment.DefenderEnchantment;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DefendersMod.MOD_ID, value = Dist.CLIENT)
public final class EnchantmentTooltipEvents {
    @SubscribeEvent
    public static void tooltip(ItemTooltipEvent event) {
        if (ModList.get().isLoaded("enchdesc") || !(event.getItemStack().getItem() instanceof EnchantedBookItem)) return;
        for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(event.getItemStack()).entrySet()) {
            if (entry.getKey() instanceof DefenderEnchantment defender && defender.enabled()) {
                event.getToolTip().add(Component.translatable(
                    "enchantment.defenders." + defender.id() + ".desc").withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    private EnchantmentTooltipEvents() {}
}
