package tophat.fun.utilities.render;

import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ColorUtil {

    // Set's the GL11 color4f value using an rgb color value.
    public static void setGLColor(Color color) {
        float r = (float)color.getRed() / 255.0f;
        float g = (float)color.getGreen() / 255.0f;
        float b = (float)color.getBlue() / 255.0f;
        float a = (float)color.getAlpha() / 255.0f;
        GL11.glColor4f((float)r, (float)g, (float)b, (float)a);
    }

    // Set's the GL11 color4f value using an int color value.
    public static void setGLColor(int color) {
        setGLColor(new Color(color));
    }

    // Set's the GL11 color4f value using manual color input.
    public static void color(float red, float green, float blue, float alpha) {
        GL11.glColor4f((float)(red / 255.0f), (float)(green / 255.0f), (float)(blue / 255.0f), (float)(alpha / 255.0f));
    }

    // Set's the GL11 color4f value using an RGB value.
    public static void color(Color color) {
        color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

}