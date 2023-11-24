/*
    Original Code by: Rise Client (https://riseclient.com/)
    Modified,Fixed & Improved Code by: MarkGG
 */
package tophat.fun.utilities.font;

import tophat.fun.utilities.font.renderer.FontManager;
import tophat.fun.utilities.font.renderer.TTFFontRenderer;

public class CFont {

    public static final FontManager FONT_MANAGER = new FontManager();

    private final TTFFontRenderer fontRenderer = FONT_MANAGER.getFont("Light 18");

    public void drawString(final String text, final double x, final double y, final int color) {
        fontRenderer.drawString(text, (float) x, (float) y, color);
    }

    public void drawStringWithShadow(final String text, final double x, final double y, final int color) {
        fontRenderer.drawString(text, (float) x + 0.5f, (float) y + 0.5f, 0xFF000000);
        fontRenderer.drawString(text, (float) x, (float) y, color);
    }

    public void drawCenteredString(final String text, final double x, final double y, final int color) {
        drawString(text, x - ((int) getWidth(text) >> 1), y, color);
    }

    public float getWidth(final String text) {
        return fontRenderer.getWidth(text);
    }

    public float getHeight() {
        return fontRenderer.getHeight("I");
    }

}
