package wtf.tophat.menus.click.beta;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;
import wtf.tophat.Client;
import wtf.tophat.modules.base.Module;
import wtf.tophat.settings.base.Setting;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.DividerSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.Methods;
import wtf.tophat.utilities.render.CategoryUtil;
import wtf.tophat.utilities.render.DrawingUtil;
import wtf.tophat.utilities.render.shaders.RoundedUtil;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class BetaClickGUI extends GuiScreen implements Methods {

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }

    private Module.Category listeningToCategory = Module.Category.COMBAT;
    private Module listeningToModule = null;
    private int firstVisibleModule = 0;

    private boolean isDragging = false;
    private int dragX, dragY;
    private float x = 50, y = 50;


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontRenderer fr = Methods.mc.fontRenderer;

        float width = 300, height = 285;

        if (isDragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }

        RoundedUtil.drawRound(x, y, width, height, 8, new Color(30, 30, 30));
        fr.drawString("TopHat", x + 10, y + 10, Color.WHITE);
        fr.drawString(Client.getVersion(), x + 15, y + 25, Color.WHITE);

        RoundedUtil.drawRound(x + 50, y + 15, 2, 260, 2, new Color(40, 40, 40));

        int categoryOffset = 40;
        for(Module.Category category : Module.Category.values()) {
            boolean hoveredCat = DrawingUtil.hovered(mouseX, mouseY, x + 10, y + categoryOffset, 32, 32);
            RoundedUtil.drawRound(hoveredCat ? x + 12 : category == listeningToCategory ? x + 12 : x + 10, y + categoryOffset, 32, 32, 8, CategoryUtil.getCategoryColor(category));
            //fr.drawString(category.getName(), x + 10, y + categoryOffset + 10, Color.WHITE);

            categoryOffset += 40;
        }

        int moduleOffset = 20;

        List<Module> modules = Client.moduleManager.getModulesByCategory(listeningToCategory);
        int maxVisibleModules = getMaxVisibleModules();

        for (int i = firstVisibleModule; i < firstVisibleModule + maxVisibleModules && i < modules.size(); i++) {
            Module module = modules.get(i);
            float modX = x + 60, modY = y + moduleOffset;
            boolean hoveredMod = DrawingUtil.hovered(mouseX, mouseY, modX, modY, 232, 20);

            RoundedUtil.drawRound(modX, modY, 232, 20, 4, hoveredMod ? new Color(38,38,38) : new Color(36,36,36));
            fr.drawString(module.getName(), x + 65, modY + 7, Color.WHITE);
            fr.drawString("X", modX + 232 - 10, modY + 7, module.isEnabled() ? Color.GREEN : Color.RED);

            moduleOffset += 25;

            if(listeningToModule == module && Client.settingManager.getSettingsByModule(listeningToModule).size() != 0) {
                RoundedUtil.drawRound(x + 305, y, 200, 285, 8, new Color(30,30,30));
                RoundedUtil.drawRound(x + 307, y + 2, 196, 281, 8, new Color(36,36,36));

                int settingOffset = 20;

                for(Setting setting : Client.settingManager.getSettingsByModule(listeningToModule)) {
                    if(setting instanceof DividerSetting) {
                        RoundedUtil.drawRound(x + 320, y + settingOffset + 7, 35, 2, 2, Color.WHITE);
                        RoundedUtil.drawRound(x + fr.getStringWidth(setting.getName()) + 375, y + settingOffset + 7, 35, 2, 2, Color.WHITE);
                        fr.drawString(setting.getName(), x + 370, y + 5 + settingOffset, Color.WHITE);
                        settingOffset += 20;
                    } else if(setting instanceof StringSetting) {
                        fr.drawString(setting.getName() + ": " + ((StringSetting) setting).get(), x + 310, y + 5 + settingOffset, Color.WHITE);
                        settingOffset += 20;
                    } else if(setting instanceof NumberSetting) {
                        fr.drawString(setting.getName() + ": " + ((NumberSetting) setting).get(), x + 310, y + 5 + settingOffset, Color.WHITE);
                        settingOffset += 20;
                    }else if(setting instanceof BooleanSetting) {
                        fr.drawString(setting.getName(), x + 310, y + 5 + settingOffset, Color.WHITE);
                        fr.drawString("X", x + 490, y + 5 + settingOffset, ((BooleanSetting) setting).get() ? Color.GREEN : Color.RED);
                        settingOffset += 15;
                    }
                }
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        float width = 300, height = 285;

        boolean isInsideMainRoundBox = DrawingUtil.hovered(mouseX, mouseY, x, y, width, height);

        if (isInsideMainRoundBox && mouseButton == 0) {
            isDragging = true;
            dragX = (int) (mouseX - x);
            dragY = (int) (mouseY - y);
        }

        int categoryOffset = 40;
        for (Module.Category category : Module.Category.values()) {
            boolean hoveredCat = DrawingUtil.hovered(mouseX, mouseY, x + 10, y + categoryOffset, 32, 32);
            if (hoveredCat && mouseButton == 0) {
                listeningToCategory = category;
            }

            categoryOffset += 40;
        }

        int moduleOffset = 20;
        List<Module> modules = Client.moduleManager.getModulesByCategory(listeningToCategory);
        int maxVisibleModules = getMaxVisibleModules();

        for (int i = firstVisibleModule; i < firstVisibleModule + maxVisibleModules && i < modules.size(); i++) {
            Module module = modules.get(i);
            float modX = x + 60, modY = y + moduleOffset;
            boolean hoveredMod = DrawingUtil.hovered(mouseX, mouseY, modX, modY, 232, 20);

            if (hoveredMod) {
                if (mouseButton == 0) {
                    module.toggle();
                } else if (mouseButton == 1) {
                    if (listeningToModule == module) {
                        listeningToModule = null;
                    } else {
                        listeningToModule = module;
                    }
                }
            }
            moduleOffset += 25;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (isDragging && state == 0) { // Check for left-click (mouse button 0)
            x = mouseX - dragX;
            y = mouseY - dragY;
        }

        isDragging = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void handleMouseInput() throws IOException {

        int scroll = Integer.signum(Mouse.getEventDWheel());

        if (scroll != 0) {
            firstVisibleModule = Math.max(0, Math.min(firstVisibleModule - scroll, Client.moduleManager.getModulesByCategory(listeningToCategory).size() - getMaxVisibleModules()));
        }
        super.handleMouseInput();
    }

    private int getMaxVisibleModules() {
        int availableHeight = 255;
        int moduleHeight = 25;
        return availableHeight / moduleHeight;
    }
}
