package tophat.fun.menu.clickgui.tophat;

import net.minecraft.client.gui.GuiScreen;
import tophat.fun.Client;
import tophat.fun.modules.Module;
import tophat.fun.modules.settings.Setting;
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
    private final static TTFFontRenderer iconFont = CFont.FONT_MANAGER.getFont("RegularIcons 18");

    private tophat.fun.modules.impl.design.ClickGUI clickGUI;

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
        if(clickGUI == null) {
            clickGUI = Client.INSTANCE.moduleManager.getByClass(tophat.fun.modules.impl.design.ClickGUI.class);
        }
        float x = 70;
        float y = 5;
        float modHeight = 20;

        float catOffset = 100;
        for (Module.Category category : Module.Category.values()) {
            if(clickGUI.gradientOutline.get()) {
                DrawHelper.drawRoundedGradientRect(x + catOffset - 1, y - 1, 102, 22 + modHeight * Client.INSTANCE.moduleManager.getModulesByCategory(category).size(), 8, new Color(24, 175, 162), new Color(0, 101, 197), new Color(24, 175, 162).brighter().brighter(), new Color(0, 101, 197).brighter().brighter());
                DrawHelper.drawRoundedRect(x + catOffset, y, 100, 20 + Client.INSTANCE.moduleManager.getModulesByCategory(category).size() * modHeight, 8, new Color(25, 25, 25));
                DrawHelper.drawRoundedRect(x + catOffset, y, 100, 20, 8, new Color(19, 19, 19));
            } else {
                DrawHelper.drawRoundedRect(x + catOffset, y, 100, 20 + Client.INSTANCE.moduleManager.getModulesByCategory(category).size() * modHeight, 8, new Color(25, 25, 25));
                DrawHelper.drawRoundedRect(x + catOffset, y, 100, 20, 8, new Color(19, 19, 19));
                DrawHelper.drawRoundedRectOutline(x + catOffset - 1, y - 1, 102, 22 + modHeight * Client.INSTANCE.moduleManager.getModulesByCategory(category).size(), 8, 2, new Color(24, 175, 162));
            }
            poppins.drawString(category.getName(), x + catOffset + 5, y + 5, -1);
            iconFont.drawString(TextUtil.getCategoryLetter(category), x + catOffset + 85, y + 7, -1);

            float modOffset = y + 4 + 20;
            for (Module module : Client.INSTANCE.moduleManager.getModulesByCategory(category)) {
                if (module.isHidden()) continue;
                poppinsR.drawString(module.getName(), x + catOffset + 5, modOffset, module.isEnabled() ? new Color(37, 239, 223).getRGB() : Color.WHITE.getRGB());
                modOffset += 20;
            }

            catOffset += 120;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        float x = 70;
        float y = 5;

        float catOffset = 100;
        for (Module.Category category : Module.Category.values()) {
            float modOffset = y + 4 + 20;
            for(Module module : Client.INSTANCE.moduleManager.getModulesByCategory(category)) {
                if(module.isHidden()) continue;
                boolean hover = RenderUtil.hovered(mouseX, mouseY, x + catOffset, modOffset, 100, 18);

                if(hover && mouseButton == 0) {
                    module.toggle();
                } else if(hover && mouseButton == 1) {
                    if (Setting.getSettingsByMododule(module).size() > 0) {
                        mc.displayGuiScreen(new ClickGUISettings(this, module));
                    }
                }

                modOffset += 20;
            }
            catOffset += 120;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
