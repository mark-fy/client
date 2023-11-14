package wtf.tophat.client.menus.click.dropdown;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.impl.hud.ClickGUI;
import wtf.tophat.client.modules.impl.render.PostProcessing;
import wtf.tophat.client.settings.base.Setting;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.DividerSetting;
import wtf.tophat.client.settings.impl.StringSetting;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.utilities.render.CategoryUtil;
import wtf.tophat.client.utilities.render.shaders.blur.GaussianBlur;
import wtf.tophat.client.utilities.render.DrawingUtil;

import java.awt.*;
import java.io.IOException;
import java.util.Locale;

public class DropDownSetting extends GuiScreen {
    private NumberSetting currentDraggingSetting = null;
    private final GuiScreen screenParent;
    private final Module parent;

    public DropDownSetting(GuiScreen screenParent, Module parent) {
        this.screenParent = screenParent;
        this.parent = parent;
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1 && screenParent != null) {
            mc.displayGuiScreen(screenParent);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontRenderer fr = mc.fontRenderer;
        boolean shadow = TopHat.moduleManager.getByClass(ClickGUI.class).fontShadow.get();

        renderBlur();

        Color colorLighter = new Color(122, 168, 255);

        double x = (width - 200) / 2.0, y = 50;
        double width = 200;
        double height = 20;

        DrawingUtil.rectangle(x, y, width, height, true, new Color(20,20,20));

        fr.drawStringOptional(shadow, EnumChatFormatting.BOLD + parent.getName(), (int) x + 5, (int) y + 5, CategoryUtil.getCategoryColor(parent.getCategory()));

        int counter = 0;
        for(Setting setting : TopHat.settingManager.getSettingsByModule(parent)) {
            if(setting.isHidden())
                continue;

            if(setting instanceof DividerSetting) {
                int stringWidth = fr.getStringWidth(setting.getName());
                int centerX = (int) ((width - stringWidth) / 2);
                int textX = (int) (x + centerX);

                DrawingUtil.rectangle(x, y + 18, width, height, true, new Color(33, 33, 33));
                fr.drawStringOptional(shadow, setting.getName(), (float) textX, (float) (y + 25), Color.WHITE);
                y += 20;
            }

            if(setting instanceof StringSetting) {
                DrawingUtil.rectangle(x, y + 18, width, height, true, new Color(33,33,33));
                fr.drawStringOptional(shadow,setting.getName() + ": ", (float) (x + 5), (float) (y + 25), Color.WHITE);
                DrawingUtil.rectangle(x + 192 - fr.getStringWidth(((StringSetting) setting).get()), y + 22, fr.getStringWidth(((StringSetting) setting).get()) + 3, 11, true, new Color(20,20,20));
                DrawingUtil.rectangle(x + 192 - fr.getStringWidth(((StringSetting) setting).get()), y + 22, fr.getStringWidth(((StringSetting) setting).get()) + 3, 11, false, CategoryUtil.getCategoryColor(parent.getCategory()));
                fr.drawStringOptional(shadow, ((StringSetting) setting).get(), (float) (x + 192 - fr.getStringWidth(((StringSetting) setting).get()) + 1), (float) y + 22 + 2, Color.WHITE);
                y += 20;
            }

            if(setting instanceof BooleanSetting) {
                DrawingUtil.rectangle(x, y + 18, width, height, true, new Color(33,33,33));
                fr.drawStringOptional(shadow,setting.getName() + ": ", (float) x + 4, (float) y + 25, Color.WHITE);
                DrawingUtil.rectangle(x + 174, y + 22, 21, 11, true, new Color(20,20,20));
                DrawingUtil.rectangle(x + 174, y + 22, 21, 11, false, ((BooleanSetting) setting).get() ? CategoryUtil.getCategoryColor(parent.getCategory()).brighter() : CategoryUtil.getCategoryColor(parent.getCategory()));
                DrawingUtil.rectangle(((BooleanSetting) setting).get() ? x + 185 : x + 175, y + 23, 9, 9, true, ((BooleanSetting) setting).get() ? colorLighter : new Color(255, 255, 255));
                y += 20;
            }

            if (setting instanceof NumberSetting) {
                NumberSetting numberSetting = (NumberSetting) setting;
                double currentValue = numberSetting.get().doubleValue(), minValue = numberSetting.min().doubleValue(), maxValue = numberSetting.max().doubleValue();
                int decimalPoints = numberSetting.decimalPoints;

                double randoValue = ((currentValue - minValue) / (maxValue - minValue)) * (185 - 6);
                double sliderPosition = x + 5 + randoValue;

                DrawingUtil.rectangle(x, y + 18, width, height + 18, true, new Color(33, 33, 33));

                String formattedValue = String.format(Locale.ROOT, setting.getName() + ": %." + decimalPoints + "f", currentValue);

                fr.drawStringOptional(shadow, formattedValue, (float) x + 4, (float) y + 25, Color.WHITE);

                DrawingUtil.rectangle(x + 5, y + 40, 185, 11, true, new Color(0, 0, 0));
                DrawingUtil.rectangle(x + 5, y + 40, randoValue, 11, true, new Color(60, 60, 60));
                DrawingUtil.rectangle(sliderPosition, y + 40, 6, 11, true, CategoryUtil.getCategoryColor(parent.getCategory()).brighter());
                DrawingUtil.rectangle(x + 5, y + 40, 185, 11, false, CategoryUtil.getCategoryColor(parent.getCategory()));
                y += 38;
            }
            counter++;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        FontRenderer fr = mc.fontRenderer;

        double x = (width - 200) / 2.0, y = 50;

        for (Setting setting : TopHat.settingManager.getSettingsByModule(parent)) {
            if(setting.isHidden())
                continue;

            if (setting instanceof StringSetting) {
                double boxX = x + 192 - fr.getStringWidth(((StringSetting) setting).get()), boxY = y + 22, boxWidth = fr.getStringWidth(((StringSetting) setting).get()) + 3, boxHeight = 11;

                if (mouseX >= boxX && mouseX <= boxX + boxWidth && mouseY >= boxY && mouseY <= boxY + boxHeight) {
                    if (mouseButton == 0) {
                        ((StringSetting) setting).forward();
                    } else if (mouseButton == 1) {
                        ((StringSetting) setting).backward();
                    }
                }

                y += 20;
            } else if (setting instanceof BooleanSetting) {
                double boxX = x + 174, boxY = y + 22, boxWidth = 21, boxHeight = 11;

                if (mouseX >= boxX && mouseX <= boxX + boxWidth && mouseY >= boxY && mouseY <= boxY + boxHeight) {
                    ((BooleanSetting) setting).toggle();
                }

                y += 20;
            } else if(setting instanceof DividerSetting) {
                y += 20;
            } else if (setting instanceof NumberSetting) {
                double boxX = x + 5, boxY = y + 40, boxWidth = 185, boxHeight = 11;
                double sliderWidth = 185;

                if (mouseX >= boxX && mouseX <= boxX + boxWidth && mouseY >= boxY && mouseY <= boxY + boxHeight) {
                    if (mouseButton == 0) {
                        currentDraggingSetting = (NumberSetting) setting;
                        handleSliderDrag(mouseX, boxX, sliderWidth);
                    }
                }

                y += 38;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (currentDraggingSetting != null) {
            double sliderX = (width - 200) / 2.0 + 0.5;
            double sliderWidth = 185;

            if (mouseX >= sliderX && mouseX <= sliderX + sliderWidth) {
                handleSliderDrag(mouseX, sliderX, sliderWidth);
            }
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (currentDraggingSetting != null && state == 0) {
            currentDraggingSetting = null;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dWheel = Mouse.getEventDWheel();

        if (dWheel != 0 && currentDraggingSetting != null) {
            double step = currentDraggingSetting.step();
            double newValue = currentDraggingSetting.get().doubleValue() + step * (dWheel > 0 ? 1 : -1);
            newValue = Math.min(currentDraggingSetting.max().doubleValue(), Math.max(currentDraggingSetting.min().doubleValue(), newValue));
            currentDraggingSetting.set(newValue);
        }

        if (currentDraggingSetting != null && Mouse.isButtonDown(0)) {
            double mouseX = Mouse.getEventX();
            double sliderX = (width - 200) / 2.0 + 5;
            double sliderWidth = 185;

            if (mouseX >= sliderX && mouseX <= sliderX + sliderWidth) {
                handleSliderDrag((int) mouseX, sliderX, sliderWidth);
            }
        }
    }

    private void handleSliderDrag(int mouseX, double sliderX, double sliderWidth) {
        double relativeX = mouseX - sliderX;
        double range = currentDraggingSetting.max().doubleValue() - currentDraggingSetting.min().doubleValue();
        double newValue = currentDraggingSetting.min().doubleValue() + (relativeX / (sliderWidth - 6)) * range;
        newValue = Math.min(currentDraggingSetting.max().doubleValue(), Math.max(currentDraggingSetting.min().doubleValue(), newValue));
        newValue = Math.round(newValue * Math.pow(10, currentDraggingSetting.decimalPoints)) / Math.pow(10, currentDraggingSetting.decimalPoints); // Apply decimal points
        currentDraggingSetting.set(newValue);
    }

    private void renderBlur() {
        if(TopHat.moduleManager.getByClass(PostProcessing.class).isEnabled() && TopHat.moduleManager.getByClass(PostProcessing.class).blurShader.get()) {
            GaussianBlur.startBlur();
            DrawingUtil.rectangle(0, 0, width, height, true, new Color(0,0,0));
            GaussianBlur.endBlur(10, 2);
        }
    }

}
