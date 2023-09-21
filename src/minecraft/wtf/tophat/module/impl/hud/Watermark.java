package wtf.tophat.module.impl.hud;

import net.minecraft.client.Minecraft;
import wtf.tophat.Client;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.module.impl.render.PostProcessing;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.shader.GaussianBlur;
import wtf.tophat.shader.RoundedUtil;
import wtf.tophat.utilities.font.CFontRenderer;
import wtf.tophat.utilities.font.CFontUtil;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;
import java.util.Locale;

import static wtf.tophat.utilities.Colors.DEFAULT_COLOR;
import static wtf.tophat.utilities.Colors.WHITE_COLOR;

@ModuleInfo(name = "Watermark",desc = "displays the client's watermark", category = Module.Category.HUD)
public class Watermark extends Module {

    private final StringSetting mode, color;
    private final BooleanSetting fontShadow;

    public Watermark() {
        Client.settingManager.add(
                mode = new StringSetting(this, "Mode", "GameSense", "GameSense", "Modern"),
                color = new StringSetting(this, "Color", "Gradient", "Gradient", "Astolfo", "Rainbow"),
                fontShadow = new BooleanSetting(this, "Font Shadow", true)
        );
        setEnabled(true);
    }

    public void renderIngame() {

        CFontRenderer fr = CFontUtil.SF_Regular_20.getRenderer();

        String text = (Client.getName() + "sense | " + Minecraft.getDebugFPS() + " fps | " + mc.getSession().getUsername()).toLowerCase(Locale.ROOT);

        int counter = 0;
        int color = 0;

        switch (this.color.getValue()) {
            case "Gradient":
                color = ColorUtil.fadeBetween(DEFAULT_COLOR, WHITE_COLOR, counter * 150L);
                break;
            case "Rainbow":
                color = ColorUtil.getRainbow(3000, (int) (counter * 150L));
                break;
            case "Astolfo":
                color = ColorUtil.blendRainbowColours(counter * 150L);
                break;
        }

        switch(mode.getValue()) {
            case "GameSense":
                int strWidth = fr.getStringWidth(text);
                DrawingUtil.rectangle(5, 5, strWidth + 11, 20, true, new Color(5,5,5));
                DrawingUtil.rectangle(6, 6, strWidth + 9, 18, true, new Color(60,60,60));
                DrawingUtil.rectangle(7, 7, strWidth + 7, 16, true, new Color(40,40,40));
                DrawingUtil.rectangle(9, 9, strWidth + 3, 12, true, new Color(60,60,60));
                DrawingUtil.rectangle(10, 10, strWidth + 1, 10, true, new Color(22,22,22));
                DrawingUtil.rectangle(10, 10, strWidth + 1, 1, true, new Color(color));

                fr.drawStringChoose(fontShadow.getValue(), text, 11, 12, Color.WHITE);
                break;
            case "Modern":
                text = Client.getName() + " - " + mc.getSession().getUsername();
                int strWidth1 = fr.getStringWidth(text);
                if(Client.moduleManager.getByClass(PostProcessing.class).blurShader.getValue()) {
                    GaussianBlur.startBlur();
                    RoundedUtil.drawRound(5, 5, strWidth1 + 6, 20, 8, new Color(13, 60, 123));
                    GaussianBlur.endBlur(8, 2);
                }

                RoundedUtil.drawRoundOutline(5, 5, strWidth1 + 6, 20, 4, 0.30f, new Color(255,255,255,25), new Color(color));
                fr.drawStringChoose(fontShadow.getValue(), text, 7, 10, Color.WHITE);
                break;
        }


        counter++;
    }

    @Override
    public void renderDummy() { renderIngame(); }
}
