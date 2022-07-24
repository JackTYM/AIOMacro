package me.jacktym.aiomacro.rendering;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BoobRendering implements LayerRenderer<EntityLivingBase> {
    private final RenderPlayer playerRenderer;

    public BoobRendering(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        //if (Main.boobMap.containsKey(entitylivingbaseIn.getUniqueID().toString()) && AIOMVigilanceConfig.renderingEnabled) {
        //int boobSize = Main.boobMap.get(entitylivingbaseIn.getUniqueID().toString());

        this.playerRenderer.bindTexture(((AbstractClientPlayer) entitylivingbaseIn).getLocationSkin());

        GlStateManager.pushMatrix();
        translate(1);
        if (entitylivingbaseIn.isSneaking()) {
            getSneakTranslation(1);
        }
        getScale(1);
        renderBoobs(0.0F);
        GlStateManager.popMatrix();
        //}
    }

    public void renderBoobs(float p_178727_1_) {
        //new ModelBiped(0.0F, 0.0F, 64, 64);
        ModelRenderer bipedBoobs = new ModelRenderer(playerRenderer.getMainModel(), 17, 20);
        bipedBoobs.addBox(-4.0F, -6.0F, -9.0F, 8, 4, 3, 0.0F);

        ModelBase.copyModelAngles(this.playerRenderer.getMainModel().bipedBody, bipedBoobs);
        bipedBoobs.rotationPointX = 0.0F;
        bipedBoobs.rotationPointY = 0.0F;
        bipedBoobs.render(p_178727_1_);
    }

    public boolean shouldCombineTextures() {
        return true;
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

    private void translate(int size) {
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
