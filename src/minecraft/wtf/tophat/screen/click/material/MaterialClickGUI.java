package wtf.tophat.screen.click.material;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import wtf.tophat.Client;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.impl.render.PostProcessing;
import wtf.tophat.settings.base.Setting;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.shader.blur.GaussianBlur;
import wtf.tophat.utilities.font.CFontRenderer;
import wtf.tophat.utilities.font.CFontUtil;
import wtf.tophat.utilities.render.CategoryUtil;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import static wtf.tophat.utilities.Colors.*;

public class MaterialClickGUI extends GuiScreen {

    @Override
    public void initGui() {
        ScaledResolution sr = new ScaledResolution(mc);

        double width = 350;
        double height = 350;
        frameX = (sr.getScaledWidth_double() - width) / 2;
        frameY = (sr.getScaledHeight_double() - height) / 2;

        super.initGui();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private final Module.Category defaultCategory = Module.Category.COMBAT;
    private Module.Category selectedCategory = Module.Category.COMBAT;
    private Module expandedModule = null;

    private double frameX;
    private double frameY;
    private boolean isDragging = false;
    private int dragOffsetX, dragOffsetY;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        CFontRenderer fr = CFontUtil.SF_Regular_20.getRenderer();
        CFontRenderer catfr = CFontUtil.ICONS_50.getRenderer();

        renderBlur();

        double width = 350;
        double height = 350;
        double x = frameX;
        double y = frameY;

        if (isDragging) {
            double newX = mouseX - dragOffsetX;
            double newY = mouseY - dragOffsetY;
            newX = Math.max(0, Math.min(sr.getScaledWidth_double() - width, newX));
            newY = Math.max(0, Math.min(sr.getScaledHeight_double() - height, newY));
            frameX = newX;
            frameY = newY;
        }

        if (!Mouse.isButtonDown(0)) {
            isDragging = false;
        }

        // Main Frame
        DrawingUtil.rectangle(x, y, width, height, true, new Color(20, 20, 20));
        // Border
        DrawingUtil.rectangle(x, y, width, height, false, CategoryUtil.getCategoryColor(selectedCategory));

        // Drag Bar
        DrawingUtil.rectangle(x, y + 1, width - 1, 15, true, new Color(30, 30, 30));

        boolean mouseHovered = DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) (x + 340), (float) (y + 3), fr.getStringWidth("X"), fr.getHeight());

        fr.drawString("X", x + 340, y + 3, mouseHovered ? CategoryUtil.getCategoryColor(selectedCategory) : Color.WHITE);
        fr.drawString(Client.getName().toLowerCase(Locale.ROOT) + " v" + Client.getVersion(), x + 3, y + 3, CategoryUtil.getCategoryColor(selectedCategory));

        // Category Box
        DrawingUtil.rectangle(x, y + 15, 50, 334, true, new Color(30, 30, 30));

        int adjustment = 10;
        for (Module.Category category : Module.Category.values()) {
            float categoryX = (float) (x + adjustment);
            float categoryY = (float) (y + CategoryUtil.getCategoryY(category));

            boolean isHovered = DrawingUtil.hovered((float) mouseX, (float) mouseY, categoryX - 2, categoryY - 2, 36, 36);

            Color textColor;

            if (isHovered) {
                textColor = CategoryUtil.getCategoryColor(category);
            } else if (category == selectedCategory) {
                textColor = CategoryUtil.getCategoryColor(category);
            } else {
                textColor = Color.WHITE;
            }

            catfr.drawStringWithShadow(CategoryUtil.getCategoryLetter(category), categoryX + 2, categoryY + 4, textColor);
        }

        int counter = 0;
        for (Module module : Client.moduleManager.getModulesByCategory(selectedCategory)) {

            boolean isHovered = DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) x + 50, (float) y + 16 + counter, 100, 15);

            Color moduleBackgroundColor = isHovered
                    ? module.isEnabled()
                    ? new Color(55, 55, 55) // Lighter color for hovered and enabled module
                    : new Color(44, 44, 44) // Lighter color for hovered and disabled module
                    : module.isEnabled()
                    ? new Color(44, 44, 44) // Regular color for enabled module
                    : new Color(33, 33, 33); // Regular color for disabled module

            DrawingUtil.rectangle(x + 50, y + 16 + counter, 100, 15, true, moduleBackgroundColor);
            fr.drawStringWithShadow(module.getName(), x + 54, y + 19 + counter, module.isEnabled() ? CategoryUtil.getCategoryColor(selectedCategory) : Color.WHITE);

            if (isHovered) {
                String text = (module.getDesc()).toLowerCase(Locale.ROOT);
                int strWidth = fr.getStringWidth(text) + 3;

                DrawingUtil.rectangle(5, this.height - 35, strWidth + 11, 20, true, new Color(5,5,5));
                DrawingUtil.rectangle(6, this.height - 34, strWidth + 9, 18, true, new Color(60,60,60));
                DrawingUtil.rectangle(7, this.height - 33, strWidth + 7, 16, true, new Color(40,40,40));
                DrawingUtil.rectangle(9, this.height - 31, strWidth + 3, 12, true, new Color(60,60,60));
                DrawingUtil.rectangle(10, this.height - 30, strWidth + 1, 10, true, new Color(22,22,22));
                DrawingUtil.rectangle(10, this.height - 30, strWidth + 1, 1, true, new Color(ColorUtil.fadeBetween(DEFAULT_COLOR, WHITE_COLOR, counter * 150L)));

                fr.drawStringWithShadow(text, 11, this.height - 28, Color.WHITE);
            }

            counter += 15;
        }

        int offset = 16;
        for (Setting setting : Client.settingManager.getSettingsByModule(expandedModule)) {

            boolean isHovered = DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) x + 150, (float) y + offset, 199, setting instanceof NumberSetting ? 32 : 15);

            Color settingBackgroundColor = isHovered ?
                    new Color(44, 44, 44) :
                    new Color(30,30,30);

            if (setting.isHidden()) {
                continue;
            }

            if (setting instanceof StringSetting) {
                DrawingUtil.rectangle(x + 150, y + offset, 199, 15, true, settingBackgroundColor);
                fr.drawStringWithShadow(setting.getName() + ": " + ((StringSetting) setting).getValue(), x + 150, y + offset + 3, Color.WHITE);
                offset += 15;
            } else if (setting instanceof BooleanSetting) {
                DrawingUtil.rectangle(x + 150, y + offset, 199, 15, true, settingBackgroundColor);
                fr.drawStringWithShadow(setting.getName() + ": " + ((BooleanSetting) setting).getValue(), x + 150, y + offset + 3, Color.WHITE);
                offset += 15;
            } else if (setting instanceof NumberSetting) {
                NumberSetting numberSetting = (NumberSetting) setting;
                double currentValue = numberSetting.getValue().doubleValue(), minValue = numberSetting.getMinimum().doubleValue(), maxValue = numberSetting.getMaximum().doubleValue();
                int decimalPoints = numberSetting.decimalPoints;

                double randoValue = ((currentValue - minValue) / (maxValue - minValue)) * (185 - 6);
                double sliderPosition = x + 154 + randoValue;

                DrawingUtil.rectangle(x + 150, y + offset, 199, 32, true, settingBackgroundColor);

                String formattedValue = String.format(Locale.ROOT, setting.getName() + ": %." + decimalPoints + "f", currentValue);

                fr.drawStringWithShadow(formattedValue, x + 150, y + offset + 3, Color.WHITE);

                DrawingUtil.rectangle(x + 154, y + offset + 15, 185, 11, true, new Color(0, 0, 0));
                //The slider that will move along with the slider pointer when it is dragged.
                DrawingUtil.rectangle(x + 154, y + offset + 15, randoValue, 11, true, new Color(60, 60, 60));
                // Slider Pointer
                DrawingUtil.rectangle(sliderPosition, y + offset + 15, 6, 11, true, new Color(ColorUtil.fadeBetween(WHITE_COLOR, LIGHT_GRAY_COLOR, counter * 150L)));
                DrawingUtil.rectangle(x + 154, y + offset + 15, 185, 11, false, Color.WHITE);
                offset += 32;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        CFontRenderer fr = CFontUtil.SF_Regular_20.getRenderer();

        if (mouseButton == 0) {
            double width = 350;
            double x = frameX;
            double y = frameY;

            if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) x, (float) y, (float) (width - 1), 15)) {
                isDragging = true;
                dragOffsetX = mouseX - (int) x;
                dragOffsetY = mouseY - (int) y;
            }

            int counter = 0;
            for(Module module : Client.moduleManager.getModulesByCategory(selectedCategory)) {
                if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) x + 50, (float) y + 16 + counter, 100, 15)) {
                    module.toggle();
                }
                counter += 15;
            }

            for (Module.Category category : Module.Category.values()) {
                if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) (x + 10), (float) (y + CategoryUtil.getCategoryY(category)), 36, 36)) {
                    selectedCategory = category;
                }
            }

            int offset = 16;
            for (Setting setting : Client.settingManager.getSettingsByModule(expandedModule)) {
                if(setting.isHidden()) {
                    continue;
                }

                if(setting instanceof StringSetting) {
                    if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) (x + 150), (float) (y + offset), 199, 15)) {
                        ((StringSetting) setting).forward();
                    }
                    offset += 15;
                }
                if(setting instanceof BooleanSetting) {
                    if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) (x + 150), (float) (y + offset), 199, 15)) {
                        ((BooleanSetting) setting).toggle();
                    }
                    offset += 15;
                }
            }

            boolean closeClicked = DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) (x + 340), (float) (y + 3), fr.getStringWidth("X"), fr.getHeight());
            if (closeClicked) {
                mc.displayGuiScreen(null);
            }
        }

        if(mouseButton == 1) {
            double x = frameX;
            double y = frameY;

            int counter = 0;
            for(Module module : Client.moduleManager.getModulesByCategory(selectedCategory)) {
                if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) x + 50, (float) y + 16 + counter, 100, 15)) {
                    expandedModule = module;
                }
                counter += 15;
            }

            int offset = 15;
            for (Setting setting : Client.settingManager.getSettingsByModule(expandedModule)) {
                if(setting.isHidden()) {
                    continue;
                }

                if(setting instanceof StringSetting) {
                    if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) (x + 150), (float) (y + offset), 199, 15)) {
                        ((StringSetting) setting).backward();
                    }
                    offset += 15;
                }
                if(setting instanceof BooleanSetting) {
                    if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) (x + 150), (float) (y + offset), 199, 15)) {
                        ((BooleanSetting) setting).toggle();
                    }
                    offset += 15;
                }
            }
        }
    }

    private void renderBlur() {
        if (Client.moduleManager.getByClass(PostProcessing.class).isEnabled() && Client.moduleManager.getByClass(PostProcessing.class).blurShader.getValue()) {
            GaussianBlur.startBlur();
            DrawingUtil.rectangle(0, 0, width, height, true, new Color(0, 0, 0));
            GaussianBlur.endBlur(10, 2);
        }
    }
}
