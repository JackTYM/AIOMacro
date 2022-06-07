package me.jacktym.aiomacro.rendering;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AssEvents {

    @SubscribeEvent
    public void onPlayerConstructed(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;

            player.registerExtendedProperties("big_booty_hook", new AssHook());
        }
    }
}
