package me.jacktym.aiomacro;

import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IWorldAccess;

import java.util.ArrayList;
import java.util.HashMap;

public class ParticleHandler implements IWorldAccess {

    public static HashMap<BlockPos, Integer> startParticles = new HashMap<>();
    public static HashMap<BlockPos, Integer> mobParticles = new HashMap<>();
    public static HashMap<BlockPos, Integer> treasureParticles = new HashMap<>();
    public static HashMap<BlockPos, Integer> burrowParticles = new HashMap<>();
    public static float firstPitch = 0;

    public void onEntityAdded(Entity entityIn) {
    }

    public void onEntityRemoved(Entity entityIn) {
    }

    public static long lastDing = 0;
    public static float lastDingPitch = 0;
    public static int dingIndex = 0;
    public static ArrayList<Float> dingSlope = new ArrayList<>();
    public static BlockPos lastParticlePoint;
    public static BlockPos lastParticlePoint2;
    public static BlockPos lastSoundPoint;
    public static BlockPos firstParticlePoint;
    public static BlockPos particlePoint;
    public static double distance;
    public static BlockPos guessPoint;

    @Override
    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... p_180442_15_) {
        if (AIOMVigilanceConfig.waypointsOn) {
            //System.out.println(particleID + " " + ignoreRange + " " + xCoord + " " + yCoord + " " + zCoord + " " + xOffset + " " + yOffset + " " + zOffset + " " + Arrays.toString(p_180442_15_));

            BlockPos bp = new BlockPos(xCoord, yCoord - 1, zCoord);

            if (particleID == 10) {
                //System.out.println("Possible Start Burrow! " + bp);

                if (!startParticles.containsKey(bp)) {
                    startParticles.put(bp, 1);
                } else {
                    startParticles.put(bp, startParticles.get(bp) + 1);
                }
            }
            if (particleID == 9) {
                //System.out.println("Possible Mob Burrow! " + bp);

                if (!mobParticles.containsKey(bp)) {
                    mobParticles.put(bp, 1);
                } else {
                    mobParticles.put(bp, mobParticles.get(bp) + 1);
                }
            }
            if (particleID == 19) {
                //System.out.println("Possible Treasure Burrow! " + bp);

                if (!treasureParticles.containsKey(bp)) {
                    treasureParticles.put(bp, 1);
                } else {
                    treasureParticles.put(bp, treasureParticles.get(bp) + 1);
                }
            }
            if (particleID == 28) {
                //System.out.println("Possible Burrow! " + bp);

                if (!burrowParticles.containsKey(bp)) {
                    burrowParticles.put(bp, 1);
                } else {
                    burrowParticles.put(bp, burrowParticles.get(bp) + 1);
                }
            }

            if (AIOMVigilanceConfig.guessWaypointsOn) {
                if (particleID == 3) {
                    System.out.println("Possible Path! " + bp);

                    boolean run = false;
                    if (lastSoundPoint != null && !run && Math.abs(xCoord - lastSoundPoint.getX()) < 2 && Math.abs(yCoord - lastSoundPoint.getY()) < 0.5 && Math.abs(zCoord - lastSoundPoint.getZ()) < 2) {
                        run = true;
                    }
                    if (run) {
                        if (lastParticlePoint == null) {
                            firstParticlePoint = new BlockPos(xCoord, yCoord, zCoord);
                        }
                        lastParticlePoint2 = lastParticlePoint;
                        lastParticlePoint = particlePoint;
                        particlePoint = new BlockPos(xCoord, yCoord, zCoord);

                        if (lastParticlePoint2 != null && particlePoint != null && firstParticlePoint != null && distance != 0 && lastSoundPoint != null) {

                        }
                    }
                }
            }
        }
    }

    public void playSound(String soundName, double x, double y, double z, float volume, float pitch) {
        if (AIOMVigilanceConfig.guessWaypointsOn) {
            if (soundName.equals("note.harp")) {
                if (lastDing == 0) {
                    firstPitch = pitch;
                }
                lastDing = Utils.currentTimeMillis();
                if (pitch < lastDingPitch) {
                    firstPitch = pitch;
                    dingIndex = 0;
                    dingSlope = new ArrayList<>();
                    lastDingPitch = pitch;
                    lastParticlePoint = null;
                    lastParticlePoint2 = null;
                    lastSoundPoint = null;
                    firstParticlePoint = null;
                }
                if (lastDingPitch == 0) {
                    lastDingPitch = pitch;
                    lastParticlePoint = null;
                    lastParticlePoint2 = null;
                    lastSoundPoint = null;
                    firstParticlePoint = null;
                    return;
                }
                dingIndex++;
                if (dingIndex > 1) {
                    dingSlope.add(pitch - lastDingPitch);
                }
                if (dingSlope.size() > 15) {
                    dingSlope.remove(0);
                }
                float slope = dingSlope.stream().reduce(0f, Float::sum) / dingSlope.size();

                lastSoundPoint = new BlockPos(x, y, z);
                lastDingPitch = pitch;

                if (lastParticlePoint != null && particlePoint != null && firstParticlePoint != null) {
                    distance = Math.E / slope - Math.sqrt(((firstParticlePoint.getX() - x) * (firstParticlePoint.getX() - x)) + ((firstParticlePoint.getY() - y) * (firstParticlePoint.getY() - y)) + ((firstParticlePoint.getZ() - z) * (firstParticlePoint.getZ() - z)));

//		            lineDist = Math.hypot((this.lastParticlePoint2[0] - this.particlePoint[0]), this.lastParticlePoint2[1] - this.particlePoint[1], this.lastParticlePoint2[2] - this.particlePoint[2])

                    double lineDist = Math.sqrt(((lastParticlePoint2.getX() - particlePoint.getX()) * (lastParticlePoint2.getX() - particlePoint.getX())) + ((lastParticlePoint2.getY() - particlePoint.getY()) * (lastParticlePoint2.getY() - particlePoint.getY())) + ((lastParticlePoint2.getZ() - particlePoint.getZ()) * (lastParticlePoint2.getZ() - particlePoint.getZ())));
                    //[this.particlePoint[0] - this.lastParticlePoint2[0], this.particlePoint[1] - this.lastParticlePoint2[1], this.particlePoint[2] - this.lastParticlePoint2[2]]
                    ArrayList<Double> changes = new ArrayList<>();
                    changes.add((particlePoint.getX() - lastParticlePoint2.getX()) / lineDist);
                    changes.add((particlePoint.getY() - lastParticlePoint2.getY()) / lineDist);
                    changes.add((particlePoint.getZ() - lastParticlePoint2.getZ()) / lineDist);

                    guessPoint = new BlockPos(lastSoundPoint.getX() + changes.get(0) * distance, lastSoundPoint.getY() + changes.get(1) * distance, lastSoundPoint.getZ() + changes.get(2) * distance);
                }
            }
        }
    }

    public void playSoundToNearExcept(EntityPlayer except, String soundName, double x, double y, double z, float volume, float f) {
    }

    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
    }

    public void markBlockForUpdate(BlockPos pos) {
    }

    public void notifyLightSet(BlockPos pos) {
    }

    public void playRecord(String recordName, BlockPos blockPosIn) {
    }

    public void playAuxSFX(EntityPlayer player, int sfxType, BlockPos blockPosIn, int data) {
    }

    public void broadcastSound(int soundID, BlockPos pos, int data) {
    }

    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
    }
}
