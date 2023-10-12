package wtf.tophat.screen;

import net.minecraft.client.gui.*;
import org.lwjgl.input.Keyboard;
import wtf.tophat.screen.alts.AltThread;
import wtf.tophat.utilities.font.CFontRenderer;
import wtf.tophat.utilities.font.CFontUtil;

import java.awt.*;
import java.io.IOException;
import java.util.Locale;

public class UIAltManager extends GuiScreen {

    private GuiTextField username, password;
    private final GuiScreen parent;
    private AltThread thread;

    public UIAltManager(GuiScreen parentScreen) {
        this.parent = parentScreen;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();

        int buttonX = this.width / 2 - 100;
        int buttonY = this.height / 2 + 25 - 45;

        int fieldX = this.width / 2 - 100;
        int fieldY = this.height / 2 - 80;

        this.buttonList.add(new GuiButton(0, buttonX, buttonY, "Login"));
        buttonY += 20;
        this.buttonList.add(new GuiButton(1, buttonX, buttonY, "Cancel"));

        this.username = new GuiTextField(1, mc.fontRenderer, fieldX, fieldY, 200, 20);
        fieldY += 25;
        this.password = new GuiTextField(2, mc.fontRenderer, fieldX, fieldY, 200, 20);
        this.username.setFocused(true);
        this.username.setMaxStringLength(200);
        this.password.setMaxStringLength(200);
        Keyboard.enableRepeatEvents(true);
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                if (this.password.getText().isEmpty()) {
                    this.thread = new AltThread(this.username.getText(), "", true);
                } else if(!this.password.getText().isEmpty() && !this.username.getText().isEmpty()) {
                    this.thread = new AltThread(this.username.getText(), this.password.getText(), true);
                }
                this.thread.start();
                break;
            case 1:
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

        int textX = this.width / 2 - frBig.getStringWidth("Alt Manager") / 2;
        int textY = this.height / 2 - fr.getHeight() / 2 - 45;

        int statusX = this.width / 2 - fr.getStringWidth((this.thread == null) ? "§ewaiting..." : this.thread.getStatus().toLowerCase(Locale.ROOT)) / 2;

        int fieldX = this.width / 2 - 100;
        int fieldY = this.height / 2 - 80;

        fr.drawString(mc.getSession().getUsername(), 5, 5, Color.WHITE);
        this.username.drawTextBox();
        this.password.drawTextBox();

        frBig.drawString("Alt Manager", textX, textY - 60, Color.WHITE);
        fr.drawString((this.thread == null) ? "§ewaiting..." : this.thread.getStatus().toLowerCase(Locale.ROOT), statusX, textY - 43, Color.WHITE);

        if (this.username.getText().isEmpty()) {
            mc.fontRenderer.drawString("Username / E-Mail", fieldX + 4, fieldY + 6, -7829368);
        }
        if (this.password.getText().isEmpty()) {
            mc.fontRenderer.drawString("Password", fieldX + 4, fieldY + 31, -7829368);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        try {
            super.keyTyped(typedChar, keyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (typedChar == '\t'
                && (this.username.isFocused() || this.password.isFocused())) {
            this.username.setFocused(!this.username.isFocused());
            this.password.setFocused(!this.password.isFocused());
        }
        if (typedChar == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }
        this.username.textboxKeyTyped(typedChar, keyCode);
        this.password.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.username.mouseClicked(mouseX, mouseY, mouseButton);
        this.password.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
        this.password.updateCursorCounter();
    }
}
