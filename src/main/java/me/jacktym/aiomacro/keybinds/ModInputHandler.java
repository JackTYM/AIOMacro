package me.jacktym.aiomacro.keybinds;

import me.jacktym.aiomacro.macros.MacroHandler;
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
    }

}
