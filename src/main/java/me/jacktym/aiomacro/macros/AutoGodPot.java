package me.jacktym.aiomacro.macros;

import me.jacktym.aiomacro.Main;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoGodPot {

    public boolean isAutoGodPot = false;

    @SubscribeEvent
    public void triggerAutoGodPot(TickEvent.ClientTickEvent event) {
        if (isAutoGodPot) {
            Main.mcPlayer.sendChatMessage("/ah");


        }
    }

}
