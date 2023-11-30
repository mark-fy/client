package tophat.fun.utilities.render;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import tophat.fun.utilities.Methods;

public class RenderUtil implements Methods {

    public static void drawSkinHead(EntityLivingBase player, float x, float y, int size) {
        if (!(player instanceof EntityPlayer))
            return;

        try {
            GL11.glPushMatrix();
            mc.getTextureManager().bindTexture(((AbstractClientPlayer) player).getLocationSkin());

            Gui.drawScaledCustomSizeModalRect((int) x, (int) y, 8, 8, 8, 8, size, size, 64, 64);
            GL11.glPopMatrix();
        } catch (Exception ignored) {
        }
    }

    public static boolean hovered(int mouseX, int mouseY, double x, double y, float width, float height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

}
