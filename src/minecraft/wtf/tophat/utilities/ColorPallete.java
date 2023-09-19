package wtf.tophat.utilities;

import wtf.tophat.Client;
import wtf.tophat.module.impl.client.Theme;

import java.awt.*;

public class ColorPallete {

    private static final Theme theme = Client.moduleManager.getByClass(Theme.class);

    public static final int DEFAULT_COLOR = theme.clientTheme.get().getRGB();
    public static final int WHITE_COLOR = new Color(255,255,255).getRGB();
    public static final int DEFAULT_COLOR_DARKER = theme.clientTheme.get().darker().getRGB();

}
