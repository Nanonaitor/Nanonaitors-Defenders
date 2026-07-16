package com.vnano.defenders.client;

import com.vnano.defenders.DefendersMod;
import com.vnano.defenders.config.DefenderConfig;
import com.vnano.defenders.event.DefenderCombatHandler;
import com.vnano.defenders.network.AttackWhileBlockingMessage;
import com.vnano.defenders.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
        event.setCanceled(true);
        player.swingArm(EnumHand.MAIN_HAND);
        RayTraceResult hit = minecraft.objectMouseOver;
        if (hit == null || hit.typeOfHit != RayTraceResult.Type.ENTITY || hit.entityHit == null) return;
        ModNetwork.CHANNEL.sendToServer(new AttackWhileBlockingMessage(hit.entityHit.getEntityId()));
    }
}
