package com.vnano.defenders.client;

import com.vnano.defenders.DefendersMod;
import com.vnano.defenders.config.DefenderConfig;
import com.vnano.defenders.event.DefenderCombatHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = DefendersMod.MOD_ID, value = Side.CLIENT)
public final class DefenderClientInput {
    private DefenderClientInput() {}

    @SubscribeEvent
    public static void onMouse(MouseEvent event) {
        if (!DefenderConfig.allowAttackingWhileBlocking
            || event.getButton() != 0 || !event.isButtonstate()) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.player;
        if (player == null || !DefenderCombatHandler.isBlockingWithDefender(player)) return;

        // Minecraft suppresses its normal left-click dispatch while an item is
        // actively in use. Invoke the same PlayerControllerMP methods used by
        // Minecraft#clickMouse so attacks retain vanilla packets, cooldowns,
        // enchantments, reach handling, and third-party combat hooks.
        RayTraceResult hit = minecraft.objectMouseOver;
        if (hit != null && hit.typeOfHit == RayTraceResult.Type.ENTITY && hit.entityHit != null) {
            minecraft.playerController.attackEntity(player, hit.entityHit);
        } else if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
            minecraft.playerController.clickBlock(hit.getBlockPos(), hit.sideHit);
        } else {
            player.resetCooldown();
        }
        player.swingArm(EnumHand.MAIN_HAND);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !DefenderConfig.allowAttackingWhileBlocking) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.player;
        RayTraceResult hit = minecraft.objectMouseOver;
        if (player == null || minecraft.currentScreen != null
            || !minecraft.gameSettings.keyBindAttack.isKeyDown()
            || !DefenderCombatHandler.isBlockingWithDefender(player)
            || hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK) return;

        if (minecraft.playerController.onPlayerDamageBlock(hit.getBlockPos(), hit.sideHit)) {
            minecraft.effectRenderer.addBlockHitEffects(hit.getBlockPos(), hit.sideHit);
            player.swingArm(EnumHand.MAIN_HAND);
        }
    }
}
