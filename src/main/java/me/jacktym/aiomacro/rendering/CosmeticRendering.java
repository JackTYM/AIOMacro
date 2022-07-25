package me.jacktym.aiomacro.rendering;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;

public class CosmeticRendering implements LayerRenderer<EntityLivingBase> {


    private final RenderPlayer playerRenderer;

    public CosmeticRendering(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        if (Main.boobMap.containsKey(entitylivingbaseIn.getUniqueID().toString()) && AIOMVigilanceConfig.renderingEnabled) {

            //int boobSize = Main.boobMap.get(entitylivingbaseIn.getUniqueID().toString());
            int boobSize = 1;

            this.playerRenderer.bindTexture(((AbstractClientPlayer) entitylivingbaseIn).getLocationSkin());

            GlStateManager.pushMatrix();
            ModelRenderer bipedBoobs = new ModelRenderer(playerRenderer.getMainModel(), 17, 20);
            bipedBoobs.addBox(-4.0F, -6.0F, -9.0F, 8, 4, 3, 0.0F);

            translateBoob(boobSize);
            if (entitylivingbaseIn.isSneaking()) {
                getSneakTranslation(boobSize);
            }
            getScale(boobSize);

            //ModelBase.copyModelAngles(this.playerRenderer.getMainModel().bipedBody, bipedBoobs);
            bipedBoobs.rotationPointX = 0.0F;
            bipedBoobs.rotationPointY = 0.0F;
            bipedBoobs.render(0.0625F);
            GlStateManager.popMatrix();
        }
        if (Main.testicleMap.containsKey(entitylivingbaseIn.getUniqueID().toString()) && AIOMVigilanceConfig.renderingEnabled) {
            int testicleSize = Main.testicleMap.get(entitylivingbaseIn.getUniqueID().toString());

            this.playerRenderer.bindTexture(((AbstractClientPlayer) entitylivingbaseIn).getLocationSkin());

            GlStateManager.pushMatrix();
            translateTesticle(testicleSize);
            if (entitylivingbaseIn.isSneaking()) {
                getSneakTranslation(testicleSize);
            }
            getScale(testicleSize);
            renderTesticles(0.0625F);
            GlStateManager.popMatrix();
        }
        if (Main.assMap.containsKey(entitylivingbaseIn.getUniqueID().toString()) && AIOMVigilanceConfig.renderingEnabled) {
            int assSize = Main.assMap.get(entitylivingbaseIn.getUniqueID().toString());

            this.playerRenderer.bindTexture(((AbstractClientPlayer) entitylivingbaseIn).getLocationSkin());

            GlStateManager.pushMatrix();
            translateAss(assSize);
            if (entitylivingbaseIn.isSneaking()) {
                getSneakTranslation(assSize);
            }
            getScale(assSize);
            renderAss(0.0625F);
            GlStateManager.popMatrix();
        }
    }


    public void renderBoobs(float p_178727_1_, int size, boolean isSneaking) {
        //System.out.println("rendered booba");
    }

    public void renderTesticles(float p_178727_1_) {
        ModelRenderer bipedTesticles = new ModelRenderer(playerRenderer.getMainModel(), 0, 21);
        bipedTesticles.addBox(-2.0F, 2.0F, -9.0F, 4, 4, 3, p_178727_1_);
        bipedTesticles.addBox(-1.0F, 3.0F, -15.0F, 2, 2, 6, p_178727_1_);

        ModelBase.copyModelAngles(this.playerRenderer.getMainModel().bipedBody, bipedTesticles);
        bipedTesticles.rotationPointX = 0.0F;
        bipedTesticles.rotationPointY = 0.0F;
        bipedTesticles.render(p_178727_1_);
    }

    public void renderAss(float p_178727_1_) {
        ModelRenderer bipedAss = new ModelRenderer(playerRenderer.getMainModel(), 18, 24);
        bipedAss.addBox(-4.0F, -0.6F, -3.0F, 8, 4, 3, p_178727_1_);

        ModelBase.copyModelAngles(this.playerRenderer.getMainModel().bipedBody, bipedAss);
        bipedAss.rotationPointX = 0.0F;
        bipedAss.rotationPointY = 0.0F;
        bipedAss.render(p_178727_1_);
    }

    public boolean shouldCombineTextures() {
        return false;
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

    private void translateBoob(int size) {
        switch (size) {
            case 0:
                GlStateManager.translate(0.0F, 0.5F, 0.3F);
                break;
            case 1:
                GlStateManager.translate(0.0F, 0.6F, 0.36F);
                break;
            case 2:
                GlStateManager.translate(0.0F, 0.7F, 0.45F);
                break;
            case 3:
                GlStateManager.translate(0.0F, 0.8F, 0.55F);
                break;
            case 4:
                GlStateManager.translate(0.0F, 0.9F, 0.65F);
                break;
            case 5:
                GlStateManager.translate(0.0F, 1.28F, 1.0F);
                break;
            case 6:
                GlStateManager.translate(0.0F, 2.4F, 2.14F);
                break;
            default:
                break;
        }
    }

    private void translateTesticle(int size) {
        switch (size) {
            case 0:
                GlStateManager.translate(0.0F, 0.563F, 0.3F);
                break;
            case 1:
                GlStateManager.translate(0.0F, 0.53F, 0.36F);
                break;
            case 2:
                GlStateManager.translate(0.0F, 0.5F, 0.5F);
                break;
            case 3:
                GlStateManager.translate(0.0F, 0.47F, 0.55F);
                break;
            case 4:
                GlStateManager.translate(0.0F, 0.435F, 0.65F);
                break;
            case 5:
                GlStateManager.translate(0.0F, 0.313F, 1.0F);
                break;
            case 6:
                GlStateManager.translate(0.0F, -0.06F, 2.15F);
                break;
            default:
                break;
        }
    }

    private void translateAss(int size) {
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
}
