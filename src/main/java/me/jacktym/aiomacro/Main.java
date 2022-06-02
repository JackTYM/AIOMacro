package me.jacktym.aiomacro;

import gg.essential.elementa.effects.StencilEffect;
import gg.essential.universal.UMinecraft;
import gg.essential.universal.UScreen;
import kotlin.jvm.internal.Intrinsics;
import me.jacktym.aiomacro.commands.AIOM;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.keybinds.ModInputHandler;
import me.jacktym.aiomacro.macros.*;
import me.jacktym.aiomacro.proxy.ClientProxy;
import me.jacktym.aiomacro.proxy.CommonProxy;
import me.jacktym.aiomacro.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
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
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

@Mod(modid = NGGlobal.MOD_ID, name = NGGlobal.MOD_NAME, version = NGGlobal.VERSION)
public class Main {

    @Instance(NGGlobal.MOD_ID)
    public static Main instance;

    @SidedProxy(clientSide = NGGlobal.NG_CLIENT_PROXY, serverSide = NGGlobal.NG_COMMON_PROXY)
    public static CommonProxy proxy;

    public static UScreen gui = null;

    public static Minecraft mc = Minecraft.getMinecraft();

    public static EntityPlayerSP mcPlayer = mc.thePlayer;

    public static WorldClient mcWorld = mc.theWorld;

    public static int tick = 0;

    public static void sendChatMessage(String message) {
        mcPlayer.addChatMessage(new ChatComponentText(message));
    }

    public static void sendMarkedChatMessage(String message) {
        mcPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "[" + EnumChatFormatting.LIGHT_PURPLE + "AIOM" + EnumChatFormatting.DARK_PURPLE + "] " + EnumChatFormatting.RESET + message));
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent preEvent) {

        proxy.preInit(preEvent);

        AIOMVigilanceConfig.INSTANCE = new AIOMVigilanceConfig();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        ClientCommandHandler.instance.registerCommand(new AIOM());

        MinecraftForge.EVENT_BUS.register(new ModInputHandler());
        MinecraftForge.EVENT_BUS.register(new FastBreak());
        MinecraftForge.EVENT_BUS.register(new Failsafe());
        MinecraftForge.EVENT_BUS.register(new FarmingHUD());
        MinecraftForge.EVENT_BUS.register(new NetherWart());
        MinecraftForge.EVENT_BUS.register(new SugarCane());
        MinecraftForge.EVENT_BUS.register(new AntiStuck());
        MinecraftForge.EVENT_BUS.register(new DesyncFailsafe());
        MinecraftForge.EVENT_BUS.register(new Nuker());
        MinecraftForge.EVENT_BUS.register(new SetPlayerLook());
        MinecraftForge.EVENT_BUS.register(new CropAura());
        MinecraftForge.EVENT_BUS.register(new BazaarFlipper());
        MinecraftForge.EVENT_BUS.register(new Main());


        MinecraftForge.EVENT_BUS.register(new CaneBuilder());
        MinecraftForge.EVENT_BUS.register(new AutoBazaarUnlocker());

        StencilEffect.Companion.enableStencil();

        ClientProxy.keyBindings = new KeyBinding[1];

        ClientProxy.keyBindings[0] = new KeyBinding("Toggle Macro", Keyboard.KEY_DELETE, "All-In-One Macro");

        for (int i = 0; i < ClientProxy.keyBindings.length; i++) {
            ClientRegistry.registerKeyBinding(ClientProxy.keyBindings[i]);
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent postEvent) {

        Utils.updateBazaarApi();

        proxy.postInit(postEvent);

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