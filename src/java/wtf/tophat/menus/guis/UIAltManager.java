package wtf.tophat.menus.guis;

import net.minecraft.client.gui.*;
import org.lwjgl.input.Keyboard;
import wtf.accounts.AccountManager;
import wtf.tophat.menus.alts.AltThread;
import wtf.tophat.utilities.render.DrawingUtil;

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
        this.buttonList.add(new GuiButton(1, buttonX, buttonY, "Add"));
        buttonY += 20;
        this.buttonList.add(new GuiButton(2, buttonX, buttonY, "Cancel"));

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
                if (password.getText().isEmpty()) {
                    thread = new AltThread(username.getText(), "", true);
                } else if(!password.getText().isEmpty() && !username.getText().isEmpty()) {
                    thread = new AltThread(username.getText(), password.getText(), true);
                }
                thread.start();
                break;
            case 1:
                if (password.getText().isEmpty()) {
                    AccountManager.save(username.getText(), "", true, "tophat");
                } else if(!password.getText().isEmpty() && !username.getText().isEmpty()) {
                    AccountManager.save(username.getText(), password.getText(), false, "tophat");
                }
                break;
            case 2:
                mc.displayGuiScreen(parent);
                break;
        }
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontRenderer fr = mc.fontRenderer;
        this.drawDefaultBackground();

        int textX = this.width / 2 - fr.getStringWidth("Alt Manager") / 2;
        int textY = this.height / 2 - fr.FONT_HEIGHT / 2 - 45;

        int statusX = this.width / 2 - fr.getStringWidth((this.thread == null) ? "§ewaiting..." : this.thread.getStatus().toLowerCase(Locale.ROOT)) / 2;

        int fieldX = this.width / 2 - 100;
        int fieldY = this.height / 2 - 80;

        this.username.drawTextBox();
        this.password.drawTextBox();

        fr.drawString("Alt Manager", textX, textY - 60, -1);
        fr.drawString((this.thread == null) ? "§ewaiting..." : this.thread.getStatus().toLowerCase(Locale.ROOT), statusX, textY - 43, -1);

        if (this.username.getText().isEmpty()) {
            mc.fontRenderer.drawString("Username / E-Mail", fieldX + 4, fieldY + 6, -7829368);
        }
        if (this.password.getText().isEmpty()) {
            mc.fontRenderer.drawString("Password", fieldX + 4, fieldY + 31, -7829368);
        }

        DrawingUtil.rectangle(0, 0, 160, this.height, true, new Color(0, 0, 0, 100));
        fr.drawString(mc.getSession().getUsername(), 5, 5, -1);
        DrawingUtil.rectangle(0, 15, 160, 1, true, Color.WHITE);

        String accounts = AccountManager.getAccounts("tophat");
        String[] accountStrings = accounts.split(",");

        int accountY = 25;
        for (String accountString : accountStrings) {
            boolean isHovered = DrawingUtil.hovered(mouseX, mouseY, 0, accountY - 8, 160, 29);
            DrawingUtil.rectangle(0, accountY - 8, 160, 29, true, isHovered ? new Color(40, 40, 40, 100) : new Color(0, 0, 0, 100));

            String[] parts = accountString.split(":");
            if (parts.length >= 2) {
                String username = parts[0];
                String passwordOrStatus = parts[1];

                fr.drawString(username, 5, accountY - 5, -1);

                if (!passwordOrStatus.equalsIgnoreCase("offline")) {
                    String passwordAsterisks = createAsteriskString(passwordOrStatus);
                    fr.drawString(passwordAsterisks, 5, accountY + 7, Color.LIGHT_GRAY.getRGB());
                } else {
                    fr.drawString("offline", 5, accountY + 7, Color.LIGHT_GRAY.getRGB());
                }

                accountY += 30;
            }
        }

        fr.drawString("Tip: to delete an account, left click it.", this.width - fr.getStringWidth("Tip: to delete an account, left click it.") - 15, this.height - 25, -1);

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

        int accountY = 25;
        String accounts = AccountManager.getAccounts("tophat");

        if (!accounts.isEmpty()) {
            String[] accountStrings = accounts.split(",");
            for (String accountString : accountStrings) {
                String[] parts = accountString.split(":");
                if (parts.length >= 2) {
                    String username = parts[0];
                    String passwordOrStatus = parts[1];
                    if (DrawingUtil.hovered(mouseX, mouseY, 0, accountY - 8, 160, 29)) {
                        if (mouseButton == 0) {
                            this.username.setText(username);
                            if (!passwordOrStatus.equalsIgnoreCase("offline")) {
                                this.password.setText(passwordOrStatus);
                            } else {
                                this.password.setText("");
                            }
                        } else if (mouseButton == 1) {
                            AccountManager.deleteAccount(username, "tophat");
                        }
                    }
                    accountY += 30;
                }
            }
        }
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

    private String createAsteriskString(String input) {
        StringBuilder asterisks = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            asterisks.append('*');
        }
        return asterisks.toString();
    }
}
