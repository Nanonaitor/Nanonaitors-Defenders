package com.vnano.defenders.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

final class ContinuousAttackCompat {
    private static final String NATIVE_NUNCHAKU = "com.mujmajnkraft.bettersurvival.items.ItemNunchaku";
    private static Method everythingClassifier;
    private static Method everythingTargetCheck;
    private static Field nunchakuCapability;
    private static Field nunchakuNetwork;
    private static Constructor<?> spinMessage;
    private static Method isSpinning;
    private static Method setSpinning;
    private static boolean reflectionResolved;

    private ContinuousAttackCompat() {}

    static boolean shouldContinue(Item item, Entity target, EntityPlayer player) {
        if (isNativeNunchaku(item)) return true;
        resolveEverythingNunchaku();
        if (everythingClassifier == null) return false;
        try {
            boolean configured = (Boolean) everythingClassifier.invoke(null, item);
            return configured && (everythingTargetCheck == null
                || (Boolean) everythingTargetCheck.invoke(null, target, player));
        } catch (IllegalAccessException | InvocationTargetException exception) {
            return false;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static void prepareAttack(EntityPlayer player) {
        resolveBetterSurvival();
        if (nunchakuCapability == null) return;
        try {
            Capability capability = (Capability) nunchakuCapability.get(null);
            Object combo = player.getCapability(capability, null);
            if (combo == null || (Boolean) isSpinning.invoke(combo)) return;

            // Better Survival rejects Nunchaku hits unless its spinning state is
            // active. Its normal handler turns that state off while an off-hand
            // Defender is in use, so restore the same client and server state
            // immediately before dispatching the held attack.
            setSpinning.invoke(combo, true);
            SimpleNetworkWrapper network = (SimpleNetworkWrapper) nunchakuNetwork.get(null);
            network.sendToServer((IMessage) spinMessage.newInstance(true));
        } catch (ReflectiveOperationException | ClassCastException ignored) {
            // Optional compatibility must remain harmless without Better Survival.
        }
    }

    private static boolean isNativeNunchaku(Item item) {
        for (Class<?> type = item.getClass(); type != null; type = type.getSuperclass()) {
            if (NATIVE_NUNCHAKU.equals(type.getName())) return true;
        }
        return false;
    }

    private static void resolveEverythingNunchaku() {
        if (reflectionResolved) return;
        reflectionResolved = true;
        try {
            Class<?> provider = Class.forName("everythingnunchaku.handlers.ForgeConfigProvider");
            everythingClassifier = provider.getMethod("isClientNunchaku", Item.class);
            everythingTargetCheck = provider.getMethod("shouldAttack", Entity.class, EntityPlayer.class);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            everythingClassifier = null;
            everythingTargetCheck = null;
        }
    }

    private static void resolveBetterSurvival() {
        if (nunchakuCapability != null) return;
        try {
            Class<?> provider = Class.forName(
                "com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo.NunchakuComboProvider");
            Class<?> combo = Class.forName(
                "com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo.INunchakuCombo");
            Class<?> packetHandler = Class.forName(
                "com.mujmajnkraft.bettersurvival.packet.BetterSurvivalPacketHandler");
            Class<?> message = Class.forName(
                "com.mujmajnkraft.bettersurvival.packet.MessageNunchakuSpinClient");
            nunchakuCapability = provider.getField("NUNCHAKUCOMBO_CAP");
            nunchakuNetwork = packetHandler.getField("NETWORK");
            spinMessage = message.getConstructor(boolean.class);
            isSpinning = combo.getMethod("isSpinning");
            setSpinning = combo.getMethod("setSpinning", boolean.class);
        } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException ignored) {
            nunchakuCapability = null;
        }
    }
}
