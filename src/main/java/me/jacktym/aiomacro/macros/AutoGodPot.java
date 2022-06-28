package me.jacktym.aiomacro.macros;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.awt.event.KeyEvent;

public class AutoGodPot {
    private boolean ahOpened = false;
    private boolean boughtGodPotion = false;
    private boolean claimedGodPotion = false;

    // @SubscribeEvent
    public void triggerAutoGodPot(TickEvent.ClientTickEvent event) {
        //if (AIOMVigilanceConfig.autogodpotion && FarmingHUD.getGodPotionTime().equals("") && Main.mc.currentScreen == null) {
        if (AIOMVigilanceConfig.autogodpotion && Main.mc.currentScreen == null) {
            Main.mcPlayer.sendChatMessage("/ah");
            boughtGodPotion = false;
            claimedGodPotion = false;
            ahOpened = true;
        }
    }

    //@SubscribeEvent
    public void ahOpenEvent(GuiScreenEvent event) {
        if (ahOpened) {
            if (event.gui instanceof GuiContainer) {
                IInventory chest = ((ContainerChest) (((GuiChest) event.gui).inventorySlots)).getLowerChestInventory();

                String chestName = chest.getDisplayName().getUnformattedText();

                if (chestName.equals("Co-op Auction House")) {
                    Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 11, 0, 0, Main.mcPlayer);
                }
                if (chestName.contains("Auctions: ") && !chestName.contains("God Potion")) {
                    Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 47, 0, 0, Main.mcPlayer);
                }
                if (chestName.startsWith("Auctions Browser")) {
                    Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 48, 0, 0, Main.mcPlayer);
                }
                if (chestName.equals("Auctions: \"God Potion\"")) {
                    if (chest.getStackInSlot(52) != null) {
                        System.out.println(chest.getStackInSlot(52).getTagCompound().getCompoundTag("display").getTagList("Lore", 8).get(2).toString());
                        //if (chest.getStackInSlot(52).getTagCompound().getCompoundTag("display").getTagList("Lore", 8).get(2).toString().contains("")) {

                        //}
                    }
                }
            }
            if (event.gui instanceof GuiEditSign) {
                try {
                    char[] charArray = "God Potion".toCharArray();
                    Robot robot = new Robot();
                    for (char ch : charArray) {
                        robot.keyPress(ch);
                    }
                    robot.keyPress(KeyEvent.VK_ESCAPE);
                } catch (Exception ignored) {
                }
            }
        }
    }
}
