package wtf.spotify;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorUtil {

    public static Color averageColor(BufferedImage bi, int width, int height, int pixelStep) {

        int[] color = new int[3];
        for (int x = 0; x < width; x += pixelStep) {
            for (int y = 0; y < height; y += pixelStep) {
                Color pixel = new Color(bi.getRGB(x, y));
                color[0] += pixel.getRed();
                color[1] += pixel.getGreen();
                color[2] += pixel.getBlue();
            }
        }
        int num = width * height;
        return new Color(color[0] / num, color[1] / num, color[2] / num);
    }
}
