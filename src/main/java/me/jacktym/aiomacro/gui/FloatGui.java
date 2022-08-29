package me.jacktym.aiomacro.gui;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.gui.buttons.SectionButton;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class FloatGui extends GuiScreen {

    int width = Main.mc.displayWidth;
    int height = Main.mc.displayHeight;
    int centerX = width/2;
    int centerY = height/2;

    //SectionButton macroSettings;
    public int sectionIndex = 0;

    public int totalSections = 11;
    public static LinkedHashMap<String, List<LinkedHashMap<String, Object>>> buttons = new LinkedHashMap<String, List<LinkedHashMap<String, Object>>>();

    public static Map<String, Object> macroTypeOptions = new HashMap<String, Object>();



    public List<Integer> sectionSpacing(int totalSections1){
        List<Integer> heights = new ArrayList<Integer>();
        int totalSpacing = Math.round(((Math.round(height * 12/26)) - (Math.round(height/26) + sectionHeights(totalSections1)))) ;
        for(int i = 1; i < totalSections1 + 1; i++){
            if(i == 1){
                heights.add(height/26);
            }

            else if(i != totalSections1){
                heights.add(((Math.round(((totalSpacing * (i - 1))/(totalSections1 - 1))) + (Math.round(height/26) + sectionHeights(totalSections1))) - Math.round(sectionHeights(totalSections1))));
            }
            else {
                heights.add(Math.round(height * 12/26) - sectionHeights(totalSections1));
            }

        }
        return heights;
    }
    public int sectionHeights(int totalSections1){
        return Math.round(height/(totalSections1 + 35));
    }



    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for(GuiButton button: buttonList){
            SectionButton button1 = (SectionButton) button;
            button1.visible = false;
            button1.drawButton(Main.mc, mouseX, mouseY);
            int width1 = fontRendererObj.getStringWidth(button1.displayString);
            fontRendererObj.drawString(button1.displayString, Math.round(button1.x + (button1.width/2) - (width1/2)), Math.round(button1.y + (button1.height/2)),  new Color(255, 0, 0, 0).getRGB());

        }


    }



    @Override
    public void initGui() {
        buttons.clear();
        macroTypeOptions.clear();
        buttonList.clear();
        //macroTypeOptions.put("Netherwart/S-Shaped", AIOMVigilanceConfig.macroType);
        int i = 0;
        sectionIndex = 0;
        buttons.put("Macro Settings", null);
        buttons.put("Test", null);
        buttons.put("Test1", null);
        buttons.put("Test2", null);
        buttons.put("Test3", null);
        buttons.put("Test4", null);
        buttons.put("Test5", null);
        buttons.put("Test6", null);
        buttons.put("Test7", null);
        buttons.put("Test8", null);
        buttons.put("Test9", null);
        System.out.println(buttons);

        for(String name: buttons.keySet()){
            buttonList.add(new SectionButton(sectionIndex, width/18, sectionSpacing(totalSections).get(i), width/20, sectionHeights(totalSections), name));
            i++;
            sectionIndex++;
        }
        for(GuiButton button: buttonList){
            button.enabled = false;
            SectionButton button1 = (SectionButton) button;
        }
        System.out.println(buttons + "        " + buttonList);
        super.initGui();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }


    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for(GuiButton button: buttonList) {
            SectionButton button1 = (SectionButton) button;
            if (mouseX >= button1.x && mouseX <= button1.x + button1.width && mouseY >= button1.y && mouseY <=button1.y + button1.height) {
                button1.enabled = !button1.enabled;
                if(button1.enabled){
                    button1.texture = button1.blueTexture;
                }
                else {
                    button1.texture = button1.greenTexture;
                }
            }
        }

    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }
}
