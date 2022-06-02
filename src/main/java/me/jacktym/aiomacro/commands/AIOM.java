package me.jacktym.aiomacro.commands;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class AIOM extends CommandBase {

    public String getCommandName() {
        return "aiom";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/aiom";
    }

    public int getRequiredPermissionLevel() {
        return 0;
    }

    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            Main.gui = AIOMVigilanceConfig.INSTANCE.gui();
        }
    }
}