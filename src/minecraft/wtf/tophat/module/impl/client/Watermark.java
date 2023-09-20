package wtf.tophat.module.impl.client;

import net.minecraft.client.Minecraft;
import wtf.tophat.Client;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.utilities.font.CFontRenderer;
import wtf.tophat.utilities.font.CFontUtil;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;
import java.util.Locale;

import static wtf.tophat.utilities.ColorPallete.DEFAULT_COLOR;
import static wtf.tophat.utilities.ColorPallete.WHITE_COLOR;

@ModuleInfo(name = "Watermark",desc = "displays the client's watermark", category = Module.Category.CLIENT)
public class Watermark extends Module {

    private final BooleanSetting fontShadow;

    public Watermark() {
        Client.settingManager.add(
                fontShadow = new BooleanSetting(this, "Font Shadow", true)
        );
        setEnabled(true);
    }

    public void renderIngame() {
        int counter = 0;

        CFontRenderer fr = CFontUtil.SF_Semibold_20.getRenderer();

        String text = (Client.getName() + "sense | " + Minecraft.getDebugFPS() + " fps | " + mc.getSession().getUsername()).toLowerCase(Locale.ROOT);
        int strWidth = fr.getStringWidth(text);
        DrawingUtil.rectangle(5, 5, strWidth + 11, 20, true, new Color(5,5,5));
        DrawingUtil.rectangle(6, 6, strWidth + 9, 18, true, new Color(60,60,60));
        DrawingUtil.rectangle(7, 7, strWidth + 7, 16, true, new Color(40,40,40));
        DrawingUtil.rectangle(9, 9, strWidth + 3, 12, true, new Color(60,60,60));
        DrawingUtil.rectangle(10, 10, strWidth + 1, 10, true, new Color(22,22,22));
        DrawingUtil.rectangle(10, 10, strWidth + 1, 1, true, new Color(ColorUtil.fadeBetween(DEFAULT_COLOR, WHITE_COLOR, counter * 150L)));

        fr.drawStringChoose(fontShadow.getValue(), text, 11, 12, Color.WHITE);
        counter++;
    }

    @Override
    public void renderDummy() { renderIngame(); }
}
