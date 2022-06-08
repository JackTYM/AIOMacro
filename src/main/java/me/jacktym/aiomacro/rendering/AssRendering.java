package me.jacktym.aiomacro.rendering;

import me.jacktym.aiomacro.Main;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;

public class AssRendering implements LayerRenderer<EntityPlayer> {

    private final RenderPlayer render;
    private final ModelRenderer model;

    public AssRendering(RenderPlayer render) {
        this.render = render;
        this.model = new ModelRenderer(this.render.getMainModel(), 18, 24);
    }

    @Override
    public void doRenderLayer(EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        boolean showAss = Main.assMap.containsKey(player.getUniqueID().toString());
        if (!showAss) {
            this.render.getMainModel().bipedBodyWear.showModel = player.isWearing(EnumPlayerModelParts.JACKET);
            this.render.getMainModel().bipedLeftArmwear.showModel = player.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
            this.render.getMainModel().bipedRightArmwear.showModel = player.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
        } else {
            int assSize = Main.assMap.get(player.getUniqueID().toString());
            GlStateManager.pushMatrix();
            this.render.getMainModel().bipedBodyWear.showModel = false;
            this.render.getMainModel().bipedLeftArmwear.showModel = false;
            this.render.getMainModel().bipedRightArmwear.showModel = false;
            this.render.bindTexture(((AbstractClientPlayer) player).getLocationSkin());
            this.getTranslation(assSize);
            this.model.addBox(-4.0F, -0.6F, -3.0F, 8, 4, 3, 0.0F);
            this.model.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.model.rotateAngleY = this.render.getMainModel().bipedBody.rotateAngleY;

            if (player.isSneaking()) {
                this.model.rotateAngleX = 0.5F;
                this.getSneakTranslation(assSize);
            } else {
                this.model.rotateAngleX = 0.0F;
            }

            this.getScale(assSize);
            this.model.render(scale);

            GlStateManager.popMatrix();
        }
    }

    private void getScale(int size) {
        switch (size) {
            case 0:
                GlStateManager.scale(1.0F, 1.0F, 1.0F);
                break;
            case 1:
                GlStateManager.scale(1.25F, 1.25F, 1.25F);
                break;
            case 2:
                GlStateManager.scale(1.5F, 1.5F, 1.5F);
                break;
            case 3:
                GlStateManager.scale(1.75F, 1.75F, 1.75F);
                break;
            case 4:
                GlStateManager.scale(2.0F, 2.0F, 2.0F);
                break;
            case 5:
                GlStateManager.scale(3.0F, 3.0F, 3.0F);
                break;
            case 6:
                GlStateManager.scale(6.0F, 6.0F, 6.0F);
                break;
            default:
                break;
        }
    }

    private void getTranslation(int size) {
        switch (size) {
            case 0:
                GlStateManager.translate(0.0F, 0.5F, 0.3F);
                break;
            case 1:
                GlStateManager.translate(0.0F, 0.5F, 0.36F);
                break;
            case 2:
            case 3:
                GlStateManager.translate(0.0F, 0.5F, 0.41F);
                break;
            case 4:
                GlStateManager.translate(0.0F, 0.5F, 0.50F);
                break;
            case 5:
                GlStateManager.translate(0.0F, 0.55F, 0.69F);
                break;
            case 6:
                GlStateManager.translate(0.0F, 0.65F, 1.25F);
                break;
            default:
                break;
        }
    }

    private void getSneakTranslation(int size) {
        switch (size) {
            case 0:
                GlStateManager.translate(0.0F, 0.01F, 0.17F);
                break;
            case 1:
                GlStateManager.translate(0.0F, -0.1F, 0.1F);
                break;
            case 2:
            case 3:
            case 4:
                GlStateManager.translate(0.0F, -0.2F, 0.05F);
                break;
            case 5:
                GlStateManager.translate(0.0F, -0.2F, 0.1F);
                break;
            case 6:
                GlStateManager.translate(0.0F, -0.2F, -0.3F);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

}
