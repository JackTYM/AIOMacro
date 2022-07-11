package me.jacktym.aiomacro.keybinds;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.macros.AutoHotBar;
import me.jacktym.aiomacro.macros.MacroHandler;
import me.jacktym.aiomacro.macros.SetPlayerLook;
import me.jacktym.aiomacro.proxy.ClientProxy;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.jetbrains.annotations.NotNull;

public class ModInputHandler {

    @SubscribeEvent
    public void onKeyInput(@NotNull KeyInputEvent event) {
        KeyBinding[] keyBindings = ClientProxy.keyBindings;

        if (keyBindings[0].isPressed()) {
            MacroHandler.toggleMacro();
        }
        if (keyBindings[1].isPressed()) {
            Main.gui = AIOMVigilanceConfig.INSTANCE.gui();
        }
        if (keyBindings[2].isPressed()) {
            if (AIOMVigilanceConfig.hotBarProfileOne.equals("") || !AutoHotBar.hotBars.containsKey(AIOMVigilanceConfig.hotBarProfileOne)) {
                Main.sendMarkedChatMessage("Error loading HotBar profile! Please set your name in /aiom");
            } else {
                AutoHotBar.hotBarSolution = AutoHotBar.findHotBarSolution(AutoHotBar.hotBars.get(AIOMVigilanceConfig.hotBarProfileOne));
                AutoHotBar.swapHotBar = true;
            }
        }
        if (keyBindings[3].isPressed()) {
            if (AIOMVigilanceConfig.hotBarProfileTwo.equals("") || !AutoHotBar.hotBars.containsKey(AIOMVigilanceConfig.hotBarProfileTwo)) {
                Main.sendMarkedChatMessage("Error loading HotBar profile! Please set your name in /aiom");
            } else {
                AutoHotBar.hotBarSolution = AutoHotBar.findHotBarSolution(AutoHotBar.hotBars.get(AIOMVigilanceConfig.hotBarProfileTwo));
                AutoHotBar.swapHotBar = true;
            }
        }
        if (keyBindings[4].isPressed()) {
            if (AIOMVigilanceConfig.hotBarProfileThree.equals("") || !AutoHotBar.hotBars.containsKey(AIOMVigilanceConfig.hotBarProfileThree)) {
                Main.sendMarkedChatMessage("Error loading HotBar profile! Please set your name in /aiom");
            } else {
                AutoHotBar.hotBarSolution = AutoHotBar.findHotBarSolution(AutoHotBar.hotBars.get(AIOMVigilanceConfig.hotBarProfileThree));
                AutoHotBar.swapHotBar = true;
            }
        }
        if (keyBindings[5].isPressed()) {
            if (!AIOMVigilanceConfig.vClipKeyBindAmount.equals("")) {
                Main.mcPlayer.setPosition(Main.mcPlayer.posX, Main.mcPlayer.posY + Double.parseDouble(AIOMVigilanceConfig.vClipKeyBindAmount), Main.mcPlayer.posZ);
            }
        }
        if (keyBindings[6].isPressed()) {
            if (!AIOMVigilanceConfig.vClipKeyBindAmount.equals("")) {
                if (SetPlayerLook.fixStaticYaw(Main.mcPlayer.rotationYaw) <= -135 && SetPlayerLook.fixStaticYaw(Main.mcPlayer.rotationYaw) >= -45) {
                    Main.mcPlayer.setPosition(Main.mcPlayer.posX + Double.parseDouble(AIOMVigilanceConfig.hClipKeyBindAmount), Main.mcPlayer.posY, Main.mcPlayer.posZ);
                } else if (SetPlayerLook.fixStaticYaw(Main.mcPlayer.rotationYaw) <= -45 && SetPlayerLook.fixStaticYaw(Main.mcPlayer.rotationYaw) >= 45) {
                    Main.mcPlayer.setPosition(Main.mcPlayer.posX, Main.mcPlayer.posY, Main.mcPlayer.posZ + Double.parseDouble(AIOMVigilanceConfig.hClipKeyBindAmount));
                } else if (SetPlayerLook.fixStaticYaw(Main.mcPlayer.rotationYaw) <= 45 && SetPlayerLook.fixStaticYaw(Main.mcPlayer.rotationYaw) >= 135) {
                    Main.mcPlayer.setPosition(Main.mcPlayer.posX - Double.parseDouble(AIOMVigilanceConfig.hClipKeyBindAmount), Main.mcPlayer.posY, Main.mcPlayer.posZ);
                } else if (SetPlayerLook.fixStaticYaw(Main.mcPlayer.rotationYaw) <= 135 && SetPlayerLook.fixStaticYaw(Main.mcPlayer.rotationYaw) >= -135) {
                    Main.mcPlayer.setPosition(Main.mcPlayer.posX, Main.mcPlayer.posY, Main.mcPlayer.posZ - Double.parseDouble(AIOMVigilanceConfig.hClipKeyBindAmount));
                }
            }
        }
    }
}
