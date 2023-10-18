package wtf.tophat.menus.click.beta;

import net.minecraft.client.gui.GuiScreen;
import wtf.tophat.Client;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.impl.render.PostProcessing;
import wtf.tophat.menus.click.dropdown.DropDownSettingFrame;
import wtf.tophat.utilities.render.shaders.RenderUtil;
import wtf.tophat.utilities.render.shaders.blur.GaussianBlur;
import wtf.tophat.utilities.render.font.CFontRenderer;
import wtf.tophat.utilities.render.font.CFontUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;
import java.io.IOException;

import static wtf.tophat.utilities.render.Colors.DEFAULT_COLOR;

public class BetaClickGUI extends GuiScreen {

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

        double x = 110, y = 20, width = 100;

        for (Module.Category category : Module.Category.values()) {
            drawBlur(x, y, width, 20);
            frSemiBold.drawString(category.getName(), x + 5, 25, Color.WHITE);

            double modY = 45;
            drawBlur(x, 40, 100, Client.moduleManager.getModulesByCategory(category).size() * 20);
            for (Module module : Client.moduleManager.getModulesByCategory(category)) {

                fr.drawString(module.getName(), x + 5, modY, module.isEnabled() ? new Color(DEFAULT_COLOR) : Color.WHITE);

                modY += 20;
            }

            x += width + 25;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        float x = 110, width = 100;
        for (Module.Category category : Module.Category.values()) {
            float modY = 45, modHeight = 20;

            for (Module module : Client.moduleManager.getModulesByCategory(category)) {
                if(RenderUtil.isHovered((float) mouseX, (float) mouseY, x, modY, width, modHeight)) {
                    if(mouseButton == 0) {
                        module.toggle();
                    } else if(mouseButton == 1) {
                        mc.displayGuiScreen(new DropDownSettingFrame(this, module));
                    }
                }
                modY += 20;
            }
            x += width + 25;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void drawBlur(double x, double y, double width, double height) {
        if(Client.moduleManager.getByClass(PostProcessing.class).isEnabled() && Client.moduleManager.getByClass(PostProcessing.class).blurShader.get()) {
            GaussianBlur.startBlur();
            DrawingUtil.rectangle(x, y, width, height, true, new Color(0, 0, 0));
            GaussianBlur.endBlur(10, 2);
        }
        DrawingUtil.rectangle(x, y, width, height, true, new Color(0, 0, 0, 150));
    }

}
