package me.jacktym.aiomacro.commands;

import me.jacktym.aiomacro.macros.PathFind;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.Vec3;

public class PathfindCommand extends CommandBase {

    public String getCommandName() {
        return "pathfind";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/pathfind";
    }

    public int getRequiredPermissionLevel() {
        return 0;
    }

    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length >= 3) {
            PathFind.clear();
            if (args.length == 3) {
                PathFind.pathFind(new Vec3(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2])), true);
            } else {
                PathFind.pathFind(new Vec3(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2])), Boolean.valueOf(args[3]));
            }
        }
        if (args[0].equals("clear")) {
            PathFind.clear();
        }
    }
}
