package wtf.tophat.module.impl.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
import wtf.tophat.Client;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.module.impl.render.PostProcessing;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.shader.RoundedUtil;
import wtf.tophat.shader.blur.GaussianBlur;
import wtf.tophat.utilities.font.CFontRenderer;
import wtf.tophat.utilities.font.CFontUtil;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;
import java.util.Locale;

import static wtf.tophat.utilities.Colors.*;
import static wtf.tophat.utilities.Colors.LIGHT_GORGE_COLOR;

@ModuleInfo(name = "Watermark",desc = "displays the client's watermark", category = Module.Category.HUD)
public class Watermark extends Module {

    private final StringSetting mode, color;
    private final BooleanSetting fontShadow, bps;

    public Watermark() {
        Client.settingManager.add(
                mode = new StringSetting(this, "Mode", "GameSense", "GameSense", "Modern", "Floyd"),
                color = new StringSetting(this, "Color", "Gradient", "Gradient", "Astolfo", "Rainbow", "Brown"),
                fontShadow = new BooleanSetting(this, "Font Shadow", true),
                bps = new BooleanSetting(this, "Blocks per Second", true)
        );
        setEnabled(true);
    }

    @Override
    public void onEnable() {
        if(mode.compare("Floyd")) {
            Display.setTitle("I can't breathe!");
        }
        super.onEnable();
    }

    public void renderIngame() {
        CFontRenderer fr = CFontUtil.SF_Regular_20.getRenderer();
        ScaledResolution sr = new ScaledResolution(mc);

        int scrWidth = sr.getScaledWidth();
        int scrHeight = sr.getScaledHeight();

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
            case "Brown":
                color = new Color(ColorUtil.fadeBetween(GORGE_COLOR, LIGHT_GORGE_COLOR, counter * 150L),true).getRGB();
                break;
        }

        switch(mode.getValue()) {
            case "Floyd": {
                RoundedUtil.drawRound(5, 5, 96, 105, 8, new Color(color));
                if (Client.moduleManager.getByClass(PostProcessing.class).isEnabled() && Client.moduleManager.getByClass(PostProcessing.class).blurShader.getValue()) {
                    GaussianBlur.startBlur();
                    RoundedUtil.drawRound(5, 5, 96, 105, 8, new Color(13, 60, 123));
                    GaussianBlur.endBlur(8, 2);
                }

                fr.drawString("Breathe - " + Client.getVersion(), 15 ,6, Color.WHITE);
                mc.getTextureManager().bindTexture(new ResourceLocation("tophat/gorge.png"));
                Gui.drawModalRectWithCustomSizedTexture(7, 15, 0,0, 92, 92, 92,92);
                break;
            }
            case "GameSense": {
                int strWidth = fr.getStringWidth(text);

                DrawingUtil.rectangle(5, 5, strWidth + 11, 20, true, new Color(5, 5, 5));
                DrawingUtil.rectangle(6, 6, strWidth + 9, 18, true, new Color(60, 60, 60));
                DrawingUtil.rectangle(7, 7, strWidth + 7, 16, true, new Color(40, 40, 40));
                DrawingUtil.rectangle(9, 9, strWidth + 3, 12, true, new Color(60, 60, 60));
                DrawingUtil.rectangle(10, 10, strWidth + 1, 10, true, new Color(22, 22, 22));
                DrawingUtil.rectangle(10, 10, strWidth + 1, 1, true, new Color(color));

                fr.drawStringChoose(fontShadow.getValue(), text, 11, 12, Color.WHITE);

                int textY = scrHeight - 25;

                if (bps.getValue()) {
                    final double motionX = mc.player.posX - mc.player.prevPosX;
                    final double motionZ = mc.player.posZ - mc.player.prevPosZ;
                    double speed = Math.sqrt(motionX * motionX + motionZ * motionZ) * 20 * mc.timer.timerSpeed;
                    speed = Math.round(speed * 10);
                    speed = speed / 10;

                    int strWidth1 = fr.getStringWidth("bps: " + speed) + 2;

                    int textX = scrWidth - strWidth1 - 15;

                    DrawingUtil.rectangle(textX - 1, textY - 1, strWidth1 + 11, 20, true, new Color(5, 5, 5));
                    DrawingUtil.rectangle(textX, textY, strWidth1 + 9, 18, true, new Color(60, 60, 60));
                    DrawingUtil.rectangle(textX + 1, textY + 1, strWidth1 + 7, 16, true, new Color(40, 40, 40));
                    DrawingUtil.rectangle(textX + 3, textY + 3, strWidth1 + 3, 12, true, new Color(60, 60, 60));
                    DrawingUtil.rectangle(textX + 4, textY + 4, strWidth1 + 1, 10, true, new Color(22, 22, 22));
                    DrawingUtil.rectangle(textX + 4, textY + 4, strWidth1 + 1, 1, true, new Color(color));

                    fr.drawStringChoose(fontShadow.getValue(), "bps: " + speed, textX + 5, textY + 5, Color.WHITE);
                    textY -= 25;
                }
                break;
            }
            case "Modern": {
                text = Client.getName() + " - " + mc.getSession().getUsername();
                int strWidth1 = fr.getStringWidth(text);
                if (Client.moduleManager.getByClass(PostProcessing.class).isEnabled() && Client.moduleManager.getByClass(PostProcessing.class).blurShader.getValue()) {
                    GaussianBlur.startBlur();
                    RoundedUtil.drawRound(5, 5, strWidth1 + 6, 20, 8, new Color(13, 60, 123));
                    GaussianBlur.endBlur(8, 2);
                }

                RoundedUtil.drawRoundOutline(5, 5, strWidth1 + 6, 20, 4, 0.30f, new Color(255, 255, 255, 25), new Color(color));
                fr.drawStringChoose(fontShadow.getValue(), text, 7, 10, Color.WHITE);
                break;
            }
        }

        counter++;
    }

    @Override
    public void renderDummy() { renderIngame(); }
}
