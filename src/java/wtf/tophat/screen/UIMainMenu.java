package wtf.tophat.screen;

import net.minecraft.client.gui.*;
import wtf.tophat.Client;
import wtf.tophat.utilities.font.CFontRenderer;
import wtf.tophat.utilities.font.CFontUtil;
import wtf.tophat.utilities.render.ColorUtil;

import java.awt.*;
import java.io.IOException;

import static wtf.tophat.utilities.Colors.DEFAULT_COLOR;
import static wtf.tophat.utilities.Colors.WHITE_COLOR;

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
        buttonY += 20;
        this.buttonList.add(new GuiButton(5, buttonX, buttonY, "Quit"));
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
                Client.shutdown();
                break;
        }
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        CFontRenderer fr = CFontUtil.SF_Regular_32.getRenderer();
        CFontRenderer frSmall = CFontUtil.SF_Regular_20.getRenderer();
        int counter = 0;
        this.drawDefaultBackground();

        int x = width / 2 - fr.getStringWidth(Client.getName()) / 2;
        int y = height / 2 - fr.getHeight() / 2 - 45;

        fr.drawString(Client.getName(), x, y, Color.WHITE);
        frSmall.drawString("v" + Client.getVersion(), x + 48, y + 14, new Color(ColorUtil.fadeBetween(DEFAULT_COLOR, WHITE_COLOR, counter * 150L)));
        counter++;
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
