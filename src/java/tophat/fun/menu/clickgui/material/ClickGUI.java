package tophat.fun.menu.clickgui.material;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import tophat.fun.Client;
import tophat.fun.modules.Module;
import tophat.fun.utilities.font.CFont;
import tophat.fun.utilities.font.renderer.TTFFontRenderer;
import tophat.fun.utilities.render.RenderUtil;
import tophat.fun.utilities.render.TextUtil;
import tophat.fun.utilities.render.shader.DrawHelper;

import java.awt.*;
import java.io.IOException;

public class ClickGUI extends GuiScreen {

    private final static TTFFontRenderer poppins = CFont.FONT_MANAGER.getFont("PoppinsMedium 18");
    private final static TTFFontRenderer poppinsR = CFont.FONT_MANAGER.getFont("PoppinsRegular 18");
    private final static TTFFontRenderer iconFont = CFont.FONT_MANAGER.getFont("RegularIcons 24");

    private Module.Category selectedCategory = Module.Category.COMBAT;
    private Module selectedModule;

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
        ScaledResolution sr = new ScaledResolution(mc);
        float x = (float) (sr.getScaledWidth() / 2) - 150;
        float y = (float) (sr.getScaledHeight() / 2) - 150;
        float width = 300;
        float height = 300;

        DrawHelper.drawRoundedRect(x, y, width, height, 8, new Color(25,25,25));

        float categoryOffset = 0;
        for(Module.Category category : Module.Category.values()) {
            boolean hover = RenderUtil.hovered(mouseX, mouseY, x + 2, y + categoryOffset + 1, 32, 32);
            DrawHelper.drawRoundedRect(x + 2, y + categoryOffset + 1, 32, 32, 8, hover ? new Color(35,35,35) : new Color(30,30,30));
            if(category == selectedCategory) {
                DrawHelper.drawRoundedRectOutline(x + 2, y + categoryOffset + 1, 32, 32, 8, 1.5f, hover ? new Color(32, 211, 196) : new Color(24, 175, 162));
            }
            iconFont.drawString(TextUtil.getCategoryLetter(category), x + 11, y + categoryOffset + 11, -1);
            categoryOffset += 34;
        }

        float moduleOffset = 0;
        for(Module module : Client.INSTANCE.moduleManager.getModulesByCategory(selectedCategory)) {
            boolean hover = RenderUtil.hovered(mouseX, mouseY, x + 32 + 2 + 2, y + moduleOffset + 1, 100, 20);
            DrawHelper.drawRoundedRect(x + 32 + 2 + 2, y + moduleOffset + 1, 100, 20, 8, hover ? new Color(35,35,35) : new Color(30,30,30));
            if(module == selectedModule) {
                DrawHelper.drawRoundedRectOutline(x + 32 + 2 + 2, y + moduleOffset + 1, 100, 20, 8, 1.5f, hover ? new Color(32, 211, 196) : new Color(24, 175, 162));
            }
            poppinsR.drawString(module.getName(), x + 32 + 2 + 5, y + moduleOffset + 5, module.isEnabled() ? hover ? new Color(32, 211, 196).getRGB() : new Color(24, 175, 162).getRGB() : hover ? new Color(255,255,255).getRGB() : new Color(200,200,200).getRGB());
            moduleOffset += 22;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution sr = new ScaledResolution(mc);
        float x = (float) (sr.getScaledWidth() / 2) - 150;
        float y = (float) (sr.getScaledHeight() / 2) - 150;
        float width = 300;
        float height = 300;

        float categoryOffset = 0;
        for(Module.Category category : Module.Category.values()) {
            boolean hover = RenderUtil.hovered(mouseX, mouseY, x + 2, y + categoryOffset + 1, 32, 32);

            switch (mouseButton) {
                case 0:
                    if(hover) {
                        selectedCategory = category;
                    }
                    break;
            }

            categoryOffset += 34;
        }

        float moduleOffset = 0;
        for(Module module : Client.INSTANCE.moduleManager.getModulesByCategory(selectedCategory)) {
            boolean hover = RenderUtil.hovered(mouseX, mouseY, x + 32 + 2 + 2, y + moduleOffset + 1, 100, 20);
            switch (mouseButton) {
                case 0:
                    if(hover) {
                        module.toggle();
                    }
                    break;
                case 1:
                    if(hover) {
                        selectedModule = module;
                    }
                    break;
            }
            moduleOffset += 22;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
