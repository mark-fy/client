package wtf.tophat.menus.click.dropdown;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import wtf.tophat.Client;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.impl.render.PostProcessing;
import wtf.tophat.utilities.render.shaders.blur.GaussianBlur;
import wtf.tophat.utilities.render.CategoryUtil;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;
import java.io.IOException;
import java.util.Locale;

import static wtf.tophat.utilities.render.Colors.DEFAULT_COLOR;
import static wtf.tophat.utilities.render.Colors.WHITE_COLOR;

@SuppressWarnings({"ConstantValue", "UnusedAssignment"})
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
        FontRenderer fr = mc.fontRenderer;
        boolean shadow = Client.moduleManager.getByClass(wtf.tophat.modules.impl.hud.ClickGUI.class).fontShadow.get();

        renderBlur();

        double x = (this.width - CategoryUtil.getTotalCategoryWidth(listeningModule)) / 2.0 - 40, y = 50;

        for (Module.Category category : Module.Category.values()) {
            double categoryWidth = CategoryUtil.getMaxModuleNameWidth(category, listeningModule) + 20, categoryHeight = 20;

            double categoryTextX = x + (categoryWidth - fr.getStringWidth(category.getName().toLowerCase(Locale.ROOT))) / 2 - 2, categoryTextY = y + 6;

            double dropdownMinX = x, dropdownMaxX = x + categoryWidth, dropdownMaxY = y + categoryHeight;

            DrawingUtil.rectangle(dropdownMinX, y, dropdownMaxX - dropdownMinX, dropdownMaxY - y, false, CategoryUtil.getCategoryColor(category));
            DrawingUtil.rectangle(x, y, categoryWidth, categoryHeight, true, new Color(20, 20, 20));

            fr.drawStringOptional(shadow, category.getName().toLowerCase(Locale.ROOT), (float) categoryTextX, (float) categoryTextY, Color.WHITE);

            double modX = x, modY = y + categoryHeight, modHeight = 20;

            for (Module module : Client.moduleManager.getModulesByCategory(category)) {

                String keybindText;
                int keybindTextWidth;

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
                Color moduleBackgroundColor = isHovered
                        ? module.isEnabled()
                        ? new Color(55, 55, 55)
                        : new Color(44, 44, 44)
                        : module.isEnabled()
                        ? new Color(44, 44, 44)
                        : new Color(33, 33, 33);

                DrawingUtil.rectangle(modX, modY, categoryWidth, modHeight, true, moduleBackgroundColor);

                String moduleName = module.getName().toLowerCase(Locale.ROOT);

                fr.drawStringOptional(shadow, moduleName, (float) (modX + 5), (float) (modY + 6), module.isEnabled() ? CategoryUtil.getCategoryColor(category) : Color.WHITE);

                fr.drawStringOptional(shadow, keybindText, (float) ((modX + moduleRectWidth - keybindTextWidth - 5) - keybindTextWidth), (float) (modY + 6), Color.darkGray);

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

                    fr.drawStringOptional(shadow, text, 11, this.height - 28, Color.WHITE);
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
                double modX = x, modY = y + 20;

                for (Module module : Client.moduleManager.getModulesByCategory(category)) {

                    if (DrawingUtil.hovered((float) mouseX, (float) mouseY, (float) modX, (float) modY, (float) categoryWidth, 20)) {
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
        if(Client.moduleManager.getByClass(PostProcessing.class).isEnabled() && Client.moduleManager.getByClass(PostProcessing.class).blurShader.get()) {
            GaussianBlur.startBlur();
            DrawingUtil.rectangle(0, 0, width, height, true, new Color(0,0,0));
            GaussianBlur.endBlur(10, 2);
        }
    }

}
