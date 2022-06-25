package me.jacktym.aiomacro.macros;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.commands.PathfindCommand;
import me.jacktym.aiomacro.rendering.BlockRendering;
import me.jacktym.aiomacro.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.List;
import java.util.*;

public class PathFind {
    public static final HashMap<Vec3, Double> pathPoints1 = new LinkedHashMap<>();
    public static final HashMap<Vec3, Color> bpMap = new HashMap<>();
    public static Vec3 pos;
    public static Vec3 destinationGlobal;
    public static Vec3 recentPoint = null;
    public static List<Vec3> badPoints = new ArrayList<>();
    public static int attempts = 0;
    public static LinkedList<Vec3> pathPoints = new LinkedList<>();
    public static LinkedList<Vec3> finalPath = new LinkedList<>();
    public static boolean globalCollision = true;
    public static List<Integer> optimizeBadIndex = new ArrayList<>();
    public static boolean optimizedFully = false;
    public static long findPathStart;
    public static ArrayList<Block> passThroughBlockList = new ArrayList<>();
    public static boolean followPath = false;

    public static void pathFind(Vec3 destination, Boolean collision) {
        if (passThroughBlockList.isEmpty()) {
            passThroughBlockList.add(Blocks.acacia_fence);
            passThroughBlockList.add(Blocks.activator_rail);
            passThroughBlockList.add(Blocks.air);
            passThroughBlockList.add(Blocks.birch_fence_gate);
            passThroughBlockList.add(Blocks.brown_mushroom);
            passThroughBlockList.add(Blocks.carpet);
            passThroughBlockList.add(Blocks.carrots);
            passThroughBlockList.add(Blocks.dark_oak_fence_gate);
            passThroughBlockList.add(Blocks.deadbush);
            passThroughBlockList.add(Blocks.detector_rail);
            passThroughBlockList.add(Blocks.fire);
            passThroughBlockList.add(Blocks.golden_rail);
            passThroughBlockList.add(Blocks.heavy_weighted_pressure_plate);
            passThroughBlockList.add(Blocks.iron_trapdoor);
            passThroughBlockList.add(Blocks.jungle_fence_gate);
            passThroughBlockList.add(Blocks.lever);
            passThroughBlockList.add(Blocks.melon_stem);
            passThroughBlockList.add(Blocks.nether_wart);
            passThroughBlockList.add(Blocks.potatoes);
            passThroughBlockList.add(Blocks.powered_comparator);
            passThroughBlockList.add(Blocks.pumpkin_stem);
            passThroughBlockList.add(Blocks.rail);
            passThroughBlockList.add(Blocks.red_flower);
            passThroughBlockList.add(Blocks.red_mushroom);
            passThroughBlockList.add(Blocks.redstone_torch);
            passThroughBlockList.add(Blocks.redstone_wire);
            passThroughBlockList.add(Blocks.reeds);
            passThroughBlockList.add(Blocks.sapling);
            passThroughBlockList.add(Blocks.snow_layer);
            passThroughBlockList.add(Blocks.spruce_fence_gate);
            passThroughBlockList.add(Blocks.standing_sign);
            passThroughBlockList.add(Blocks.stone_pressure_plate);
            passThroughBlockList.add(Blocks.tallgrass);
            passThroughBlockList.add(Blocks.torch);
            passThroughBlockList.add(Blocks.trapdoor);
            passThroughBlockList.add(Blocks.tripwire);
            passThroughBlockList.add(Blocks.tripwire_hook);
            passThroughBlockList.add(Blocks.unlit_redstone_torch);
            passThroughBlockList.add(Blocks.unpowered_comparator);
            passThroughBlockList.add(Blocks.vine);
            passThroughBlockList.add(Blocks.wall_sign);
            passThroughBlockList.add(Blocks.waterlily);
            passThroughBlockList.add(Blocks.web);
            passThroughBlockList.add(Blocks.wheat);
            passThroughBlockList.add(Blocks.wooden_button);
            passThroughBlockList.add(Blocks.wooden_pressure_plate);
            passThroughBlockList.add(Blocks.yellow_flower);
        }

        BlockPos blockPos = new BlockPos(Main.mcPlayer.posX, Main.mcPlayer.posY, Main.mcPlayer.posZ);
        pos = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        System.out.println(pos);

        destinationGlobal = destination;

        bpMap.put(pos, Color.WHITE);
        bpMap.put(destination, Color.BLACK);

        globalCollision = collision;

        pathPoints.add(pos);
        finalPath.add(pos);

        findPathStart = Utils.currentTimeMillis();

        followPath = false;
    }

    @SubscribeEvent
    public void findPath(TickEvent.ClientTickEvent event) {
        new Thread(() -> {
            if (pathPoints.isEmpty()) {
                pathPoints.add(pos);
            }
            LinkedList<Vec3> pathPointsLocal = pathPoints;
            if (destinationGlobal != null && pos != null && !followPath) {
                for (int x = 1; x <= 1000; x++) {
                    try {
                        if (pathPointsLocal.isEmpty()) {
                            pathPointsLocal.add(pos);
                        }
                        if (vec3Equals(pathPointsLocal.get(0), destinationGlobal)) {
                            if (!vec3Contains(finalPath, destinationGlobal)) {
                                finalPath.add(destinationGlobal);
                            }
                            if (optimizedFully) {
                                Main.sendMarkedChatMessage("Path found in " + (Utils.currentTimeMillis() - findPathStart) + " Milliseconds. " + finalPath.size() + " Blocks!");
                                followPath = true;
                                return;
                            } else {
                                optimizePath(finalPath);
                            }
                        } else {
                            for (int i = 1; i <= 6; i++) {
                                Vec3 lastPoint = pathPointsLocal.get(0);
                                Vec3 point = pathPointsLocal.get(0);

                                if (point == null) {
                                    lastPoint = pos;
                                    point = pos;
                                }

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
                                }
                                if (blockIsPassable(Main.mcWorld.getBlockState(new BlockPos(lastPoint.addVector(0, -1, 0))).getBlock())) {
                                    if (blockIsPassable(Main.mcWorld.getBlockState(new BlockPos(point)).getBlock())
                                            && blockIsPassable(Main.mcWorld.getBlockState(new BlockPos(point.addVector(0, 1, 0))).getBlock())
                                            && (!blockIsPassable(Main.mcWorld.getBlockState(new BlockPos(point.addVector(0, -1, 0))).getBlock()) || i == 4)
                                            && !vec3Contains(badPoints, point)
                                            && !vec3Contains(pathPointsLocal, point)
                                            && !vec3Contains(finalPath, point)) {
                                        pathPointsLocal.add(point);
                                    }
                                } else if (blockIsPassable(Main.mcWorld.getBlockState(new BlockPos(point)).getBlock())
                                        && blockIsPassable(Main.mcWorld.getBlockState(new BlockPos(point.addVector(0, 1, 0))).getBlock())
                                        && !vec3Contains(badPoints, point)
                                        && !vec3Contains(pathPointsLocal, point)
                                        && !vec3Contains(finalPath, point)) {
                                    pathPointsLocal.add(point);
                                }

                                finalPath.add(pathPointsLocal.get(0));
                                pathPointsLocal.remove(0);

                                pathPointsLocal = sortPathPoints(pathPointsLocal);

                                if (!pathPointsLocal.isEmpty()) {
                                    if (vec3Equals(pathPointsLocal.get(0), recentPoint)) {
                                        attempts++;
                                    } else {
                                        attempts = 0;
                                    }
                                    if (attempts >= 3) {
                                        if (!vec3Contains(badPoints, pathPointsLocal.get(0))) {
                                            badPoints.add(pathPointsLocal.get(0));
                                            pathPointsLocal.remove(pathPointsLocal.get(0));
                                            pathPointsLocal = sortPathPoints(pathPointsLocal);
                                            attempts = 0;
                                            return;
                                        }
                                    }

                                    recentPoint = pathPointsLocal.get(0);
                                } else {
                                    Main.sendMarkedChatMessage("Pathfinding Stopped! No Path Found.");
                                    PathfindCommand.clear();
                                }

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (followPath) {

            }
        }).start();
    }

    private boolean blockIsPassable(Block block) {
        for (Block b : passThroughBlockList) {
            if (b.toString().equals(block.toString())) {
                return true;
            }
        }
        return false;
    }

    private void optimizePath(List<Vec3> path) {
        List<Vec3> optimizedPath = new ArrayList<>();
        for (int i = 0; i <= path.size() - 1; i++) {
            Vec3 pathPoint = path.get(i);

            if (i == path.size() - 1) {
                optimizedPath.add(pathPoint);
                break;
            }

            int touchingPoint = 0;
            for (int x = i + 1; x <= path.size() - 1; x++) {
                if (vec3Touching(pathPoint, path.get(x))
                        && !optimizeBadIndex.contains(x)) {
                    touchingPoint = x;
                }
            }
            if (touchingPoint != 0) {
                optimizedPath.add(pathPoint);
                optimizedPath.add(path.get(touchingPoint));
                i = touchingPoint - 1;
            } else {
                optimizeBadIndex.add(i);
                return;
            }
        }
        BlockRendering.renderMap.clear();
        optimizedPath.forEach(vec3 -> BlockRendering.renderMap.put(new BlockPos(vec3), Color.BLUE));
        BlockRendering.renderMap.put(new BlockPos(pos), Color.WHITE);
        BlockRendering.renderMap.put(new BlockPos(destinationGlobal), Color.BLACK);

        finalPath = new LinkedList<>(optimizedPath);

        optimizedFully = true;
    }

    private boolean vec3Touching(Vec3 vec1, Vec3 vec2) {
        Vec3 distanceVec = vec1.subtract(vec2);
        double blockRange = Math.abs(distanceVec.xCoord) + Math.abs(distanceVec.yCoord) + Math.abs(distanceVec.zCoord);

        return blockRange == 1.0;
    }

    private LinkedList<Vec3> sortPathPoints(List<Vec3> pp) {
        LinkedList<Vec3> newList = new LinkedList<>(pp);
        HashMap<Vec3, Double> hashmap = new LinkedHashMap<>();
        for (Vec3 vec3 : newList) {
            if (Utils.distanceBetweenPoints(destinationGlobal, vec3) != null) {
                hashmap.put(vec3, Utils.distanceBetweenPoints(destinationGlobal, vec3));
            }
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
        vecArray.remove(null);
        for (Vec3 vec3 : vecArray) {
            if (vec3.subtract(vecSearch).toString().equals("(0.0, 0.0, 0.0)")) {
                return true;
            }
        }
        return false;
    }
}