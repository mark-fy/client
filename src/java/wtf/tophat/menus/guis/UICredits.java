package wtf.tophat.menus.guis;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import wtf.tophat.utilities.render.font.CFontRenderer;
import wtf.tophat.utilities.render.font.CFontUtil;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UICredits extends GuiScreen {

    private final GuiScreen parent;

    public UICredits(GuiScreen parentScreen) {
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
        CFontRenderer frBig = CFontUtil.SF_Regular_32.getRenderer();
        CFontRenderer fr = CFontUtil.SF_Regular_20.getRenderer();
        this.drawDefaultBackground();

        List<String> changes = new ArrayList<String>();

        changes.add("TopHat v0.0.4");
        changes.add("- made by MarkGG");
        changes.add("- made by Fyre");
        changes.add("- made by Drakon");

        frBig.drawString(changes.get(0), 5, 5, Color.WHITE);
        int counter = 10;
        for(String change : changes) {
            if(change.equalsIgnoreCase("TopHat v0.0.4"))
                continue;
            fr.drawString(change, 15, 15 + counter, Color.WHITE);
            counter += 10;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

}
