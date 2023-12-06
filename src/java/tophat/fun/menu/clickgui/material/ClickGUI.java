package tophat.fun.menu.clickgui.material;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import tophat.fun.Client;
import tophat.fun.modules.Module;
import tophat.fun.modules.settings.Setting;
import tophat.fun.modules.settings.impl.BooleanSetting;
import tophat.fun.modules.settings.impl.NumberSetting;
import tophat.fun.modules.settings.impl.StringSetting;
import tophat.fun.utilities.font.CFont;
import tophat.fun.utilities.font.renderer.TTFFontRenderer;
import tophat.fun.utilities.math.MathUtil;
import tophat.fun.utilities.render.RectUtil;
import tophat.fun.utilities.render.RenderUtil;
import tophat.fun.utilities.render.TextUtil;
import tophat.fun.utilities.render.shader.DrawHelper;

import java.awt.*;
import java.io.IOException;

public class ClickGUI extends GuiScreen {

    private final static TTFFontRenderer poppins = CFont.FONT_MANAGER.getFont("PoppinsMedium 18");
    private final static TTFFontRenderer poppinsR = CFont.FONT_MANAGER.getFont("PoppinsRegular 18");
    private final static TTFFontRenderer iconFont = CFont.FONT_MANAGER.getFont("RegularIcons 24");

    private Module.Category selectedCategory = Module.Category.COMBAT;
    private Module selectedModule;

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        float x = (float) (sr.getScaledWidth() / 2) - 150;
        float y = (float) (sr.getScaledHeight() / 2) - 150;
        float width = 300;
        float height = 300;

        DrawHelper.drawRoundedRect(x, y, width, height, 8, new Color(25,25,25));

        float categoryOffset = 0;
        for(Module.Category category : Module.Category.values()) {
            boolean hover = RenderUtil.hovered(mouseX, mouseY, x + 2, y + categoryOffset + 1, 32, 32);
            DrawHelper.drawRoundedRect(x + 2, y + categoryOffset + 1, 32, 32, 8, hover ? new Color(35,35,35) : new Color(30,30,30));
            if(category == selectedCategory) {
                DrawHelper.drawRoundedRectOutline(x + 2, y + categoryOffset + 1, 32, 32, 8, 1.5f, hover ? new Color(32, 211, 196) : new Color(24, 175, 162));
            }
            iconFont.drawString(TextUtil.getCategoryLetter(category), x + 11, y + categoryOffset + 11, -1);
            categoryOffset += 34;
        }

        float moduleOffset = 0;
        for(Module module : Client.INSTANCE.moduleManager.getModulesByCategory(selectedCategory)) {
            boolean hover = RenderUtil.hovered(mouseX, mouseY, x + 32 + 2 + 2, y + moduleOffset + 1, 100, 20);
            DrawHelper.drawRoundedRect(x + 32 + 2 + 2, y + moduleOffset + 1, 100, 20, 8, hover ? new Color(35,35,35) : new Color(30,30,30));
            if(module == selectedModule) {
                DrawHelper.drawRoundedRectOutline(x + 32 + 2 + 2, y + moduleOffset + 1, 100, 20, 8, 1.5f, hover ? new Color(32, 211, 196) : new Color(24, 175, 162));
            }
            poppinsR.drawString(module.getName(), x + 32 + 2 + 5, y + moduleOffset + 5, module.isEnabled() ? hover ? new Color(32, 211, 196).getRGB() : new Color(24, 175, 162).getRGB() : hover ? new Color(255,255,255).getRGB() : new Color(200,200,200).getRGB());
            moduleOffset += 22;
        }

        if(selectedModule != null) {
            boolean hoverKey = RenderUtil.hovered(mouseX, mouseY, x + 32 + 100 + 2 + 2 + 2, y + 1, 160, 20);
            DrawHelper.drawRoundedRect(x + 32 + 100 + 2 + 2 + 2, y + 1, 160, 20, 8, hoverKey ? new Color(35,35,35) : new Color(30,30,30));
            poppinsR.drawString("Keybind: ", x + 32 + 100 + 2 + 2 + 2 + 5, y + 5, -1);

            float settingOffset = 22;
            if(Setting.getSettingsByModule(selectedModule) != null) {
                for (Setting setting : Setting.getSettingsByModule(selectedModule)) {
                    if(setting.isHidden()) continue;
                    boolean hover = RenderUtil.hovered(mouseX, mouseY, x + 32 + 100 + 2 + 2 + 2, y + settingOffset + 1, 160, setting instanceof NumberSetting ? 32 : 20);
                    Color hoverColor = hover ? new Color(35, 35, 35) : new Color(30, 30, 30);

                    if (setting instanceof BooleanSetting) {
                        DrawHelper.drawRoundedRect(x + 32 + 100 + 2 + 2 + 2, y + settingOffset + 1, 160, 20, 8, hoverColor);
                        poppinsR.drawString(setting.getName(), x + 32 + 100 + 2 + 2 + 2 + 5, y + settingOffset + 5, -1);
                        poppinsR.drawString("X", x + 32 + 243 + 2 + 2 + 2 + 5, y + settingOffset + 5, ((BooleanSetting) setting).get() ? Color.GREEN.getRGB() : Color.RED.getRGB());
                        settingOffset += 22;
                    }

                    if (setting instanceof StringSetting) {
                        DrawHelper.drawRoundedRect(x + 32 + 100 + 2 + 2 + 2, y + settingOffset + 1, 160, 20, 8, hoverColor);
                        poppinsR.drawString(setting.getName(), x + 32 + 100 + 2 + 2 + 2 + 5, y + settingOffset + 5, -1);
                        poppinsR.drawString(((StringSetting) setting).get(), x + width - 40, y + settingOffset + 5, -1);
                        settingOffset += 22;
                    }

                    if (setting instanceof NumberSetting) {
                        float value = ((NumberSetting) setting).get().floatValue();
                        DrawHelper.drawRoundedRect(x + 32 + 100 + 2 + 2 + 2, y + settingOffset + 1, 160, 32, 8, hoverColor);
                        poppinsR.drawString(setting.getName() + ": " + ((NumberSetting) setting).get().floatValue(), x + 32 + 100 + 2 + 2 + 2 + 5, y + settingOffset + 5, -1);
                        float min = ((NumberSetting) setting).min().floatValue();
                        float max = ((NumberSetting) setting).max().floatValue();
                        float fillWidth = Math.min(Math.max((value - min) / (max - min), 0), 1) * 150;
                        DrawHelper.drawRoundedRect(x + 32 + 100 + 2 + 2 + 2 + 4, y + settingOffset + 26, 150, 3, 1, new Color(25,25,25));
                        DrawHelper.drawRoundedRectOutline(x + 32 + 100 + 2 + 2 + 2 + 3, y + settingOffset + 24, 150 + 2, 5, 2, 2, new Color(24,175,162));
                        DrawHelper.drawRoundedRect(x + 32 + 100 + 2 + 2 + 2 + 4, y + settingOffset + 25, fillWidth, 3, 1, new Color(31, 227, 207));
                        DrawHelper.drawRoundedRect(x + 32 + 100 + 2 + 2 + 2 + fillWidth + 1,y + settingOffset + 21.5, 3, 9, 1, new Color(255, 255, 255));
                        DrawHelper.drawRoundedRectOutline(x + 32 + 100 + 2 + 2 + 2 + fillWidth, y + settingOffset + 21, 5, 11, 1, 2, new Color(105, 105, 105));

                        // border where the mouse works at
                        RectUtil.rectangle(x + 32 + 100 + 2 + 2 + 2 + 4, y + settingOffset + 24, 150, 4, true, new Color(255555555));
                        //

                        // make this work only inside the border and fix it's dragging at wrong position
                        if (RenderUtil.hovered(mouseX, mouseY, x + 32 + 100 + 2 + 2 + 2 + 4, y + settingOffset + 22, 150, 4)) {
                            if (Mouse.isButtonDown(0)) {
                                double normalizedX = (mouseX - (x + 32 + 100 + 2 + 2 + 2)) / 190.0;
                                double newValue = min + normalizedX * (max - min);
                                newValue = MathUtil.round(newValue, ((NumberSetting) setting).decimalPoints);
                                ((NumberSetting) setting).set(newValue);
                            }
                        }
                        settingOffset += 34;
                    }
                }
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution sr = new ScaledResolution(mc);
        float x = (float) (sr.getScaledWidth() / 2) - 150;
        float y = (float) (sr.getScaledHeight() / 2) - 150;
        float width = 300;
        float height = 300;

        float categoryOffset = 0;
        for(Module.Category category : Module.Category.values()) {
            boolean hover = RenderUtil.hovered(mouseX, mouseY, x + 2, y + categoryOffset + 1, 32, 32);

            switch (mouseButton) {
                case 0:
                    if(hover) {
                        selectedCategory = category;
                    }
                    break;
            }

            categoryOffset += 34;
        }

        float moduleOffset = 0;
        for(Module module : Client.INSTANCE.moduleManager.getModulesByCategory(selectedCategory)) {
            boolean hover = RenderUtil.hovered(mouseX, mouseY, x + 32 + 2 + 2, y + moduleOffset + 1, 100, 20);
            switch (mouseButton) {
                case 0:
                    if(hover) {
                        module.toggle();
                    }
                    break;
                case 1:
                    if(hover) {
                        selectedModule = module;
                    }
                    break;
            }
            moduleOffset += 22;
        }

        if(selectedModule != null) {
            boolean hoverKey = RenderUtil.hovered(mouseX, mouseY, x + 32 + 100 + 2 + 2 + 2, y + 1, 160, 20);
            if(hoverKey && mouseButton == 0) {
                // listening to key presses = true;
            }

            float settingOffset = 22;
            if(Setting.getSettingsByModule(selectedModule) != null) {
                for (Setting setting : Setting.getSettingsByModule(selectedModule)) {
                    if(setting.isHidden()) continue;
                    boolean hover = RenderUtil.hovered(mouseX, mouseY, x + 32 + 100 + 2 + 2 + 2, y + settingOffset + 1, 160, 20);
                    if (setting instanceof BooleanSetting) {
                        if(hover && mouseButton == 0) {
                            ((BooleanSetting) setting).toggle();
                        }
                        settingOffset += 22;
                    }

                    if (setting instanceof StringSetting) {
                        if(hover) {
                            switch (mouseButton) {
                                case 0:
                                    ((StringSetting) setting).forward();
                                    break;
                                case 1:
                                    ((StringSetting) setting).backward();
                                    break;
                            }
                        }
                        settingOffset += 22;
                    }

                    if(setting instanceof NumberSetting) {
                        settingOffset += 34;
                    }
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
