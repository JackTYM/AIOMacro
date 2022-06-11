package me.jacktym.aiomacro.macros;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.util.Utils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

public class MinionAura {

    public static List<Entity> claimedMinions = new ArrayList<>();
    public static List<Entity> minionsToClaim = new ArrayList<>();
    public static Entity recentClickedEntity;

    @SubscribeEvent
    public void tickEvent(TickEvent.ClientTickEvent event) {
        if (Main.mcWorld != null && Main.mcPlayer != null && AIOMVigilanceConfig.macroType == 6 && MacroHandler.isMacroOn) {
            List<Entity> entityList = Main.mcWorld.loadedEntityList;

            if (!entityList.isEmpty()) {
                for (Entity entity : entityList) {
                    if (entity instanceof EntityArmorStand) {
                        ItemStack chestStack = ((EntityArmorStand) entity).getCurrentArmor(1);

                        if (chestStack != null) {
                            if (chestStack.getDisplayName().equals("Leather Pants") && !minionsToClaim.contains(entity) && !claimedMinions.contains(entity)) {
                                minionsToClaim.add(entity);
                            }
                        }
                    }
                }
            }

            for (Entity entity : minionsToClaim) {
                if (Main.mc.currentScreen == null) {
                    Utils.useEntity(entity);
                    recentClickedEntity = entity;
                }
            }
        }
    }

    @SubscribeEvent
    public void guiScreenEvent(GuiScreenEvent event) {
        if (event.gui instanceof GuiChest && AIOMVigilanceConfig.macroType == 6 && MacroHandler.isMacroOn && recentClickedEntity != null) {
            IInventory chest = ((ContainerChest) (((GuiChest) event.gui).inventorySlots)).getLowerChestInventory();

            String chestName = chest.getDisplayName().getUnformattedText();

            if (chestName.contains("Minion")) {
                if (chest.getStackInSlot(48) != null && chest.getStackInSlot(48).getDisplayName() != null) {
                    if (Utils.stripColor(chest.getStackInSlot(48).getDisplayName()).equals("Collect All")) {
                        Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 48, 0, 0, Main.mcPlayer);
                        if (chest.getStackInSlot(21) == null) {
                            Main.mcPlayer.closeScreen();
                            minionsToClaim.remove(recentClickedEntity);
                            claimedMinions.add(recentClickedEntity);
                            recentClickedEntity = null;
                        }
                    }
                }
            }
        }
    }
}
