package com.vnano.defenders.mixin.client;

import com.vnano.defenders.item.ItemDefender;
import com.vnano.defenders.config.DefenderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    private static boolean defenders$isActivelyGuarding(LocalPlayer player) {
        return player != null && player.isUsingItem()
            && DefenderConfig.ALLOW_ATTACKING_WHILE_BLOCKING.get()
            && player.getUsedItemHand() == InteractionHand.OFF_HAND
            && player.getOffhandItem().getItem() instanceof ItemDefender;
    }

    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void defenders$prioritizeOffhandGuard(CallbackInfo callback) {
        Minecraft minecraft = (Minecraft) (Object) this;
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.gameMode == null || player.isUsingItem()
            || !(player.getOffhandItem().getItem() instanceof ItemDefender)) return;

        // Entity interaction and main-hand use normally run before off-hand use.
        // Give the Defender priority when a target would swallow the input, and
        // when the main hand is another Defender so it cannot emit a fake swing.
        boolean targetInReach = minecraft.hitResult instanceof EntityHitResult;
        boolean dualDefenders = player.getMainHandItem().getItem() instanceof ItemDefender;
        if (!targetInReach && !dualDefenders) return;

        minecraft.gameMode.useItem(player, InteractionHand.OFF_HAND);
        callback.cancel();
    }

    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z", ordinal = 0))
    private boolean defenders$processAttackClicksWhileGuarding(LocalPlayer player) {
        if (!defenders$isActivelyGuarding(player)) return player.isUsingItem();

        // Preserve vanilla's release branch when the use key is released. Only
        // enter the normal attack-click branch while the Defender is held up.
        return !Minecraft.getInstance().options.keyUse.isDown();
    }

    @Redirect(method = "startUseItem", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;isDestroying()Z"))
    private boolean defenders$allowGuardToStartWhileMining(MultiPlayerGameMode gameMode) {
        LocalPlayer player = Minecraft.getInstance().player;
        boolean hasOffhandDefender = player != null
            && DefenderConfig.ALLOW_ATTACKING_WHILE_BLOCKING.get()
            && player.getOffhandItem().getItem() instanceof ItemDefender;
        return gameMode.isDestroying() && !hasOffhandDefender;
    }

    @Redirect(method = "startAttack", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/player/LocalPlayer;isHandsBusy()Z"))
    private boolean defenders$allowAttackWhileGuarding(LocalPlayer player) {
        return player.isHandsBusy() && !defenders$isActivelyGuarding(player);
    }

    @Redirect(method = "continueAttack", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
    private boolean defenders$allowMiningWhileGuarding(LocalPlayer player) {
        return player.isUsingItem() && !defenders$isActivelyGuarding(player);
    }
}
