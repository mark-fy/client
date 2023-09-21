package wtf.tophat.screen.cgui;

import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import wtf.tophat.Client;
import wtf.tophat.module.base.Module;
import wtf.tophat.utilities.font.CFontRenderer;
import wtf.tophat.utilities.font.CFontUtil;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;
import java.io.IOException;
import java.util.Locale;

import static wtf.tophat.utilities.Colors.DEFAULT_COLOR;
import static wtf.tophat.utilities.Colors.WHITE_COLOR;

public class ClickGUI extends GuiScreen {

    private Module listeningModule = null;

    @Override
    public boolean doesGuiPauseGame() { return false; }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        CFontRenderer fr = CFontUtil.SF_Regular_20.getRenderer();
        CFontRenderer frBig = CFontUtil.SF_Semibold_20.getRenderer();
        boolean shadow = Client.moduleManager.getByClass(wtf.tophat.module.impl.hud.ClickGUI.class).fontShadow.getValue();

        Color color = new Color(0,85,255);

        double x = (this.width - getTotalCategoryWidth()) / 2.0;
        double y = 50;

        for (Module.Category category : Module.Category.values()) {
            double categoryWidth = getMaxModuleNameWidth(category) + 20;
            double categoryHeight = 20;

            double categoryTextX = x + (categoryWidth - fr.getStringWidth(category.getName().toLowerCase(Locale.ROOT))) / 2 - 2;
            double categoryTextY = y + 6;

            DrawingUtil.rectangle(x, y, categoryWidth, categoryHeight, true, new Color(20, 20, 20));

            frBig.drawStringChoose(shadow, category.getName().toLowerCase(Locale.ROOT), (int) categoryTextX, (int) categoryTextY, Color.WHITE);

            double modX = x, modY = y + categoryHeight, modHeight = 20;

            for (Module module : Client.moduleManager.getModulesByCategory(category)) {

                String keybindText;
                int keybindTextWidth = 0;

                if (module == listeningModule) {
                    keybindText = " [...]";
                    keybindTextWidth = fr.getStringWidth(keybindText);
                } else if (module.getKeyCode() == Keyboard.KEY_NONE) {
                    keybindText = " [NONE]";
                    keybindTextWidth = fr.getStringWidth(keybindText);
                } else {
                    keybindText = " [" + Keyboard.getKeyName(module.getKeyCode()) + "]";
                    keybindTextWidth = fr.getStringWidth(keybindText);
                }

                double moduleRectWidth = categoryWidth + keybindTextWidth;

                // Check if mouse is hovering over the module rectangle
                // Check if mouse is hovering over the module rectangle
                boolean isHovered = mouseX >= modX && mouseX <= modX + categoryWidth && mouseY >= modY && mouseY <= modY + modHeight;

                boolean isNotHoveringOutsideText = mouseX <= modX + categoryWidth;

                Color moduleBackgroundColor = isHovered && isNotHoveringOutsideText
                        ? module.isEnabled()
                        ? new Color(55, 55, 55) // Lighter color for hovered and enabled module
                        : new Color(44, 44, 44) // Lighter color for hovered and disabled module
                        : module.isEnabled()
                        ? new Color(44, 44, 44) // Regular color for enabled module
                        : new Color(33, 33, 33); // Regular color for disabled module


                DrawingUtil.rectangle(modX, modY, categoryWidth, modHeight, true, moduleBackgroundColor);

                // Draw module name
                String moduleName = module.getName().toLowerCase(Locale.ROOT);
                int moduleNameX = (int) modX + 5;

                fr.drawStringChoose(shadow, moduleName, moduleNameX, (int) (modY + 6), module.isEnabled() ? color : Color.WHITE);

                // Calculate the position for keybind text
                int keybindX = (int) (modX + moduleRectWidth - keybindTextWidth - 5);
                int keybindY = (int) modY + 6;

                fr.drawStringChoose(shadow, keybindText, keybindX - keybindTextWidth, keybindY, Color.darkGray);

                if (isHovered) {
                    int counter = 0;

                    String text = (module.getDesc()).toLowerCase(Locale.ROOT);
                    int strWidth = fr.getStringWidth(text) + 3;

                    DrawingUtil.rectangle(5, this.height - 35, strWidth + 11, 20, true, new Color(5,5,5));
                    DrawingUtil.rectangle(6, this.height - 34, strWidth + 9, 18, true, new Color(60,60,60));
                    DrawingUtil.rectangle(7, this.height - 33, strWidth + 7, 16, true, new Color(40,40,40));
                    DrawingUtil.rectangle(9, this.height - 31, strWidth + 3, 12, true, new Color(60,60,60));
                    DrawingUtil.rectangle(10, this.height - 30, strWidth + 1, 10, true, new Color(22,22,22));
                    DrawingUtil.rectangle(10, this.height - 30, strWidth + 1, 1, true, new Color(ColorUtil.fadeBetween(DEFAULT_COLOR, WHITE_COLOR, counter * 150L)));

                    fr.drawStringChoose(shadow, text, 11, this.height - 28, Color.WHITE);
                    counter++;
                }

                modY += 20;
            }

            x += categoryWidth + 15;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        double x = (this.width - getTotalCategoryWidth()) / 2.0;
        double y = 50;

        if (mouseButton == 0 || mouseButton == 1 || mouseButton == 2) {
            for (Module.Category category : Module.Category.values()) {
                double categoryWidth = getMaxModuleNameWidth(category) + 20;
                double categoryHeight = 20;
                double modX = x, modY = y + categoryHeight, modHeight = 20;

                for (Module module : Client.moduleManager.getModulesByCategory(category)) {
                    double modWidth = categoryWidth;

                    if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) modX, (float) modY, (float) modWidth, (float) modHeight)) {
                        if (mouseButton == 0) {
                            module.toggle();
                        } else if (mouseButton == 1) {
                            if (Client.settingManager.getSettingsByModule(module).size() > 0) {
                                mc.displayGuiScreen(new SettingFrame(this, module));
                            }
                        } else if (mouseButton == 2) {
                            listeningModule = module;
                        }
                    }

                    modY += 20;
                }

                x += categoryWidth + 15;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (listeningModule != null) {
            if (keyCode == Keyboard.KEY_SPACE || keyCode == Keyboard.KEY_ESCAPE) {
                listeningModule.setKeyCode(Keyboard.KEY_NONE);
            } else {
                listeningModule.setKeyCode(keyCode);
            }
            listeningModule = null;
        }
    }

    private double getMaxModuleNameWidth(Module.Category category) {
        CFontRenderer fr = CFontUtil.SF_Regular_20.getRenderer();
        double maxModuleNameWidth = 0;

        for (Module module : Client.moduleManager.getModulesByCategory(category)) {
            String keybindText;
            int keybindTextWidth = 0;

            if (module == listeningModule) {
                keybindText = " [...]";
                keybindTextWidth = fr.getStringWidth(keybindText);
            } else if (module.getKeyCode() == Keyboard.KEY_NONE) {
                keybindText = " [NONE]";
                keybindTextWidth = fr.getStringWidth(keybindText);
            } else {
                keybindText = " [" + Keyboard.getKeyName(module.getKeyCode()) + "]";
                keybindTextWidth = fr.getStringWidth(keybindText);
            }

            double moduleNameWidth = fr.getStringWidth(module.getName().toLowerCase(Locale.ROOT)) + keybindTextWidth;
            if (moduleNameWidth > maxModuleNameWidth) {
                maxModuleNameWidth = moduleNameWidth;
            }
        }

        return maxModuleNameWidth;
    }

    private double getTotalCategoryWidth() {
        double totalCategoryWidth = 0;

        for (Module.Category category : Module.Category.values()) {
            double categoryWidth = getMaxModuleNameWidth(category) + 20;
            totalCategoryWidth += categoryWidth;
        }

        return totalCategoryWidth;
    }

}
