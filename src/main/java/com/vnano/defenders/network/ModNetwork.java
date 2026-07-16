package com.vnano.defenders.network;

import com.vnano.defenders.DefendersMod;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class ModNetwork {
    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(DefendersMod.MOD_ID);

    private ModNetwork() {}

    public static void init() {
        CHANNEL.registerMessage(AttackWhileBlockingMessage.Handler.class, AttackWhileBlockingMessage.class, 0, Side.SERVER);
    }
}
