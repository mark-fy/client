package tophat.fun.utilities.render;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RoundUtil {

    public static void drawRoundedRect(double x, double y, double width, double height, double cornerRadius, Color color) {
        int i;
        GL11.glPushMatrix();
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glBlendFunc((int)770, (int)771);
        ColorUtil.setGLColor(color);
        GL11.glBegin((int)9);
        double cornerX = x + width - cornerRadius;
        double cornerY = y + height - cornerRadius;
        for (i = 0; i <= 90; i += 30) {
            GL11.glVertex2d((double)(cornerX + Math.sin((double)((double)i * Math.PI / 180.0)) * cornerRadius), (double)(cornerY + Math.cos((double)((double)i * Math.PI / 180.0)) * cornerRadius));
        }
        cornerX = x + width - cornerRadius;
        cornerY = y + cornerRadius;
        for (i = 90; i <= 180; i += 30) {
            GL11.glVertex2d((double)(cornerX + Math.sin((double)((double)i * Math.PI / 180.0)) * cornerRadius), (double)(cornerY + Math.cos((double)((double)i * Math.PI / 180.0)) * cornerRadius));
        }
        cornerX = x + cornerRadius;
        cornerY = y + cornerRadius;
        for (i = 180; i <= 270; i += 30) {
            GL11.glVertex2d((double)(cornerX + Math.sin((double)((double)i * Math.PI / 180.0)) * cornerRadius), (double)(cornerY + Math.cos((double)((double)i * Math.PI / 180.0)) * cornerRadius));
        }
        cornerX = x + cornerRadius;
        cornerY = y + height - cornerRadius;
        for (i = 270; i <= 360; i += 30) {
            GL11.glVertex2d((double)(cornerX + Math.sin((double)((double)i * Math.PI / 180.0)) * cornerRadius), (double)(cornerY + Math.cos((double)((double)i * Math.PI / 180.0)) * cornerRadius));
        }
        GL11.glEnd();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glPopMatrix();
        ColorUtil.setGLColor(Color.white);
    }

    public static void drawRoundedRect2(double x, double y, double width, double height, double cornerRadius, Color color) {
        drawRoundedRect2(x, y, width, height, cornerRadius, true, true, true, true, color.getRGB());
    }

    public static void drawRoundedRect2(double x, double y, double width, double height, double cornerRadius, boolean leftTop, boolean rightTop, boolean rightBottom, boolean leftBottom, int color) {
        int i;
        GL11.glPushMatrix();
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glEnable((int)3042);
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        ColorUtil.setGLColor(color);
        GL11.glBegin((int)9);
        double cornerX = x + width - cornerRadius;
        double cornerY = y + height - cornerRadius;
        if (rightBottom) {
            for (i = 0; i <= 90; ++i) {
                GL11.glVertex2d((double)(cornerX + Math.sin((double)((double)i * Math.PI / 180.0)) * cornerRadius), (double)(cornerY + Math.cos((double)((double)i * Math.PI / 180.0)) * cornerRadius));
            }
        } else {
            GL11.glVertex2d((double)(x + width), (double)(y + height));
        }
        if (rightTop) {
            cornerX = x + width - cornerRadius;
            cornerY = y + cornerRadius;
            for (i = 90; i <= 180; ++i) {
                GL11.glVertex2d((double)(cornerX + Math.sin((double)((double)i * Math.PI / 180.0)) * cornerRadius), (double)(cornerY + Math.cos((double)((double)i * Math.PI / 180.0)) * cornerRadius));
            }
        } else {
            GL11.glVertex2d((double)(x + width), (double)y);
        }
        if (leftTop) {
            cornerX = x + cornerRadius;
            cornerY = y + cornerRadius;
            for (i = 180; i <= 270; ++i) {
                GL11.glVertex2d((double)(cornerX + Math.sin((double)((double)i * Math.PI / 180.0)) * cornerRadius), (double)(cornerY + Math.cos((double)((double)i * Math.PI / 180.0)) * cornerRadius));
            }
        } else {
            GL11.glVertex2d((double)x, (double)y);
        }
        if (leftBottom) {
            cornerX = x + cornerRadius;
            cornerY = y + height - cornerRadius;
            for (i = 270; i <= 360; ++i) {
                GL11.glVertex2d((double)(cornerX + Math.sin((double)((double)i * Math.PI / 180.0)) * cornerRadius), (double)(cornerY + Math.cos((double)((double)i * Math.PI / 180.0)) * cornerRadius));
            }
        } else {
            GL11.glVertex2d((double)x, (double)(y + height));
        }
        GL11.glEnd();
        ColorUtil.setGLColor(new Color(255, 255, 255, 255));
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glPopMatrix();
    }

    public static void drawRoundedRect3(double x, double y, double width, double height, double cornerRadius, Color color) {
        drawRoundedRect3(x, y, width, height, cornerRadius, true, true, true, true, color);
    }

    public static void drawRoundedRect3(double x, double y, double width, double height, double cornerRadius, boolean leftTop, boolean rightTop, boolean rightBottom, boolean leftBottom, Color color) {
        int i;
        GL11.glPushMatrix();
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        ColorUtil.setGLColor(color);
        GL11.glBegin((int)9);
        double cornerX = x + width - cornerRadius;
        double cornerY = y + height - cornerRadius;
        if (rightBottom) {
            for (i = 0; i <= 90; i += 30) {
                GL11.glVertex2d((double)(cornerX + Math.sin((double)((double)i * Math.PI / 180.0)) * cornerRadius), (double)(cornerY + Math.cos((double)((double)i * Math.PI / 180.0)) * cornerRadius));
            }
        } else {
            GL11.glVertex2d((double)(x + width), (double)(y + height));
        }
        if (rightTop) {
            cornerX = x + width - cornerRadius;
            cornerY = y + cornerRadius;
            for (i = 90; i <= 180; i += 30) {
                GL11.glVertex2d((double)(cornerX + Math.sin((double)((double)i * Math.PI / 180.0)) * cornerRadius), (double)(cornerY + Math.cos((double)((double)i * Math.PI / 180.0)) * cornerRadius));
            }
        } else {
            GL11.glVertex2d((double)(x + width), (double)y);
        }
        if (leftTop) {
            cornerX = x + cornerRadius;
            cornerY = y + cornerRadius;
            for (i = 180; i <= 270; i += 30) {
                GL11.glVertex2d((double)(cornerX + Math.sin((double)((double)i * Math.PI / 180.0)) * cornerRadius), (double)(cornerY + Math.cos((double)((double)i * Math.PI / 180.0)) * cornerRadius));
            }
        } else {
            GL11.glVertex2d((double)x, (double)y);
        }
        if (leftBottom) {
            cornerX = x + cornerRadius;
            cornerY = y + height - cornerRadius;
            for (i = 270; i <= 360; i += 30) {
                GL11.glVertex2d((double)(cornerX + Math.sin((double)((double)i * Math.PI / 180.0)) * cornerRadius), (double)(cornerY + Math.cos((double)((double)i * Math.PI / 180.0)) * cornerRadius));
            }
        } else {
            GL11.glVertex2d((double)x, (double)(y + height));
        }
        GL11.glEnd();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glPopMatrix();
        ColorUtil.setGLColor(Color.white);
    }

}
