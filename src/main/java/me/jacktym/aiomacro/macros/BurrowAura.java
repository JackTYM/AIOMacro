package me.jacktym.aiomacro.macros;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.util.Utils;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


public class BurrowAura {

    private static int tick = 0;

    @SubscribeEvent
    public void digBurrow(TickEvent.ClientTickEvent e) {
        if (tick >= 5) {
            tick = 0;
            if (AIOMVigilanceConfig.burrowEnabled) {
                if (Main.notNull) {
                    for (BlockPos point : DianaWaypoints.points.keySet()) {
                        if (Utils.distanceBetweenPoints(new Vec3(point), Main.mcPlayer.getPositionVector()) <= 5) {
                            int spadeIndex = 0;
                            for (int i = 0; i <= 9; i++) {
                                if (Main.mcPlayer.inventory.mainInventory[i] != null && Main.mcPlayer.inventory.mainInventory[i].getDisplayName() != null && Main.mcPlayer.inventory.mainInventory[i].getDisplayName().contains("Ancestral Spade")) {
                                    spadeIndex = i;
                                    break;
                                }
                            }
                            int currentItem = Main.mcPlayer.inventory.currentItem;
                            Main.mcPlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(spadeIndex));
                            Main.mcPlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, point, EnumFacing.DOWN));
                            Main.mcPlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(currentItem));
                        }
                    }
                }
            }
        }
        tick++;
    }
}
