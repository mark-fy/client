package wtf.tophat.modules.impl.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import wtf.tophat.Client;
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
        Client.settingManager.add(
                mode = new StringSetting(this, "Mode", "GameSense", "GameSense", "Modern", "Floyd", "Exhibition"),
                color = new StringSetting(this, "Color", "Gradient", "Gradient", "Astolfo", "Rainbow", "Brown"),
                fontShadow = new BooleanSetting(this, "Font Shadow", true),
                bps = new BooleanSetting(this, "Blocks per Second", true)
        );
        setEnabled(true);
    }
    public void renderIngame() {
        FontRenderer fr = mc.fontRenderer;
        ScaledResolution sr = new ScaledResolution(mc);

        int scrWidth = sr.getScaledWidth();
        int scrHeight = sr.getScaledHeight();

        String text = (Client.getName() + "sense | " + Minecraft.getDebugFPS() + " fps | " + mc.getSession().getUsername()).toLowerCase(Locale.ROOT);

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
                fr.drawStringWithShadow(String.format("E§7xhibition [§f%s§7] [§f%s FPS§7] [§f%s ms§7]", getCurrentTime(), Minecraft.getDebugFPS(), getPing()), 3, 4, new Color(157,6,99));
                fr.drawStringWithShadow(String.format("XYZ: §f%s, %s, %s §7b/s: §f%s", Math.round(getX()), Math.round(getY()), Math.round(getZ()), getBPS()), 3, sr.getScaledHeight() - 10, new Color(170,170,170));
                fr.drawStringWithShadow(String.format("Release Build - §f§l%s§7 - User", Client.getVersion()), sr.getScaledWidth() - fr.getStringWidth("Release Build - §f§l" + Client.getVersion() + "§7 - User") + 5, sr.getScaledHeight() - 10, new Color(170,170,170));
                break;
            }
            case "Floyd": {
                RoundedUtil.drawRound(5, 5, 96, 105, 8, new Color(color));
                if (Client.moduleManager.getByClass(PostProcessing.class).isEnabled() && Client.moduleManager.getByClass(PostProcessing.class).blurShader.get()) {
                    GaussianBlur.startBlur();
                    RoundedUtil.drawRound(5, 5, 96, 105, 8, new Color(13, 60, 123));
                    GaussianBlur.endBlur(8, 2);
                }

                fr.drawString("TopHat - " + Client.getVersion(), 15 ,6, Color.WHITE);
                mc.getTextureManager().bindTexture(new ResourceLocation("tophat/gorge.png"));
                Gui.drawModalRectWithCustomSizedTexture(7, 15, 0,0, 92, 92, 92,92);
                break;
            }
            case "Watermark (flagged)": {
                RoundedUtil.drawRound(5, 5, 96, 105, 8, new Color(color));
                if (Client.moduleManager.getByClass(PostProcessing.class).isEnabled() && Client.moduleManager.getByClass(PostProcessing.class).blurShader.get()) {
                    GaussianBlur.startBlur();
                    RoundedUtil.drawRound(5, 5, 96, 105, 8, new Color(13, 60, 123));
                    GaussianBlur.endBlur(8, 2);
                }

                fr.drawString("TopHat - " + Client.getVersion(), 15 ,6, Color.WHITE);
                mc.getTextureManager().bindTexture(new ResourceLocation("tophat/logo.png"));
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
                text = Client.getName() + " - " + mc.getSession().getUsername();
                int strWidth1 = fr.getStringWidth(text);
                int x = 5;
                int y = 5;
                int height = 20;
                int padding = 6;
                int extraWidth = 2;
                int cornerRadius = 8;
                Color backgroundColor = new Color(13, 60, 123);
                Color outlineColor = new Color(255, 255, 255, 25);
                int textOffset = 4;

                if (Client.moduleManager.getByClass(PostProcessing.class).isEnabled() && Client.moduleManager.getByClass(PostProcessing.class).blurShader.get()) {
                    GaussianBlur.startBlur();
                    int totalWidth = strWidth1 + padding + extraWidth * 2;
                    RoundedUtil.drawRound(x, y, totalWidth, height, cornerRadius, backgroundColor);
                    GaussianBlur.endBlur(8, 2);
                }

                int outlineX = x - extraWidth;
                int outlineWidth = strWidth1 + padding + extraWidth * 2;
                RoundedUtil.drawRoundOutline(outlineX, y, outlineWidth, height, cornerRadius, 0.30f, outlineColor, new Color(color));
                fr.drawStringOptional(fontShadow.get(), text, x + textOffset - 1, y + 6, Color.WHITE);
                break;
            }
        }

        counter++;
    }

    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
        return dateFormat.format(calendar.getTime());
    }

    @Override
    public void renderDummy() { renderIngame(); }
}
