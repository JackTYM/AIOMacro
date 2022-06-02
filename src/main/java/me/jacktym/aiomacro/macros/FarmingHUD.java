package me.jacktym.aiomacro.macros;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.util.Utils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;

public class FarmingHUD {

    public static int getInventoryPrice() {
        ItemStack[] inventory = Main.mcPlayer.inventory.mainInventory;

        int inventoryPrice = 0;

        for (ItemStack itemStack : inventory) {
            if (itemStack != null) {
                String displayName = itemStack.getDisplayName();
                if (displayName.contains("Mutant Nether Wart") || displayName.contains("Enchanted Sugar Cane")) {
                    inventoryPrice += 51200 * itemStack.stackSize;
                }
                if (displayName.contains("Enchanted Nether Wart") || displayName.contains("Enchanted Sugar")) {
                    inventoryPrice += 320 * itemStack.stackSize;
                }
                if (displayName.contains("Nether Wart") || displayName.contains("Sugar Cane")) {
                    inventoryPrice += 2 * itemStack.stackSize;
                }
            }
        }
        return inventoryPrice;
    }

    public static int getCropsPerHour() {
        ItemStack[] inventory = Main.mcPlayer.inventory.mainInventory;

        int crops = 0;

        for (ItemStack itemStack : inventory) {
            if (itemStack != null) {

                String displayName = itemStack.getDisplayName();

                if (displayName.contains("Mutant Nether Wart") || displayName.contains("Enchanted Sugar Cane")) {
                    crops += 25600 * itemStack.stackSize;
                }
                if (displayName.contains("Enchanted Nether Wart") || displayName.contains("Enchanted Sugar")) {
                    crops += 160 * itemStack.stackSize;
                }
                if (displayName.contains("Nether Wart") || displayName.contains("Sugar Cane")) {
                    crops += itemStack.stackSize;
                }
            }
        }
        return crops;
    }

    public static int getFinalCrops() {
        ItemStack[] inventory = Main.mcPlayer.inventory.mainInventory;

        int finalCrops = 0;

        for (ItemStack itemStack : inventory) {

            if (itemStack != null) {
                String displayName = itemStack.getDisplayName();
                if (displayName.contains("Mutant Nether Wart") || displayName.contains("Enchanted Sugar Cane")) {
                    finalCrops += itemStack.stackSize;
                }
            }
        }
        return finalCrops;
    }

    public static int getTimeFarming() {
        long millisecondsMacroing = Utils.timeSinceMillis(MacroHandler.macroStartMillis);

        return (int) (millisecondsMacroing / 1000);
    }

    @SubscribeEvent
    public void onRender(@NotNull RenderGameOverlayEvent.Text event) {
        if (AIOMVigilanceConfig.farmingHUDOn) {
            FontRenderer fontRendererObj = Main.mc.fontRendererObj;

            for (int i = 0; i < getHUDList().size(); i++) {
                String draw = getHUDList().get(i);
                fontRendererObj.drawString(draw, 0, 10 * i, AIOMVigilanceConfig.hudColor.getRGB());
            }
        }
    }

    public ArrayList<String> getHUDList() {
        ArrayList<String> hudList = new ArrayList<>();
        if (Main.mcPlayer != null && Main.mcWorld != null) {

            if (AIOMVigilanceConfig.totalProfitHUD) {
                hudList.add("Total Profit: " + getInventoryPrice() + " Coins");
            }
            if (AIOMVigilanceConfig.profitPerHourHUD) {
                hudList.add("Profit Per Hour: " + getProfitPerHour() + " Coins");
            }
            if (AIOMVigilanceConfig.profitPer12HoursHUD) {
                hudList.add("Profit Per 12 Hours: " + getProfitPerHour() * 12 + " Coins");
            }
            if (AIOMVigilanceConfig.profitPer24HoursHUD) {
                hudList.add("Profit Per 24 Hours: " + getProfitPerHour() * 24 + " Coins");
            }
            if (AIOMVigilanceConfig.cropsPerHourHUD) {
                hudList.add("Crops Per Hour: " + getCropsPerHour() + " Crops");
            }
            if (AIOMVigilanceConfig.expPerHourHUD) {
                hudList.add("Exp Per Hour: " + getExpPerHour() + " Exp");
            }
            if (AIOMVigilanceConfig.totalFinalCropHUD) {
                hudList.add("Final Upgraded Crops: " + getFinalCrops() + " Crops");
            }
            if (AIOMVigilanceConfig.hoeCounterHUD) {
                hudList.add("Hoes: " + getHoeCounter());
            }
            if (AIOMVigilanceConfig.godPotionTimeHUD) {
                hudList.add("God Potion Time: " + getGodPotionTime());
            }
            if (AIOMVigilanceConfig.boosterCookieTimeHUD) {
                hudList.add("Booster Cookie Time: " + getBoosterCookieTime());
            }
            if (AIOMVigilanceConfig.jacobsEventHUD) {
                hudList.add("Jacobs Event Time: " + getJacobsEventTime());
            }
            if (AIOMVigilanceConfig.farmingTime) {
                Duration duration = Duration.ofSeconds(getTimeFarming());
                long hours = duration.toHours();
                long minutes = duration.toMinutes() - hours * 60;
                long seconds = (duration.getSeconds() - minutes * 60 - hours * 3600);

                if (getTimeFarming() == 0 || !MacroHandler.isMacroOn) {
                    hudList.add("Total Time Farming: None.");
                } else if (hours == 0 && minutes == 0 && seconds >= 1) {
                    hudList.add("Total Time Farming: " + seconds + " Seconds.");
                } else if (hours == 0 && minutes >= 1) {
                    hudList.add("Total Time Farming: " + minutes + "Minutes and " + seconds + " Seconds.");
                } else if (hours >= 1) {
                    hudList.add("Total Time Farming: " + hours + "Hours, " + minutes + "Minutes and " + seconds + " Seconds.");
                }
            }
        }
        return hudList;
    }

    private String getJacobsEventTime() {
        return "";
    }

    private String getBoosterCookieTime() {
        return "";
    }

    private String getGodPotionTime() {
        return "";
    }

    private String getHoeCounter() {
        return "";
    }

    private String getExpPerHour() {
        return "";
    }

    public int getProfitPerHour() {
        int profit = getInventoryPrice();

        int profitPerHour = 0;
        if (MacroHandler.isMacroOn) {

            long millisecondsMacroing = Utils.timeSinceMillis(MacroHandler.macroStartMillis);

            int secondsMacroing = (int) (millisecondsMacroing / 1000);

            if (secondsMacroing != 0) {
                profitPerHour = (3600 / secondsMacroing) * profit;
            }

        }
        return profitPerHour;
    }

}
