package me.jacktym.aiomacro.macros;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.rendering.BlockRendering;
import me.jacktym.aiomacro.util.Utils;
import net.minecraft.client.settings.KeyBinding;

public class MacroHandler {

    public static long macroStartMillis;

    public static boolean isMacroOn = false;
    public static boolean isNetherWart = false;
    public static boolean isSugarCane = false;

    public static void toggleMacro() {
        if (!isMacroOn && Main.notNull) {
            isNetherWart = false;
            isSugarCane = false;
            isMacroOn = true;
            macroStartMillis = Utils.currentTimeMillis();

            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindLeft.getKeyCode(), false);
            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindRight.getKeyCode(), false);
            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindForward.getKeyCode(), false);
            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindBack.getKeyCode(), false);
            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindAttack.getKeyCode(), false);

            System.out.println(AIOMVigilanceConfig.macroType);

            if (AIOMVigilanceConfig.macroType == 0) {
                NetherWart.left = true;
                NetherWart.forward = false;
                isNetherWart = true;
                isSugarCane = false;
                SetPlayerLook.setDefault();
                SetPlayerLook.toggled = true;
                if (AIOMVigilanceConfig.ungrab) {
                    Utils.unGrab();
                }
            } else if (AIOMVigilanceConfig.macroType == 1) {
                SugarCane.left = true;
                isNetherWart = false;
                isSugarCane = true;
                SetPlayerLook.setDefault();
                SetPlayerLook.toggled = true;
                if (AIOMVigilanceConfig.ungrab) {
                    Utils.unGrab();
                }
            } else if (AIOMVigilanceConfig.macroType == 3) {
                Main.mcPlayer.sendChatMessage("/bz");
            } else if (AIOMVigilanceConfig.macroType == 6) {
                MinionAura.recentClickedEntity = null;
                MinionAura.claimedMinions.clear();
                MinionAura.inMinion = false;
            }
            Main.sendMarkedChatMessage("Macro Enabled!");
        } else if (isMacroOn) {
            Main.sendMarkedChatMessage("Macro Disabled!");
            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindRight.getKeyCode(), false);
            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindBack.getKeyCode(), false);
            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindLeft.getKeyCode(), false);
            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindForward.getKeyCode(), false);
            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindAttack.getKeyCode(), false);

            CarpentryMacro.claimDiamonds = true;
            CarpentryMacro.craftDiamonds = false;
            CarpentryMacro.putInBackPack = false;

            BlockRendering.renderMap.clear();

            isNetherWart = false;
            isSugarCane = false;

            SetPlayerLook.toggled = false;

            isMacroOn = false;

            if (AIOMVigilanceConfig.ungrab) {
                Utils.reGrab();
            }
        }
    }
}