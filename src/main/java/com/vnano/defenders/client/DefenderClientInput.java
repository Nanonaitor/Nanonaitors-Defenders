package com.vnano.defenders.client;

import com.vnano.defenders.DefendersMod;
import com.vnano.defenders.config.DefenderConfig;
import com.vnano.defenders.event.DefenderCombatHandler;
import com.vnano.defenders.item.ItemDefender;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = DefendersMod.MOD_ID, value = Side.CLIENT)
public final class DefenderClientInput {
    private static final Field CURRENT_BLOCK = ReflectionHelper.findField(PlayerControllerMP.class,
        "currentBlock", "field_178895_c");
    private static final Field CURRENT_TOOL = ReflectionHelper.findField(PlayerControllerMP.class,
        "currentItemHittingBlock", "field_85183_f");
    private static final Field BLOCK_DAMAGE = ReflectionHelper.findField(PlayerControllerMP.class,
        "curBlockDamageMP", "field_78770_f");
    private static final Field STEP_SOUND_TICK = ReflectionHelper.findField(PlayerControllerMP.class,
        "stepSoundTickCounter", "field_78780_h");
    private static final Field IS_HITTING_BLOCK = ReflectionHelper.findField(PlayerControllerMP.class,
        "isHittingBlock", "field_78778_j");

    private static MiningState preservedMining;

    private DefenderClientInput() {}

    @SubscribeEvent
    public static void onMouse(MouseEvent event) {
        if (!DefenderConfig.allowAttackingWhileBlocking || !event.isButtonstate()) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.player;
        if (event.getButton() == 1) {
            preserveMiningState(minecraft);
            return;
        }
        if (event.getButton() != 0) return;
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
        if (!DefenderConfig.allowAttackingWhileBlocking) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        if (event.phase == TickEvent.Phase.START) {
            preserveMiningState(minecraft);
            return;
        }

        restoreMiningState(minecraft);
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

    private static void preserveMiningState(Minecraft minecraft) {
        if (preservedMining != null || minecraft.player == null || minecraft.playerController == null
            || !minecraft.gameSettings.keyBindAttack.isKeyDown()
            || !(minecraft.player.getHeldItemOffhand().getItem() instanceof ItemDefender)) return;
        RayTraceResult hit = minecraft.objectMouseOver;
        if (hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK) return;

        try {
            PlayerControllerMP controller = minecraft.playerController;
            if (!IS_HITTING_BLOCK.getBoolean(controller)) return;
            BlockPos current = (BlockPos) CURRENT_BLOCK.get(controller);
            if (!hit.getBlockPos().equals(current)) return;
            preservedMining = new MiningState(current, (ItemStack) CURRENT_TOOL.get(controller),
                BLOCK_DAMAGE.getFloat(controller), STEP_SOUND_TICK.getFloat(controller));

            // Vanilla would abort mining merely because an off-hand item is in
            // use. Hide the active mining flag until its input pass is over so
            // no ABORT_DESTROY_BLOCK packet is sent and progress is retained.
            IS_HITTING_BLOCK.setBoolean(controller, false);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Unable to preserve Defender mining progress", exception);
        }
    }

    private static void restoreMiningState(Minecraft minecraft) {
        if (preservedMining == null || minecraft.playerController == null) return;
        try {
            PlayerControllerMP controller = minecraft.playerController;
            CURRENT_BLOCK.set(controller, preservedMining.block);
            CURRENT_TOOL.set(controller, preservedMining.tool);
            BLOCK_DAMAGE.setFloat(controller, preservedMining.damage);
            STEP_SOUND_TICK.setFloat(controller, preservedMining.stepSoundTick);
            IS_HITTING_BLOCK.setBoolean(controller, true);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Unable to restore Defender mining progress", exception);
        } finally {
            preservedMining = null;
        }
    }

    private static final class MiningState {
        private final BlockPos block;
        private final ItemStack tool;
        private final float damage;
        private final float stepSoundTick;

        private MiningState(BlockPos block, ItemStack tool, float damage, float stepSoundTick) {
            this.block = block;
            this.tool = tool;
            this.damage = damage;
            this.stepSoundTick = stepSoundTick;
        }
    }
}
