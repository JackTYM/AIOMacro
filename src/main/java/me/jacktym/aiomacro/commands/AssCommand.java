package me.jacktym.aiomacro.commands;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.rendering.AssHook;
import me.jacktym.aiomacro.rendering.SizeMessage;
import me.jacktym.aiomacro.rendering.ToggleMessage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

import java.util.List;

public class AssCommand extends CommandBase {
    public String getCommandName() {
        return "ass";
    }

    public int getRequiredPermissionLevel() {
        return 0;
    }

    public String getCommandUsage(ICommandSender sender) {
        return "Usage: /ass <0-6|on|off>";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1) {
            String check = args[0].toLowerCase();
            EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
            AssHook hook = AssHook.get(player);

            if (check.equals("on") || check.equals("off")) {
                boolean value = check.equals("on");

                if (hook != null) {
                    hook.setEnabled(value);

                    if (!player.worldObj.isRemote) {
                        Main.NETWORK.sendToAll(new ToggleMessage(player.getUniqueID(), value));
                    } else {
                        Main.NETWORK.sendToServer(new ToggleMessage(player.getUniqueID(), value));
                    }
                    return;
                }
            } else {
                try {
                    int value = Integer.parseInt(args[0]);

                    if (value >= 0 && value <= 6) {
                        if (hook != null) {
                            hook.setSize(value);

                            if (!player.worldObj.isRemote) {
                                Main.NETWORK.sendToAll(new SizeMessage(player.getUniqueID(), value));
                            } else {
                                Main.NETWORK.sendToServer(new SizeMessage(player.getUniqueID(), value));
                            }
                            return;
                        }
                    }
                } catch (Exception e) {
                    throw new WrongUsageException("Usage: /ass <0-6|on|off>");
                }
            }
        }

        throw new WrongUsageException("Usage: /ass <0-6|on|off>");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "0", "1", "2", "3", "4", "5", "6", "on", "off") : null;
    }
}
