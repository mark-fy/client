package wtf.tophat.screen;

import net.minecraft.client.gui.*;
import wtf.tophat.Client;
import wtf.tophat.utilities.font.CFontRenderer;
import wtf.tophat.utilities.font.CFontUtil;

import java.awt.*;
import java.io.IOException;

public class UIAltManager extends GuiScreen {

    private final GuiScreen parent;

    public UIAltManager(GuiScreen parentScreen) {
        this.parent = parentScreen;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();

        int buttonX = this.width / 2 - 100;
        int buttonY = this.height / 2 + 25 - 45;

        this.buttonList.add(new GuiButton(0, buttonX, buttonY, "Login"));
        buttonY += 20;
        this.buttonList.add(new GuiButton(1, buttonX, buttonY, "Cancel"));
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:

                break;
            case 1:
                mc.displayGuiScreen(parent);
                break;
        }
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        CFontRenderer fr = CFontUtil.SF_Regular_20.getRenderer();
        this.drawDefaultBackground();

        fr.drawString(mc.getSession().getUsername(), 5, 5, Color.WHITE);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
