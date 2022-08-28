package me.jacktym.aiomacro.macros;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.util.Utils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class BazaarFlipper {

    private static final HashMap<String, Double> margins = new HashMap<>();
    private static final HashMap<String, Double> sortedMargins = new LinkedHashMap<>();
    public static List<String> blackList = new ArrayList<>();
    public static HashMap<String, String> gameToApi = new HashMap<>();
    private static String key = "";
    private static boolean doOrders = false;
    private static boolean manageOrders = false;
    private static int orderTicks = 0;
    private static int mainTicks = 0;
    private static int orderNum = 0;
    private static ItemStack currentManagedStack;
    private static boolean manageBuy = true;
    private static boolean manageFill = false;
    private static int manageOrderPhase = 0;
    private static int manageTicks = 0;
    private final HashMap<String, Double> volumes = new HashMap<>();
    private final HashMap<String, Double> prices = new HashMap<>();
    private final HashMap<String, Integer> categories = new HashMap<>();
    private final HashMap<String, Integer> subCategories = new HashMap<>();
    private final HashMap<String, Integer> items = new HashMap<>();
    private final HashMap<String, Double> buyOrders = new HashMap<>();
    private final HashMap<String, Double> sellOrders = new HashMap<>();
    private final HashMap<String, String> apiToGame = new HashMap<>();
    private final List<String> buyCuts = new ArrayList<>();
    private final List<String> sellCuts = new ArrayList<>();
    private final List<String> buyFills = new ArrayList<>();
    private final List<String> sellFills = new ArrayList<>();
    private final List<String> buyClaims = new ArrayList<>();
    private final List<String> sellClaims = new ArrayList<>();
    private String lastName = "";
    private double lastPrice = 0.0;
    private int lastAmount = 0;
    private String lastCancelledItemName = "";
    private long lastOrderMillis = 0;

    public BazaarFlipper() {
        try {
            String categoriesString = IOUtils.toString(new BufferedReader(new InputStreamReader(Objects.requireNonNull(Main.class.getResourceAsStream("/categories.json")))));
            JsonElement categoriesElement = new JsonParser().parse(categoriesString);
            JsonObject categoriesObject = categoriesElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : categoriesObject.entrySet()) {
                categories.put(entry.getKey(), entry.getValue().getAsInt());
            }

            String subCategoriesString = IOUtils.toString(new BufferedReader(new InputStreamReader(Objects.requireNonNull(Main.class.getResourceAsStream("/subCategories.json")))));
            JsonElement subCategoriesElement = new JsonParser().parse(subCategoriesString);
            JsonObject subCategoriesObject = subCategoriesElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : subCategoriesObject.entrySet()) {
                subCategories.put(entry.getKey(), entry.getValue().getAsInt());
            }

            String itemsString = IOUtils.toString(new BufferedReader(new InputStreamReader(Objects.requireNonNull(Main.class.getResourceAsStream("/items.json")))));
            JsonElement itemsElement = new JsonParser().parse(itemsString);
            JsonObject itemsObject = itemsElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : itemsObject.entrySet()) {
                items.put(entry.getKey(), entry.getValue().getAsInt());
            }

            String gameToApiString = IOUtils.toString(new BufferedReader(new InputStreamReader(Objects.requireNonNull(Main.class.getResourceAsStream("/gameToApi.json")))));
            JsonElement gameToApiElement = new JsonParser().parse(gameToApiString);
            JsonObject gameToApiObject = gameToApiElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : gameToApiObject.entrySet()) {
                gameToApi.put(entry.getKey(), entry.getValue().getAsString());
            }

            String apiToGameString = IOUtils.toString(new BufferedReader(new InputStreamReader(Objects.requireNonNull(Main.class.getResourceAsStream("/apiToGame.json")))));
            JsonElement apiToGameElement = new JsonParser().parse(apiToGameString);
            JsonObject apiToGameObject = apiToGameElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : apiToGameObject.entrySet()) {
                apiToGame.put(entry.getKey(), entry.getValue().getAsString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sortMargins() {
        List<Map.Entry<String, Double>> list = new LinkedList<>(margins.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        sortedMargins.clear();
        for (Map.Entry<String, Double> entry : list) {
            sortedMargins.put(entry.getKey(), entry.getValue());
        }
    }

    @SubscribeEvent
    public void tickEvent(@NotNull TickEvent.ClientTickEvent event) {
        if (MacroHandler.isMacroOn && AIOMVigilanceConfig.macroType == 3 && Main.mcPlayer != null && Main.mcWorld != null) {
            if (mainTicks >= 300) {
                mainTicks = 0;
                if (Utils.bazaarApi != null) {
                    JsonObject products = Utils.bazaarApi;

                    Object[] productArray = products.entrySet().toArray();

                    margins.clear();
                    volumes.clear();
                    prices.clear();
                    sortedMargins.clear();

                    iterateProducts(productArray);

                    if (AIOMVigilanceConfig.bazaarFlipDevMode) {
                        System.out.println("Main Tick");
                        System.out.println("Buy Orders: " + buyOrders);
                        System.out.println("Buy Fills: " + buyFills);
                        System.out.println("Buy Cuts: " + buyCuts);
                        System.out.println("Sell Orders: " + sellOrders);
                        System.out.println("Sell Fills: " + sellFills);
                        System.out.println("Sell Cuts: " + sellCuts);
                        System.out.println("Do Orders: " + doOrders);
                        System.out.println("Manage Orders: " + manageOrders);
                        System.out.println("Next Key: " + getKey());
                    }

                    if (getKey().equals("")) {
                        Main.sendMarkedChatMessage("Error! No Items Found In Current Parameters! Please change them or refer to #bazaar-flip in the discord or the Usage section of the ReadMe");
                    }

                    if (!doOrders && !manageOrders) {
                        if (AIOMVigilanceConfig.bazaarFlipDevMode) {
                            System.out.println("Nothing Happening Currently");
                        }
                    }
                    if (isOnBazaarMenu()) {
                        if (AIOMVigilanceConfig.bazaarFlipDevMode) {
                            System.out.println("Inside Bazaar Menu | Running Next Event");
                        }
                        runNextEvent();
                    } else if (!isGuiOpen()) {
                        if (AIOMVigilanceConfig.bazaarFlipDevMode) {
                            System.out.println("Not in gui, Opening Bazaar");
                        }
                        Main.sendMarkedChatMessage("Opened Bazaar Menu!");
                        if (!AIOMVigilanceConfig.bazaarFlipNpcMode) {
                            Main.mcPlayer.sendChatMessage("/bz");
                        } else {
                            Utils.openNpc("Bazaar");
                        }
                    }
                }
            }
            mainTicks++;
        }
    }

    private boolean isGuiOpen() {
        return Main.mc.currentScreen != null;
    }

    private boolean isOnBazaarMenu() {
        GuiScreen screen = Main.mc.currentScreen;

        if (screen instanceof GuiChest) {
            IInventory chest = ((ContainerChest) (((GuiChest) screen).inventorySlots)).getLowerChestInventory();

            String chestName = chest.getDisplayName().getUnformattedText();
            return chestName.startsWith("Bazaar");
        }
        return false;
    }

    private void runNextEvent() {
        if (!checkForCuts() && !checkForFills()) {
            if (AIOMVigilanceConfig.bazaarFlipDevMode) {
                System.out.println("No cuts / fills");
            }
            if ((orderNum < Integer.parseInt(AIOMVigilanceConfig.maxFlips) || Integer.parseInt(AIOMVigilanceConfig.maxFlips) == 0) && Utils.currentTimeMillis() >= lastOrderMillis + 5000) {
                if (AIOMVigilanceConfig.bazaarFlipDevMode) {
                    System.out.println("Grabbing key for next order");
                }
                key = getKey();
                if (!key.equals("")) {
                    if (AIOMVigilanceConfig.bazaarFlipDevMode) {
                        System.out.println("Starting next order");
                    }
                    doOrders = true;
                }
            }
        } else {
            Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 41, 0, 0, Main.mcPlayer);

            currentManagedStack = null;
            manageBuy = true;
            manageFill = false;
            manageOrderPhase = 0;
            manageTicks = 0;

            manageOrders = true;
        }
    }

    @SubscribeEvent
    public void setBuyOrders(@NotNull GuiScreenEvent.DrawScreenEvent event) {
        if (doOrders && event.gui instanceof GuiChest && key != null && !key.equals("") && MacroHandler.isMacroOn && AIOMVigilanceConfig.macroType == 3) {
            if (orderTicks >= AIOMVigilanceConfig.bazaarFlipDelay) {
                orderTicks = 0;
                IInventory chest = ((ContainerChest) (((GuiChest) event.gui).inventorySlots)).getLowerChestInventory();

                String chestName = chest.getDisplayName().getUnformattedText();
                if (chestName.startsWith("Bazaar")) {
                    Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, categories.get(key), 0, 0, Main.mcPlayer);
                    Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, subCategories.get(key), 0, 0, Main.mcPlayer);
                }
                if (AIOMVigilanceConfig.devmode) {
                    if (((GuiChest) event.gui).inventorySlots.inventorySlots.get(items.get(key)).getStack() != null) {
                        System.out.println(Utils.stripColor(((GuiChest) event.gui).inventorySlots.inventorySlots.get(items.get(key)).getStack().getDisplayName().toLowerCase()));
                        System.out.println(apiToGame.get(key).toLowerCase());
                    }
                    System.out.println((((GuiChest) event.gui).inventorySlots.inventorySlots.get(10).getStack() != null && ((GuiChest) event.gui).inventorySlots.inventorySlots.get(10).getStack().getItem() != Items.golden_horse_armor));
                    if (((GuiChest) event.gui).inventorySlots.inventorySlots.get(10).getStack() != null) {
                        System.out.println(((GuiChest) event.gui).inventorySlots.inventorySlots.get(10).getStack().getItem());
                    }
                }
                if (((GuiChest) event.gui).inventorySlots.inventorySlots.get(items.get(key)).getStack() != null && Utils.stripColor(((GuiChest) event.gui).inventorySlots.inventorySlots.get(items.get(key)).getStack().getDisplayName().toLowerCase()).equals(apiToGame.get(key).toLowerCase()) && ((GuiChest) event.gui).inventorySlots.inventorySlots.get(10).getStack() != null && ((GuiChest) event.gui).inventorySlots.inventorySlots.get(10).getStack().getItem() != Items.golden_horse_armor) {
                    Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, items.get(key), 0, 0, Main.mcPlayer);
                }

                if (((GuiChest) event.gui).inventorySlots.inventorySlots.get(10).getStack() != null && ((GuiChest) event.gui).inventorySlots.inventorySlots.get(10).getStack().getItem() == Items.golden_horse_armor) {
                    Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 15, 0, 0, Main.mcPlayer);
                }

                if (chestName.contains("How many do you want?")) {
                    Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 10, 0, 0, Main.mcPlayer);
                }

                if (chestName.contains("How much do you want to pay?")) {
                    Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 12, 0, 0, Main.mcPlayer);
                }

                if (chestName.contains("Confirm Buy Order")) {
                    Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 13, 0, 0, Main.mcPlayer);
                    doOrders = false;
                    if (!AIOMVigilanceConfig.bazaarFlipNpcMode) {
                        Main.mcPlayer.sendChatMessage("/bz");
                    } else {
                        Utils.openNpc("Bazaar");
                    }
                    lastOrderMillis = Utils.currentTimeMillis();
                    key = "";
                }
            }
            orderTicks++;
        }
    }

    @SubscribeEvent
    public void manageOrders(@NotNull TickEvent.ClientTickEvent event) {
        if (Main.mc.currentScreen instanceof GuiChest && MacroHandler.isMacroOn && AIOMVigilanceConfig.macroType == 3 && manageOrders) {
            if (manageTicks >= AIOMVigilanceConfig.bazaarFlipDelay) {
                manageTicks = 0;
                IInventory chest = ((ContainerChest) (((GuiChest) Main.mc.currentScreen).inventorySlots)).getLowerChestInventory();

                String chestName = chest.getDisplayName().getUnformattedText();
                if (currentManagedStack == null) {
                    if (chestName.equals("Co-op Bazaar Orders")) {
                        if (buyCuts.size() != 0) {
                            ItemStack stack = getBazaarStackFromName(true, Main.mc.currentScreen, buyCuts.get(0));
                            if (stack != null) {
                                currentManagedStack = stack;
                                manageBuy = true;
                                manageFill = false;
                                manageOrderPhase = 1;
                            }
                        } else if (buyFills.size() != 0) {
                            ItemStack stack = getBazaarStackFromName(true, Main.mc.currentScreen, buyFills.get(0));
                            if (stack != null) {
                                currentManagedStack = stack;
                                manageBuy = true;
                                manageFill = true;
                                manageOrderPhase = 1;
                            }
                        } else if (sellCuts.size() != 0) {
                            ItemStack stack = getBazaarStackFromName(false, Main.mc.currentScreen, sellCuts.get(0));
                            if (stack != null) {
                                currentManagedStack = stack;
                                manageBuy = false;
                                manageFill = false;
                                manageOrderPhase = 1;
                            }
                        } else if (sellFills.size() != 0) {
                            ItemStack stack = getBazaarStackFromName(false, Main.mc.currentScreen, sellFills.get(0));
                            if (stack != null) {
                                currentManagedStack = stack;
                                manageBuy = false;
                                manageFill = true;
                                manageOrderPhase = 1;
                            }
                        }
                    }
                } else {
                    if (manageBuy) {
                        if (!manageFill) {
                            switch (manageOrderPhase) {
                                case 1:
                                    String filledLore = currentManagedStack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8).get(3).toString();
                                    int slot = getBazaarSlotFromStack(true, Main.mc.currentScreen, currentManagedStack);

                                    if (filledLore.contains("Filled:")) {
                                        Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, slot, 0, 0, Main.mcPlayer);
                                    }
                                    Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, slot, 0, 0, Main.mcPlayer);

                                    if (chestName.equals("Order options")) {
                                        manageOrderPhase = 2;
                                    }
                                case 2:
                                    if (chestName.equals("Order options")) {
                                        Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 11, 0, 0, Main.mcPlayer);
                                    }
                                    if (chestName.equals("Co-op Bazaar Orders")) {
                                        manageOrderPhase = 3;
                                    }
                                case 3:
                                    if (chestName.equals("Co-op Bazaar Orders")) {
                                        Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 31, 0, 0, Main.mcPlayer);
                                    }
                                    if (chestName.startsWith("Bazaar")) {
                                        manageOrderPhase = 4;
                                    }
                                case 4:
                                    if (chestName.startsWith("Bazaar")) {
                                        key = gameToApi.get(buyCuts.get(0));
                                        doOrders = true;
                                        buyCuts.remove(0);
                                        currentManagedStack = null;
                                        manageOrderPhase = 0;
                                        manageOrders = false;
                                        if (!AIOMVigilanceConfig.bazaarFlipNpcMode) {
                                            Main.mcPlayer.sendChatMessage("/bz");
                                        } else {
                                            Utils.openNpc("Bazaar");
                                        }
                                    }
                            }
                        } else {
                            switch (manageOrderPhase) {
                                case 1:
                                    if (chestName.equals("Co-op Bazaar Orders")) {
                                        int slot = getBazaarSlotFromStack(true, Main.mc.currentScreen, currentManagedStack);

                                        if (slot != 0) {
                                            Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, slot, 0, 0, Main.mcPlayer);
                                        }
                                        if (buyClaims.size() != 0) {
                                            buyClaims.remove(0);
                                            manageOrderPhase = 2;
                                        }
                                    }
                                case 2:
                                    if (chestName.equals("Co-op Bazaar Orders")) {
                                        Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 31, 0, 0, Main.mcPlayer);
                                    } else if (chestName.startsWith("Bazaar")) {
                                        manageOrderPhase = 3;
                                    }
                                case 3:
                                    key = gameToApi.get(Utils.stripColor(currentManagedStack.getDisplayName()).split("BUY: ")[1]);
                                    doSellOrder(true, chestName);
                            }
                        }
                    } else {
                        //Sell Orders
                        if (!manageFill) {
                            switch (manageOrderPhase) {
                                case 1:
                                    String filledLore = currentManagedStack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8).get(3).toString();
                                    int slot = getBazaarSlotFromStack(false, Main.mc.currentScreen, currentManagedStack);

                                    if (slot != 0) {
                                        if (filledLore.contains("Filled:")) {
                                            Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, slot, 0, 0, Main.mcPlayer);
                                        }
                                        Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, slot, 0, 0, Main.mcPlayer);

                                        if (chestName.equals("Order options")) {
                                            manageOrderPhase = 2;
                                        }
                                    }
                                case 2:
                                    if (chestName.equals("Order options")) {
                                        Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 13, 0, 0, Main.mcPlayer);
                                    } else if (chestName.equals("Co-op Bazaar Orders")) {
                                        manageOrderPhase = 3;
                                    }
                                case 3:
                                    if (chestName.equals("Co-op Bazaar Orders")) {
                                        Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 31, 0, 0, Main.mcPlayer);
                                        manageOrderPhase = 4;
                                    }
                                case 4:
                                    key = gameToApi.get(Utils.stripColor(currentManagedStack.getDisplayName()).split("SELL: ")[1]);
                                    doSellOrder(false, chestName);
                            }
                        } else {
                            switch (manageOrderPhase) {
                                case 1:
                                    int slot = getBazaarSlotFromStack(false, Main.mc.currentScreen, currentManagedStack);

                                    if (slot != 0) {
                                        Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, slot, 0, 0, Main.mcPlayer);
                                    }
                                    if (sellClaims.size() != 0) {
                                        sellClaims.remove(0);
                                        manageOrderPhase = 2;
                                    }
                                case 2:
                                    if (chestName.equals("Co-op Bazaar Orders")) {
                                        Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 31, 0, 0, Main.mcPlayer);
                                    } else if (chestName.startsWith("Bazaar")) {
                                        sellOrders.remove(sellFills.get(0));
                                        sellFills.remove(0);
                                        currentManagedStack = null;
                                        manageOrderPhase = 0;
                                        manageOrders = false;
                                    }
                            }
                        }
                    }
                }
            }
            manageTicks++;
        }
    }

    private void doSellOrder(boolean buy, String chestName) {
        if (chestName.startsWith("Bazaar")) {
            if (buy) {
                Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, categories.get(gameToApi.get(Utils.stripColor(currentManagedStack.getDisplayName()).split("BUY: ")[1])), 0, 0, Main.mcPlayer);
                Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, subCategories.get(gameToApi.get(Utils.stripColor(currentManagedStack.getDisplayName()).split("BUY: ")[1])), 0, 0, Main.mcPlayer);
            } else {
                Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, categories.get(gameToApi.get(Utils.stripColor(currentManagedStack.getDisplayName()).split("SELL: ")[1])), 0, 0, Main.mcPlayer);
                Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, subCategories.get(gameToApi.get(Utils.stripColor(currentManagedStack.getDisplayName()).split("SELL: ")[1])), 0, 0, Main.mcPlayer);
            }
        } else if (((GuiChest) Main.mc.currentScreen).inventorySlots.inventorySlots.get(items.get(key)).getStack() != null && apiToGame.containsKey(key) && ((GuiChest) Main.mc.currentScreen).inventorySlots.inventorySlots.get(items.get(key)).getStack().getDisplayName() != null && Utils.stripColor(((GuiChest) Main.mc.currentScreen).inventorySlots.inventorySlots.get(items.get(key)).getStack().getDisplayName()).equals(apiToGame.get(key))) {
            if (buy) {
                Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, items.get(gameToApi.get(Utils.stripColor(currentManagedStack.getDisplayName()).split("BUY: ")[1])), 0, 0, Main.mcPlayer);
            } else {
                Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, items.get(gameToApi.get(Utils.stripColor(currentManagedStack.getDisplayName()).split("SELL: ")[1])), 0, 0, Main.mcPlayer);
            }
        } else if ((buy &&
                chestName.contains(Utils.stripColor(currentManagedStack.getDisplayName()).split("BUY: ")[1]))
                || (!buy
                && chestName.contains(Utils.stripColor(currentManagedStack.getDisplayName()).split("SELL: ")[1]))) {
            Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 16, 0, 0, Main.mcPlayer);
        } else if (chestName.contains("At what price are you selling?")) {
            Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 12, 0, 0, Main.mcPlayer);
        } else if (chestName.contains("Confirm Sell Offer")) {
            Main.mc.playerController.windowClick(Main.mcPlayer.openContainer.windowId, 13, 0, 0, Main.mcPlayer);
            if (buy) {
                buyOrders.remove(buyFills.get(0));
                buyFills.remove(0);
            } else {
                sellCuts.remove(0);
            }
            currentManagedStack = null;
            manageOrderPhase = 0;
            manageOrders = false;
            key = "";
            if (!AIOMVigilanceConfig.bazaarFlipNpcMode) {
                Main.mcPlayer.sendChatMessage("/bz");
            } else {
                Utils.openNpc("Bazaar");
            }
        }
    }

    private ItemStack getBazaarStackFromName(boolean buy, GuiScreen gui, String item_name) {
        IInventory chest = ((ContainerChest) (((GuiChest) gui).inventorySlots)).getLowerChestInventory();
        for (int i = 1; i <= 7; i++) {
            ItemStack stack;
            if (buy) {
                stack = chest.getStackInSlot(18 + i);
                if (stack != null) {
                    if (stack.getDisplayName().contains("BUY")) {
                        if (Utils.stripColor(stack.getDisplayName()).split("BUY: ")[1].equals(item_name)) {
                            return stack;
                        }
                    }
                }
            } else {
                stack = chest.getStackInSlot(9 + i);
                if (stack != null) {
                    if (stack.getDisplayName().contains("SELL")) {
                        if (Utils.stripColor(stack.getDisplayName()).split("SELL: ")[1].equals(item_name)) {
                            return stack;
                        }
                    }
                }
            }
        }
        return null;
    }

    private int getBazaarSlotFromStack(boolean buy, GuiScreen gui, ItemStack stackToFind) {
        IInventory chest = ((ContainerChest) (((GuiChest) gui).inventorySlots)).getLowerChestInventory();
        for (int i = 1; i < 7; i++) {
            ItemStack stack;
            if (buy) {
                stack = chest.getStackInSlot(18 + i);
                if (stack != null) {
                    if (stack.getDisplayName().equals(stackToFind.getDisplayName())) {
                        return 18 + i;
                    }
                }
            } else {
                stack = chest.getStackInSlot(9 + i);
                if (stack != null) {
                    if (stack.getDisplayName().equals(stackToFind.getDisplayName())) {
                        return 9 + i;
                    }
                }
            }
        }
        return 0;
    }

    private void iterateProducts(Object[] productArray) {

        for (Object o : productArray) {
            JsonObject jsonObject = new JsonParser().parse(String.valueOf(o).split("=")[1]).getAsJsonObject();

            JsonObject quickStatus = jsonObject.getAsJsonObject("quick_status");

            double sellPrice = quickStatus.get("sellPrice").getAsDouble();

            double buyPrice = quickStatus.get("buyPrice").getAsDouble();

            double sellVolume = quickStatus.get("sellVolume").getAsDouble();

            double margin = buyPrice - sellPrice;

            String productId = quickStatus.get("productId").getAsString();

            margins.put(productId, margin);
            volumes.put(productId, sellVolume);
            prices.put(productId, buyPrice);

            sortMargins();
        }
    }

    private String getKey() {
        if (sortedMargins.entrySet().stream().findFirst().isPresent()) {
            String key = sortedMargins.entrySet().stream().findFirst().get().getKey();

            if (!key.equals("")) {

                blackList = AIOMVigilanceConfig.getBazaarBlacklist();

                if (AIOMVigilanceConfig.bazaarFlipDevMode) {
                    System.out.println(sortedMargins.entrySet());
                }

                if ((Double.parseDouble(AIOMVigilanceConfig.minPrice) <= prices.get(key) || Double.parseDouble(AIOMVigilanceConfig.minPrice) == 0)
                        && (Double.parseDouble(AIOMVigilanceConfig.maxPrice) >= prices.get(key) * 64 || Double.parseDouble(AIOMVigilanceConfig.maxPrice) == 0)
                        && (Double.parseDouble(AIOMVigilanceConfig.minMargin) <= margins.get(key) || Double.parseDouble(AIOMVigilanceConfig.minMargin) == 0)
                        && (Double.parseDouble(AIOMVigilanceConfig.maxMargin) >= margins.get(key) || Double.parseDouble(AIOMVigilanceConfig.maxMargin) == 0)
                        && (Double.parseDouble(AIOMVigilanceConfig.minVolume) <= volumes.get(key) || Double.parseDouble(AIOMVigilanceConfig.minVolume) == 0)
                        && (Double.parseDouble(AIOMVigilanceConfig.maxVolume) <= volumes.get(key) || Double.parseDouble(AIOMVigilanceConfig.maxVolume) == 0)
                        && !buyOrders.containsKey(apiToGame.get(key))
                        && !sellOrders.containsKey(apiToGame.get(key))
                        && !blackList.contains(key)) {
                    return key;
                } else {
                    sortedMargins.remove(key);
                    return getKey();
                }
            }
        }
        return "";
    }

    private boolean checkForCuts() {
        for (Map.Entry<String, Double> set : buyOrders.entrySet()) {
            if (checkForCut(set.getKey(), set.getValue(), false)) {
                return true;
            }
        }
        for (Map.Entry<String, Double> set : sellOrders.entrySet()) {
            if (checkForCut(set.getKey(), set.getValue(), true)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkForFills() {
        return (buyFills.size() != 0 || sellFills.size() != 0);
    }

    private boolean checkForCut(String item_name, double itemPrice, boolean sell) {
        for (Object o : Utils.bazaarApi.entrySet().toArray()) {
            if (String.valueOf(o).split("=")[0].equals(gameToApi.get(item_name))) {
                JsonObject item = new JsonParser().parse(String.valueOf(o).split("=")[1]).getAsJsonObject();

                JsonObject summary;

                if (!sell) {
                    summary = item.get("sell_summary").getAsJsonArray().get(0).getAsJsonObject();
                } else {
                    summary = item.get("buy_summary").getAsJsonArray().get(0).getAsJsonObject();
                }

                double pricePerUnit = Double.parseDouble(summary.get("pricePerUnit").getAsString());

                if (sell) {
                    if (pricePerUnit < itemPrice) {
                        Main.sendMarkedChatMessage(item_name + " Has Been Undercut! Relisting.");
                        sellCuts.add(item_name);
                        return true;
                    }
                } else {
                    if (pricePerUnit > itemPrice) {
                        Main.sendMarkedChatMessage(item_name + " Has Been Overcut! Relisting.");
                        if (!buyCuts.contains(item_name)) {
                            buyCuts.add(item_name);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public void chestOpen(@NotNull GuiScreenEvent.DrawScreenEvent event) {
        if (event.gui instanceof GuiChest) {
            IInventory chest = ((ContainerChest) (((GuiChest) event.gui).inventorySlots)).getLowerChestInventory();

            String chestName = chest.getDisplayName().getUnformattedText();

            if (chest.hasCustomName()) {
                if (chestName.equals("Confirm Buy Order") || chestName.equals("Confirm Sell Offer")) {
                    ItemStack stack = chest.getStackInSlot(13);

                    if (stack != null) {
                        String pricePerUnitLore = stack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8).get(2).toString();

                        String orderLore = stack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8).get(4).toString();

                        String strippedPricePerUnitLore = Utils.stripColor(pricePerUnitLore);

                        String strippedOrderLore = Utils.stripColor(orderLore);

                        String pricePerUnitString = strippedPricePerUnitLore.split("Price per unit: ")[1].split(" coins")[0];

                        String amountString;
                        String itemName;

                        if (strippedOrderLore.contains("Order")) {
                            amountString = strippedOrderLore.split("Order: ")[1].split("x ")[0];
                            itemName = strippedOrderLore.split("Order: ")[1].split("x ")[1].replace("\"", "");
                        } else {
                            amountString = strippedOrderLore.split("Selling: ")[1].split("x ")[0];
                            itemName = strippedOrderLore.split("Selling: ")[1].split("x ")[1].replace("\"", "");
                        }

                        double pricePerUnit = Double.parseDouble(pricePerUnitString.replace(",", ""));
                        int amount = Integer.parseInt(amountString.replace(",", ""));

                        lastName = itemName;
                        lastPrice = pricePerUnit;
                        lastAmount = amount;
                    }
                }
                if (chestName.equals("co-op bazaar orders")) {
                    Slot lastMouseSlot = ((GuiChest) event.gui).getSlotUnderMouse();

                    if (lastMouseSlot != null) {
                        ItemStack lastItemStack = chest.getStackInSlot(lastMouseSlot.slotNumber);

                        if (lastItemStack != null) {
                            String lastStackName = Utils.stripColor(lastItemStack.getDisplayName());

                            if (lastStackName.contains("SELL: ") || lastStackName.contains("BUY: ")) {
                                lastCancelledItemName = lastStackName.split(": ")[1];
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void chatReceived(ClientChatReceivedEvent e) {
        String message = e.message.getUnformattedText();

        String strippedMessage = Utils.stripColor(message);

        //Sell Offer Setup | Buy Order Setup
        if (strippedMessage.contains("er Setup!")) {
            int orderCheckNum = Integer.parseInt(strippedMessage.split("Setup! ")[1].split("x ")[0]);
            if (strippedMessage.contains(orderCheckNum + "x ")) {
                String orderCheckItem = strippedMessage.split(orderCheckNum + "x ")[1].split(" for ")[0];

                System.out.println(lastName);
                System.out.println(orderCheckItem);
                System.out.println(lastAmount);
                System.out.println(orderCheckNum);

                if (orderCheckNum == lastAmount && orderCheckItem.equals(lastName)) {
                    if (strippedMessage.contains("Buy Order Setup!")) {
                        buyOrders.put(lastName, lastPrice);
                        orderNum++;
                    } else {
                        sellOrders.put(lastName, lastPrice);
                        orderNum--;
                    }
                }
            }
        }
        if (strippedMessage.contains("Cancelled! Refunded")) {
            if (strippedMessage.contains("buy")) {
                buyOrders.remove(lastCancelledItemName);
                buyCuts.remove(lastCancelledItemName);
            } else {
                sellOrders.remove(lastCancelledItemName);
                sellCuts.remove(lastCancelledItemName);
            }
        }
        if (strippedMessage.contains("Bazaar! Claimed")) {
            if (strippedMessage.contains("bought for ")) {
                String itemName = strippedMessage.split("x ")[1].split(" worth")[0];
                buyOrders.remove(itemName);
                buyClaims.add(itemName);
            } else {
                String itemName = strippedMessage.split("x ")[1].split(" at ")[0];
                sellOrders.remove(itemName);
                sellClaims.add(itemName);
            }
        }
        if (strippedMessage.contains("was filled!")) {
            String itemName = strippedMessage.split("x ")[1].split(" was filled!")[0];
            if (strippedMessage.contains("Buy")) {
                buyFills.add(itemName);
            } else {
                sellFills.add(itemName);
            }
        }
    }
}