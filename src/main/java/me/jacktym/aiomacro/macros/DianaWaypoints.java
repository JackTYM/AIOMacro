package me.jacktym.aiomacro.macros;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.ParticleHandler;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.util.Utils;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DianaWaypoints {

    public int tickCount = 0;
    BlockPos guessPoint = null;
    ArrayList<BlockPos> unknownBurrows = new ArrayList<>();
    HashMap<BlockPos, String> points = new HashMap<>();
    ArrayList<BlockPos> otherInquisitorPoints = new ArrayList<>();
    boolean worldAccessAdded = false;
    ArrayList<BlockPos> clickedBurials = new ArrayList<>();

    @SubscribeEvent
    public void renderTicks(RenderWorldLastEvent event) {
        if (AIOMVigilanceConfig.waypointsOn) {
            if (guessPoint != null && AIOMVigilanceConfig.guessWaypointsOn) {
                drawWaypoint(guessPoint, "Guess");
            }

            for (Map.Entry<BlockPos, String> point : points.entrySet()) {
                if (!clickedBurials.contains(point.getKey())) {
                    drawWaypoint(point.getKey(), point.getValue());
                }
            }

            for (BlockPos otherInquisitorPoint : otherInquisitorPoints) {
                drawWaypoint(otherInquisitorPoint, "Other Inquisitor");
            }
        }
    }

    @SubscribeEvent
    public void handleWorldAccess(TickEvent.ClientTickEvent event) {
        if (tickCount >= 100) {
            tickCount = 0;
            ParticleHandler.startParticles.clear();
            ParticleHandler.treasureParticles.clear();
            ParticleHandler.mobParticles.clear();
            ParticleHandler.burrowParticles.clear();
        }
        tickCount++;

        if (Main.notNull) {
            boolean hasAccess = false;

            try {
                final Field worldAccesses = ReflectionHelper.findField(World.class, "field_73021_x", "worldAccesses");

                for (IWorldAccess iWorldAccess : ((List<IWorldAccess>) worldAccesses.get((Main.mcWorld)))) {
                    if (iWorldAccess.getClass().equals(ParticleHandler.class)) {
                        hasAccess = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            worldAccessAdded = hasAccess;

            if (!worldAccessAdded) {
                worldAccessAdded = true;
                System.out.println("Added World Access");
                Main.mcWorld.addWorldAccess(new ParticleHandler());
            }

            for (Map.Entry<BlockPos, Integer> bpSet : ParticleHandler.burrowParticles.entrySet()) {
                if (bpSet.getValue() >= 16) {
                    unknownBurrows.add(bpSet.getKey());
                }
            }

            for (Map.Entry<BlockPos, Integer> bpSet : ParticleHandler.mobParticles.entrySet()) {
                if (bpSet.getValue() >= 3) {
                    if (unknownBurrows.contains(bpSet.getKey()) && !points.containsKey(bpSet.getKey())) {
                        points.put(bpSet.getKey(), "Mob");
                    }
                }
            }

            for (Map.Entry<BlockPos, Integer> bpSet : ParticleHandler.treasureParticles.entrySet()) {
                if (bpSet.getValue() >= 3) {
                    if (unknownBurrows.contains(bpSet.getKey()) && !points.containsKey(bpSet.getKey())) {
                        points.put(bpSet.getKey(), "Treasure");
                    }
                }
            }

            for (Map.Entry<BlockPos, Integer> bpSet : ParticleHandler.startParticles.entrySet()) {
                if (bpSet.getValue() >= 3) {
                    if (unknownBurrows.contains(bpSet.getKey()) && !points.containsKey(bpSet.getKey())) {
                        points.put(bpSet.getKey(), "Start");
                    }
                }
            }
        } else {
            if (worldAccessAdded) {
                System.out.println("Removed World Access");
                worldAccessAdded = false;
            }
        }
    }

    public void drawWaypoint(BlockPos waypoint, String pointName) {
        if (pointName.equals("Start")) {
            Utils.renderBeacon(waypoint, Color.GREEN, waypoint.toString());
        } else if (pointName.equals("Treasure")) {
            Utils.renderBeacon(waypoint, Color.YELLOW, waypoint.toString());
        } else if (pointName.equals("Mob")) {
            Utils.renderBeacon(waypoint, Color.RED, waypoint.toString());
        }
    }
}
