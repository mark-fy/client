package wtf.tophat.utilities.render.font;

import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public enum CFontUtil {

    SF_Regular_12 ("SF Regular 12", new CFontRenderer(new ResourceLocation("tophat/fonts/SF Regular.ttf"), 12)),
    SF_Regular_16 ("SF Regular 16", new CFontRenderer(new ResourceLocation("tophat/fonts/SF Regular.ttf"), 16)),
    SF_Regular_18 ("SF Regular 18", new CFontRenderer(new ResourceLocation("tophat/fonts/SF Regular.ttf"), 18)),
    SF_Regular_20 ("SF Regular 20", new CFontRenderer(new ResourceLocation("tophat/fonts/SF Regular.ttf"), 20)),
    SF_Regular_24 ("SF Regular 24", new CFontRenderer(new ResourceLocation("tophat/fonts/SF Regular.ttf"), 24)),
    SF_Regular_28 ("SF Regular 28", new CFontRenderer(new ResourceLocation("tophat/fonts/SF Regular.ttf"), 28)),
    SF_Regular_32 ("SF Regular 32", new CFontRenderer(new ResourceLocation("tophat/fonts/SF Regular.ttf"), 32)),
    SF_Regular_36 ("SF Regular 36", new CFontRenderer(new ResourceLocation("tophat/fonts/SF Regular.ttf"), 36)),
    SF_Regular_40 ("SF Regular 40", new CFontRenderer(new ResourceLocation("tophat/fonts/SF Regular.ttf"), 40)),
    SF_Semibold_12 ("SF Semibold 12", new CFontRenderer(new ResourceLocation("tophat/fonts/SF Semibold.ttf"), 12)),
    SF_Semibold_16 ("SF Semibold 16", new CFontRenderer(new ResourceLocation("tophat/fonts/SF Semibold.ttf"), 16)),
    SF_Semibold_18 ("SF Semibold 18", new CFontRenderer(new ResourceLocation("tophat/fonts/SF Semibold.ttf"), 18)),
    SF_Semibold_20 ("SF Semibold 20", new CFontRenderer(new ResourceLocation("tophat/fonts/SF Semibold.ttf"), 20)),
    SF_Semibold_24 ("SF Semibold 24", new CFontRenderer(new ResourceLocation("tophat/fonts/SF Semibold.ttf"), 24)),
    SF_Semibold_28 ("SF Semibold 28", new CFontRenderer(new ResourceLocation("tophat/fonts/SF Semibold.ttf"), 28)),
    SF_Semibold_32 ("SF Semibold 32", new CFontRenderer(new ResourceLocation("tophat/fonts/SF Semibold.ttf"), 32)),
    SF_Semibold_36 ("SF Semibold 36", new CFontRenderer(new ResourceLocation("tophat/fonts/SF Semibold.ttf"), 36)),
    SF_Semibold_40 ("SF Semibold 40", new CFontRenderer(new ResourceLocation("tophat/fonts/SF Semibold.ttf"), 40)),
    ICONS_20 ("ICONS 20", new CFontRenderer(new ResourceLocation("tophat/fonts/Icons.ttf"), 20)),
    ICONS_24 ("ICONS 24", new CFontRenderer(new ResourceLocation("tophat/fonts/Icons.ttf"), 24)),
    ICONS_28 ("ICONS 28", new CFontRenderer(new ResourceLocation("tophat/fonts/Icons.ttf"), 28)),
    ICONS_30 ("ICONS 30", new CFontRenderer(new ResourceLocation("tophat/fonts/Icons.ttf"), 30)),
    ICONS_36 ("ICONS 36", new CFontRenderer(new ResourceLocation("tophat/fonts/Icons.ttf"), 36)),
    ICONS_40 ("ICONS 40", new CFontRenderer(new ResourceLocation("tophat/fonts/Icons.ttf"), 40)),
    ICONS_50 ("ICONS 50", new CFontRenderer(new ResourceLocation("tophat/fonts/Icons.ttf"), 50));

    public final String name;
    public final CFontRenderer renderer;

    CFontUtil(String name, CFontRenderer renderer) {
        this.name = name;
        this.renderer = renderer;
    }

    public String getName() {
        return name;
    }

    public CFontRenderer getRenderer() {
        return renderer;
    }

    public static CFontUtil getFontByName(String name) {
        return Arrays.stream(values()).filter(font -> font.getName().equalsIgnoreCase(name)).findFirst().get();
    }
}