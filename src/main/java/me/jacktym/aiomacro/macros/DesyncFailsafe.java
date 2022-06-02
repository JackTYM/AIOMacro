package me.jacktym.aiomacro.macros;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DesyncFailsafe {

    ArrayList<BlockPos> toCheck = new ArrayList<>();

    ArrayList<BlockPos> checked = new ArrayList<>();

    int notBroken = 0;

    int tick = 0;

    @SubscribeEvent
    public void checkTick(TickEvent.ClientTickEvent event) {
        if (AIOMVigilanceConfig.desyncFailsafe && Main.mcPlayer != null && Main.mcWorld != null && toCheck != null && toCheck.size() >= 1) {
            if (AIOMVigilanceConfig.macroType == 0) {
                BlockPos blockPos = toCheck.get(0);
                IBlockState blockState = Main.mcWorld.getBlockState(blockPos);

                if (blockState.getBlock() == Blocks.nether_wart && blockState.getValue(BlockNetherWart.AGE) == 3) {
                    notBroken++;
                }
                if (blockState.getBlock() == Blocks.potatoes && blockState.getValue(BlockPotato.AGE) == 7) {
                    notBroken++;
                }
                if (blockState.getBlock() == Blocks.carrots && blockState.getValue(BlockCarrot.AGE) == 7) {
                    notBroken++;
                }
                if (blockState.getBlock() == Blocks.wheat && blockState.getValue(BlockCrops.AGE) == 7) {
                    notBroken++;
                }

                toCheck.remove(blockPos);
                checked.add(blockPos);
            }
            Main.sendChatMessage(notBroken + " " + tick);
            if (notBroken >= AIOMVigilanceConfig.desyncMaxMisses && tick >= 100) {
                Failsafe.goToIsland = true;
                notBroken = 0;
                tick = 0;
            }

            tick++;
        }
    }

    @SubscribeEvent
    public void checkBlocks(@NotNull RenderWorldLastEvent event) {
        if (AIOMVigilanceConfig.desyncFailsafe && Main.mcPlayer != null && Main.mcWorld != null) {
            BlockPos playerPos = Main.mcPlayer.getPosition().add(0, 1, 0);
            Vec3i vec3i = new Vec3i(2, 2, 2);
            ArrayList<Vec3> blocks = new ArrayList<>();
            for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i))) {
                IBlockState blockState = Main.mcWorld.getBlockState(blockPos);
                if (blockState.getBlock() == Blocks.air) {
                    continue;
                }
                blocks.add(new Vec3((double) blockPos.getX() + 0.5, blockPos.getY(), (double) blockPos.getZ() + 0.5));
            }
            for (Vec3 block : blocks) {
                if (AIOMVigilanceConfig.macroType == 0) {
                    if (block.yCoord == playerPos.getY()) {
                        if (AIOMVigilanceConfig.yaw == 90) {
                            // -x

                            Block b = Main.mcWorld.getBlockState(new BlockPos(block)).getBlock();
                            if (b == Blocks.nether_wart || b == Blocks.carrots || b == Blocks.potatoes || b == Blocks.wheat) {
                                if (block.zCoord - .5 == playerPos.getZ() && block.xCoord - .5 >= playerPos.getX() - 1 && block.xCoord <= playerPos.getX() && !toCheck.contains(new BlockPos(block)) && !checked.contains(new BlockPos(block))) {
                                    toCheck.add(new BlockPos(block));
                                }
                            }
                        }
                        if (AIOMVigilanceConfig.yaw == 180) {
                            // -z

                            Block b = Main.mcWorld.getBlockState(new BlockPos(block)).getBlock();
                            if (b == Blocks.nether_wart || b == Blocks.carrots || b == Blocks.potatoes || b == Blocks.wheat) {
                                if (block.xCoord - .5 == playerPos.getX() && block.zCoord - .5 >= playerPos.getZ() - 1 && block.zCoord <= playerPos.getZ() && !toCheck.contains(new BlockPos(block)) && !checked.contains(new BlockPos(block))) {
                                    toCheck.add(new BlockPos(block));
                                }
                            }
                        }
                        if (AIOMVigilanceConfig.yaw == -90) {
                            // +x

                            Block b = Main.mcWorld.getBlockState(new BlockPos(block)).getBlock();
                            if (b == Blocks.nether_wart || b == Blocks.carrots || b == Blocks.potatoes || b == Blocks.wheat) {
                                if (block.zCoord - .5 == playerPos.getZ() && block.xCoord - .5 <= playerPos.getX() + 1 && block.xCoord >= playerPos.getX() && !toCheck.contains(new BlockPos(block)) && !checked.contains(new BlockPos(block))) {
                                    toCheck.add(new BlockPos(block));
                                }
                            }
                        }
                        if (AIOMVigilanceConfig.yaw == 0) {
                            // +z

                            Block b = Main.mcWorld.getBlockState(new BlockPos(block)).getBlock();
                            if (b == Blocks.nether_wart || b == Blocks.carrots || b == Blocks.potatoes || b == Blocks.wheat) {
                                if (block.xCoord - .5 == playerPos.getX() && block.zCoord - .5 <= playerPos.getZ() + 1 && block.zCoord >= playerPos.getZ() && !toCheck.contains(new BlockPos(block)) && !checked.contains(new BlockPos(block))) {
                                    toCheck.add(new BlockPos(block));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
