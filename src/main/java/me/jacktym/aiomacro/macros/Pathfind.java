package me.jacktym.aiomacro.macros;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.commands.PathfindCommand;
import me.jacktym.aiomacro.rendering.BlockRendering;
import me.jacktym.aiomacro.util.Utils;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.List;
import java.util.*;

public class Pathfind {

    //Vec3, <Blocks From Start, Blocks From End>
    public static final HashMap<Vec3, Double> pathPoints1 = new LinkedHashMap<>();
    public static final HashMap<Vec3, Color> bpMap = new HashMap<>();
    private static final HashMap<Integer, Boolean> glCapMap = new HashMap<>();
    public static Vec3 pos;
    public static Vec3 destinationGlobal;
    public static Vec3 recentPoint = null;
    public static ArrayList<Vec3> badPoints = new ArrayList<>();
    public static int attempts = 0;
    public static LinkedList<Vec3> pathPoints = new LinkedList<>();

    public static void pathfind(Vec3 destination) {

        BlockPos blockPos = new BlockPos(Main.mcPlayer.posX, Main.mcPlayer.posY, Main.mcPlayer.posZ);
        pos = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        destinationGlobal = destination;

        bpMap.put(pos, Color.WHITE);
        bpMap.put(destination, Color.BLACK);

        pathPoints.add(pos);
    }

    @SubscribeEvent
    public void findPath(TickEvent.ClientTickEvent event) {
        for (int x = 1; x < 20; x++) {
            if (pathPoints.isEmpty()) {
                pathPoints.add(pos);
            }
            if (destinationGlobal != null && pos != null && !vec3Equals(destinationGlobal, pathPoints.get(0))) {
                for (int i = 1; i <= 10; i++) {
                    Vec3 point = pathPoints.get(0);
                    if (Main.mcWorld.getBlockState(new BlockPos(point.addVector(0, -1, 0))).getBlock() == Blocks.air) {
                        if (!vec3Contains(badPoints, point)) {
                            pathPoints.add(point.addVector(0, -1, 0));
                        }
                    } else {
                        switch (i) {
                            case 1:
                                point = point.addVector(0, 0, 1);
                                break;
                            case 2:
                                point = point.addVector(1, 0, 0);
                                break;
                            case 3:
                                point = point.addVector(0, 0, -1);
                                break;
                            case 4:
                                point = point.addVector(0, -1, 0);
                                break;
                            case 5:
                                point = point.addVector(-1, 0, 0);
                                break;
                            case 6:
                                point = point.addVector(0, 1, 0);
                                break;
                            case 7:
                                point = point.addVector(0, 1, 1);
                                break;
                            case 8:
                                point = point.addVector(1, 1, 0);
                                break;
                            case 9:
                                point = point.addVector(0, 1, -1);
                                break;
                            case 10:
                                point = point.addVector(-1, 1, 0);
                                break;
                        }
                        //(Main.mcWorld.getBlockState(new BlockPos(point.addVector(0, -1, 0))).getBlock() != Blocks.air
                        //                            || Main.mcWorld.getBlockState(new BlockPos(point.addVector(0, -2, 0))).getBlock() != Blocks.air)
                        if (Main.mcWorld.getBlockState(new BlockPos(point)).getBlock() == Blocks.air
                                && !vec3Contains(badPoints, point)
                                && !vec3Contains(pathPoints, point)) {
                            pathPoints.add(point);
                        }
                    }
                }

                pathPoints = sortPathPoints(pathPoints);

                if (!pathPoints.isEmpty()) {
                    if (vec3Equals(pathPoints.get(0), recentPoint)) {
                        attempts++;
                    } else {
                        attempts = 0;
                    }
                    if (attempts == 10) {
                        badPoints.add(pathPoints.get(0));
                        //pathPoints.remove(pathPoints.get(0));

                        //Remove duplicates
                        ArrayList<Vec3> tempList = new ArrayList<>();
                        for (Vec3 vec3 : badPoints) {
                            if (!vec3Contains(tempList, vec3)) {
                                tempList.add(vec3);
                            }
                        }
                        badPoints.clear();
                        badPoints.addAll(tempList);
                        tempList.clear();
                        attempts = 0;
                        return;
                    }

                    recentPoint = pathPoints.get(0);

                    if (!vec3Contains(new ArrayList<>(bpMap.keySet()), pathPoints.get(0))) {
                        bpMap.put(pathPoints.get(0), Color.GREEN);
                    }
                    //for (Vec3 vec3 : badPoints) {
                    //    if (vec3Contains(new ArrayList<>(bpMap.keySet()), vec3)) {
                    //        bpMap.remove(vec3);
                    //    }
                    //}
                } else {
                    Main.sendMarkedChatMessage("Pathfinding Stopped! No Path Found.");
                    PathfindCommand.clear();
                }
            }
        }
    }

    private LinkedList<Vec3> sortPathPoints(List<Vec3> pp) {
        LinkedList<Vec3> newList = new LinkedList<>(pp);
        HashMap<Vec3, Double> hashmap = new LinkedHashMap<>();
        for (Vec3 vec3 : newList) {
            hashmap.put(vec3, Utils.distanceBetweenPoints(destinationGlobal, vec3));
        }
        List<Map.Entry<Vec3, Double>> list = new LinkedList<>(hashmap.entrySet());
        list.sort(Map.Entry.comparingByValue());
        newList.clear();
        for (Map.Entry<Vec3, Double> entry : list) {
            if (!vec3Contains(newList, entry.getKey()) && !vec3Contains(badPoints, entry.getKey())) {
                newList.add(entry.getKey());
            }
        }
        return newList;
    }

    private boolean vec3Equals(Vec3 vec1, Vec3 vec2) {
        if (vec1 != null && vec2 != null) {
            return vec1.subtract(vec2).toString().equals("(0.0, 0.0, 0.0)");
        }
        return false;
    }

    private boolean vec3Contains(List<Vec3> vecArray, Vec3 vecSearch) {
        AxisAlignedBB aabb = new AxisAlignedBB(new BlockPos(Main.mcPlayer.getPositionVector().addVector(-1000, -1000, -1000)), new BlockPos(Main.mcPlayer.getPositionVector().addVector(1000, 1000, 1000)));
        Main.mcWorld.findNearestEntityWithinAABB(EntityWither.class, aabb, Main.mcPlayer);

        for (Vec3 vec3 : vecArray) {
            if (vec3.subtract(vecSearch).toString().equals("(0.0, 0.0, 0.0)")) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent e) {
        if (!bpMap.isEmpty()) {
            for (Map.Entry<Vec3, Color> entry : bpMap.entrySet()) {
                BlockPos blockPos = new BlockPos(entry.getKey());
                Color color = entry.getValue();
                BlockRendering.renderMap.put(blockPos, color);
            }
        }
    }
}
