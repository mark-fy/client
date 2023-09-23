package wtf.tophat.screen.cgui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;
import wtf.tophat.Client;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.impl.hud.ClickGUI;
import wtf.tophat.module.impl.render.PostProcessing;
import wtf.tophat.settings.base.Setting;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.shader.GaussianBlur;
import wtf.tophat.utilities.font.CFontRenderer;
import wtf.tophat.utilities.font.CFontUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;
import java.io.IOException;
import java.util.Locale;

public class SettingFrame extends GuiScreen {

    private NumberSetting currentDraggingSetting = null;
    private final GuiScreen screenParent;
    private final Module parent;

    public SettingFrame(GuiScreen screenParent, Module parent) {
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
        CFontRenderer fr = CFontUtil.SF_Regular_20.getRenderer();
        CFontRenderer frBig = CFontUtil.SF_Semibold_20.getRenderer();
        boolean shadow = Client.moduleManager.getByClass(ClickGUI.class).fontShadow.getValue();

        if(Client.moduleManager.getByClass(PostProcessing.class).blurShader.getValue()) {
            GaussianBlur.startBlur();
            DrawingUtil.rectangle(0, 0, width, height, true, new Color(0,0,0));
            GaussianBlur.endBlur(10, 2);
        }

        Color color = new Color(0,85,255);

        double x = (width - 200) / 2.0, y = 50;
        double width = 200;
        double height = 20;

        DrawingUtil.rectangle(x, y, width, height, true, new Color(20,20,20));

        frBig.drawStringChoose(shadow, parent.getName().toLowerCase(Locale.ROOT), (int) x + 5, (int) y + 5, Color.WHITE);
        for(Setting setting : Client.settingManager.getSettingsByModule(parent)) {
            if(setting.isHidden())
                continue;

            if(setting instanceof StringSetting) {
                DrawingUtil.rectangle(x, y + 18, width, height, true, new Color(33,33,33));
                fr.drawStringChoose(shadow,setting.getName().toLowerCase(Locale.ROOT) + ": ", (int) x + 5, (int) y + 25, Color.WHITE);
                drawBox(fr,((StringSetting) setting).getValue().toLowerCase(Locale.ROOT), x + 187 - fr.getStringWidth(((StringSetting) setting).getValue()), y + 22, fr.getStringWidth(((StringSetting) setting).getValue()) + 3, 11, color, shadow);
                y += 20;
            }

            if(setting instanceof BooleanSetting) {
                DrawingUtil.rectangle(x, y + 18, width, height, true, new Color(33,33,33));
                fr.drawStringChoose(shadow,setting.getName().toLowerCase(Locale.ROOT) + ": ", (int) x + 4, (int) y + 25, Color.WHITE);
                drawBoxSmaller(fr, ((BooleanSetting) setting).getValue() ? "ON" : "OFF", x + 175, y + 22, fr.getStringWidth(((BooleanSetting) setting).getValue() ? "ON" : "OFF") + 3, 11, color, shadow);
                y += 20;
            }

            if (setting instanceof NumberSetting) {
                NumberSetting numberSetting = (NumberSetting) setting;
                double currentValue = numberSetting.getValue().doubleValue(), minValue = numberSetting.getMinimum().doubleValue(), maxValue = numberSetting.getMaximum().doubleValue();
                int decimalPoints = numberSetting.decimalPoints;

                double randoValue = ((currentValue - minValue) / (maxValue - minValue)) * (185 - 6);
                double sliderPosition = x + 5 + randoValue;

                DrawingUtil.rectangle(x, y + 18, width, height + 18, true, new Color(33, 33, 33));

                String formattedValue = String.format(Locale.ROOT, setting.getName() + ": %." + decimalPoints + "f", currentValue);

                fr.drawStringChoose(shadow, formattedValue.toLowerCase(Locale.ROOT), (int) x + 4, (int) y + 25, Color.WHITE);

                DrawingUtil.rectangle(x + 5, y + 40, 185, 11, true, new Color(0, 0, 0));
                DrawingUtil.rectangle(x + 5, y + 40, randoValue, 11, true, new Color(60, 60, 60));
                DrawingUtil.rectangle(sliderPosition, y + 40, 6, 11, true, new Color(125, 125, 125));
                DrawingUtil.rectangle(x + 5, y + 40, 185, 11, false, color);
                y += 38;
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        FontRenderer fr = mc.fontRenderer;

        double x = (width - 200) / 2.0, y = 50;

        for (Setting setting : Client.settingManager.getSettingsByModule(parent)) {
            if (setting instanceof StringSetting) {
                double boxX = x + 187 - fr.getStringWidth(((StringSetting) setting).getValue()), boxY = y + 22, boxWidth = fr.getStringWidth(((StringSetting) setting).getValue()) + 3, boxHeight = 11;

                if (mouseX >= boxX && mouseX <= boxX + boxWidth && mouseY >= boxY && mouseY <= boxY + boxHeight) {
                    if (mouseButton == 0) {
                        ((StringSetting) setting).forward();
                    } else if (mouseButton == 1) {
                        ((StringSetting) setting).backward();
                    }
                }

                y += 20;
            } else if (setting instanceof BooleanSetting) {
                double boxX = x + 179, boxY = y + 22, boxWidth = 11, boxHeight = 11;

                if (mouseX >= boxX && mouseX <= boxX + boxWidth && mouseY >= boxY && mouseY <= boxY + boxHeight) {
                    ((BooleanSetting) setting).toggle();
                }

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

                y += 40;
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
            double newValue = currentDraggingSetting.getValue().doubleValue() + step * (dWheel > 0 ? 1 : -1);
            newValue = Math.min(currentDraggingSetting.getMaximum().doubleValue(), Math.max(currentDraggingSetting.getMinimum().doubleValue(), newValue));
            currentDraggingSetting.setValue(newValue);
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

    private void drawBox(CFontRenderer fr, String text, double x, double y, double width, double height, Color color, boolean shadow) {
        DrawingUtil.rectangle(x, y, width, height, true, new Color(20,20,20));
        DrawingUtil.rectangle(x, y, width, height, false, color);
        fr.drawStringChoose(shadow, text, (int) x + 1, (int) y + 2, Color.WHITE);
    }

    private void drawBoxSmaller(CFontRenderer fr, String text, double x, double y, double width, double height, Color color, boolean shadow) {
        DrawingUtil.rectangle(x, y, width, height, true, new Color(20,20,20));
        DrawingUtil.rectangle(x, y, width, height, false, color);
        fr.drawStringChoose(shadow, text, (int) x, (int) y + 2, Color.WHITE);
    }

    private void handleSliderDrag(int mouseX, double sliderX, double sliderWidth) {
        double relativeX = mouseX - sliderX;
        double range = currentDraggingSetting.getMaximum().doubleValue() - currentDraggingSetting.getMinimum().doubleValue();
        double newValue = currentDraggingSetting.getMinimum().doubleValue() + (relativeX / (sliderWidth - 6)) * range;
        newValue = Math.min(currentDraggingSetting.getMaximum().doubleValue(), Math.max(currentDraggingSetting.getMinimum().doubleValue(), newValue));
        newValue = Math.round(newValue * Math.pow(10, currentDraggingSetting.decimalPoints)) / Math.pow(10, currentDraggingSetting.decimalPoints); // Apply decimal points
        currentDraggingSetting.setValue(newValue);
    }

}
