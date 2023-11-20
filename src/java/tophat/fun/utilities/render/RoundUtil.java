package tophat.fun.utilities.render;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RoundUtil {

    public static void round(double x, double y, double width, double height, double cornerRadius, Color color) {
        int i;
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        ColorUtil.setGLColor(color);
        GL11.glBegin(9);
        double cornerX = x + width - cornerRadius;
        double cornerY = y + height - cornerRadius;
        for (i = 0; i <= 90; i += 30) {
            GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
        }
        cornerX = x + width - cornerRadius;
        cornerY = y + cornerRadius;
        for (i = 90; i <= 180; i += 30) {
            GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
        }
        cornerX = x + cornerRadius;
        cornerY = y + cornerRadius;
        for (i = 180; i <= 270; i += 30) {
            GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
        }
        cornerX = x + cornerRadius;
        cornerY = y + height - cornerRadius;
        for (i = 270; i <= 360; i += 30) {
            GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
        }
        GL11.glEnd();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        ColorUtil.setGLColor(Color.white);
    }

    public static void round(double x, double y, double width, double height, double cornerRadius, int outlineThickness, Color color) {
        int i;
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        ColorUtil.setGLColor(color);
        GL11.glBegin(GL11.GL_LINE_LOOP); // Use GL_LINE_LOOP to draw the outline only
        GL11.glLineWidth((float) outlineThickness);

        double cornerX = x + width - cornerRadius;
        double cornerY = y + height - cornerRadius;

        for (i = 0; i <= 90; i += 30) {
            GL11.glVertex2d(cornerX + Math.sin((double) i * Math.PI / 180.0) * cornerRadius,
                    cornerY + Math.cos((double) i * Math.PI / 180.0) * cornerRadius);
        }

        cornerX = x + width - cornerRadius;
        cornerY = y + cornerRadius;

        for (i = 90; i <= 180; i += 30) {
            GL11.glVertex2d(cornerX + Math.sin((double) i * Math.PI / 180.0) * cornerRadius,
                    cornerY + Math.cos((double) i * Math.PI / 180.0) * cornerRadius);
        }

        cornerX = x + cornerRadius;
        cornerY = y + cornerRadius;

        for (i = 180; i <= 270; i += 30) {
            GL11.glVertex2d(cornerX + Math.sin((double) i * Math.PI / 180.0) * cornerRadius,
                    cornerY + Math.cos((double) i * Math.PI / 180.0) * cornerRadius);
        }

        cornerX = x + cornerRadius;
        cornerY = y + height - cornerRadius;

        for (i = 270; i <= 360; i += 30) {
            GL11.glVertex2d(cornerX + Math.sin((double) i * Math.PI / 180.0) * cornerRadius,
                    cornerY + Math.cos((double) i * Math.PI / 180.0) * cornerRadius);
        }

        GL11.glEnd();
        GL11.glLineWidth(1.0f); // Reset line width to default value
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        ColorUtil.setGLColor(Color.white);
    }


    public static void round2(double x, double y, double width, double height, double cornerRadius, Color color) {
        round2(x, y, width, height, cornerRadius, true, true, true, true, color.getRGB());
    }

    public static void round2(double x, double y, double width, double height, double cornerRadius, boolean leftTop, boolean rightTop, boolean rightBottom, boolean leftBottom, int color) {
        int i;
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(3042);
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        ColorUtil.setGLColor(color);
        GL11.glBegin(9);
        double cornerX = x + width - cornerRadius;
        double cornerY = y + height - cornerRadius;
        if (rightBottom) {
            for (i = 0; i <= 90; ++i) {
                GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
            }
        } else {
            GL11.glVertex2d(x + width, y + height);
        }
        if (rightTop) {
            cornerX = x + width - cornerRadius;
            cornerY = y + cornerRadius;
            for (i = 90; i <= 180; ++i) {
                GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
            }
        } else {
            GL11.glVertex2d(x + width, y);
        }
        if (leftTop) {
            cornerX = x + cornerRadius;
            cornerY = y + cornerRadius;
            for (i = 180; i <= 270; ++i) {
                GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
            }
        } else {
            GL11.glVertex2d(x, y);
        }
        if (leftBottom) {
            cornerX = x + cornerRadius;
            cornerY = y + height - cornerRadius;
            for (i = 270; i <= 360; ++i) {
                GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
            }
        } else {
            GL11.glVertex2d(x, y + height);
        }
        GL11.glEnd();
        ColorUtil.setGLColor(new Color(255, 255, 255, 255));
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
    }

    public static void round3(double x, double y, double width, double height, double cornerRadius, Color color) {
        round3(x, y, width, height, cornerRadius, true, true, true, true, color);
    }

    public static void round3(double x, double y, double width, double height, double cornerRadius, boolean leftTop, boolean rightTop, boolean rightBottom, boolean leftBottom, Color color) {
        int i;
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        ColorUtil.setGLColor(color);
        GL11.glBegin(9);
        double cornerX = x + width - cornerRadius;
        double cornerY = y + height - cornerRadius;
        if (rightBottom) {
            for (i = 0; i <= 90; i += 30) {
                GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
            }
        } else {
            GL11.glVertex2d(x + width, y + height);
        }
        if (rightTop) {
            cornerX = x + width - cornerRadius;
            cornerY = y + cornerRadius;
            for (i = 90; i <= 180; i += 30) {
                GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
            }
        } else {
            GL11.glVertex2d(x + width, y);
        }
        if (leftTop) {
            cornerX = x + cornerRadius;
            cornerY = y + cornerRadius;
            for (i = 180; i <= 270; i += 30) {
                GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
            }
        } else {
            GL11.glVertex2d(x, y);
        }
        if (leftBottom) {
            cornerX = x + cornerRadius;
            cornerY = y + height - cornerRadius;
            for (i = 270; i <= 360; i += 30) {
                GL11.glVertex2d(cornerX + Math.sin((double)i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos((double)i * Math.PI / 180.0) * cornerRadius);
            }
        } else {
            GL11.glVertex2d(x, y + height);
        }
        GL11.glEnd();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        ColorUtil.setGLColor(Color.white);
    }

    public static void round4(double x, double y, double width, double height, int cornerRadius) {
        GL11.glBegin(GL11.GL_QUADS);
        for (int i = 0; i <= 90; i += 30) {
            double cornerX = x + width - cornerRadius;
            double cornerY = y + height - cornerRadius;
            GL11.glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos(i * Math.PI / 180.0) * cornerRadius);
        }
        for (int i = 90; i <= 180; i += 30) {
            double cornerX = x + width - cornerRadius;
            double cornerY = y + cornerRadius;
            GL11.glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos(i * Math.PI / 180.0) * cornerRadius);
        }
        for (int i = 180; i <= 270; i += 30) {
            double cornerX = x + cornerRadius;
            double cornerY = y + cornerRadius;
            GL11.glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos(i * Math.PI / 180.0) * cornerRadius);
        }
        for (int i = 270; i <= 360; i += 30) {
            double cornerX = x + cornerRadius;
            double cornerY = y + height - cornerRadius;
            GL11.glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0) * cornerRadius, cornerY + Math.cos(i * Math.PI / 180.0) * cornerRadius);
        }
        GL11.glEnd();
    }

}
