package wtf.tophat.screen.click.dropdown;

import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import wtf.tophat.Client;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.impl.render.PostProcessing;
import wtf.tophat.shader.blur.GaussianBlur;
import wtf.tophat.utilities.font.CFontRenderer;
import wtf.tophat.utilities.font.CFontUtil;
import wtf.tophat.utilities.render.CategoryUtil;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;
import java.io.IOException;
import java.util.Locale;

import static wtf.tophat.utilities.Colors.DEFAULT_COLOR;
import static wtf.tophat.utilities.Colors.WHITE_COLOR;

public class DropDownClickGUI extends GuiScreen {

    private Module listeningModule = null;

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        CFontRenderer fr = CFontUtil.SF_Regular_20.getRenderer();
        CFontRenderer frSemiBold = CFontUtil.SF_Semibold_20.getRenderer();
        CFontRenderer catfr = CFontUtil.ICONS_24.getRenderer();
        boolean shadow = Client.moduleManager.getByClass(wtf.tophat.module.impl.hud.ClickGUI.class).fontShadow.getValue();

        renderBlur();

        double x = (this.width - CategoryUtil.getTotalCategoryWidth(listeningModule)) / 2.0 - 40;
        double y = 50;

        for (Module.Category category : Module.Category.values()) {
            double categoryWidth = CategoryUtil.getMaxModuleNameWidth(category, listeningModule) + 20, categoryHeight = 20;

            double categoryTextX = x + (categoryWidth - fr.getStringWidth(category.getName().toLowerCase(Locale.ROOT))) / 2 - 2;
            double categoryTextY = y + 6;

            double categoryIconX = CategoryUtil.calculateCategoryIconX(category, x, categoryWidth, catfr);

            double dropdownMinX = x, dropdownMaxX = x + categoryWidth, dropdownMaxY = y + categoryHeight;

            DrawingUtil.rectangle(dropdownMinX, y, dropdownMaxX - dropdownMinX, dropdownMaxY - y, false, CategoryUtil.getCategoryColor(category));

            DrawingUtil.rectangle(x, y, categoryWidth, categoryHeight, true, new Color(20, 20, 20));

            catfr.drawStringWithShadow(CategoryUtil.getCategoryLetter(category), categoryIconX, categoryTextY, CategoryUtil.getCategoryColor(category));
            frSemiBold.drawStringChoose(shadow, category.getName().toLowerCase(Locale.ROOT), (int) categoryTextX, (int) categoryTextY, Color.WHITE);

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

                String moduleName = module.getName().toLowerCase(Locale.ROOT);
                int moduleNameX = (int) modX + 5;

                fr.drawStringChoose(shadow, moduleName, moduleNameX, (int) (modY + 6), module.isEnabled() ? CategoryUtil.getCategoryColor(category) : Color.WHITE);

                int keybindX = (int) (modX + moduleRectWidth - keybindTextWidth - 5), keybindY = (int) modY + 6;

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

            dropdownMaxY = modY;

            DrawingUtil.rectangle(dropdownMinX, y, dropdownMaxX - dropdownMinX, dropdownMaxY - y, false, CategoryUtil.getCategoryColor(category));

            x += categoryWidth + 15;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        double x = (this.width - CategoryUtil.getTotalCategoryWidth(listeningModule)) / 2.0 - 40;
        double y = 50;

        if (mouseButton == 0 || mouseButton == 1 || mouseButton == 2) {
            for (Module.Category category : Module.Category.values()) {
                double categoryWidth = CategoryUtil.getMaxModuleNameWidth(category, listeningModule) + 20;
                double categoryHeight = 20;
                double modX = x, modY = y + categoryHeight, modHeight = 20;

                for (Module module : Client.moduleManager.getModulesByCategory(category)) {

                    if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) modX, (float) modY, (float) categoryWidth, (float) modHeight)) {
                        if (mouseButton == 0) {
                            module.toggle();
                        } else if (mouseButton == 1) {
                            if (Client.settingManager.getSettingsByModule(module).size() > 0) {
                                mc.displayGuiScreen(new DropDownSettingFrame(this, module));
                            }
                        } else {
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

    private void renderBlur() {
        if(Client.moduleManager.getByClass(PostProcessing.class).isEnabled() && Client.moduleManager.getByClass(PostProcessing.class).blurShader.getValue()) {
            GaussianBlur.startBlur();
            DrawingUtil.rectangle(0, 0, width, height, true, new Color(0,0,0));
            GaussianBlur.endBlur(10, 2);
        }
    }

}
