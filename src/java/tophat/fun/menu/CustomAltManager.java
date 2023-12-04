package tophat.fun.menu;

import by.radioegor146.nativeobfuscator.Native;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import tophat.fun.menu.alt.AltManager;
import tophat.fun.menu.alt.AltThread;
import tophat.fun.utilities.font.CFont;
import tophat.fun.utilities.font.renderer.TTFFontRenderer;
import tophat.fun.utilities.render.RectUtil;
import tophat.fun.utilities.render.RenderUtil;
import tophat.fun.utilities.render.TextUtil;
import tophat.fun.utilities.render.shader.DrawHelper;

import java.awt.*;
import java.io.IOException;

@Native
public class CustomAltManager extends GuiScreen {

    private final static TTFFontRenderer poppins = CFont.FONT_MANAGER.getFont("PoppinsSemiBold 20"), poppinsR = CFont.FONT_MANAGER.getFont("PoppinsMedium 20");
    private final ResourceLocation resourceLocation = new ResourceLocation("tophat/images/user.png");

    private boolean listeningToUser = false, listeningToPass = false;
    private String usernameBoxText = "", passwordBoxText = "";

    private AltThread thread;

    public CustomAltManager() {}

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);

        float x = (float) sr.getScaledWidth() / 2;
        float y = (float) sr.getScaledHeight() / 2;

        // Background
        RectUtil.rectangle(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), true, new Color(22, 22, 22));
        
        mc.getTextureManager().bindTexture(resourceLocation);
        Gui.drawModalRectWithCustomSizedTexture((int) x - 27, (int) y - 105, 0, 0, 54, 54, 54, 54);

        // Buttons
        String[] buttonLabels = {"Login", "Add", "Back"};
        double offset = y - 25;

        for (String label : buttonLabels) {
            offset += 23;

            boolean hover = RenderUtil.hovered(mouseX, mouseY, x - 60, offset, 120, 20);

            // Draw button background
            DrawHelper.drawRoundedRect(x - 60, offset - 1, 120, 20, 6, hover ? new Color(33,33,33) : new Color(22, 22, 22));
            DrawHelper.drawRoundedRectOutline(x - 60 - 1, offset - 2, 122, 22, 6, 2, hover ? new Color(14, 103, 95) : new Color(14, 109, 101));

            // Draw button label
            poppins.drawCenteredString(label, x, (float) offset + 2, -1);
        }
        poppinsR.drawCenteredString(thread == null ? "§7Status: §rWaiting..." : thread.getStatus(), x + (poppinsR.getWidth("§x") + TextUtil.isColorCode(thread == null ? "§7Status: §rWaiting..." : thread.getStatus())), (float) (offset + 24), -1);

        // Password Input Box
        boolean hoverPass = RenderUtil.hovered(mouseX, mouseY, x - 60,  y - 75 + 46, 200, 20);
        DrawHelper.drawRoundedRect(x - 100,  y - 75 + 46, 200, 20, 6,  hoverPass ? new Color(33,33,33) : new Color(22, 22, 22));
        DrawHelper.drawRoundedRectOutline(x - 100 - 1,  y - 75 + 46 - 1, 202, 22, 7, 2, hoverPass ? new Color(14, 103, 95) : new Color(14, 109, 101));
        String truncatedPassword = passwordBoxText.length() >= 28 ? passwordBoxText.substring(0, 28) + "..." : passwordBoxText;
        poppinsR.drawString(truncatedPassword.isEmpty() ? (hoverPass ? (listeningToPass ? "_" : "Password") : "Password") : (hoverPass ? truncatedPassword + "_" : truncatedPassword), x - 95, y - 75 + 46 + 3.5f, truncatedPassword.isEmpty() ? Color.lightGray.getRGB() : -1);

        // Username/Email Input Box
        boolean hoverUser = RenderUtil.hovered(mouseX, mouseY, x - 60, y - 75 + 23, 200, 20);
        DrawHelper.drawRoundedRect(x - 100, y - 75 + 23, 200, 20, 6, hoverUser ? new Color(33,33,33) : new Color(22, 22, 22));
        DrawHelper.drawRoundedRectOutline(x - 100 - 1, y - 75 + 23 - 1, 200 + 2, 22, 7, 2, hoverUser ? new Color(14, 103, 95) : new Color(14, 109, 101));
        String truncatedUsername = usernameBoxText.length() >= 28 ? usernameBoxText.substring(0, 28) + "..." : usernameBoxText;
        poppinsR.drawString(truncatedUsername.isEmpty() ? (hoverUser ? (listeningToUser ? "_" : "Username/Email") : "Username/Email") : (hoverUser ? truncatedUsername + "_" : truncatedUsername), x - 95, y - 75 + 23 + 3.5f, truncatedUsername.isEmpty() ? Color.lightGray.getRGB() : -1);

        DrawHelper.drawRoundedRectOutline(4,6.5, 164, this.height - 13, 6, 2, new Color(14, 109, 101));

        poppinsR.drawString(mc.getSession().getUsername(), 10, 10, -1);

        String accounts = AltManager.getAccounts("tophat");
        String[] accountStrings = accounts.split(",");
        int accountY = 30;
        for (String accountString : accountStrings) {
            boolean isHovered = RenderUtil.hovered(mouseX, mouseY, 8, accountY - 4, 160, 32);
            DrawHelper.drawRoundedRect(8.5, accountY - 4, 155, 32, 6, isHovered ? new Color(40, 40, 40) : new Color(22, 22, 22));
            DrawHelper.drawRoundedRectOutline(7.5, accountY - 5, 157, 34, 7, 2, isHovered ? new Color(14, 103, 95) : new Color(14, 109, 101));

            String[] parts = accountString.split(":");
            if (parts.length >= 2) {
                String username = parts[0];
                String passwordOrStatus = parts[1];
                int maxUsernameLength = 24;

                if (username.length() > maxUsernameLength) {
                    username = username.substring(0, maxUsernameLength - 3) + "...";
                }

                poppinsR.drawString(username, 10, accountY - 3, -1);

                poppinsR.drawString(!passwordOrStatus.equalsIgnoreCase("cracked") ? TextUtil.createAsteriskString(passwordOrStatus) : "cracked", 10, accountY + 10, Color.LIGHT_GRAY.getRGB());

                accountY += 36;
            }
        }

        // Tip
        poppinsR.drawString("Tip: to delete an account, left click it.", this.width - poppinsR.getWidth("Tip: to delete an account, left click it.") - 15, this.height - 25, Color.LIGHT_GRAY.getRGB());

        // Copyright
        poppins.drawCenteredString("Copyright © TopHat Client 2023", x, sr.getScaledHeight() - 25, new Color(70, 70, 70).getRGB());

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution sr = new ScaledResolution(mc);

        float x = (float) sr.getScaledWidth() / 2;
        float y = (float) sr.getScaledHeight() / 2;
        double offset = y - 25;

        boolean hoverPass = RenderUtil.hovered(mouseX, mouseY, x - 60,  y - 75 + 46, 200, 20);
        boolean hoverUser = RenderUtil.hovered(mouseX, mouseY, x - 60, y - 75 + 23, 200, 20);

        listeningToUser = hoverUser;
        listeningToPass = hoverPass && !hoverUser;

        String[] buttonLabels = {"Login", "Add", "Back"};
        for (int i = 0; i < buttonLabels.length; i++) {
            offset += 23;

            if (RenderUtil.hovered(mouseX, mouseY, x - 60, offset, 120, 20) && mouseButton == 0) {
                switch (i) {
                    case 0:
                        thread = new AltThread(usernameBoxText, passwordBoxText.isEmpty() ? "" : passwordBoxText);
                        thread.start();
                        break;
                    case 1:
                        if (!usernameBoxText.isEmpty()) {
                            AltManager.save(usernameBoxText, passwordBoxText, passwordBoxText.isEmpty(), "tophat");
                        }
                        thread.setStatus("§eInfo: §rSaved account §6" + usernameBoxText);
                        break;
                    case 2:
                        mc.displayGuiScreen(new CustomMainMenu());
                        break;
                }
                break;
            }
        }

        String accounts = AltManager.getAccounts("tophat");
        String[] accountStrings = accounts.split(",");
        int accountY = 30;
        for (String accountString : accountStrings) {
            boolean isHovered = RenderUtil.hovered(mouseX, mouseY, 8, accountY - 4, 160, 32);

            String[] parts = accountString.split(":");
            if (parts.length >= 2) {
                String username = parts[0];
                String passwordOrStatus = parts[1];

                if(isHovered && mouseButton == 0) {
                    usernameBoxText = username;
                    passwordBoxText = !passwordOrStatus.equalsIgnoreCase("cracked") ? passwordOrStatus : "";
                }

                if(isHovered && mouseButton == 1) {
                    thread.setStatus("§eInfo: §rRemoved account §6" + username);
                    AltManager.deleteAccount(username, "tophat");
                }
                accountY += 36;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        boolean allowedChar = Character.isLetterOrDigit(typedChar) || typedChar == '_' || typedChar == '.' || typedChar == '-' || typedChar == '+' || typedChar == '@';
        if (listeningToUser) {
            if (keyCode == Keyboard.KEY_BACK && usernameBoxText.length() > 0) {
                usernameBoxText = usernameBoxText.substring(0, usernameBoxText.length() - 1);
            } else if (allowedChar && usernameBoxText.length() < 254) {
                usernameBoxText += typedChar;
            }
        } else if (listeningToPass) {
            if (keyCode == Keyboard.KEY_BACK && passwordBoxText.length() > 0) {
                passwordBoxText = passwordBoxText.substring(0, passwordBoxText.length() - 1);
            } else if (TextUtil.isPrintableChar(typedChar) && passwordBoxText.length() < 254) {
                passwordBoxText += typedChar;
            }
        }

        if (keyCode == Keyboard.KEY_ESCAPE) {
            return;
        }

        super.keyTyped(typedChar, keyCode);
    }



}
