package me.jacktym.aiomacro.commands;

import me.jacktym.aiomacro.macros.Pathfind;
import me.jacktym.aiomacro.rendering.BlockRendering;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.Vec3;

public class PathfindCommand extends CommandBase {

    public static void clear() {
        Pathfind.bpMap.clear();
        Pathfind.pathPoints.clear();
        Pathfind.pathPoints1.clear();
        Pathfind.destinationGlobal = null;
        Pathfind.attempts = 0;
        Pathfind.badPoints.clear();
        BlockRendering.renderMap.clear();
        Pathfind.recentPoint = null;
        Pathfind.pos = null;
    }

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
            clear();
            Pathfind.pathfind(new Vec3(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2])));
        }
        if (args[0].equals("clear")) {
            clear();
        }
    }
}
