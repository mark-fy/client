/*
    Original Code by: Rise Client (https://riseclient.com/)
    Modified,Fixed & Improved Code by: MarkGG
 */
package tophat.fun.utilities.font.renderer;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class FontManager {

    private final HashMap<String, TTFFontRenderer> fonts = new HashMap<>();
    private final TTFFontRenderer defaultFont;
    private final ThreadPoolExecutor executorService;
    private final ConcurrentLinkedQueue<TextureData> textureQueue;

    public TTFFontRenderer getFont(final String key) {
        return this.fonts.getOrDefault(key, this.defaultFont);
    }

    public FontManager() {

        this.executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        this.textureQueue = new ConcurrentLinkedQueue<>();

        this.defaultFont = new TTFFontRenderer(executorService, textureQueue, new Font("PoppinsRegular", Font.PLAIN, 18));

        try {
            loadFont("/assets/minecraft/tophat/font/verdana/Verdana.ttf", "Verdana", new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 32, 128}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/verdana/Verdana-Bold.ttf", "VerdanaBold", new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 32, 128}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/verdana/Verdana-Bold-Italic.ttf", "VerdanaBoldItalic", new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 32, 128}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/others/Biko-Regular.otf", "Biko", new int[]{16, 18, 28, 36, 48}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/others/Helvetica-Neue-Thin.ttf", "HelveticaThin", new int[]{18, 20, 24, 36, 48, 72, 96}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/icons/Regular-Icons.ttf", "RegularIcons", new int[]{18}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/icons/Regular-Icons2.ttf", "RegularIcons2", new int[]{18}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/icons/Arrow-Icons.otf", "ArrowIcons", new int[]{18}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/icons/CSGO-Icons.ttf", "CSGOIcons", new int[]{18}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/sf_pro_display/SF-Pro-SemiBold.ttf", "SFProSemiBold", new int[]{16, 18, 20, 22, 24, 32}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/sf_ui_display/SF-UI-Light.ttf", "SFUILight", new int[]{16, 18, 20, 22, 24, 32}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/sf_ui_display/SF-UI-Regular.ttf", "SFUIRegular", new int[]{16, 18, 20, 22, 24, 32}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/poppins/Poppins-Thin.ttf", "PoppinsThin", new int[]{16, 18, 20, 22, 24, 32}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/poppins/Poppins-SemiBold.ttf", "PoppinsSemiBold", new int[]{14, 16, 18, 20, 22, 24, 32}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/poppins/Poppins-Regular.ttf", "PoppinsRegular", new int[]{16, 18, 20, 22, 24, 32}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/poppins/Poppins-Medium.ttf", "PoppinsMedium", new int[]{14, 16, 18, 20, 22, 24, 32}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/poppins/Poppins-Light.ttf", "PoppinsLight", new int[]{16, 18, 20, 22, 24, 32}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/poppins/Poppins-ExtraLight.ttf", "PoppinsExtraLight", new int[]{16, 18, 20, 22, 24, 32}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/poppins/Poppins-ExtraBold.ttf", "PoppinsExtraBold", new int[]{16, 18, 20, 22, 24, 32}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/poppins/Poppins-Black.ttf", "PoppinsBlack", new int[]{16, 18, 20, 22, 24, 32}, Font.PLAIN);
            loadFont("/assets/minecraft/tophat/font/poppins/Poppins-Bold.ttf", "PoppinsBold", new int[]{16, 18, 20, 22, 24, 32}, Font.PLAIN);
        } catch (Exception ignored) {
        }

        executorService.shutdown();

        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(10L);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
            while (!textureQueue.isEmpty()) {
                final TextureData textureData = textureQueue.poll();
                GlStateManager.bindTexture(textureData.getTextureId());
                GL11.glTexParameteri(3553, 10241, 9728);
                GL11.glTexParameteri(3553, 10240, 9728);
                GL11.glTexImage2D(3553, 0, 6408, textureData.getWidth(), textureData.getHeight(), 0, 6408, 5121, textureData.getBuffer());
            }
        }
    }

    private void loadFont(String fontPath, String fontName, int[] sizes, int style) throws Exception {
        for (int size : sizes) {
            InputStream iStream = this.getClass().getResourceAsStream(fontPath);
            Font myFont = Font.createFont(0, iStream);
            myFont = myFont.deriveFont(style, (float) size);
            this.fonts.put(fontName + " " + size, new TTFFontRenderer(executorService, textureQueue, myFont));
        }
    }

}
