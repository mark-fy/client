package wtf.tophat.screen.cgui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import wtf.tophat.Client;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.impl.client.Theme;
import wtf.tophat.utilities.font.CFontRenderer;
import wtf.tophat.utilities.font.CFontUtil;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;
import java.io.IOException;
import java.util.Locale;

import static wtf.tophat.utilities.ColorPallete.DEFAULT_COLOR;
import static wtf.tophat.utilities.ColorPallete.WHITE_COLOR;

public class ClickGUI extends GuiScreen {

    private Module listeningModule = null;

    @Override
    public boolean doesGuiPauseGame() { return false; }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        CFontRenderer fr = CFontUtil.SF_Regular_20.getRenderer();
        CFontRenderer frBig = CFontUtil.SF_Semibold_20.getRenderer();
        boolean shadow = Client.moduleManager.getByClass(wtf.tophat.module.impl.client.ClickGUI.class).fontShadow.get();

        Theme colors = Client.moduleManager.getByClass(Theme.class);
        Color color = colors.clientTheme.get();

        double categoryWidth = 100, categoryHeight = 20;
        double totalCategoryWidth = Module.Category.values().length * (categoryWidth + 15) - 15;

        double x = (this.width - totalCategoryWidth) / 2.0, y = 50;

        for (Module.Category category : Module.Category.values()) {

            DrawingUtil.rectangle(x, y, categoryWidth, categoryHeight, true, new Color(20, 20, 20));

            frBig.drawStringChoose(shadow, category.getName().toLowerCase(Locale.ROOT), (int) (x + (categoryWidth - fr.getStringWidth(category.getName().toLowerCase(Locale.ROOT))) / 2) - 2, (int) y + 6, Color.WHITE);

            double modX = x, modY = y + categoryHeight, modWidth = 100, modHeight = 20;

            for (Module module : Client.moduleManager.getModulesByCategory(category)) {
                Color moduleColor = module.isEnabled() ? new Color(44, 44, 44) : new Color(33, 33, 33);
                DrawingUtil.rectangle(modX, modY, modWidth, modHeight, true, moduleColor);

                String moduleName = module.getName().toLowerCase(Locale.ROOT);
                int moduleNameX = (int) modX + 5;

                fr.drawStringChoose(shadow, moduleName, moduleNameX, (int) (modY + 6), module.isEnabled() ? color : Color.WHITE);

                String keybindText;

                if (module == listeningModule) {
                    keybindText = " [...]";
                } else if (module.getKeyCode() == Keyboard.KEY_NONE) {
                    keybindText = " [NONE]";
                } else {
                    keybindText = " [" + Keyboard.getKeyName(module.getKeyCode()) + "]";
                }

                int keybindX = (int) (modX + modWidth - 5 - fr.getStringWidth(keybindText)), keybindY = (int) modY + 6;

                fr.drawStringChoose(shadow, keybindText, keybindX, keybindY, Color.darkGray);

                if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) modX, (float) modY, (float) modWidth, (float) modHeight)) {
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
        double categoryWidth = 100, categoryHeight = 20;

        if (mouseButton == 0) {
            double x = (this.width - Module.Category.values().length * (categoryWidth + 15) + 15) / 2.0;
            double y = 50;

            for (Module.Category category : Module.Category.values()) {
                double modX = x, modY = y + categoryHeight, modWidth = 100, modHeight = 20;

                for (Module module : Client.moduleManager.getModulesByCategory(category)) {
                    if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) modX, (float) modY, (float) modWidth, (float) modHeight)) {
                        module.toggle();
                    }

                    modY += 20;
                }

                x += categoryWidth + 15;
            }
        } else if (mouseButton == 1) {
            double x = (this.width - Module.Category.values().length * (categoryWidth + 15) + 15) / 2.0;
            double y = 50;

            for (Module.Category category : Module.Category.values()) {
                double modX = x, modY = y + categoryHeight, modWidth = 100, modHeight = 20;

                for (Module module : Client.moduleManager.getModulesByCategory(category)) {
                    if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) modX, (float) modY, (float) modWidth, (float) modHeight)) {
                        if (Client.settingManager.getSettingsByModule(module).size() < 1)
                            return;

                        mc.displayGuiScreen(new SettingFrame(this, module));
                    }

                    modY += 20;
                }

                x += categoryWidth + 15;
            }
        } else if (mouseButton == 2) {
            double x = (this.width - Module.Category.values().length * (categoryWidth + 15) + 15) / 2.0;
            double y = 50;

            for (Module.Category category : Module.Category.values()) {
                double modX = x, modY = y + categoryHeight, modWidth = 100, modHeight = 20;

                for (Module module : Client.moduleManager.getModulesByCategory(category)) {
                    if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) modX, (float) modY, (float) modWidth, (float) modHeight)) {
                        listeningModule = module;
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

}
