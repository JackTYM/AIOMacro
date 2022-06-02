package me.jacktym.aiomacro.macros;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Nuker {

    private static final Map<Integer, Boolean> glCapMap = new HashMap<>();
    ArrayList<BlockPos> toBreak = new ArrayList<>();
    BlockPos bp = null;

    public static void setGlState(int cap, boolean state) {
        if (state) {
            GL11.glEnable(cap);
        } else {
            GL11.glDisable(cap);
        }
    }

    @SubscribeEvent
    public void playerTick(@NotNull TickEvent.ClientTickEvent event) {
        if ((AIOMVigilanceConfig.macroType == 2 || AutoBazaarUnlocker.autoWheatOn) && Main.mcPlayer != null && Main.mcWorld != null && toBreak.size() != 0) {
            for (int i = 0; i <= AIOMVigilanceConfig.nukerBPS; i++) {
                if (toBreak.size() != 0) {
                    BlockPos blockPos = toBreak.get(0);
                    BlockPos playerPos = Main.mcPlayer.getPosition();
                    Vec3i vec3i = new Vec3i(3, 1, 3);
                    ArrayList<BlockPos> blocks = new ArrayList<>();
                    for (BlockPos bp : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i))) {
                        blocks.add(bp);
                    }

                    if (!blocks.contains(new BlockPos(blockPos.getX() - 0.5, blockPos.getY(), blockPos.getZ() - 0.5)) || !blocks.contains(new BlockPos(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5))) {
                        toBreak.remove(blockPos);
                    }
                    if (toBreak.size() != 0) {
                        bp = new BlockPos(toBreak.get(0));
                        Main.mcPlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, new BlockPos(toBreak.get(0)), EnumFacing.DOWN));
                        toBreak.remove(0);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        if ((AIOMVigilanceConfig.macroType == 2 || AutoBazaarUnlocker.autoWheatOn) && Main.mcPlayer != null && Main.mcWorld != null && bp != null && toBreak.size() != 0) {
            try {
                BlockPos blockPos = bp;
                Color color = AIOMVigilanceConfig.nukerColor;
                int width = 5;
                float partialTicks = event.partialTicks;

                RenderManager renderManager = Main.mc.getRenderManager();
                double x = (double) blockPos.getX() - renderManager.viewerPosX;
                double y = (double) blockPos.getY() - renderManager.viewerPosY;
                double z = (double) blockPos.getZ() - renderManager.viewerPosZ;
                AxisAlignedBB axisAlignedBB = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
                Block block = Main.mcWorld.getBlockState(blockPos).getBlock();
                if (block != null) {
                    EntityPlayerSP player = Main.mcPlayer;
                    double posX = player.lastTickPosX + ((player).posX - player.lastTickPosX) * (double) partialTicks;
                    double posY = player.lastTickPosY + ((player).posY - player.lastTickPosY) * (double) partialTicks;
                    double posZ = player.lastTickPosZ + ((player).posZ - player.lastTickPosZ) * (double) partialTicks;
                    block.setBlockBoundsBasedOnState(Main.mcWorld, blockPos);
                    axisAlignedBB = block.getCollisionBoundingBox(Main.mcWorld, blockPos, Main.mcWorld.getBlockState(blockPos)).expand(0.002f, 0.002f, 0.002f).offset(-posX, -posY, -posZ);
                }
                GL11.glBlendFunc(770, 771);

                glCapMap.put(3042, GL11.glGetBoolean(3042));
                GL11.glEnable(3042);

                glCapMap.put(3553, GL11.glGetBoolean(3553));
                GL11.glDisable(3553);
                glCapMap.put(2929, GL11.glGetBoolean(2929));
                GL11.glDisable(2929);

                GL11.glDepthMask(false);
                GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() != 255 ? color.getAlpha() / 255.0f : 26 / 255.0f);
                GL11.glLineWidth(width);
                glCapMap.put(2848, GL11.glGetBoolean(2848));
                GL11.glEnable(2848);
                GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() != 255 ? color.getAlpha() : 26);

                AxisAlignedBB boundingBox = axisAlignedBB;
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                worldrenderer.begin(3, DefaultVertexFormats.POSITION);
                worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
                worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
                worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
                worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
                worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
                worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
                worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
                worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
                worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
                worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
                worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
                worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
                worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
                worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
                worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
                worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
                tessellator.draw();

                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glDepthMask(true);
                glCapMap.forEach(Nuker::setGlState);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void pickBlocks(@NotNull RenderWorldLastEvent event) {
        if (MacroHandler.isMacroOn && AIOMVigilanceConfig.macroType == 2 && Main.mcPlayer != null && Main.mcWorld != null) {
            BlockPos playerPos = Main.mcPlayer.getPosition();
            Vec3i vec3i;
            if (AIOMVigilanceConfig.nukerBlock != 2) {
                vec3i = new Vec3i(3, 1, 3);
            } else {
                vec3i = new Vec3i(3, 3, 3);
            }
            ArrayList<Vec3> blocks = new ArrayList<>();
            for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i))) {
                IBlockState blockState = Main.mcWorld.getBlockState(blockPos);
                if (blockState.getBlock() == Blocks.air) {
                    continue;
                }
                blocks.add(new Vec3((double) blockPos.getX() + 0.5, blockPos.getY(), (double) blockPos.getZ() + 0.5));
            }

            for (Vec3 block : blocks) {
                Block b = Main.mcWorld.getBlockState(new BlockPos(block)).getBlock();
                if (AIOMVigilanceConfig.nukerBlock == 0) {
                    if (b == Blocks.mycelium && !toBreak.contains(new BlockPos(block))) {
                        toBreak.add(new BlockPos(block));
                    }
                }
                if (AIOMVigilanceConfig.nukerBlock == 1) {
                    if (b == Blocks.sand && !toBreak.contains(new BlockPos(block)) && Main.mcWorld.getBlockState(new BlockPos(block)).getBlock().getMetaFromState(Main.mcWorld.getBlockState(new BlockPos(block))) == 1) {
                        toBreak.add(new BlockPos(block));
                    }
                }
                if (AIOMVigilanceConfig.nukerBlock == 2) {
                    if ((b == Blocks.log || b == Blocks.log2) && !toBreak.contains(new BlockPos(block))) {
                        toBreak.add(new BlockPos(block));
                    }
                }
            }
        }
        if (AutoBazaarUnlocker.autoWheatOn) {
            assert Main.mcPlayer != null;
            BlockPos playerPos = Main.mcPlayer.getPosition();
            Vec3i vec3i = new Vec3i(3, 1, 3);
            ArrayList<Vec3> blocks = new ArrayList<>();
            for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i))) {
                IBlockState blockState = Main.mcWorld.getBlockState(blockPos);
                if (blockState.getBlock() == Blocks.air) {
                    continue;
                }
                blocks.add(new Vec3((double) blockPos.getX() + 0.5, blockPos.getY(), (double) blockPos.getZ() + 0.5));
            }

            for (Vec3 block : blocks) {
                Block b = Main.mcWorld.getBlockState(new BlockPos(block)).getBlock();
                if (b == Blocks.wheat && !toBreak.contains(new BlockPos(block))) {
                    toBreak.add(new BlockPos(block));
                }
            }
        }
    }
}
