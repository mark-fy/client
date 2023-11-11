package wtf.tophat.client.menus;

import net.minecraft.client.gui.*;
import wtf.tophat.auth.HwidUtil;
import wtf.tophat.auth.NetworkUtil;
import wtf.tophat.client.TopHat;

import java.io.IOException;

public class UILoginScreen extends GuiScreen {

    private GuiTextField uid;
    public static String username;

    public UILoginScreen() {}

    @Override
    public void initGui() {
        this.buttonList.clear();

        int buttonX = this.width / 2 - 100;
        int buttonY = this.height / 2 + 25 - 45;

        this.buttonList.add(new GuiButton(0, buttonX, buttonY, "Login"));
        buttonY += 20;
        this.buttonList.add(new GuiButton(1, buttonX, buttonY, "Quit"));

        this.uid = new GuiTextField(1, mc.fontRenderer, this.width / 2 - 98, this.height / 2 + 25 - 70, 196, 20);
        this.uid.setFocused(true);
        this.uid.setMaxStringLength(4);
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                if (uid.getText() == null || uid.getText().trim().isEmpty()) {
                    return;
                }

                TopHat.printL("[DEBUG] HWID: " + HwidUtil.getHWID());
                System.out.println(NetworkUtil.getRawContent());
                if(NetworkUtil.doesHWIDExistInContent(HwidUtil.getHWID()) && uid.getText().equalsIgnoreCase(NetworkUtil.getUIDFromHWID(HwidUtil.getHWID()))) {
                    username = NetworkUtil.getUsernameFromHWID(HwidUtil.getHWID());
                    mc.displayGuiScreen(new UIMainMenu());
                    TopHat.printL("Welcome to TopHat, " + NetworkUtil.getUsernameFromHWID(HwidUtil.getHWID()) + "!");
                } else if(!NetworkUtil.doesHWIDExistInContent(HwidUtil.getHWID())) {
                    TopHat.printL("HWID not found in the database.");
                } else {
                    TopHat.printL("Wrong UID!");
                }
                break;
            case 1:
                mc.shutdownMinecraftApplet();
                break;
        }
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontRenderer fr = mc.fontRenderer;
        this.drawDefaultBackground();
        this.uid.drawTextBox();

        int x = width / 2 - fr.getStringWidth(TopHat.getName() + " - Login Menu") / 2;
        int y = height / 2 - fr.FONT_HEIGHT / 2 - 45;


        fr.drawString(TopHat.getName() + " - Login Menu", x, y - 25, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        try {
            super.keyTyped(typedChar, keyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (typedChar == '\t' && this.uid.isFocused()) {
            this.uid.setFocused(!this.uid.isFocused());
        }
        if (typedChar == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }
        this.uid.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.uid.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        this.uid.updateCursorCounter();
    }
}