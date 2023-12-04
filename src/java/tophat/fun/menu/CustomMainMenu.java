package tophat.fun.menu;

import by.radioegor146.nativeobfuscator.Native;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import tophat.fun.utilities.font.CFont;
import tophat.fun.utilities.font.renderer.TTFFontRenderer;
import tophat.fun.utilities.render.RectUtil;
import tophat.fun.utilities.render.RenderUtil;
import tophat.fun.utilities.render.shader.DrawHelper;

import java.awt.*;
import java.io.IOException;

@Native
public class CustomMainMenu extends GuiScreen {

    private final static TTFFontRenderer poppins = CFont.FONT_MANAGER.getFont("PoppinsSemiBold 20");

    private final ResourceLocation resourceLocation = new ResourceLocation("tophat/logo/3000x.png");

    public CustomMainMenu() {}

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

        // Copyright
        poppins.drawCenteredString("Copyright © TopHat Client 2023", x, sr.getScaledHeight() - 25, new Color(70, 70, 70).getRGB());

        // Buttons
        String[] buttonLabels = {"Singleplayer", "Multiplayer", "Accounts", "Settings", "Exit"};
        double offset = y - 65;

        for (String label : buttonLabels) {
            offset += 23;

            boolean hover = RenderUtil.hovered(mouseX, mouseY, x - 60, offset, 120, 20);

            // Draw button background
            DrawHelper.drawRoundedRect(x - 60, offset - 1, 120, 20, 6, hover ? new Color(33,33,33) : new Color(22, 22, 22));
            DrawHelper.drawRoundedRectOutline(x - 60 - 1, offset - 2, 122, 22, 6, 2, hover ? new Color(14, 103, 95) : new Color(14, 109, 101));

            // Draw button label
            poppins.drawCenteredString(label, x, (float) offset + 2.5f, -1);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution sr = new ScaledResolution(mc);

        float x = (float) sr.getScaledWidth() / 2;
        float y = (float) sr.getScaledHeight() / 2;
        double offset = y - 65;

        if (mouseButton == 0) {
            String[] buttonLabels = {"Singleplayer", "Multiplayer", "Accounts", "Settings", "Exit"};
            for (int i = 0; i < buttonLabels.length; i++) {
                offset += 23;

                if (RenderUtil.hovered(mouseX, mouseY, x - 60, offset, 120, 20)) {
                    switch (i) {
                        case 0:
                            mc.displayGuiScreen(new GuiSelectWorld(this));
                            break;
                        case 1:
                            mc.displayGuiScreen(new GuiMultiplayer(this));
                            break;
                        case 2:
                            mc.displayGuiScreen(new CustomAltManager());
                            break;
                        case 3:
                            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                            break;
                        case 4:
                            mc.shutdown();
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

}
