package wtf.tophat.menus.click.beta;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import wtf.tophat.utilities.render.shaders.RoundedUtil;

import java.awt.*;

public class BetaClickGUI extends GuiScreen {

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontRenderer fr = mc.fontRenderer;

        float x = 50, y = 50, width = 300, height = 300;

        RoundedUtil.drawRound(x, y, width, height, 8, new Color(20, 20, 20));
        fr.drawString("TopHat", x, y, Color.WHITE);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

}
