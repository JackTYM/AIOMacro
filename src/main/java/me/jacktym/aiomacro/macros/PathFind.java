package me.jacktym.aiomacro.macros;

import me.jacktym.aiomacro.Main;
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
    public static boolean clear = false;
    public static Vec3 destinationGlobal;
    public Vec3 pos;
    public Vec3 recentPoint = null;
    public List<Vec3> badPoints = new ArrayList<>();
    public int attempts = 0;
    public LinkedList<Vec3> pathPoints = new LinkedList<>();
    public static boolean globalCollision = true;
    public static List<Integer> optimizeBadIndex = new ArrayList<>();
    public static boolean optimizedFully = false;
    public static long findPathStart;
    public LinkedList<Vec3> finalPath = new LinkedList<>();
    public static boolean followPath = false;
    public ArrayList<Block> passThroughBlockList = new ArrayList<>();
    public boolean sendDoneMessage = false;
    public boolean sendFailMessage = false;

    public static void pathFind(Vec3 destination, Boolean collision) {
        destinationGlobal = destination;

        globalCollision = collision;

        //pathPoints.add(pos);
        //finalPath.add(pos);

        findPathStart = Utils.currentTimeMillis();

        followPath = false;
    }

    public static void clear() {
        destinationGlobal = null;
        BlockRendering.renderMap.clear();
        optimizedFully = false;
        optimizeBadIndex.clear();
        followPath = false;
        clear = true;
    }

    @SubscribeEvent
    public void findPath(TickEvent.ClientTickEvent event) {
        if (Main.mcPlayer != null && Main.mcWorld != null) {
            if (pos == null) {
                BlockPos blockPos = new BlockPos(Main.mcPlayer.posX, Main.mcPlayer.posY, Main.mcPlayer.posZ);
                pos = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            }
            if (pathPoints.isEmpty()) {
                pathPoints.add(pos);
            }
            if (clear) {
                pathPoints.clear();
                finalPath.clear();
                badPoints.clear();
                sendDoneMessage = false;
                sendFailMessage = false;
                pos = null;
                clear = false;
                return;
            }
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
            if (destinationGlobal != null && !followPath) {
                if (sendDoneMessage) {
                    Main.sendMarkedChatMessage("Path found in " + (Utils.currentTimeMillis() - findPathStart) + " Milliseconds. " + finalPath.size() + " Blocks!");
                    followPath = true;
                    return;
                }
                if (sendFailMessage) {
                    Main.sendMarkedChatMessage("Pathfinding Stopped! No Path Found.");
                    clear();
                    return;
                }
                if (pathPoints.isEmpty()) {
                    pathPoints.add(pos);
                }
                if (finalPath.isEmpty()) {
                    finalPath.add(pos);
                }
                new Thread(() -> {
                    for (int x = 1; x <= 10; x++) {
                        try {
                            System.out.println(getNextVec());
                            if (vec3Equals(getNextVec(), destinationGlobal)) {
                                if (!vec3Contains(finalPath, destinationGlobal)) {
                                    finalPath.add(destinationGlobal);
                                    finalPath.add(destinationGlobal);
                                }
                                if (optimizedFully) {
                                    sendDoneMessage = true;
                                    return;
                                } else {
                                    optimizePath(finalPath);
                                }
                            } else {
                                for (int i = 1; i <= 6; i++) {
                                    if (pathPoints.size() == 0) {
                                        pathPoints.add(pos);
                                    }
                                    Vec3 lastPoint = getNextVec();
                                    Vec3 point = getNextVec();

                                    //System.out.println(point);
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
                                                && !vec3Contains(pathPoints, point)
                                                && !vec3Contains(finalPath, point)) {
                                            pathPoints.add(point);
                                        }
                                    } else if (blockIsPassable(Main.mcWorld.getBlockState(new BlockPos(point)).getBlock())
                                            && blockIsPassable(Main.mcWorld.getBlockState(new BlockPos(point.addVector(0, 1, 0))).getBlock())
                                            && !vec3Contains(badPoints, point)
                                            && !vec3Contains(pathPoints, point)
                                            && !vec3Contains(finalPath, point)) {
                                        pathPoints.add(point);
                                    }
                                }
                                if (pathPoints.isEmpty()) {
                                    pathPoints.add(pos);
                                }
                                if (pathPoints.size() >= 2
                                        && getNextVec() != null) {
                                    finalPath.add(getNextVec());
                                    pathPoints.remove(0);

                                    pathPoints = sortPathPoints(pathPoints);

                                    if (!pathPoints.isEmpty()) {
                                        if (vec3Equals(getNextVec(), recentPoint)) {
                                            attempts++;
                                        } else {
                                            attempts = 0;
                                        }
                                        if (attempts >= 3) {
                                            if (!vec3Contains(badPoints, getNextVec())) {
                                                badPoints.add(getNextVec());
                                                attempts = 0;
                                                return;
                                            }
                                        }

                                        recentPoint = getNextVec();
                                    } else {
                                        sendFailMessage = true;
                                    }

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else if (followPath) {

            }
        }
    }

    private Vec3 getNextVec() {
        try {
            if (pathPoints.get(0) != null) {
                return pathPoints.get(0);
            }
        } catch (Exception ignored) {
        }
        return pos;
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
        finalPath = new LinkedList<>(optimizedPath);

        optimizedFully = true;
    }

    private boolean vec3Touching(Vec3 vec1, Vec3 vec2) {
        try {
            Vec3 distanceVec = vec1.subtract(vec2);
            double blockRange = Math.abs(distanceVec.xCoord) + Math.abs(distanceVec.yCoord) + Math.abs(distanceVec.zCoord);

            return blockRange == 1.0;
        } catch (Exception ignored) {
        }
        return false;
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
        try {
            for (Vec3 vec3 : vecArray) {
                if (vec3.subtract(vecSearch).toString().equals("(0.0, 0.0, 0.0)")) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }
}