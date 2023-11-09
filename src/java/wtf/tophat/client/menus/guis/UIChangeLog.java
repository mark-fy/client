package wtf.tophat.client.menus.guis;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UIChangeLog extends GuiScreen {

    private final GuiScreen parent;

    public UIChangeLog(GuiScreen parentScreen) {
        this.parent = parentScreen;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height - 25, 200, 20, "Back"));
        super.initGui();
    }


    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(parent);
                break;
        }
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontRenderer fr = mc.fontRenderer;
        this.drawDefaultBackground();

        List<String> changes = new ArrayList<String>();

        changes.add("TopHat v0.0.5");
        changes.add("+ account manager");
        changes.add("+ config system");
        changes.add("+ script system");
        changes.add("+ antibot module");
        changes.add("");

        fr.drawString(changes.get(0), 5, 5, Color.WHITE);
        int counter = 10;
        for(String change : changes) {
            if(change.equalsIgnoreCase("TopHat v0.0.5"))
                continue;
            fr.drawString(change, 15, 15 + counter, getColor(change));
            counter += 10;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private Color getColor(String character) {
        if(character.contains("+"))
            return new Color(0,255,0);
        else if(character.contains("*"))
            return new Color(255,255,0);
        else if(character.contains("-"))
            return new Color(255,0,0);
        return Color.WHITE;
    }

}