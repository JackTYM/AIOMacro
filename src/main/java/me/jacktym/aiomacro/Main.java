package me.jacktym.aiomacro;

import gg.essential.elementa.effects.StencilEffect;
import gg.essential.universal.UMinecraft;
import gg.essential.universal.UScreen;
import kotlin.jvm.internal.Intrinsics;
import me.jacktym.aiomacro.commands.AIOM;
import me.jacktym.aiomacro.commands.AssCommand;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.keybinds.ModInputHandler;
import me.jacktym.aiomacro.macros.*;
import me.jacktym.aiomacro.proxy.ClientProxy;
import me.jacktym.aiomacro.proxy.CommonProxy;
import me.jacktym.aiomacro.rendering.AssEvents;
import me.jacktym.aiomacro.rendering.AssLayer;
import me.jacktym.aiomacro.rendering.SizeMessage;
import me.jacktym.aiomacro.rendering.ToggleMessage;
import me.jacktym.aiomacro.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Mod(modid = NGGlobal.MOD_ID, name = NGGlobal.MOD_NAME, version = NGGlobal.VERSION)
public class Main {

    @Instance(NGGlobal.MOD_ID)

    @SidedProxy(clientSide = NGGlobal.NG_CLIENT_PROXY, serverSide = NGGlobal.NG_COMMON_PROXY)
    public static CommonProxy proxy;

    public static UScreen gui = null;

    public static Minecraft mc = Minecraft.getMinecraft();

    public static EntityPlayerSP mcPlayer = mc.thePlayer;

    public static WorldClient mcWorld = mc.theWorld;

    public static boolean remindToUpdate = false;

    public static boolean sentUpdateReminder = false;

    public static int tick = 0;

    public static void sendChatMessage(String message) {
        mcPlayer.addChatMessage(new ChatComponentText(message));
    }

    public static void sendMarkedChatMessage(String message) {
        mcPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "[" + EnumChatFormatting.LIGHT_PURPLE + "AIOM" + EnumChatFormatting.DARK_PURPLE + "] " + EnumChatFormatting.RESET + message));
    }

    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("aiom");

    @EventHandler
    public void preInit(FMLPreInitializationEvent preEvent) {

        NETWORK.registerMessage(SizeMessage.class, SizeMessage.class, 0, Side.SERVER);
        NETWORK.registerMessage(SizeMessage.class, SizeMessage.class, 1, Side.CLIENT);
        NETWORK.registerMessage(ToggleMessage.class, ToggleMessage.class, 2, Side.SERVER);
        NETWORK.registerMessage(ToggleMessage.class, ToggleMessage.class, 3, Side.CLIENT);

        proxy.preInit(preEvent);

        AIOMVigilanceConfig.INSTANCE = new AIOMVigilanceConfig();

        try {
            URL versionUrl = new URL("https://raw.githubusercontent.com/JackTYM/AIOMacro/master/currentVersion.txt");
            HttpURLConnection versionConnection = (HttpURLConnection) versionUrl.openConnection();
            InputStream response = versionConnection.getInputStream();
            try (Scanner scanner = new Scanner(response)) {
                String responseBody = scanner.useDelimiter("\\A").next();

                int versionInt = Integer.parseInt(responseBody.replace(".", ""));

                int clientVersion = Integer.parseInt(NGGlobal.VERSION.split("v")[1].replace(".", ""));

                if (clientVersion < versionInt) {
                    remindToUpdate = true;
                }
                if (clientVersion > versionInt) {
                    System.out.println("You are in the future!");
                }
            }
        } catch (Exception ignored) {
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        ClientCommandHandler.instance.registerCommand(new AIOM());
        ClientCommandHandler.instance.registerCommand(new AssCommand());

        MinecraftForge.EVENT_BUS.register(new ModInputHandler());
        MinecraftForge.EVENT_BUS.register(new FastBreak());
        MinecraftForge.EVENT_BUS.register(new Failsafe());
        MinecraftForge.EVENT_BUS.register(new FarmingHUD());
        MinecraftForge.EVENT_BUS.register(new NetherWart());
        MinecraftForge.EVENT_BUS.register(new SugarCane());
        MinecraftForge.EVENT_BUS.register(new AntiStuck());
        MinecraftForge.EVENT_BUS.register(new Nuker());
        MinecraftForge.EVENT_BUS.register(new SetPlayerLook());
        MinecraftForge.EVENT_BUS.register(new CropAura());
        MinecraftForge.EVENT_BUS.register(new BazaarFlipper());
        MinecraftForge.EVENT_BUS.register(new Main());
        MinecraftForge.EVENT_BUS.register(new CaneBuilder());
        MinecraftForge.EVENT_BUS.register(new AutoBazaarUnlocker());
        MinecraftForge.EVENT_BUS.register(new AssEvents());

        StencilEffect.Companion.enableStencil();

        ClientProxy.keyBindings = new KeyBinding[2];

        ClientProxy.keyBindings[0] = new KeyBinding("Toggle Macro", Keyboard.KEY_DELETE, "All-In-One Macro");
        ClientProxy.keyBindings[1] = new KeyBinding("Open Gui", Keyboard.KEY_BACKSLASH, "All-In-One Macro");

        for (int i = 0; i < ClientProxy.keyBindings.length; i++) {
            ClientRegistry.registerKeyBinding(ClientProxy.keyBindings[i]);
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent postEvent) {

        Utils.updateBazaarApi();

        proxy.postInit(postEvent);

        RenderPlayer slim_render = Main.mc.getRenderManager().getSkinMap().get("slim");
        slim_render.addLayer(new AssLayer(slim_render));

        RenderPlayer default_render = Main.mc.getRenderManager().getSkinMap().get("default");
        default_render.addLayer(new AssLayer(default_render));

    }

    @SubscribeEvent
    public final void onJoinWorld(@NotNull EntityJoinWorldEvent e) {
        if (mcWorld != null && mcPlayer != null && !sentUpdateReminder && remindToUpdate) {

            ChatStyle style = new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/JackTYM/AIOMacro/releases/latest"));
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click To Open GitHub!"));
            ChatComponentText linkMessage = new ChatComponentText("https://github.com/JackTYM/AIOMacro/releases/latest");

            linkMessage.setChatStyle(style.setChatHoverEvent(hoverEvent));

            mcPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "[" + EnumChatFormatting.LIGHT_PURPLE + "AIOM" + EnumChatFormatting.DARK_PURPLE + "] " + EnumChatFormatting.RESET + "A New Update Is Available! You could be missing out on new features or bugfixes! "));
            mcPlayer.addChatMessage(linkMessage);
            sentUpdateReminder = true;
        }
    }

    @SubscribeEvent
    public final void tick(@NotNull TickEvent.ClientTickEvent event) {
        Intrinsics.checkNotNullParameter(event, "event");
        if (gui != null) {
            try {
                UMinecraft.getMinecraft().displayGuiScreen(gui);
            } catch (Exception var3) {
                var3.printStackTrace();
            }

            gui = null;
        }

        mcPlayer = mc.thePlayer;

        mcWorld = mc.theWorld;

        tick++;

        if (tick > 20) {
            tick = 0;
        }

    }
}