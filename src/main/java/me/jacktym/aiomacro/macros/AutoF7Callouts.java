package me.jacktym.aiomacro.macros;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.util.Utils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoF7Callouts {

    public static boolean enteredBoss = false;

    public static boolean sentPhase1Message = false;
    public static boolean sentPhase2Message = false;
    public static boolean sentPhase3Message = false;
    public static boolean sentPhase4Message = false;

    @SubscribeEvent
    public void checkPosTick(TickEvent.ClientTickEvent e) {
        if (Main.mcPlayer != null && Main.mcWorld != null) {
            /*if (enteredBoss) {
                if (Main.mcPlayer.posY <= 200 && !sentPhase2Message && !AIOMVigilanceConfig.autoF7CalloutPhase2.equals("")) {
                    Main.mcPlayer.sendChatMessage(AIOMVigilanceConfig.autoF7CalloutPhase2);
                    sentPhase2Message = true;
                }
                if (Main.mcPlayer.posY <= 150 && !sentPhase3Message && !AIOMVigilanceConfig.autoF7CalloutPhase3.equals("")) {
                    Main.mcPlayer.sendChatMessage(AIOMVigilanceConfig.autoF7CalloutPhase3);
                    sentPhase3Message = true;
                }
                if (Main.mcPlayer.posY <= 100 && !sentPhase4Message && !AIOMVigilanceConfig.autoF7CalloutPhase4.equals("")) {
                    Main.mcPlayer.sendChatMessage(AIOMVigilanceConfig.autoF7CalloutPhase4);
                    sentPhase4Message = true;

                    sentPhase1Message = false;
                    sentPhase2Message = false;
                    sentPhase3Message = false;
                    enteredBoss = false;
                }
            } else {
                List<EntityArmorStand> armorStandList = Main.mcWorld.getEntitiesWithinAABB(EntityArmorStand.class,
                        new AxisAlignedBB(Main.mcPlayer.posX - 100, Main.mcPlayer.posY - 100, Main.mcPlayer.posZ - 100, Main.mcPlayer.posX + 100, Main.mcPlayer.posY + 100, Main.mcPlayer.posZ + 100));
                for (EntityArmorStand entityArmorStand : armorStandList) {
                    if (Utils.stripColor(entityArmorStand.getCustomNameTag()).contains("﴾ Maxor ﴿") && !sentPhase1Message && !AIOMVigilanceConfig.autoF7CalloutPhase1.equals("")) {
                        Main.mcPlayer.sendChatMessage(AIOMVigilanceConfig.autoF7CalloutPhase1);
                        sentPhase1Message = true;

                        enteredBoss = true;

                        sentPhase4Message = false;
                    }
                }
            }*/


        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void ClientChatReceived(ClientChatReceivedEvent e) {
        String strippedMessage = Utils.stripColor(e.message.getUnformattedText());

        if (strippedMessage.contains("WELL WELL WELL LOOK WHO’S HERE")) {
            if (!sentPhase1Message && !AIOMVigilanceConfig.autoF7CalloutPhase1.equals("")) {
                Main.mcPlayer.sendChatMessage(AIOMVigilanceConfig.autoF7CalloutPhase1);
                sentPhase1Message = true;
            }
        }
        if (strippedMessage.contains("Pathetic Maxor, just like expected")) {
            if (!sentPhase2Message && !AIOMVigilanceConfig.autoF7CalloutPhase2.equals("")) {
                Main.mcPlayer.sendChatMessage(AIOMVigilanceConfig.autoF7CalloutPhase2);
                sentPhase2Message = true;
            }
        }
        if (strippedMessage.contains("At least my son died by your hands")) {
            if (!sentPhase3Message && !AIOMVigilanceConfig.autoF7CalloutPhase3.equals("")) {
                Main.mcPlayer.sendChatMessage(AIOMVigilanceConfig.autoF7CalloutPhase3);
                sentPhase3Message = true;
            }
        }
        if (strippedMessage.contains("Necron, forgive me")) {
            if (!sentPhase4Message && !AIOMVigilanceConfig.autoF7CalloutPhase4.equals("")) {
                Main.mcPlayer.sendChatMessage(AIOMVigilanceConfig.autoF7CalloutPhase4);
                sentPhase4Message = true;
            }
        }
        if (strippedMessage.contains("Here, I found this map when I first entered the dungeon.")) {
            sentPhase1Message = false;
            sentPhase2Message = false;
            sentPhase3Message = false;
            sentPhase4Message = false;
        }
    }
}
