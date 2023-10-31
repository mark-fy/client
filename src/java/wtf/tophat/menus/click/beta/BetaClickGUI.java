package wtf.tophat.menus.click.beta;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import wtf.tophat.Client;
import wtf.tophat.modules.base.Module;
import wtf.tophat.utilities.Methods;
import wtf.tophat.utilities.render.CategoryUtil;
import wtf.tophat.utilities.render.DrawingUtil;
import wtf.tophat.utilities.render.shaders.RoundedUtil;

import java.awt.*;
import java.io.IOException;

public class BetaClickGUI extends GuiScreen implements Methods {

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }

    private Module.Category listeningToCategory = Module.Category.COMBAT;
    private Module listeningToModule = null;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontRenderer fr = Methods.mc.fontRenderer;

        float x = 50, y = 50, width = 300, height = 285;

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
        for(Module module : Client.moduleManager.getModulesByCategory(listeningToCategory)) {
            float modX = x + 60, modY = y + moduleOffset;
            boolean hoveredMod = DrawingUtil.hovered(mouseX, mouseY, x + 60, modY, 232, 20);

            RoundedUtil.drawRound(modX, modY, 232, 20, 4, hoveredMod ? new Color(38,38,38) : new Color(36,36,36));
            fr.drawString(module.getName(), x + 65, modY + 7, Color.WHITE);
            fr.drawString("X", modX + 232 - 10, modY + 7, module.isEnabled() ? Color.GREEN : Color.RED);

            moduleOffset += 25;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        float x = 50, y = 50, width = 300, height = 285;

        int categoryOffset = 40;
        for (Module.Category category : Module.Category.values()) {
            boolean hoveredCat = DrawingUtil.hovered(mouseX, mouseY, x + 10, y + categoryOffset, 32, 32);
            if (hoveredCat && mouseButton == 0) {
                listeningToCategory = category;
            }

            categoryOffset += 40;
        }

        int moduleOffset = 20;
        for (Module module : Client.moduleManager.getModulesByCategory(listeningToCategory)) {
            float modX = x + 60, modY = y + moduleOffset;
            boolean hoveredMod = DrawingUtil.hovered(mouseX, mouseY, modX, modY, 232, 20);

            if (hoveredMod) {
                if (mouseButton == 0) {
                    module.toggle();
                    this.sendChat("enabled: " + module.getName());
                } else if (mouseButton == 1) {
                    listeningToModule = module;
                }
            }
            moduleOffset += 25;
        }
    }

}
