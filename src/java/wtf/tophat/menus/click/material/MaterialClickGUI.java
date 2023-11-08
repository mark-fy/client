package wtf.tophat.menus.click.material;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import wtf.tophat.TopHat;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.impl.hud.ClickGUI;
import wtf.tophat.modules.impl.render.PostProcessing;
import wtf.tophat.settings.base.Setting;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.DividerSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.render.shaders.blur.GaussianBlur;
import wtf.tophat.utilities.render.CategoryUtil;
import wtf.tophat.utilities.render.DrawingUtil;
import wtf.tophat.utilities.sound.SoundUtil;

import java.awt.*;
import java.util.Locale;

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

    private NumberSetting currentDraggingSetting = null;

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private Module.Category selectedCategory = Module.Category.COMBAT;
    private Module expandedModule = null;

    private double frameX;
    private double frameY;
    private boolean isDragging = false;
    private int dragOffsetX, dragOffsetY;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        FontRenderer fr = mc.fontRenderer;

        renderBlur();

        double width = 350, height = 350, x = frameX, y = frameY;

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
        DrawingUtil.rectangle(x, y, width, height, false, CategoryUtil.getCategoryColor(selectedCategory));

        // Drag Bar
        DrawingUtil.rectangle(x, y + 1, width - 1, 15, true, new Color(30, 30, 30));

        boolean hoveredOverClose = DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) (x + 340), (float) (y + 3), fr.getStringWidth("X"), fr.FONT_HEIGHT);

        fr.drawString("X", (float) (x + 340), (float) y + 3, hoveredOverClose ? CategoryUtil.getCategoryColor(selectedCategory) : Color.WHITE);
        fr.drawString(TopHat.getName().toLowerCase(Locale.ROOT) + " v" + TopHat.getVersion(), (int) (x + 3), (int) (y + 3), CategoryUtil.getCategoryColor(selectedCategory));

        // Category Box
        DrawingUtil.rectangle(x, y + 15, 50, 334, true, new Color(30, 30, 30));

        int adjustment = 10;
        for (Module.Category category : Module.Category.values()) {
            float categoryX = (float) (x + adjustment);
            float categoryY = (float) (y + CategoryUtil.getCategoryY(category));

            boolean isHovered = DrawingUtil.hovered((float) mouseX, (float) mouseY, categoryX - 2, categoryY + 4, 36, 12);

            Color textColor;

            if (isHovered) {
                textColor = Color.LIGHT_GRAY;
            } else if (category == selectedCategory) {
                textColor = CategoryUtil.getCategoryColor(category);
            } else {
                textColor = Color.WHITE;
            }

            fr.drawStringWithShadow(category.getName(), categoryX - 2, categoryY + 4, textColor);
        }

        int counter = 0;
        for (Module module : TopHat.moduleManager.getModulesByCategory(selectedCategory)) {

            boolean isHovered = DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) x + 50, (float) y + 16 + counter, 100, 15);

            Color moduleBackgroundColor = isHovered
                    ? module.isEnabled()
                    ? new Color(55, 55, 55)
                    : new Color(44, 44, 44)
                    : module.isEnabled()
                    ? new Color(44, 44, 44)
                    : new Color(33, 33, 33);

            if(module.equals(expandedModule) && !TopHat.settingManager.getSettingsByModule(expandedModule).isEmpty()) {
                moduleBackgroundColor = new Color(44, 44, 44);
            }

            DrawingUtil.rectangle(x + 50, y + 16 + counter, 100, 15, true, moduleBackgroundColor);
            fr.drawStringWithShadow(module.getName(), (float) (x + 54), (float) (y + 19 + counter), module.isEnabled() ? CategoryUtil.getCategoryColor(selectedCategory) : Color.WHITE);

            if (isHovered) {
                String text = (module.getDesc()).toLowerCase(Locale.ROOT);
                int strWidth = fr.getStringWidth(text) + 3;

                DrawingUtil.rectangle(5, this.height - 35, strWidth + 11, 20, true, new Color(5,5,5));
                DrawingUtil.rectangle(6, this.height - 34, strWidth + 9, 18, true, new Color(60,60,60));
                DrawingUtil.rectangle(7, this.height - 33, strWidth + 7, 16, true, new Color(40,40,40));
                DrawingUtil.rectangle(9, this.height - 31, strWidth + 3, 12, true, new Color(60,60,60));
                DrawingUtil.rectangle(10, this.height - 30, strWidth + 1, 10, true, new Color(22,22,22));
                DrawingUtil.rectangle(10, this.height - 30, strWidth + 1, 1, true, CategoryUtil.getCategoryColor(selectedCategory));

                fr.drawStringWithShadow(text, 11, this.height - 28, Color.WHITE);
            }

            counter += 15;
        }

        int offset = 16;
        for (Setting setting : TopHat.settingManager.getSettingsByModule(expandedModule)) {

            boolean isHovered = DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) x + 150, (float) y + offset, 199, setting instanceof NumberSetting ? 32 : 15);

            Color settingBackgroundColor = isHovered ?
                    new Color(44, 44, 44) :
                    new Color(30, 30, 30);

            if (setting.isHidden()) {
                continue;
            }

            if (setting instanceof DividerSetting) {
                int stringWidth = fr.getStringWidth(setting.getName());
                int centerX = (199 - stringWidth) / 2;
                int textX = (int) (x + 150 + centerX);

                DrawingUtil.rectangle(x + 150, y + offset, 199, 15, true, settingBackgroundColor);
                fr.drawStringWithShadow(setting.getName(), (float) textX, (float) y + offset + 3, Color.WHITE);
                offset += 15;
            } else if (setting instanceof StringSetting) {
                DrawingUtil.rectangle(x + 150, y + offset, 199, 15, true, settingBackgroundColor);
                fr.drawStringWithShadow(setting.getName() + ": " + ((StringSetting) setting).get(), (float) x + 152, (float) y + offset + 3, Color.WHITE);
                offset += 15;
            } else if (setting instanceof BooleanSetting) {
                DrawingUtil.rectangle(x + 150, y + offset, 199, 15, true, settingBackgroundColor);
                fr.drawStringWithShadow(setting.getName() + ": " + ((BooleanSetting) setting).get(), (float) x + 152, (float) y + offset + 3, Color.WHITE);
                offset += 15;
            } else if (setting instanceof NumberSetting) {
                NumberSetting numberSetting = (NumberSetting) setting;
                double currentValue = numberSetting.get().doubleValue(), minValue = numberSetting.min().doubleValue(), maxValue = numberSetting.max().doubleValue();
                int decimalPoints = numberSetting.decimalPoints;

                double randoValue = ((currentValue - minValue) / (maxValue - minValue)) * (185 - 6);
                double sliderPosition = x + 154 + randoValue;

                DrawingUtil.rectangle(x + 150, y + offset, 199, 32, true, settingBackgroundColor);

                String formattedValue = String.format(Locale.ROOT, setting.getName() + ": %." + decimalPoints + "f", currentValue);

                fr.drawStringWithShadow(formattedValue, (float) x + 152, (float) y + offset + 3, Color.WHITE);

                DrawingUtil.rectangle(x + 154, y + offset + 15, 185, 11, true, new Color(0, 0, 0));
                DrawingUtil.rectangle(x + 154, y + offset + 15, randoValue, 11, true, new Color(60, 60, 60));
                DrawingUtil.rectangle(sliderPosition, y + offset + 15, 6, 11, true, CategoryUtil.getCategoryColor(selectedCategory));
                DrawingUtil.rectangle(x + 154, y + offset + 15, 185, 11, false, Color.WHITE);
                offset = handleNumberSetting((NumberSetting) setting, x, y, offset, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        FontRenderer fr = mc.fontRenderer;

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
            for (Module module : TopHat.moduleManager.getModulesByCategory(selectedCategory)) {
                if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) x + 50, (float) y + 16 + counter, 100, 15)) {
                    module.toggle();
                    if(TopHat.moduleManager.getByClass(ClickGUI.class).sound.get()) {
                        SoundUtil.play(!module.isEnabled() ? SoundUtil.toggleOnSound : SoundUtil.toggleOffSound);
                    }
                }
                counter += 15;
            }

            for (Module.Category category : Module.Category.values()) {
                if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) (x + 10), (float) (y + CategoryUtil.getCategoryY(category)), 36, 12)) {
                    selectedCategory = category;
                    expandedModule = null;
                }
            }

            int offset = 16;
            for (Setting setting : TopHat.settingManager.getSettingsByModule(expandedModule)) {
                if (setting.isHidden()) {
                    continue;
                }

                if (setting instanceof DividerSetting) {
                    offset += 15;
                } else if (setting instanceof StringSetting) {
                    if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) (x + 151), (float) (y + offset), 199, 15)) {
                        ((StringSetting) setting).forward();
                    }
                    offset += 15;
                } else if (setting instanceof BooleanSetting) {
                    if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) (x + 151), (float) (y + offset), 199, 15)) {
                        ((BooleanSetting) setting).toggle();
                    }
                    offset += 15;
                } else if (setting instanceof NumberSetting) {
                    offset = handleNumberSettingClick((NumberSetting) setting, x, y, offset, mouseX, mouseY, mouseButton);
                }
            }

            boolean closeClicked = DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) (x + 340), (float) (y + 3), fr.getStringWidth("X"), fr.FONT_HEIGHT);
            if (closeClicked) {
                mc.displayGuiScreen(null);
            }
        }

        if (mouseButton == 1) {
            double x = frameX;
            double y = frameY;

            int counter = 0;
            for (Module module : TopHat.moduleManager.getModulesByCategory(selectedCategory)) {
                if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) x + 50, (float) y + 16 + counter, 100, 15)) {
                    expandedModule = module;
                }
                counter += 15;
            }

            int offset = 15;
            for (Setting setting : TopHat.settingManager.getSettingsByModule(expandedModule)) {
                if (setting.isHidden()) {
                    continue;
                }

                if (setting instanceof DividerSetting) {
                    offset += 15;
                } else if (setting instanceof StringSetting) {
                    if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) (x + 151), (float) (y + offset), 199, 15)) {
                        ((StringSetting) setting).backward();
                    }
                    offset += 15;
                } else if (setting instanceof BooleanSetting) {
                    if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) (x + 151), (float) (y + offset), 199, 15)) {
                        ((BooleanSetting) setting).toggle();
                    }
                    offset += 15;
                } else if (setting instanceof NumberSetting) {
                    offset += 32;
                }
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (currentDraggingSetting != null && Mouse.isButtonDown(0)) {
            double sliderX = frameX + 154;
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

    private void renderBlur() {
        if (TopHat.moduleManager.getByClass(PostProcessing.class).isEnabled() && TopHat.moduleManager.getByClass(PostProcessing.class).blurShader.get()) {
            GaussianBlur.startBlur();
            DrawingUtil.rectangle(0, 0, width, height, true, new Color(0, 0, 0));
            GaussianBlur.endBlur(10, 2);
        }
    }

    private int handleNumberSetting(NumberSetting setting, double x, double y, int offset, int mouseX, int mouseY) {
        double minValue = setting.min().doubleValue();
        double maxValue = setting.max().doubleValue();
        double randoValue = ((setting.get().doubleValue() - minValue) / (maxValue - minValue)) * (185 - 6);
        double sliderPosition = x + 154 + randoValue;

        if(Mouse.isButtonDown(0)) {
            if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) x + 154, (float) y + offset + 15, 185, 11)) {
                if (mouseX < sliderPosition) {
                    currentDraggingSetting = setting;
                    handleSliderDrag(mouseX, x + 154, 185);
                } else if (mouseX > sliderPosition + 6) {
                    currentDraggingSetting = setting;
                    handleSliderDrag(mouseX, x + 154, 185);
                }
            }
        }

        return offset + 32;
    }

    private int handleNumberSettingClick(NumberSetting setting, double x, double y, int offset, int mouseX, int mouseY, int mouseButton) {
        if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) x + 154, (float) y + offset + 15, 185, 11)) {
            if (Mouse.isButtonDown(mouseButton)) {
                currentDraggingSetting = setting;
                handleSliderDrag(mouseX, x + 154, 185);
            }
        }
        return offset + 32;
    }

    private void handleSliderDrag(int mouseX, double sliderX, double sliderWidth) {
        double relativeX = mouseX - sliderX;
        double range = currentDraggingSetting.max().doubleValue() - currentDraggingSetting.min().doubleValue();
        double newValue = currentDraggingSetting.min().doubleValue() + (relativeX / (sliderWidth - 6)) * range;
        newValue = Math.min(currentDraggingSetting.max().doubleValue(), Math.max(currentDraggingSetting.min().doubleValue(), newValue));
        newValue = Math.round(newValue * Math.pow(10, currentDraggingSetting.decimalPoints)) / Math.pow(10, currentDraggingSetting.decimalPoints);
        currentDraggingSetting.set(newValue);
    }
}
