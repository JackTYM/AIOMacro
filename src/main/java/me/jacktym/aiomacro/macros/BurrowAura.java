package me.jacktym.aiomacro.macros;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.util.Utils;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


public class BurrowAura {

    private static int tick = 0;
    @SubscribeEvent
    public void digBurrow(TickEvent.ClientTickEvent e) {
        if(tick >= 5){
            if(AIOMVigilanceConfig.burrowEnabled){
                if (Main.mcWorld != null && Main.mcPlayer != null){

                    if(!(Main.mcPlayer.getHeldItem() == null) && Utils.stripColor(Main.mcPlayer.getHeldItem().getDisplayName()).contains("Ancestral Spade")) {
                        for(BlockPos point: DianaWaypoints.points.keySet()){
                            Main.mcPlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, point, EnumFacing.DOWN));
                        }
                    }
                }
            }
        }
        tick++;
    }
}
