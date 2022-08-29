package me.jacktym.aiomacro.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class SectionButton extends GuiButton {
    public final ResourceLocation greenTexture = new ResourceLocation("textures/float/sectionBase.png");
    public final ResourceLocation blueTexture = new ResourceLocation("textures/float/sectionClick.png");
    public final ResourceLocation orangeTexture = new ResourceLocation("textures/float/sectionHover.png");

    public ResourceLocation texture = greenTexture;
    public int width;
    public int height;
    public int x;
    public int y;


    public SectionButton(int buttonId, int x, int y, int widthIn, int heightIn, String text) {
        super(buttonId, x, y, widthIn, heightIn, text);
        this.x = x;
        this.y = y;
        this.width = widthIn;
        this.height = heightIn;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {

        mc.renderEngine.bindTexture(texture);
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            hovered = true;
        } else {
            hovered = false;
            if(enabled){
                texture = blueTexture;
            }
            else {
                texture = greenTexture;
            }
        }
        if (hovered) {
            texture = orangeTexture;
            mc.renderEngine.bindTexture(texture);
        }

        drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);

        super.drawButton(mc, mouseX, mouseY);
    }

}
