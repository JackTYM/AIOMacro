package me.jacktym.aiomacro.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.JVMAnnotationPropertyCollector;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;
import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.gui.HUDLocationGui;
import me.jacktym.aiomacro.macros.*;
import me.jacktym.aiomacro.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class AIOMVigilanceConfig extends Vigilant {

    public static final File modDir = new File(Minecraft.getMinecraft().mcDataDir, "config/AIO-Macro");
    public static final File configFile = new File(modDir + "/AIO-Macro.toml");

    //Macro Settings
    public static AIOMVigilanceConfig INSTANCE;
    public static boolean awaitShowColourWindow = false;
    @Property(
            type = PropertyType.SELECTOR,
            name = "Macro Type",
            description = "Choose the macro you would like to use.",
            category = "Macro Settings",
            options = {"Netherwart/S-Shaped", "Sugarcane", "Nuker", "Bazaar Flipper", "Fairy Soul Aura", "Shiny Pig ESP", "Minion Aura"}
    )
    public static int macroType;
    @Property(
            type = PropertyType.SLIDER,
            name = "Pitch",
            description = "Sets the value you are looking up and down (Check f3).",
            category = "Macro Settings",
            min = -90,
            max = 90
    )
    public static int pitch;
    @Property(
            type = PropertyType.SLIDER,
            name = "Yaw",
            description = "Sets the value you are looking left to right (Check f3).",
            category = "Macro Settings",
            min = -180,
            max = 180
    )
    public static int yaw;
    @Property(
            type = PropertyType.SWITCH,
            name = "Auto Godpot",
            description = "Automatically buys and consumes a god potion. (Requires gold in purse).",
            category = "Macro Settings"
    )
    public static boolean autogodpotion;
    @Property(
            type = PropertyType.SWITCH,
            name = "Fast Break",
            description = "Breaks crops at 40bps to allow for faster rancher boot speeds",
            category = "Macro Settings"
    )
    public static boolean fastBreak;
    @Property(
            type = PropertyType.SELECTOR,
            name = "Fast Break BPS",
            description = "How many blocks per second (20 Ticks) Fast Break breaks.",
            category = "Macro Settings",
            options = {"20bps", "40bps", "60bps", "80bps"}
    )
    public static int fastBreakBPS;
    @Property(
            type = PropertyType.SWITCH,
            name = "Auto Sell",
            description = "Automatically sells crop to the npc at 7 stacks",
            category = "Macro Settings"
    )
    public static boolean autoSell;
    @Property(
            type = PropertyType.SWITCH,
            name = "Drop 180",
            description = "Does a 180 turn after dropping during a farming macro",
            category = "Macro Settings"
    )
    public static boolean drop180;
    //Failsafes
    @Property(
            type = PropertyType.SWITCH,
            name = "Webhook Alerts",
            description = "Sends webhook on failsafe, message, party invite, island visit, etc.",
            category = "Failsafes",
            subcategory = "Webhooks"
    )
    public static boolean webhookAlerts;
    @Property(
            type = PropertyType.SLIDER,
            name = "Auto Webhook",
            description = "How often to automatically send a webhook (0 to disable)",
            category = "Failsafes",
            max = 360
    )
    public static int autoWebhook = 0;
    @Property(
            type = PropertyType.TEXT,
            name = "Webhook Link",
            description = "Link to send webhook alerts to.",
            category = "Failsafes",
            subcategory = "Webhooks"
    )
    public static String webhookLink;
    @Property(
            type = PropertyType.SLIDER,
            name = "Random Delay Minimum",
            description = "The Minimum Random Delay (Milliseconds).",
            category = "Failsafes",
            min = 100,
            max = 5000
    )
    public static int randomDelayMin = 1000;
    @Property(
            type = PropertyType.SLIDER,
            name = "Random Delay Maximum",
            description = "The Maximum Random Delay (Milliseconds).",
            category = "Failsafes",
            min = 100,
            max = 5000
    )
    public static int randomDelayMax = 2000;
    @Property(
            type = PropertyType.SLIDER,
            name = "Auto Disable",
            description = "How long to farm until disabling (0 to disable)",
            category = "Failsafes",
            max = 360
    )
    public static int autoDisable = 0;
    @Property(
            type = PropertyType.SELECTOR,
            name = "Island Failsafe",
            description = "Triggers when not on island.",
            category = "Failsafes",
            options = {"Stop Farming", "Teleport To Island", "Disable"}
    )
    public static int islandfailsafe;
    @Property(
            type = PropertyType.SWITCH,
            name = "Jacob Failsafe",
            description = "Stops farming during Jacob's contests.",
            category = "Failsafes"
    )
    public static boolean jacobfailsafe;
    @Property(
            type = PropertyType.SWITCH,
            name = "Bedrock Failsafe",
            description = "Stops farming when looking at bedrock.",
            category = "Failsafes"
    )
    public static boolean bedrockfailsafe;
    @Property(
            type = PropertyType.SWITCH,
            name = "Set Spawn",
            description = "Sets your island spawn after each row (Used to infinite farm when using teleport to island failsafe).",
            category = "Failsafes"
    )
    public static boolean setSpawnFailsafe;
    @Property(
            type = PropertyType.SWITCH,
            name = "Play Sound",
            description = "Plays a sound to alert you when a failsafe is triggered.",
            category = "Failsafes"
    )
    public static boolean soundfailsafe;
    @Property(
            type = PropertyType.SWITCH,
            name = "AntiStuck",
            description = "Detects when a player is stuck and attempts to unstick them.",
            category = "Failsafes"
    )
    public static boolean antiStuckFailsafe;
    @Property(
            type = PropertyType.SWITCH,
            name = "AntiStuck Jump",
            description = "Includes jumping in AntiStuck motion (helpful in some farms, unhelpful in others).",
            category = "Failsafes"
    )
    public static boolean antiStuckJump;
    @Property(
            type = PropertyType.SWITCH,
            name = "Desync Failsafe",
            description = "Detects when a player desyncs from farming and resyncs.",
            category = "Failsafes"
    )
    public static boolean desyncFailsafe;
    @Property(
            type = PropertyType.SWITCH,
            name = "Banwave Failsafe",
            description = "Detects when a banwave is in place, stops macroing, and leaves SkyBlock.",
            category = "Failsafes"
    )
    public static boolean banwaveFailsafe;
    @Property(
            type = PropertyType.NUMBER,
            name = "Minimum Bans",
            description = "The minimum bans to trigger the banwave failsafe.",
            category = "Failsafes",
            min = 1,
            max = 5
    )
    public static int banWavePlayers = 3;

    //Farming HUD
    @Property(
            type = PropertyType.SWITCH,
            name = "Toggle Farming HUD",
            description = "Toggles showing of the Farming HUD.",
            category = "Farming HUD"
    )
    public static boolean farmingHUDOn;
    @Property(
            type = PropertyType.SLIDER,
            name = "Farming HUD X",
            description = "Sets the X value of farming hud.",
            category = "Farming HUD"
    )
    public static int farmingHUDX = 0;
    @Property(
            type = PropertyType.SLIDER,
            name = "Farming HUD Y",
            description = "Sets the Y value of farming hud.",
            category = "Farming HUD"
    )
    public static int farmingHUDY = 0;
    @Property(
            type = PropertyType.SWITCH,
            name = "Total Profit",
            description = "Displays how much profit you have made farming so far.",
            category = "Farming HUD",
            subcategory = "Profit"
    )
    public static boolean totalProfitHUD;
    @Property(
            type = PropertyType.SWITCH,
            name = "Profit Per Hour",
            description = "Displays how much profit you make every hour.",
            category = "Farming HUD",
            subcategory = "Profit"
    )
    public static boolean profitPerHourHUD;
    @Property(
            type = PropertyType.SWITCH,
            name = "Profit Per 12 Hours",
            description = "Displays how much profit you make every 12 hours.",
            category = "Farming HUD",
            subcategory = "Profit"
    )
    public static boolean profitPer12HoursHUD;
    @Property(
            type = PropertyType.SWITCH,
            name = "Profit Per 24 Hours",
            description = "Displays how much profit you make every 24 hours.",
            category = "Farming HUD",
            subcategory = "Profit"
    )
    public static boolean profitPer24HoursHUD;
    @Property(
            type = PropertyType.SWITCH,
            name = "Crops Per Hour",
            description = "Displays how many crops you yield in an hour.",
            category = "Farming HUD",
            subcategory = "Profit"
    )
    public static boolean cropsPerHourHUD;
    @Property(
            type = PropertyType.SWITCH,
            name = "Total Final Crops Yielded",
            description = "Displays how many final crop upgrades you yielded.",
            category = "Farming HUD",
            subcategory = "Statistics"
    )
    public static boolean totalFinalCropHUD;
    @Property(
            type = PropertyType.SWITCH,
            name = "Mathematical Hoe Counter",
            description = "Displays the counter on your mathematical hoe.",
            category = "Farming HUD",
            subcategory = "Statistics"
    )
    public static boolean hoeCounterHUD;
    @Property(
            type = PropertyType.SWITCH,
            name = "God Potion Time",
            description = "Displays time remaining on God Potion.",
            category = "Farming HUD",
            subcategory = "Failsafes"
    )
    public static boolean godPotionTimeHUD;
    @Property(
            type = PropertyType.SWITCH,
            name = "Booster Cookie Time",
            description = "Displays time remaining on Booster Cookie.",
            category = "Farming HUD",
            subcategory = "Failsafes"
    )
    public static boolean boosterCookieTimeHUD;
    @Property(
            type = PropertyType.SWITCH,
            name = "Jacobs Farming Event Timers",
            description = "Displays time until Jacob's Farming Event Starts/Ends.",
            category = "Farming HUD",
            subcategory = "Failsafes"
    )
    public static boolean jacobsEventHUD;
    @Property(
            type = PropertyType.SWITCH,
            name = "Total Farming Time",
            description = "Displays total time spent macroing.",
            category = "Farming HUD",
            subcategory = "Failsafes"
    )
    public static boolean farmingTime;
    @Property(
            type = PropertyType.COLOR,
            name = "HUD Text Color",
            description = "Sets the color for the HUD Text.",
            category = "Farming HUD",
            subcategory = "Color"
    )
    public static Color hudColor = Color.WHITE;

    @Property(
            type = PropertyType.BUTTON,
            name = "Change Farming HUD Position",
            description = "Changes the position of the Farming HUD.",
            category = "Farming HUD"
    )
    public final void changeFarmingHUD() {
        Minecraft.getMinecraft().displayGuiScreen(new HUDLocationGui());
    }

    @Property(
            type = PropertyType.BUTTON,
            name = "Reset Farming HUD Position",
            description = "Resets the position of the Farming HUD.",
            category = "Farming HUD"
    )
    public final void resetFarmingHUD() {
        farmingHUDX = 0;
        farmingHUDY = 0;
    }

    //Nuker
    @Property(
            type = PropertyType.SELECTOR,
            name = "Nuker Block",
            description = "Chooses the block to be nuked.",
            category = "Nuker",
            options = {"Mycelium", "Red Sand", "Wood", "Any Crop But Cane / Cactus", "Cane/Cactus", "Custom Nuker"}
    )
    public static int nukerBlock;
    @Property(
            type = PropertyType.TEXT,
            name = "Custom Nuker Block",
            description = "A custom block name to nuke.",
            category = "Nuker"
    )
    public static String customNukerBlock = "";
    @Property(
            type = PropertyType.SELECTOR,
            name = "Nuker BPS",
            description = "How many blocks per second (20 Ticks) Nuker breaks.",
            category = "Nuker",
            options = {"20bps", "40bps", "60bps", "80bps"}
    )
    public static int nukerBPS;
    @Property(
            type = PropertyType.COLOR,
            name = "Next Block Color",
            description = "Sets the color displayed on the next nuked block.",
            category = "Nuker"
    )
    public static Color nukerColor = Color.RED;


    //Debugging
    @Property(
            type = PropertyType.SWITCH,
            name = "Developer Mode",
            description = "Used to print to the logs to debug.",
            category = "Debugging"
    )
    public static boolean devmode;
    @Property(
            type = PropertyType.SLIDER,
            name = "Turn Speed",
            description = "How fast you turn",
            category = "Debugging",
            min = 1,
            max = 50
    )
    public static int turnSpeed = 10;
    @Property(
            type = PropertyType.SELECTOR,
            name = "Auto Wheat Phase",
            description = "Sets what phase to start the auto wheat on",
            category = "Debugging",
            subcategory = "Auto Wheat",
            options = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"}
    )
    public static int autoWheatPhase;
    @Property(
            type = PropertyType.CHECKBOX,
            name = "Auto Wheat Private Lobby Mode",
            description = "Instead of using private lobbies, switches between private ones (requires rank) [WIP!!!]",
            category = "Debugging",
            subcategory = "Auto Wheat"
    )
    public static boolean autoWheatPrivate;
    //Bazaar Flipper
    @Property(
            type = PropertyType.TEXT,
            name = "Bazaar Flipper Max Orders",
            description = "The max orders the bot will place.",
            category = "Bazaar Flipper"
    )
    public static String maxFlips = "6";
    //Cane Builder
    @Property(
            type = PropertyType.NUMBER,
            name = "Cane Builder Layers",
            description = "How many layers to build",
            category = "Cane Builder",
            min = 1,
            max = 9
    )
    public static int caneBuilderLayers = 1;
    @Property(
            type = PropertyType.SELECTOR,
            name = "Cane Builder Phase",
            description = "Sets what phase to start the cane builder on",
            category = "Cane Builder",
            subcategory = "Debug",
            options = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"}
    )
    public static int caneBuilderPhase;
    @Property(
            type = PropertyType.SELECTOR,
            name = "Crop Aura BPS",
            description = "How many blocks per second (20 Ticks) Crop Aura places.",
            category = "Cane Builder",
            options = {"20bps", "40bps", "60bps", "80bps"}
    )
    public static int cropAuraBPS;
    public AIOMVigilanceConfig() {
        super(configFile, "AIO-Macro Config", new JVMAnnotationPropertyCollector(), new AIOMSortingBehavior());
        System.out.println("Config Init!!");

        awaitShowColourWindow = false;
        hudColor = Color.WHITE;
        if (webhookLink == null) {
            webhookLink = "";
        }

        this.preload();
        this.writeData();
        if (modDir.mkdirs()) {
            System.out.println("[AIOM] Created config directory");
        }
        this.initialize();
    }

    public static List<String> getBazaarBlacklist() {
        List<String> blacklistBazaar = new ArrayList<>();
        if (blackListItems.contains(",")) {
            for (String item : blackListItems.split(",")) {
                if (BazaarFlipper.gameToApi.containsKey(item)) {
                    blacklistBazaar.add(BazaarFlipper.gameToApi.get(item));
                } else {
                    System.out.println("[AIOM] Error! " + item + " Not Found! Please check spelling and capitalization!");
                }
            }
        } else if (!blackListItems.equals("")) {
            if (BazaarFlipper.gameToApi.containsKey(blackListItems)) {
                blacklistBazaar.add(BazaarFlipper.gameToApi.get(blackListItems));
            } else {
                System.out.println("[AIOM] Error! " + blackListItems + " Not Found! Please check spelling and capitalization!");
            }
        }
        return blacklistBazaar;
    }

    @Property(
            type = PropertyType.BUTTON,
            name = "Macro On",
            description = "Toggles the macro on and off",
            placeholder = "Toggle!",
            category = "Macro Settings"
    )
    public final void macroToggle() {
        MacroHandler.toggleMacro();
    }

    //Account Linking
    @Property(
            type = PropertyType.TEXT,
            name = "AIOM Linking Code",
            description = "Get in the AIOM Discord by running /link",
            category = "Account Linking"
    )
    public static String linkCode = "";

    @Property(
            type = PropertyType.BUTTON,
            name = "Test Look Omg!?!?",
            description = "Epic Gamering.",
            placeholder = "Click!",
            category = "Debugging"
    )
    public final void toggleTestLook() {
        SetPlayerLook.setDefault();

        SetPlayerLook.toggled = !SetPlayerLook.toggled;
    }

    @Property(
            type = PropertyType.BUTTON,
            name = "Show Block Pos Omg!?!?",
            description = "Epic Gamering.",
            placeholder = "Click!",
            category = "Debugging"
    )
    public final void toggleBlockPos() {
        AutoBazaarUnlocker.showBlockPos = !AutoBazaarUnlocker.showBlockPos;
    }

    @Property(
            type = PropertyType.BUTTON,
            name = "Toggle Cane Builder",
            description = "Omg Free Money",
            placeholder = "Click Me!",
            category = "Cane Builder"
    )
    public final void toggleCaneBuilder() {

        CaneBuilder.phase = caneBuilderPhase;

        CaneBuilder.startY = Main.mcPlayer.posY;

        CaneBuilder.startZ = new BlockPos(Main.mcPlayer.posX, Main.mcPlayer.posY - 1, Main.mcPlayer.posZ).getZ();

        CaneBuilder.west = (0 < Main.mcPlayer.posX);

        CaneBuilder.caneBuilderOn = !CaneBuilder.caneBuilderOn;

        SetPlayerLook.toggled = CaneBuilder.caneBuilderOn;

    }

    @Property(
            type = PropertyType.BUTTON,
            name = "Crop Aura Toggle",
            description = "Epic Gamering.",
            placeholder = "Click!",
            category = "Cane Builder"
    )
    public final void toggleCropAura() {
        CropAura.toggled = !CropAura.toggled;
    }

    @Property(
            type = PropertyType.TEXT,
            name = "Bazaar Flipper Min Price",
            description = "The minimum price of an item to order it (0 To Disable).",
            category = "Bazaar Flipper"
    )
    public static String minPrice = "1000";
    @Property(
            type = PropertyType.TEXT,
            name = "Bazaar Flipper Max Price",
            description = "The maximum price of an item to order it (0 To Disable).",
            category = "Bazaar Flipper"
    )
    public static String maxPrice = "1000000";
    @Property(
            type = PropertyType.TEXT,
            name = "Bazaar Flipper Min Margin",
            description = "The minimum margin of an item to order it (0 To Disable).",
            category = "Bazaar Flipper"
    )
    public static String minMargin = "50000";
    @Property(
            type = PropertyType.TEXT,
            name = "Bazaar Flipper Max Margin",
            description = "The maximum margin of an item to order it stopping manipulations and api bugs (0 To Disable).",
            category = "Bazaar Flipper"
    )
    public static String maxMargin = "500000";
    @Property(
            type = PropertyType.TEXT,
            name = "Bazaar Flipper Min Volume",
            description = "The minimum volume of an item to order it (0 To Disable).",
            category = "Bazaar Flipper"
    )
    public static String minVolume = "500000";
    @Property(
            type = PropertyType.TEXT,
            name = "Bazaar Flipper Max Volume",
            description = "The maximum volume of an item to order it (0 To Disable).",
            category = "Bazaar Flipper"
    )
    public static String maxVolume = "0";
    @Property(
            type = PropertyType.TEXT,
            name = "Blacklist",
            description = "Items to blacklist when flipping",
            category = "Bazaar Flipper"
    )
    public static String blackListItems = "❁ Perfect Jasper Gemstone,Enchanted Cobblestone";
    @Property(
            type = PropertyType.CHECKBOX,
            name = "Developer Mode",
            description = "Helps with debugging",
            category = "Bazaar Flipper"
    )
    public static boolean bazaarFlipDevMode = false;
    @Property(
            type = PropertyType.CHECKBOX,
            name = "NPC Mode",
            description = "Clicks the Bazaar NPC Rather than using /bz",
            category = "Bazaar Flipper"
    )
    public static boolean bazaarFlipNpcMode = false;
    @Property(
            type = PropertyType.SLIDER,
            name = "Tick Delay",
            description = "Test for tick delay",
            category = "Bazaar Flipper",
            min = 1,
            max = 100
    )
    public static int bazaarFlipDelay = 20;

    @Property(
            type = PropertyType.BUTTON,
            name = "Auto Wheat Omg!?!?",
            description = "Epic Gamering.",
            placeholder = "Click!",
            category = "Debugging",
            subcategory = "Auto Wheat"
    )
    public final void toggleAutoWheat() {
        AutoBazaarUnlocker.autoWheatOn = !AutoBazaarUnlocker.autoWheatOn;

        AutoBazaarUnlocker.phase = autoWheatPhase;

        SetPlayerLook.toggled = false;
    }

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Remote Controlling",
            description = "Allows your account to be controlled remotely via the discord bot (Only through a linked account)",
            category = "Account Linking"
    )
    public static boolean remoteControllingOn = true;
    @Property(
            type = PropertyType.SWITCH,
            name = "Enable Rendering",
            description = "Disables all rendering, can be used to fix rendering bugs.",
            category = "Rendering"
    )
    public static boolean renderingEnabled = true;

    @Property(
            type = PropertyType.BUTTON,
            name = "Test Webhook",
            description = "Sends a test message to the webhook provided.",
            placeholder = "Send!",
            category = "Failsafes",
            subcategory = "Webhooks"
    )
    public final void testWebhook() {

        String screenshotLink = Objects.requireNonNull(Utils.takeScreenshot()).replace("\"", "");

        String jsonString = "{\"content\":null,\"embeds\":[{\"title\":\"Test Message | No Actions Done\",\"description\":\"A webhook test was requested. No\\nactions were taken.\\n\\nAccount: " + Main.mcPlayer.getName() + "\",\"color\":5814783,\"author\":{\"name\":\"AIO-Macro\"},\"image\":{\"url\":\"" + screenshotLink + "\"}}]}\n";

        String trimmed = jsonString.trim();

        JsonParser parser = new JsonParser();

        JsonElement jsonElement = parser.parse(trimmed);

        Utils.sendWebhook(jsonElement, AIOMVigilanceConfig.webhookLink);

    }

    @Property(
            type = PropertyType.BUTTON,
            name = "Link Account",
            description = "Links your Discord account to your Minecraft using the link code",
            category = "Account Linking"
    )
    public final void link() {
        sendRequest("{\"content\":null,\"embeds\":[{\"title\":\"Account Link Request\",\"description\":\"" + linkCode + ":" + Main.mcPlayer.getGameProfile().getId().toString() + "\"}]}\n");
    }

    @Property(
            type = PropertyType.BUTTON,
            name = "Unlink Account",
            description = "Unlinks your Discord account from your Minecraft",
            category = "Account Linking"
    )
    public final void unlink() {
        sendRequest("{\"content\":null,\"embeds\":[{\"title\":\"Account Unlink Request\",\"description\":\"" + linkCode + ":" + Main.mcPlayer.getGameProfile().getId().toString() + "\"}]}\n");
    }

    private void sendRequest(String jsonString) {
        try {
            String readMe = "Hello anybody reading through this. This isn't a rat, this is used for account linking so PLEASE do not nuke this webhook, it will make this more annoying.";

            String linkHook = "https://pastebin.com/raw/KrS1EkjK";

            HttpClient linkClient = HttpClientBuilder.create().build();
            HttpGet linkRequest = new HttpGet(linkHook);
            HttpResponse response = linkClient.execute(linkRequest);

            String linkResult = IOUtils.toString(new BufferedReader(new InputStreamReader(response.getEntity().getContent())));

            String trimmed = jsonString.trim();

            JsonParser parser = new JsonParser();

            JsonElement jsonElement = parser.parse(trimmed);

            Utils.sendWebhook(jsonElement, linkResult);

        } catch (Exception ignored) {
        }
    }

    public final int getRandomDelay() {
        return randomDelayMin + (new Random().nextInt() % (randomDelayMax - randomDelayMin + 1));
    }

    public final boolean isAwaitShowColourWindow() {
        return awaitShowColourWindow;
    }

    public final void setAwaitShowColourWindow(boolean var1) {
        awaitShowColourWindow = var1;
    }

    public final AIOMVigilanceConfig getINSTANCE() {
        return INSTANCE;
    }

    public final void setINSTANCE(AIOMVigilanceConfig var1) {
        INSTANCE = var1;
    }
}
