package wtf.tophat.menus;

import net.minecraft.client.gui.*;
import wtf.tophat.Client;
import wtf.tophat.menus.guis.UIAltManager;
import wtf.tophat.menus.guis.UIChangeLog;
import wtf.tophat.menus.guis.microsoft.GuiLoginMicrosoft;
import wtf.tophat.utilities.render.ColorUtil;

import java.awt.*;
import java.io.IOException;

import static wtf.tophat.utilities.render.Colors.DEFAULT_COLOR;
import static wtf.tophat.utilities.render.Colors.WHITE_COLOR;

@SuppressWarnings({"ConstantValue", "UnusedAssignment"})
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

        this.buttonList.add(new GuiButton(5, 5, this.height - 25, 100, 20, "Changelog"));
        this.buttonList.add(new GuiButton(6, this.width - 105, this.height - 25, 100, 20, "Credits"));

        buttonY += 20;
        this.buttonList.add(new GuiButton(7, buttonX, buttonY, "Quit"));
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
                //mc.displayGuiScreen(new UICredits(this));
                break;
            case 7:
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

        int x = width / 2 - fr.getStringWidth(Client.getName()) / 2;
        int y = height / 2 - fr.FONT_HEIGHT / 2 - 45;

        fr.drawString(Client.getName(), x, y, -1);
        fr.drawString("v" + Client.getVersion(), x + 48, y + 14, new Color(ColorUtil.fadeBetween(DEFAULT_COLOR, WHITE_COLOR, counter * 150L)).getRGB());
        counter++;
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
