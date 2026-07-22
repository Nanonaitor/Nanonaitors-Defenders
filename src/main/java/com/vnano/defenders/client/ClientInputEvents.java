package com.vnano.defenders.client;

import com.vnano.defenders.config.DefenderConfig;
import com.vnano.defenders.event.CombatEvents;
import com.vnano.defenders.item.ItemDefender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.TickEvent;

public final class ClientInputEvents {
    private static final int GUARD_SWING_TICKS=6;
    private static boolean attackWasDown;
    private static boolean useWasDown;
    private static int guardSwingTicksRemaining;

    public static void register(){
        TickEvent.ClientTickEvent.Pre.BUS.addListener(ClientInputEvents::beforeClientTick);
        TickEvent.ClientTickEvent.Post.BUS.addListener(ClientInputEvents::afterClientTick);
    }

    private static void beforeClientTick(TickEvent.ClientTickEvent.Pre event){
        Minecraft minecraft=Minecraft.getInstance();
        LocalPlayer player=minecraft.player;
        MultiPlayerGameMode gameMode=minecraft.gameMode;
        if(player==null||gameMode==null){attackWasDown=false;useWasDown=false;return;}

        boolean useDown=minecraft.options.keyUse.isDown();
        if(minecraft.screen==null&&useDown&&!useWasDown&&gameMode.isDestroying()
            &&player.getOffhandItem().getItem() instanceof ItemDefender){
            gameMode.useItem(player,InteractionHand.OFF_HAND);
        }
        useWasDown=useDown;
    }

    private static void afterClientTick(TickEvent.ClientTickEvent.Post event){
        Minecraft minecraft=Minecraft.getInstance();
        LocalPlayer player=minecraft.player;
        MultiPlayerGameMode gameMode=minecraft.gameMode;
        boolean attackDown=minecraft.options.keyAttack.isDown();
        if(player==null||gameMode==null){attackWasDown=attackDown;guardSwingTicksRemaining=0;return;}
        if(minecraft.screen!=null){attackWasDown=attackDown;advanceGuardSwing(player);return;}

        boolean guarding=DefenderConfig.ALLOW_ATTACKING.get()&&CombatEvents.isBlocking(player);
        if(!guarding){attackWasDown=attackDown;advanceGuardSwing(player);return;}

        HitResult hit=minecraft.hitResult;
        if(attackDown&&hit instanceof BlockHitResult blockHit){
            continueMining(minecraft,player,gameMode,blockHit);
        }else{
            if(gameMode.isDestroying())gameMode.stopDestroyBlock();
            if(attackDown&&!attackWasDown){
                if(hit instanceof EntityHitResult entityHit)gameMode.attack(player,entityHit.getEntity());
                startFreshMainHandSwing(player);
            }
        }
        attackWasDown=attackDown;
        advanceGuardSwing(player);
    }

    private static void startFreshMainHandSwing(LocalPlayer player){
        guardSwingTicksRemaining=GUARD_SWING_TICKS;
        player.swinging=true;
        player.swingingArm=InteractionHand.MAIN_HAND;
        player.swingTime=0;
        player.connection.send(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
    }

    private static void advanceGuardSwing(LocalPlayer player){
        if(guardSwingTicksRemaining<=0)return;
        int elapsed=GUARD_SWING_TICKS-guardSwingTicksRemaining+1;
        player.swinging=true;
        player.swingingArm=InteractionHand.MAIN_HAND;
        player.swingTime=elapsed;
        player.oAttackAnim=(elapsed-1)/(float)GUARD_SWING_TICKS;
        player.attackAnim=elapsed/(float)GUARD_SWING_TICKS;
        guardSwingTicksRemaining--;
    }

    private static void continueMining(Minecraft minecraft,LocalPlayer player,
            MultiPlayerGameMode gameMode,BlockHitResult hit){
        BlockPos pos=hit.getBlockPos();
        if(minecraft.level==null||minecraft.level.isEmptyBlock(pos))return;
        Direction direction=hit.getDirection();
        boolean progressed=gameMode.isDestroying()
            ?gameMode.continueDestroyBlock(pos,direction)
            :gameMode.startDestroyBlock(pos,direction);
        if(progressed){
            minecraft.level.addBreakingBlockEffect(pos,hit);
            player.swing(InteractionHand.MAIN_HAND);
        }
    }

    private ClientInputEvents(){}
}
