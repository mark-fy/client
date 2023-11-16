package tophat.fun.menu.click.tophat;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import tophat.fun.Client;
import tophat.fun.modules.Module;
import tophat.fun.utilities.font.CFont;
import tophat.fun.utilities.font.renderer.TTFFontRenderer;
import tophat.fun.utilities.render.RectUtil;
import tophat.fun.utilities.render.RoundUtil;

import java.awt.*;
import java.io.IOException;

public class ClickGUI extends GuiScreen {

    private final static TTFFontRenderer poppins = CFont.FONT_MANAGER.getFont("PoppinsMedium 18");
    private final static TTFFontRenderer descFont = CFont.FONT_MANAGER.getFont("PoppinsMedium 14");

    private Module.Category selectedCategory = Module.Category.COMBAT;

    @Override
    public void initGui() {
        ScaledResolution sr = new ScaledResolution(mc);

        double width = 350;
        double height = 350;
        super.initGui();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);

        double x = sr.getScaledWidth_double() / 2;
        double y = sr.getScaledHeight_double() / 2;

        RoundUtil.drawRoundedRect(x - 170, y - 150, 340, 300, 10, new Color(22,22,22));

        int catX = (int) (x - 135);

        int catOffset = 120;
        for(Module.Category category : Module.Category.values()) {
            RoundUtil.drawRoundedRect(catX - 30, catOffset - 2, 60, 25, 8, new Color(56, 139, 211));
            poppins.drawCenteredString(category.getName(), catX, 5 + catOffset, -1);
            catOffset += 30;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution sr = new ScaledResolution(mc);

        double x = sr.getScaledWidth_double() / 2;
        double y = sr.getScaledHeight_double() / 2;
        int catOffset = 120;
        for(Module.Category category : Module.Category.values()) {
            boolean hover = RectUtil.hovered(mouseX, mouseY, x - 165, catOffset - 8, 80, 25);
            if(mouseButton == 0 && hover) {
                selectedCategory = category;
            }
            catOffset += 35;
        }

        int modOffset = 120;
        for(Module module : Client.INSTANCE.moduleManager.getModulesByCategory(selectedCategory)) {
            boolean hover = RectUtil.hovered(mouseX, mouseY, x - 75, modOffset - 8, 80, 40);

            if(mouseButton == 0 && hover) {
                module.toggle();
            }
            modOffset += 42;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
