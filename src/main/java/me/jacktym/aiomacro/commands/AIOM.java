package me.jacktym.aiomacro.commands;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.macros.AutoHotBar;
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
        } else {
            if (args[0].equals("hotbar")) {
                if (args.length == 1) {
                    Main.sendMarkedChatMessage("Proper Usage: /aiom hotbar (save/load/delete) {name} | /aiom hotbar list");
                } else {
                    if (args[1].equals("save")) {
                        AutoHotBar.saveHotBar(args[2]);
                        Main.sendMarkedChatMessage(args[2] + " has been saved!");
                    } else if (args[1].equals("load")) {
                        AutoHotBar.hotBarSolution = AutoHotBar.findHotBarSolution(AutoHotBar.hotBars.get(args[2]));
                        AutoHotBar.swapHotBar = true;
                    } else if (args[1].equals("list")) {
                        Main.sendMarkedChatMessage("HotBar List: " + AutoHotBar.hotBars.keySet());
                    } else if (args[1].equals("delete")) {
                        AutoHotBar.hotBars.remove(args[2]);
                        Main.sendMarkedChatMessage(args[2] + " has been removed!");
                    } else {
                        Main.sendMarkedChatMessage("Proper Usage: /aiom hotbar (save/load/delete) {name} | /aiom hotbar list");
                    }
                }
            } else if (args[0].equals("vclip")) {
                if (args.length == 1) {
                    Main.sendMarkedChatMessage("/aiom vclip (num)");
                } else {
                    Main.mcPlayer.setPosition(Main.mcPlayer.posX, Main.mcPlayer.posY + Double.parseDouble(args[1]), Main.mcPlayer.posZ);
                }
            } else if (args[0].equals("customclip")) {
                if (args.length <= 3) {
                    Main.sendMarkedChatMessage("/aiom customclip (x) (y) (z))");
                } else {
                    Main.mcPlayer.setPosition(Main.mcPlayer.posX + Double.parseDouble(args[1]), Main.mcPlayer.posY + Double.parseDouble(args[2]), Main.mcPlayer.posZ + Double.parseDouble(args[3]));
                }
            }
        }
    }
}