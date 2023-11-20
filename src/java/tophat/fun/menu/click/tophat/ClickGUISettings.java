package tophat.fun.menu.click.tophat;

import net.minecraft.client.gui.GuiScreen;
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
import tophat.fun.utilities.render.RenderUtil;
import tophat.fun.utilities.render.RoundUtil;

import java.awt.*;
import java.io.IOException;

public class ClickGUISettings extends GuiScreen {

    private final static TTFFontRenderer poppins = CFont.FONT_MANAGER.getFont("PoppinsMedium 18");
    private final static TTFFontRenderer poppinsR = CFont.FONT_MANAGER.getFont("PoppinsRegular 18");
    private final static TTFFontRenderer checkmark = CFont.FONT_MANAGER.getFont("RegularIcons2 18");
    private final static TTFFontRenderer arrows = CFont.FONT_MANAGER.getFont("ArrowIcons 18");

    private final GuiScreen screenParent;
    private final Module parent;

    private int startIndex = 0;
    private final int maxSettingsDisplayed = 5;

    public ClickGUISettings(GuiScreen screenParent, Module parent) {
        this.screenParent = screenParent;
        this.parent = parent;
    }

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
        float x = (float) this.width / 2 - 100;
        float y = (float) this.height / 2 - 100;
        float width = 200;
        float height = 200;

        RoundUtil.round(x - 1, y - 1, width + 2, height + 2, 8, new Color(24, 175, 162));
        RoundUtil.round(x, y, width, height, 8, new Color(25, 25, 25));
        RoundUtil.round(x, y, width, 20, 8, new Color(19, 19, 19));
        poppins.drawString(parent.getName(), x + 5, y + 5, -1);

        float offset = 20;
        int settingsDisplayed = 0;

        for (int i = startIndex; i < Client.INSTANCE.settingManager.getSettingsByModule(parent).size(); i++) {
            Setting setting = Client.INSTANCE.settingManager.getSettingsByModule(parent).get(i);

            if (setting.isHidden()) continue;

            if (setting instanceof BooleanSetting) {
                boolean hover = RenderUtil.hovered(mouseX, mouseY, x + 180 + 1, y + offset + 5, 10, 10);
                poppinsR.drawString(setting.getName(), x + 5, y + offset + 5, -1);
                Color bg = new Color(24, 175, 162);
                if(hover) {
                    bg = new Color(29, 201, 185);
                }
                RoundUtil.round(x + 180, y + offset + 4, 12, 12, 6, bg);
                RoundUtil.round(x + 180 + 1, y + offset + 5, 10, 10, 4, ((BooleanSetting) setting).get() ? bg : new Color(25, 25, 25));
                checkmark.drawString(((BooleanSetting) setting).get() ? "g" : "", x + 180 + 1, y + offset + 7.5f, -1);
                offset += 20;
            } else if (setting instanceof StringSetting) {
                float settingWidth = poppinsR.getWidth(((StringSetting) setting).get());
                boolean hoverRight = RenderUtil.hovered(mouseX, mouseY, x + settingWidth + 120 + settingWidth, y + offset + 5.5, 10, 10);
                boolean hoverLeft = RenderUtil.hovered(mouseX, mouseY, x + settingWidth + 109.5, y + offset + 5.5, 10, 10);

                poppinsR.drawString(setting.getName(), x + 5, y + offset + 5, -1);
                arrows.drawString("2", x + settingWidth + 121 + settingWidth, y + offset + 7.5f, hoverRight ? Color.LIGHT_GRAY.getRGB() : -1);
                arrows.drawString("Y", x + settingWidth + 110, y + offset + 8, hoverLeft ? Color.LIGHT_GRAY.getRGB() : -1);
                poppinsR.drawString(((StringSetting) setting).get(), x + poppinsR.getWidth(((StringSetting) setting).get()) + 120, y + offset + 5, -1);
                offset += 20;
            } else if (setting instanceof NumberSetting) {
                float value = ((NumberSetting) setting).get().floatValue();
                poppinsR.drawString(setting.getName() + ": " + value, x + 5, y + offset + 5, -1);
                RoundUtil.round(x + 3, y + offset + 25,190 + 2, 5, 2, new Color(24, 175, 162));
                RoundUtil.round(x + 4, y + offset + 26,190, 3, 1, new Color(25, 25, 25));
                float min = ((NumberSetting) setting).min().floatValue();
                float max = ((NumberSetting) setting).max().floatValue();
                float fillWidth = Math.min(Math.max((value - min) / (max - min), 0), 1) * 190;
                RoundUtil.round(x + 4, y + offset + 26, fillWidth, 3, 1, new Color(31, 227, 207));
                RoundUtil.round(x + fillWidth, y + offset + 22, 5, 11, 1, new Color(105, 105, 105));
                RoundUtil.round(x + fillWidth + 1, y + offset + 22.5, 3, 10, 1, new Color(255, 255, 255));

                if (RenderUtil.hovered(mouseX, mouseY, x + 4, y + offset + 22, 190, 15)) {
                    if (Mouse.isButtonDown(0)) {
                        double normalizedX = (mouseX - (x + 4)) / 190.0;
                        double newValue = min + normalizedX * (max - min);
                        int decimalPoints = ((NumberSetting) setting).decimalPoints;
                        newValue = MathUtil.round(newValue, decimalPoints);
                        ((NumberSetting) setting).set(newValue);
                    }
                }
                offset += 35;
            }

            settingsDisplayed++;

            if (settingsDisplayed >= maxSettingsDisplayed) {
                break;
            }
        }

        int scroll = Mouse.getDWheel();
        if (scroll != 0) {
            startIndex -= Integer.signum(scroll);
            startIndex = Math.max(0, Math.min(startIndex, Client.INSTANCE.settingManager.getSettingsByModule(parent).size() - maxSettingsDisplayed));
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        float x = (float) this.width / 2 - 100;
        float y = (float) this.height / 2 - 100;

        float offset = 20;
        int settingsDisplayed = 0;

        for (int i = startIndex; i < Client.INSTANCE.settingManager.getSettingsByModule(parent).size(); i++) {
            Setting setting = Client.INSTANCE.settingManager.getSettingsByModule(parent).get(i);

            if (setting.isHidden()) continue;

            if(setting instanceof BooleanSetting) {
                boolean hover = RenderUtil.hovered(mouseX, mouseY, x + 180 + 1, y + offset + 5, 10, 10);
                if(hover && mouseButton == 0) {
                    ((BooleanSetting) setting).toggle();
                }
                offset += 20;
            } else if(setting instanceof StringSetting) {
                float settingWidth = poppinsR.getWidth(((StringSetting) setting).get());
                boolean hoverRight = RenderUtil.hovered(mouseX, mouseY, x + settingWidth + 120 + settingWidth, y + offset + 5.5, 10, 10);
                boolean hoverLeft = RenderUtil.hovered(mouseX, mouseY, x + settingWidth + 109.5, y + offset + 5.5, 10, 10);

                if(hoverRight && mouseButton == 0) {
                    ((StringSetting) setting).forward();
                }

                if(hoverLeft && mouseButton == 0) {
                    ((StringSetting) setting).backward();
                }

                offset += 20;
            } else if(setting instanceof NumberSetting) {
                offset += 35;
            }

            settingsDisplayed++;

            if (settingsDisplayed >= maxSettingsDisplayed) {
                break;
            }
        }

        int scroll = Mouse.getDWheel();
        if (scroll != 0) {
            startIndex -= Integer.signum(scroll);
            startIndex = Math.max(0, Math.min(startIndex, Client.INSTANCE.settingManager.getSettingsByModule(parent).size() - maxSettingsDisplayed));
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1 && screenParent != null) {
            mc.displayGuiScreen(screenParent);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

}
