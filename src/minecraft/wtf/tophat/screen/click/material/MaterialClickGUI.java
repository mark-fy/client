package wtf.tophat.screen.click.material;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import wtf.tophat.Client;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.impl.render.PostProcessing;
import wtf.tophat.shader.blur.GaussianBlur;
import wtf.tophat.utilities.font.CFontRenderer;
import wtf.tophat.utilities.font.CFontUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;
import java.io.IOException;
import java.util.Locale;

public class MaterialClickGUI extends GuiScreen {

    @Override
    public void initGui() {
        ScaledResolution sr = new ScaledResolution(mc);

        double width = 350;
        double height = 350;
        frameX = (sr.getScaledWidth_double() - width) / 2;
        frameY = (sr.getScaledHeight_double() - height) / 2;

        super.initGui();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private final Module.Category defaultCategory = Module.Category.COMBAT;

    private double frameX;
    private double frameY;
    private boolean isDragging = false;
    private int dragOffsetX, dragOffsetY;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        CFontRenderer fr = CFontUtil.SF_Regular_20.getRenderer();

        renderBlur();

        double width = 350;
        double height = 350;
        double x = frameX;
        double y = frameY;

        if (isDragging) {
            double newX = mouseX - dragOffsetX;
            double newY = mouseY - dragOffsetY;
            // Ensure the frame stays within the screen boundaries
            newX = Math.max(0, Math.min(sr.getScaledWidth_double() - width, newX));
            newY = Math.max(0, Math.min(sr.getScaledHeight_double() - height, newY));
            frameX = newX;
            frameY = newY;
        }

        if (!Mouse.isButtonDown(0)) {
            isDragging = false;
        }

        // Main Frame
        DrawingUtil.rectangle(x, y, width, height, true, new Color(20, 20, 20));
        DrawingUtil.rectangle(x, y, width, height, false, new Color(0, 85, 255));

        // Drag Bar
        DrawingUtil.rectangle(x, y, width - 1, 15, true, new Color(30, 30, 30));

        boolean mouseHovered = mouseX >= x + 340 && mouseX <= x + 340 + fr.getStringWidth("X") && mouseY >= y + 3 && mouseY <= y + 3 + fr.getHeight();

        fr.drawString("X", x + 340, y + 3, mouseHovered ? new Color(0, 85, 255) : Color.WHITE);
        fr.drawString(Client.getName().toLowerCase(Locale.ROOT), x + 3, y + 3, new Color(0, 85, 255));

        // Category Box
        DrawingUtil.rectangle(x, y + 15, 50, 334, true, new Color(30, 30, 30));

        int counter = 30;
        for(Module.Category category : Module.Category.values()) {
            if(category.equals(Module.Category.COMBAT)) {
                mc.getTextureManager().bindTexture(new ResourceLocation("tophat/categories/combat.png"));
                drawModalRectWithCustomSizedTexture((int) (x + 2), (int) y + 20, 0,0, 48, 48, 48,48);
            }

            if(category.equals(Module.Category.MOVE)) {
                mc.getTextureManager().bindTexture(new ResourceLocation("tophat/categories/move.png"));
                drawModalRectWithCustomSizedTexture((int) (x + 2), (int) y + 68, 0,0, 48, 48, 48,48);
            }

            if(category.equals(Module.Category.PLAYER)) {
                mc.getTextureManager().bindTexture(new ResourceLocation("tophat/categories/player.png"));
                drawModalRectWithCustomSizedTexture((int) (x + 2), (int) y + 116, 0,0, 48, 48, 48,48);
            }

            if(category.equals(Module.Category.RENDER)) {
                mc.getTextureManager().bindTexture(new ResourceLocation("tophat/categories/render.png"));
                drawModalRectWithCustomSizedTexture((int) (x + 2), (int) y + 164, 0,0, 48, 48, 48,48);
            }

            if(category.equals(Module.Category.MISC)) {
                mc.getTextureManager().bindTexture(new ResourceLocation("tophat/categories/misc.png"));
                drawModalRectWithCustomSizedTexture((int) (x + 2), (int) y + 212, 0,0, 48, 48, 48,48);
            }

            if(category.equals(Module.Category.HUD)) {
                mc.getTextureManager().bindTexture(new ResourceLocation("tophat/categories/hud.png"));
                drawModalRectWithCustomSizedTexture((int) (x + 2), (int) y + 260, 0,0, 48, 48, 48,48);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        CFontRenderer fr = CFontUtil.SF_Regular_20.getRenderer();

        if (mouseButton == 0) {
            double width = 350;
            double x = frameX;
            double y = frameY;

            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 15) {
                isDragging = true;
                dragOffsetX = mouseX - (int) x;
                dragOffsetY = mouseY - (int) y;
            }

            // Check if the mouse click occurred on the "X" text area and close the GUI
            double xText = x + 340;
            double yText = y + 3;
            if (mouseX >= xText && mouseX <= xText + fr.getStringWidth("X") && mouseY >= yText && mouseY <= yText + fr.getHeight()) {
                mc.displayGuiScreen(null); // Close the GUI
            }
        }
    }

    private void renderBlur() {
        if(Client.moduleManager.getByClass(PostProcessing.class).isEnabled() && Client.moduleManager.getByClass(PostProcessing.class).blurShader.getValue()) {
            GaussianBlur.startBlur();
            DrawingUtil.rectangle(0, 0, width, height, true, new Color(0,0,0));
            GaussianBlur.endBlur(10, 2);
        }
    }
}
