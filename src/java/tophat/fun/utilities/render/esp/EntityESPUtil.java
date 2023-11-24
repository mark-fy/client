package tophat.fun.utilities.render.esp;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import tophat.fun.utilities.Methods;

import java.awt.*;

public class EntityESPUtil implements Methods {

    public static void renderBoxESP(EntityLivingBase player, double renderPosX, double renderPosY, double renderPosZ, Color color) {
        double width = player.width;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableDepth();
        GlStateManager.disableCull();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(3.0F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(renderPosX, renderPosY, renderPosZ);
        GlStateManager.rotate(-player.rotationYaw, 0.0F, 1.0F, 0.0F);

        double boxLeft = -width / 2.0;
        double boxRight = width / 2.0;
        double boxTop = player.height;
        double boxBottom = 0;
        double boxFront = -width / 2.0;
        double boxBack = width / 2.0;

        GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GL11.glBegin(GL11.GL_QUADS);
        // Front face
        GL11.glVertex3d(boxLeft, boxTop, boxFront);
        GL11.glVertex3d(boxRight, boxTop, boxFront);
        GL11.glVertex3d(boxRight, boxBottom, boxFront);
        GL11.glVertex3d(boxLeft, boxBottom, boxFront);
        // Back face
        GL11.glVertex3d(boxLeft, boxTop, boxBack);
        GL11.glVertex3d(boxRight, boxTop, boxBack);
        GL11.glVertex3d(boxRight, boxBottom, boxBack);
        GL11.glVertex3d(boxLeft, boxBottom, boxBack);
        // Top face
        GL11.glVertex3d(boxLeft, boxTop, boxFront);
        GL11.glVertex3d(boxRight, boxTop, boxFront);
        GL11.glVertex3d(boxRight, boxTop, boxBack);
        GL11.glVertex3d(boxLeft, boxTop, boxBack);
        // Bottom face
        GL11.glVertex3d(boxLeft, boxBottom, boxFront);
        GL11.glVertex3d(boxRight, boxBottom, boxFront);
        GL11.glVertex3d(boxRight, boxBottom, boxBack);
        GL11.glVertex3d(boxLeft, boxBottom, boxBack);
        // Left face
        GL11.glVertex3d(boxLeft, boxTop, boxFront);
        GL11.glVertex3d(boxLeft, boxTop, boxBack);
        GL11.glVertex3d(boxLeft, boxBottom, boxBack);
        GL11.glVertex3d(boxLeft, boxBottom, boxFront);
        // Right face
        GL11.glVertex3d(boxRight, boxTop, boxFront);
        GL11.glVertex3d(boxRight, boxTop, boxBack);
        GL11.glVertex3d(boxRight, boxBottom, boxBack);
        GL11.glVertex3d(boxRight, boxBottom, boxFront);
        GL11.glEnd();

        GlStateManager.enableDepth();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

}
