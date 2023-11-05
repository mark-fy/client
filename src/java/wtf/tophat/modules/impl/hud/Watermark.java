package wtf.tophat.modules.impl.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import wtf.tophat.TopHat;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.modules.impl.render.PostProcessing;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.render.shaders.RoundedUtil;
import wtf.tophat.utilities.render.shaders.blur.GaussianBlur;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static wtf.tophat.utilities.render.Colors.*;

@SuppressWarnings({"ConstantValue", "UnusedAssignment"})
@ModuleInfo(name = "Watermark",desc = "displays the client's watermark", category = Module.Category.HUD)
public class Watermark extends Module {

    private final StringSetting mode, color;
    private final BooleanSetting fontShadow, bps;

    public Watermark() {
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "GameSense", "GameSense", "Modern", "Exhibition"),
                color = new StringSetting(this, "Color", "Gradient", "Gradient", "Astolfo", "Rainbow"),
                fontShadow = new BooleanSetting(this, "Font Shadow", true),
                bps = new BooleanSetting(this, "Blocks per Second", true).setHidden(() -> !mode.is("GameSense"))
        );
        setEnabled(true);
    }
    public void renderIngame() {
        FontRenderer fr = mc.fontRenderer;
        ScaledResolution sr = new ScaledResolution(mc);

        int scrWidth = sr.getScaledWidth();
        int scrHeight = sr.getScaledHeight();

        String text = (TopHat.getName() + "sense | " + Minecraft.getDebugFPS() + " fps | " + mc.getSession().getUsername()).toLowerCase(Locale.ROOT);

        int counter = 0;
        int color = 0;

        switch (this.color.get()) {
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

        switch(mode.get()) {
            case "Exhibition": {
                fr.drawStringWithShadow(String.format("E§7xhibition [§f%s§7] [§f%s FPS§7] [§f%s ms§7]", getCurrentTime(), Minecraft.getDebugFPS(), getPing()), 3, 4, color);
                fr.drawStringWithShadow(String.format("XYZ: §f%s, %s, %s §7b/s: §f%s", Math.round(getX()), Math.round(getY()), Math.round(getZ()), getBPS()), 3, sr.getScaledHeight() - 10, new Color(170,170,170));
                fr.drawStringWithShadow(String.format("Release Build - §f§l%s§7 - User", TopHat.getVersion()), sr.getScaledWidth() - fr.getStringWidth("Release Build - §f§l" + TopHat.getVersion() + "§7 - User") + 5, sr.getScaledHeight() - 10, new Color(170,170,170));
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

                fr.drawStringOptional(fontShadow.get(), text, 11, 12, Color.WHITE);

                int textY = scrHeight - 25;

                if (bps.get()) {
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

                    fr.drawStringOptional(fontShadow.get(), "bps: " + speed, textX + 5, textY + 5, Color.WHITE);
                    textY -= 25;
                }
                break;
            }
            case "Modern": {
                text = TopHat.getName() + " - " + mc.getSession().getUsername();
                int strWidth1 = fr.getStringWidth(text);
                Color backgroundColor = new Color(13, 60, 123);
                Color outlineColor = new Color(255, 255, 255, 25);

                if (TopHat.moduleManager.getByClass(PostProcessing.class).isEnabled() && TopHat.moduleManager.getByClass(PostProcessing.class).blurShader.get()) {
                    GaussianBlur.startBlur();
                    int totalWidth = strWidth1 + 6 + 2 * 2;
                    RoundedUtil.drawRound(5, 5, totalWidth, 20, 8, backgroundColor);
                    GaussianBlur.endBlur(8, 2);
                }

                int outlineX = 5 - 2;
                int outlineWidth = strWidth1 + 6 + 2 * 2;
                RoundedUtil.drawRoundOutline(outlineX, 5, outlineWidth, 20, 8, 0.30f, outlineColor, new Color(color));
                fr.drawString(text, 5 + 4 - 1, 5 + 6, Color.WHITE);
                break;
            }
        }

        counter++;
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
        return dateFormat.format(calendar.getTime());
    }

    @Override
    public void renderDummy() { renderIngame(); }
}
