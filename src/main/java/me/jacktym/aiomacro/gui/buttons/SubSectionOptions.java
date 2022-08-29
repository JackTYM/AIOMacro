package me.jacktym.aiomacro.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class SubSectionOptions extends GuiButton {
    public static final ResourceLocation greenTexture = new ResourceLocation("textures/float/sectionBase.png");
    public static final ResourceLocation blueTexture = new ResourceLocation("textures/float/sectionClick.png");
    public static final ResourceLocation orangeTexture = new ResourceLocation("textures/float/sectionHover.png");

    public static ResourceLocation texture = greenTexture;
    public static int width;
    public static int height;
    public static int x;
    public static int y;
    public static int optionsCount;
    public SubSectionOptions(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, int optionsCountIn) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.x = x;
        this.y = y;
        this.width = widthIn;
        this.height = heightIn;
        this.optionsCount = optionsCountIn;
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
