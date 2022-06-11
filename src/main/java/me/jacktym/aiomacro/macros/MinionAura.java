package me.jacktym.aiomacro.macros;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

public class MinionAura {

    @SubscribeEvent
    public void tickEvent(TickEvent.ClientTickEvent event) {
        if (Main.mcWorld != null && Main.mcPlayer != null && AIOMVigilanceConfig.macroType == 6 && MacroHandler.isMacroOn) {
            List<Entity> entityList = Main.mcWorld.loadedEntityList;

            if (!entityList.isEmpty()) {
                for (Entity entity : entityList) {
                    if (entity instanceof EntityArmorStand) {
                        System.out.println(Utils.stripColor(entity.getDisplayName().getUnformattedText()));
                        ItemStack chestStack = ((EntityArmorStand) entity).getCurrentArmor(4);

                        if (chestStack != null) {
                            System.out.println(chestStack.getItem().toString());
                            //NBTTagCompound nbtTagCompound = chestStack.getSubCompound("SkullOwner", false);
                        }
                    }
                }
            }
        }
    }
}
