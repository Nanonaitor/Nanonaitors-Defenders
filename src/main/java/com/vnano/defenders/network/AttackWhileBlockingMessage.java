package com.vnano.defenders.network;

import com.vnano.defenders.config.DefenderConfig;
import com.vnano.defenders.event.DefenderCombatHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class AttackWhileBlockingMessage implements IMessage {
    private int entityId;

    public AttackWhileBlockingMessage() {}

    public AttackWhileBlockingMessage(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
    }

    public static final class Handler implements IMessageHandler<AttackWhileBlockingMessage, IMessage> {
        @Override
        public IMessage onMessage(AttackWhileBlockingMessage message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                if (!DefenderConfig.allowAttackingWhileBlocking) return;
                Entity target = player.world.getEntityByID(message.entityId);
                if (target == null || target == player || player.getDistanceSq(target) > 16.0D) return;
                if (!DefenderCombatHandler.isBlockingWithDefender(player) || !target.canBeAttackedWithItem()) return;
                player.attackTargetEntityWithCurrentItem(target);
                player.swingArm(EnumHand.MAIN_HAND);
                if (!player.isHandActive()) player.setActiveHand(EnumHand.OFF_HAND);
            });
            return null;
        }
    }
}
