package tophat.fun.menu;

import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import tophat.fun.menu.alt.AltManager;
import tophat.fun.utilities.font.CFont;
import tophat.fun.utilities.font.renderer.TTFFontRenderer;
import tophat.fun.utilities.render.RectUtil;
import tophat.fun.utilities.render.RenderUtil;
import tophat.fun.utilities.render.RoundUtil;

import java.awt.*;
import java.io.IOException;

public class CustomAltManager extends GuiScreen {

    private final static TTFFontRenderer poppins = CFont.FONT_MANAGER.getFont("PoppinsSemiBold 20");
    private final static TTFFontRenderer poppinsR = CFont.FONT_MANAGER.getFont("PoppinsMedium 20");

    private final ResourceLocation resourceLocation = new ResourceLocation("tophat/images/user.png");

    public CustomAltManager() {}

    @Override
    public void initGui() {
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
            RoundUtil.round(x - 60 - 1, offset - 1, 122, 22, 7, hover ? new Color(14, 103, 95) : new Color(14, 109, 101));
            RoundUtil.round(x - 60, offset, 120, 20, 6, hover ? new Color(33,33,33) : new Color(22, 22, 22));

            // Draw button label
            poppins.drawCenteredString(label, x, (float) offset + 3.5f, -1);
        }

        // Input Boxes
        String[] inputLabels = {"Username/Email", "Password"};
        double iOffset = y - 75;

        for (String label : inputLabels) {
            iOffset += 23;

            boolean hover = RenderUtil.hovered(mouseX, mouseY, x - 60, iOffset, 200, 20);

            // Draw button background
            RoundUtil.round(x - 100 - 1, iOffset - 1, 200 + 2, 22, 7, hover ? new Color(14, 103, 95) : new Color(14, 109, 101));
            RoundUtil.round(x - 100, iOffset, 200, 20, 6, hover ? new Color(33,33,33) : new Color(22, 22, 22));

            // Draw button label
            poppinsR.drawString(label, x - 95, (float) iOffset + 3.5f, Color.lightGray.getRGB());
        }

        RoundUtil.round(5,5, 160 + 2, this.height - 13, 6, new Color(14, 109, 101));
        RoundUtil.round(6,6, 160, this.height - 15, 6, new Color(22,22,22));

        poppinsR.drawString(mc.getSession().getUsername(), 10, 8, -1);

        String accounts = AltManager.getAccounts("tophat");
        String[] accountStrings = accounts.split(",");

        int accountY = 30;
        for (String accountString : accountStrings) {
            boolean isHovered = RenderUtil.hovered(mouseX, mouseY, 8, accountY - 6, 160, 32);
            RoundUtil.round(7.5, accountY - 7, 157, 34, 7, isHovered ? new Color(14, 103, 95) : new Color(14, 109, 101));
            RoundUtil.round(8.5, accountY - 6, 155, 32, 6, isHovered ? new Color(40, 40, 40) : new Color(22, 22, 22));

            String[] parts = accountString.split(":");
            if (parts.length >= 2) {
                String username = parts[0];
                String passwordOrStatus = parts[1];

                poppinsR.drawString(username, 10, accountY - 3, -1);

                if (!passwordOrStatus.equalsIgnoreCase("offline")) {
                    String passwordAsterisks = createAsteriskString(passwordOrStatus);
                    poppinsR.drawString(passwordAsterisks, 10, accountY + 10, Color.LIGHT_GRAY.getRGB());
                } else {
                    poppinsR.drawString("cracked", 10, accountY + 10, Color.LIGHT_GRAY.getRGB());
                }

                accountY += 36;
            }
        }

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

        if (mouseButton == 0) {
            String[] buttonLabels = {"Login", "Add", "Back"};
            for (int i = 0; i < buttonLabels.length; i++) {
                offset += 23;

                if (RenderUtil.hovered(mouseX, mouseY, x - 60, offset, 120, 20)) {
                    switch (i) {
                        case 0:

                            break;
                        case 1:

                            break;
                        case 2:
                            mc.displayGuiScreen(new CustomMainMenu());
                            break;
                    }
                    break;
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode != Keyboard.KEY_ESCAPE) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    private String createAsteriskString(String input) {
        StringBuilder asterisks = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            asterisks.append('*');
        }
        return asterisks.toString();
    }

}
