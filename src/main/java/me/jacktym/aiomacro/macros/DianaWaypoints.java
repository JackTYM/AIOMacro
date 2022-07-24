package me.jacktym.aiomacro.macros;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.ParticleHandler;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityCritFX;
import net.minecraft.client.particle.EntityParticleEmitter;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DianaWaypoints {

    Vec3 guessPoint = null;

    ArrayList<Map.Entry<Vec3, String>> points = new ArrayList<>();
    ArrayList<Vec3> locations = new ArrayList<>();
    ArrayList<Vec3> pastLocations = new ArrayList<>();

    ArrayList<Vec3> otherInquisitorPoints = new ArrayList<>();

    ArrayList<Vec3> clickedBurials = new ArrayList<>();
    boolean worldAccessAdded = false;

    @SubscribeEvent
    public void renderTicks(RenderWorldEvent event) {
        if (AIOMVigilanceConfig.waypointsOn) {
            if (guessPoint != null && AIOMVigilanceConfig.guessWaypointsOn) {
                drawWaypoint(guessPoint, "Guess");
            }

            for (Map.Entry<Vec3, String> point : points) {
                if (!clickedBurials.contains(point.getKey())) {
                    drawWaypoint(point.getKey(), point.getValue());
                }
            }

            for (Vec3 otherInquisitorPoint : otherInquisitorPoints) {
                drawWaypoint(otherInquisitorPoint, "Other Inquisitor");
            }
        }
    }

    //@SubscribeEvent
    public void entityJoinWorldEvent(EntityJoinWorldEvent e) {
    }

    //List<Entity> alreadyPrinted = new ArrayList<>();

    @SubscribeEvent
    public void tickEvent(TickEvent.PlayerTickEvent event) {

        if (Main.notNull) {
            if (!worldAccessAdded) {
                worldAccessAdded = true;
                System.out.println("Added World Access");
                Main.mcWorld.addWorldAccess(new ParticleHandler());
            }
        } else {
            if (worldAccessAdded) {
                System.out.println("Removed World Access");
                worldAccessAdded = false;
            }
        }

        List<Entity> entityList = Main.mcWorld.loadedEntityList;

        for (Entity entity : entityList) {

            /*if (!alreadyPrinted.contains(entity)) {
                System.out.println(entity.getDisplayName() + " " + entity.getName() + " " + entity.getPositionVector());
            }
            alreadyPrinted.add(entity);*/

            if (entity instanceof EntityCritFX || entity instanceof EntityCrit2FX) {
                System.out.println("particle");
                try {
                    final Field particleType = ReflectionHelper.findField(((EntityParticleEmitter) entity).getClass(), "field_174849_az", "field_178932_g", "particleTypes");

                    System.out.println(((EnumParticleTypes) particleType.get((entity))).getParticleName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void drawWaypoint(Vec3 waypoint, String pointName) {

    }
}
