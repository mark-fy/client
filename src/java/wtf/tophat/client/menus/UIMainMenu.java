package wtf.tophat.client.menus;

import net.minecraft.client.gui.*;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.menus.guis.UIAltManager;
import wtf.tophat.client.menus.guis.UIChangeLog;
import wtf.tophat.client.utilities.render.ColorUtil;

import java.awt.*;
import java.io.IOException;
public class UIMainMenu extends GuiScreen {

    public UIMainMenu() {}

    @Override
    public void initGui() {
        this.buttonList.clear();

        int buttonX = this.width / 2 - 100;
        int buttonY = this.height / 2 + 25 - 45;

        this.buttonList.add(new GuiButton(0, buttonX, buttonY, "Singleplayer"));
        buttonY += 20;
        this.buttonList.add(new GuiButton(1, buttonX, buttonY, "Multiplayer"));
        buttonY += 20;
        this.buttonList.add(new GuiButton(3, buttonX, buttonY, "Alt Manager"));
        buttonY += 20;
        this.buttonList.add(new GuiButton(4, buttonX, buttonY, "Options"));
        this.buttonList.add(new GuiButton(5, this.width - 105, this.height - 25, 100, 20, "Changelog"));
        buttonY += 20;
        this.buttonList.add(new GuiButton(6, buttonX, buttonY, "Quit"));
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(new GuiSelectWorld(this));
                break;
            case 1:
                mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 3:
                mc.displayGuiScreen(new UIAltManager(this));
                break;
            case 4:
                mc.displayGuiScreen(new GuiOptions(this, mc.settings));
                break;
            case 5:
                mc.displayGuiScreen(new UIChangeLog(this));
                break;
            case 6:
                mc.shutdownMinecraftApplet();
                break;
        }
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontRenderer fr = mc.fontRenderer;
        int counter = 0;
        this.drawDefaultBackground();

        int x = width / 2 - fr.getStringWidth(TopHat.getName()) / 2;
        int y = height / 2 - fr.FONT_HEIGHT / 2 - 45;

        fr.drawString(TopHat.getName(), x, y, -1);
        fr.drawString("v" + TopHat.getVersion(), x + 32, y + 14, new Color(ColorUtil.fadeBetween(new Color(1, 236, 183).getRGB(), new Color(255, 255, 255).getRGB(), counter * 150L)).getRGB());
        counter++;
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}