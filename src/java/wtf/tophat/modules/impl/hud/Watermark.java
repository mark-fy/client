package wtf.tophat.modules.impl.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import wtf.tophat.TopHat;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.modules.impl.render.PostProcessing;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.render.shaders.RoundedUtil;
import wtf.tophat.utilities.render.shaders.blur.GaussianBlur;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@SuppressWarnings({"ConstantValue", "UnusedAssignment"})
@ModuleInfo(name = "Watermark",desc = "displays the client's watermark", category = Module.Category.HUD)
public class Watermark extends Module {

    private final StringSetting mode, color;
    private final BooleanSetting fontShadow, BPS, exhiXYZ, exhiBPS, exhiVersion;
    private final NumberSetting red, green, blue, red1, green1, blue1, darkFactor;

    public Watermark() {
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "GameSense", "GameSense", "Modern", "Exhibition"),
                color = new StringSetting(this, "Color", "Gradient", "Gradient", "Fade", "Astolfo", "Rainbow"),
                fontShadow = new BooleanSetting(this, "Font Shadow", true).setHidden(() -> !mode.is("GameSense")),
                BPS = new BooleanSetting(this, "BPS", true).setHidden(() -> !mode.is("GameSense")),
                exhiXYZ = new BooleanSetting(this, "XYZ", true).setHidden(() -> !mode.is("Exhibition")),
                exhiBPS = new BooleanSetting(this, "BPS", true).setHidden(() -> !mode.is("Exhibition")),
                exhiVersion = new BooleanSetting(this, "Build Version", true).setHidden(() -> !mode.is("Exhibition")),
                red = new NumberSetting(this, "Red", 0, 255, 95, 0).setHidden(() -> !color.is("Gradient") && !color.is("Fade")),
                green = new NumberSetting(this, "Green", 0, 255, 61, 0).setHidden(() -> !color.is("Gradient") && !color.is("Fade")),
                blue = new NumberSetting(this, "Blue", 0, 255, 248, 0).setHidden(() -> !color.is("Gradient") && !color.is("Fade")),
                red1 = new NumberSetting(this, "Second Red", 0, 255, 255, 0).setHidden(() -> !color.is("Gradient")),
                green1 = new NumberSetting(this, "Second Green", 0, 255, 255, 0).setHidden(() -> !color.is("Gradient")),
                blue1 = new NumberSetting(this, "Second Blue", 0, 255, 255, 0).setHidden(() -> !color.is("Gradient")),
                darkFactor = new NumberSetting(this, "Dark Factor", 0 ,1, 0.49, 2).setHidden(() -> !color.is("Fade"))
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
                color = ColorUtil.fadeBetween(new Color(red.get().intValue(), green.get().intValue(), blue.get().intValue()).getRGB(), new Color(red1.get().intValue(), green1.get().intValue(), blue1.get().intValue()).getRGB(), counter * 150L);
                break;
            case "Fade":
                int firstColor = new Color(red.get().intValue(), green.get().intValue(), blue.get().intValue()).getRGB();
                color = ColorUtil.fadeBetween(firstColor, ColorUtil.darken(firstColor, darkFactor.get().floatValue()), counter * 150L);
                break;
            case "Rainbow":
                color = ColorUtil.getRainbow(3000, (int) (counter * 150L));
                break;
            case "Astolfo":
                color = ColorUtil.blendRainbowColours(counter * 150L);
                break;
        }

        switch(mode.get()) {
            case "Exhibition": {
                fr.drawStringWithShadow(String.format("E§7xhibition [§f%s§7] [§f%s FPS§7] [§f%s ms§7]", getCurrentTime(), Minecraft.getDebugFPS(), getPing()), 3, 4, color);

                String displayText = "";
                if (exhiXYZ.get()) {
                    displayText += "XYZ: §f" + Math.round(getX()) + ", " + Math.round(getY()) + ", " + Math.round(getZ()) + " ";
                }
                if (exhiBPS.get()) {
                    displayText += "§7b/s: §f" + getBPS();
                }

                fr.drawStringWithShadow(displayText, 3, sr.getScaledHeight() - 10, new Color(170, 170, 170));
                if(exhiVersion.get())
                    fr.drawStringWithShadow(String.format("Release Build - §f§l%s§7 - TopHat", TopHat.getVersion()), sr.getScaledWidth() - fr.getStringWidth("Release Build - §f§l" + TopHat.getVersion() + "§7 - TopHat") + 5, sr.getScaledHeight() - 10, new Color(170,170,170));
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

                if (BPS.get()) {
                    double motionX = mc.player.posX - mc.player.prevPosX, motionZ = mc.player.posZ - mc.player.prevPosZ;
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

                if (TopHat.moduleManager.getByClass(PostProcessing.class).isEnabled() && TopHat.moduleManager.getByClass(PostProcessing.class).blurShader.get()) {
                    GaussianBlur.startBlur();
                    RoundedUtil.drawRound(5, 5, fr.getStringWidth(text) + 6 + 2 * 2, 20, 8, new Color(13, 60, 123));
                    GaussianBlur.endBlur(8, 2);
                }

                RoundedUtil.drawRoundOutline(5 - 2, 5, fr.getStringWidth(text) + 6 + 2 * 2, 20, 8, 0.30f, new Color(255, 255, 255, 25), new Color(color));
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
