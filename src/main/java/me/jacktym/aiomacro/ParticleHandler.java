package me.jacktym.aiomacro;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IWorldAccess;

public class ParticleHandler implements IWorldAccess {

    @Override
    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... p_180442_15_) {
        //Drip Lava = Treasure
        //
        //

        //System.out.println(particleID);

        if (particleID == 10) {
            System.out.println("Possible Start Burrow! " + particleID);
        }
        if (particleID == 9) {
            System.out.println("Possible Mob Burrow! " + particleID);
        }
        if (particleID == 19) {
            System.out.println("Possible Treasure Burrow! " + particleID);
        }
    }

    public void onEntityAdded(Entity entityIn) {
    }

    public void onEntityRemoved(Entity entityIn) {
    }

    public void playSound(String soundName, double x, double y, double z, float volume, float pitch) {
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
