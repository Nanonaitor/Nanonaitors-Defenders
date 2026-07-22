package com.vnano.defenders.client;

import com.vnano.defenders.DefendersMod;
import java.util.UUID;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/** Keeps Defender movement balancing from changing the camera's field of view. */
@Mod.EventBusSubscriber(modid = DefendersMod.MOD_ID, value = Side.CLIENT)
public final class DefenderFovHandler {
    private static final UUID MOVEMENT_UUID = UUID.fromString("d37d6237-d2fe-4f73-b970-68d3cb7633d9");

    @SubscribeEvent
    public static void onFovUpdate(FOVUpdateEvent event) {
        EntityPlayer player = event.getEntity();
        IAttributeInstance speed = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        AttributeModifier compensation = speed.getModifier(MOVEMENT_UUID);
        if (compensation == null || compensation.getOperation() != 2) return;

        double factor = 1.0D + compensation.getAmount();
        double walkSpeed = player.capabilities.getWalkSpeed();
        if (factor <= 0.0D || walkSpeed <= 0.0D) return;
        double withCompensation = (speed.getAttributeValue() / walkSpeed + 1.0D) * 0.5D;
        double withoutCompensation = (speed.getAttributeValue() / factor / walkSpeed + 1.0D) * 0.5D;
        if (withCompensation > 0.0D) {
            event.setNewfov((float)(event.getNewfov() * withoutCompensation / withCompensation));
        }
    }

    private DefenderFovHandler() {}
}
