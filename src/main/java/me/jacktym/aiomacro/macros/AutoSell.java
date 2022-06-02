package me.jacktym.aiomacro.macros;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.util.Utils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AutoSell {

    private static boolean guiOpen = false;
    private static boolean tradeOpen = false;

    public static ArrayList<ItemStack> getCropSlots() {
        ArrayList<ItemStack> cropSlots = new ArrayList<>();

        ItemStack[] inventory = Main.mcPlayer.inventory.mainInventory;

        for (ItemStack itemStack : inventory) {
            if (itemStack != null) {
                String displayName = itemStack.getDisplayName();
                if (displayName.contains("Mutant Nether Wart") || displayName.contains("Enchanted Sugar Cane")) {
                    cropSlots.add(itemStack);
                }
            }
        }
        return cropSlots;
    }

    //@SubscribeEvent
    public void autoSellCrops(@NotNull TickEvent.ClientTickEvent event) {
        if (AIOMVigilanceConfig.autoSell && Main.mcPlayer != null && Main.mcWorld != null) {
            if (getCropSlots().size() >= 8 && !guiOpen) {
                int currentItem = Main.mcPlayer.inventory.currentItem;
                Main.mcPlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(8));
                Main.mc.playerController.sendUseItem(Main.mcPlayer, Main.mcWorld, Main.mcPlayer.inventory.getStackInSlot(8));
                Main.mcPlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(currentItem));
                guiOpen = true;
                tradeOpen = false;
            } else if (getCropSlots().size() < 8) {
                guiOpen = false;
                tradeOpen = false;
            }
        }
    }

    //@SubscribeEvent
    public void drawScreen(@NotNull GuiScreenEvent.DrawScreenEvent event) {
        if (guiOpen && event.gui instanceof GuiChest) {
            if (((GuiChest) event.gui).inventorySlots.inventorySlots.get(22).getStack() != null && !tradeOpen) {
                Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 22, 0, 0, Main.mcPlayer);
                tradeOpen = true;
            } else if (((GuiChest) event.gui).inventorySlots.inventorySlots.get(49).getStack() != null && tradeOpen) {
                for (ItemStack iStack : getCropSlots()) {
                    for (Slot slot : Main.mcPlayer.openContainer.inventorySlots) {
                        if (slot.getStack() != null) {
                            System.out.println("all" + slot.slotNumber + " " + slot.getStack().getDisplayName() + slot.inventory);
                        }
                        if (slot.getStack() == iStack) {
                            System.out.println("correct" + slot.slotNumber + " " + slot.getStack().getDisplayName() + slot.inventory);
                            Main.mc.playerController.windowClick(Main.mcPlayer.inventoryContainer.windowId, slot.slotNumber, 0, 0, Main.mcPlayer);
                        }
                    }
                }
            }
        }
    }

    //@SubscribeEvent
    public void onGuiOpen(@NotNull GuiScreenEvent event) {
        if (guiOpen && event.gui instanceof GuiChest) {
            IInventory gc = ((ContainerChest) (((GuiChest) event.gui).inventorySlots)).getLowerChestInventory();

            //Main.mc.fontRendererObj.drawStringWithShadow()
            long currentTimeMillis = Utils.currentTimeMillis();
            if (Utils.millisPassed(currentTimeMillis + 5000)) {
                Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 23, 0, 0, Main.mcPlayer);
                System.out.println("clicked");
                for (Slot slot : ((GuiChest) event.gui).inventorySlots.inventorySlots) {
                    //System.out.println(slot.inventory + " " + slot.slotNumber + " " + slot.inventory);
                    Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, slot.slotNumber, 0, 0, Main.mcPlayer);
                    for (ItemStack iStack : ((GuiChest) event.gui).inventorySlots.getInventory()) {
                        Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 23, 0, 0, Main.mcPlayer);
                        System.out.println(slot.inventory + " " + slot.slotNumber + " " + slot.inventory);
                        //    if (iStack != null) {
                        //        System.out.println(iStack.getDisplayName() + " " + iStack.getItem() + " ");
                        //    if (slot.getStack() != null) {
                        //        System.out.println(slot.slotNumber + " " + slot.getStack().getDisplayName());
                        //        //Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 23 + slot., );
                    }
                }
            }
        }
    }
}
