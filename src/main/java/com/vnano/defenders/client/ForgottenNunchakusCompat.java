package com.vnano.defenders.client;

import com.vnano.defenders.DefendersMod;
import com.vnano.defenders.config.DefenderConfig;
import com.vnano.defenders.event.CombatEvents;
import com.vnano.defenders.item.ItemDefender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Optional compatibility for Forgotten Nunchakus (mod id {@code fn}). */
@Mod.EventBusSubscriber(modid = DefendersMod.MOD_ID, value = Dist.CLIENT)
public final class ForgottenNunchakusCompat {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !DefenderConfig.ALLOW_ATTACKING_WHILE_BLOCKING.get()) return;

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.gameMode == null || !isForgottenNunchaku(player.getMainHandItem())
            || !(player.getOffhandItem().getItem() instanceof ItemDefender)) return;

        // Minecraft and Forgotten Nunchakus can both suppress offhand use while
        // left-click is held. Address the offhand directly so guarding can begin.
        if (minecraft.options.keyUse.isDown() && !player.isUsingItem()) {
            minecraft.gameMode.useItem(player, InteractionHand.OFF_HAND);
        }

        // Forgotten Nunchakus intentionally skips its inventory-tick auto-attack
        // whenever the player is using an item. Reproduce its guarded attack path
        // without importing any of its classes, keeping this compatibility optional.
        if (!CombatEvents.isBlocking(player) || !minecraft.options.keyAttack.isDown()
            || player.getAttackStrengthScale(.5f) < 1 || !(minecraft.hitResult instanceof EntityHitResult hit)) return;

        Entity target = hit.getEntity();
        if (!target.isAlive() || target instanceof ItemEntity || target instanceof ExperienceOrb
            || target instanceof AbstractArrow) return;
        minecraft.gameMode.attack(player, target);
    }

    private static boolean isForgottenNunchaku(ItemStack stack) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return "fn".equals(id.getNamespace()) && id.getPath().endsWith("_nunchakus");
    }

    private ForgottenNunchakusCompat() {}
}
