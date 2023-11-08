package wtf.tophat.client.menus.click.beta;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.impl.hud.ClickGUI;
import wtf.tophat.client.settings.base.Setting;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.DividerSetting;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.settings.impl.StringSetting;
import wtf.tophat.client.utilities.Methods;
import wtf.tophat.client.utilities.render.CategoryUtil;
import wtf.tophat.client.utilities.render.DrawingUtil;
import wtf.tophat.client.utilities.render.shaders.RoundedUtil;
import wtf.tophat.client.utilities.sound.SoundUtil;

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
        fr.drawString(TopHat.getVersion(), x + 15, y + 25, Color.WHITE);

        RoundedUtil.drawRound(x + 50, y + 15, 2, 260, 2, new Color(40, 40, 40));

        int categoryOffset = 40;
        for(Module.Category category : Module.Category.values()) {
            boolean hoveredCat = DrawingUtil.hovered(mouseX, mouseY, x + 10, y + categoryOffset, 32, 32);
            RoundedUtil.drawRound(hoveredCat ? x + 12 : category == listeningToCategory ? x + 12 : x + 10, y + categoryOffset, 32, 32, 8, CategoryUtil.getCategoryColor(category));
            //fr.drawString(category.getName(), x + 10, y + categoryOffset + 10, Color.WHITE);

            categoryOffset += 40;
        }

        int moduleOffset = 20;

        List<Module> modules = TopHat.moduleManager.getModulesByCategory(listeningToCategory);
        int maxVisibleModules = getMaxVisibleModules();

        for (int i = firstVisibleModule; i < firstVisibleModule + maxVisibleModules && i < modules.size(); i++) {
            Module module = modules.get(i);
            if(module.isHidden())
                continue;
            float modX = x + 60, modY = y + moduleOffset;
            boolean hoveredMod = DrawingUtil.hovered(mouseX, mouseY, modX, modY, 232, 20);

            RoundedUtil.drawRound(modX, modY, 232, 20, 4, hoveredMod ? new Color(38,38,38) : new Color(36,36,36));
            fr.drawString(module.getName(), x + 65, modY + 7, listeningToModule == module ? Color.LIGHT_GRAY : Color.WHITE);
            fr.drawString("X", modX + 232 - 10, modY + 7, module.isEnabled() ? Color.GREEN : Color.RED);

            moduleOffset += 25;

            if(listeningToModule == module && TopHat.settingManager.getSettingsByModule(listeningToModule).size() != 0) {
                RoundedUtil.drawRound(x + 305, y, 200, 285, 8, new Color(30,30,30));

                int settingOffset = 5;

                for(Setting setting : TopHat.settingManager.getSettingsByModule(listeningToModule)) {
                    if(setting.isHidden())
                        continue;

                    if(setting instanceof DividerSetting) {
                        fr.drawString(setting.getName(), x + 370, y + 5 + settingOffset, Color.WHITE);
                        settingOffset += 20;
                    } else if(setting instanceof StringSetting) {
                        RoundedUtil.drawRound(x + 310, y + settingOffset + 1, 190, 15, 2, new Color(38,38,38));
                        fr.drawString(setting.getName() + ": " + ((StringSetting) setting).get() , x + 313, y + 5 + settingOffset, Color.WHITE);
                        settingOffset += 20;
                    } else if(setting instanceof NumberSetting) {
                        RoundedUtil.drawRound(x + 310, y + settingOffset + 1, 190, 35, 2, new Color(38,38,38));
                        RoundedUtil.drawRound(x + 312, y + settingOffset + 25, 186, 2, 2, Color.WHITE);
                        RoundedUtil.drawRound(x + 312, y + settingOffset + 25, 40, 2, 2,  CategoryUtil.getCategoryColor(listeningToModule.getCategory()));
                        RoundedUtil.drawRound(x + 350, y + settingOffset + 21, 2, 10, 1, Color.DARK_GRAY);
                        fr.drawString(setting.getName() + ": " + ((NumberSetting) setting).get(), x + 313, y + 5 + settingOffset, Color.WHITE);
                        settingOffset += 40;
                    } else if(setting instanceof BooleanSetting) {
                        RoundedUtil.drawRound(x + 310, y + settingOffset + 1, 190, 15, 2, new Color(38,38,38));
                        fr.drawString(setting.getName(), x + 313, y + 5 + settingOffset, Color.WHITE);

                        RoundedUtil.drawRound(x + 472, (float) (y + settingOffset + 2.5), 25, 12, 5, ((BooleanSetting) setting).get() ? CategoryUtil.getCategoryColor(listeningToModule.getCategory()) : new Color(60,60,60));
                        RoundedUtil.drawRound(((BooleanSetting) setting).get() ? x + 486 : x + 473, (float) (y + settingOffset + 3.5), 10, 10, 4, Color.WHITE);

                        settingOffset += 20;
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
        List<Module> modules = TopHat.moduleManager.getModulesByCategory(listeningToCategory);
        int maxVisibleModules = getMaxVisibleModules();

        for (int i = firstVisibleModule; i < firstVisibleModule + maxVisibleModules && i < modules.size(); i++) {
            Module module = modules.get(i);
            float modX = x + 60, modY = y + moduleOffset;
            boolean hoveredMod = DrawingUtil.hovered(mouseX, mouseY, modX, modY, 232, 20);

            if (hoveredMod) {
                if (mouseButton == 0) {
                    if(TopHat.moduleManager.getByClass(ClickGUI.class).sound.get()) {
                        SoundUtil.play(!module.isEnabled() ? SoundUtil.toggleOnSound : SoundUtil.toggleOffSound);
                    }
                    module.toggle();
                } else if (mouseButton == 1) {
                    if (listeningToModule == module) {
                        listeningToModule = null;
                    } else {
                        listeningToModule = module;
                    }
                }
            }

            if(listeningToModule == module && TopHat.settingManager.getSettingsByModule(listeningToModule).size() != 0) {
                int settingOffset = 5;

                for(Setting setting : TopHat.settingManager.getSettingsByModule(listeningToModule)) {

                    boolean hoveredSetting = DrawingUtil.hovered(mouseX, mouseY, x + 310, y + settingOffset + 1, 190, 15);

                    if(setting instanceof DividerSetting) {
                        settingOffset += 20;
                    } else if(setting instanceof StringSetting) {
                        if(mouseButton == 0 && hoveredSetting) {
                            ((StringSetting) setting).forward();
                        } else if(mouseButton == 1 && hoveredSetting) {
                            ((StringSetting) setting).backward();
                        }
                        settingOffset += 20;
                    } else if(setting instanceof NumberSetting) {
                        settingOffset += 40;
                    } else if(setting instanceof BooleanSetting) {
                        if(mouseButton == 0 && hoveredSetting) {
                            ((BooleanSetting) setting).toggle();
                        }
                        settingOffset += 20;
                    }
                }
            }
            moduleOffset += 25;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (isDragging && state == 0) {
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
            firstVisibleModule = Math.max(0, Math.min(firstVisibleModule - scroll, TopHat.moduleManager.getModulesByCategory(listeningToCategory).size() - getMaxVisibleModules()));
        }
        super.handleMouseInput();
    }

    private int getMaxVisibleModules() {
        int availableHeight = 255;
        int moduleHeight = 25;
        return availableHeight / moduleHeight;
    }
}
