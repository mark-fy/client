package wtf.tophat.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.OnDeathEvent;
import wtf.tophat.events.impl.PacketEvent;
import wtf.tophat.events.impl.Render2DEvent;
import wtf.tophat.events.impl.UpdateEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.modules.impl.combat.Killaura;
import wtf.tophat.settings.impl.DividerSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.network.ServerUtil;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.DrawingUtil;
import wtf.tophat.utilities.render.shaders.RoundedUtil;
import wtf.tophat.utilities.render.shaders.blur.GaussianBlur;

import java.awt.*;
import java.text.DecimalFormat;

@ModuleInfo(name = "Session Info", desc = "displays info about the current session", category = Module.Category.RENDER)
public class SessionInfo extends Module {

    public int deaths = 0;
    public int kills = 0;

    private final StringSetting mode, color, color2;
    private final NumberSetting red, green, blue, red1, green1, blue1, darkFactor, red2, green2, blue2, red3, green3, blue3, darkFactor2;
    private final DividerSetting outlineColor, textColor;

    public SessionInfo() {
        TopHat.settingManager.add(
                outlineColor = new DividerSetting(this, "Outline Color"),
                mode = new StringSetting(this, "Mode", "Modern", "Modern"),
                color = new StringSetting(this, "Color", "Gradient", "Gradient", "Fade", "Astolfo", "Rainbow"),
                red = new NumberSetting(this, "Red", 0, 255, 95, 0).setHidden(() -> (!color.is("Gradient") && !color.is("Fade"))),
                green = new NumberSetting(this, "Green", 0, 255, 61, 0).setHidden(() -> (!color.is("Gradient") && !color.is("Fade"))),
                blue = new NumberSetting(this, "Blue", 0, 255, 248, 0).setHidden(() -> (!color.is("Gradient") && !color.is("Fade"))),
                red1 = new NumberSetting(this, "Second Red", 0, 255, 255, 0).setHidden(() -> !color.is("Gradient")),
                green1 = new NumberSetting(this, "Second Green", 0, 255, 255, 0).setHidden(() -> !color.is("Gradient")),
                blue1 = new NumberSetting(this, "Second Blue", 0, 255, 255, 0).setHidden(() -> !color.is("Gradient")),
                darkFactor = new NumberSetting(this, "Dark Factor", 0, 1, 0.49, 2).setHidden(() -> !color.is("Fade")),
                textColor = new DividerSetting(this, "Text Color"),
                color2 = new StringSetting(this, "Color", "Gradient", "Gradient", "Fade", "Astolfo", "Rainbow"),
                red2 = new NumberSetting(this, "Red", 0, 255, 95, 0).setHidden(() -> (!color2.is("Gradient") && !color2.is("Fade"))),
                green2 = new NumberSetting(this, "Green", 0, 255, 61, 0).setHidden(() -> (!color2.is("Gradient") && !color2.is("Fade"))),
                blue2 = new NumberSetting(this, "Blue", 0, 255, 248, 0).setHidden(() -> (!color2.is("Gradient") && !color2.is("Fade"))),
                red3 = new NumberSetting(this, "Second Red", 0, 255, 255, 0).setHidden(() -> !color2.is("Gradient")),
                green3 = new NumberSetting(this, "Second Green", 0, 255, 255, 0).setHidden(() -> !color2.is("Gradient")),
                blue3 = new NumberSetting(this, "Second Blue", 0, 255, 255, 0).setHidden(() -> !color2.is("Gradient")),
                darkFactor2 = new NumberSetting(this, "Dark Factor", 0, 1, 0.49, 2).setHidden(() -> !color2.is("Fade"))
        );
    }

    @Override
    public void onDisable() {
        deaths = 0;
        super.onDisable();
    }

    @Listen
    public void onDeath(OnDeathEvent event) {
        deaths++;
    }

    @Listen
    public void onPacket(PacketEvent event) {
        Packet<?> e = event.getPacket();
        if (e instanceof S02PacketChat) {
            S02PacketChat s02PacketChat = (S02PacketChat)e;
            String cp21 = s02PacketChat.getChatComponent().getUnformattedText();
            if (cp21.contains("was killed by " + mc.session.getUsername())) {
                kills++;
            }

            if (cp21.contains("was slain by " + mc.session.getUsername())) {
                kills++;
            }

            if (cp21.contains("was shot and killed by " + mc.session.getUsername())) {
                kills++;
            }

            if (cp21.contains("was snowballed to death by " + mc.session.getUsername())) {
                kills++;
            }

            if (cp21.contains("was killed with magic by " + mc.session.getUsername())) {
                kills++;
            }

            if (cp21.contains("was killed with an explosion by " + mc.session.getUsername())) {
                kills++;
            }

            if (cp21.contains("was killed with a potion by " + mc.session.getUsername())) {
                kills++;
            }
        }
    }

    @Listen
    public void onRender(Render2DEvent event) {
        int color = 0;
        int counter = 0;
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

        int color2 = 0;
        int counter2 = 0;
        switch (this.color2.get()) {
            case "Gradient":
                color2 = ColorUtil.fadeBetween(new Color(red.get().intValue(), green.get().intValue(), blue.get().intValue()).getRGB(), new Color(red1.get().intValue(), green1.get().intValue(), blue1.get().intValue()).getRGB(), counter2 * 150L);
                break;
            case "Fade":
                int firstColor = new Color(red.get().intValue(), green.get().intValue(), blue.get().intValue()).getRGB();
                color2 = ColorUtil.fadeBetween(firstColor, ColorUtil.darken(firstColor, darkFactor.get().floatValue()), counter2 * 150L);
                break;
            case "Rainbow":
                color2 = ColorUtil.getRainbow(3000, (int) (counter2 * 150L));
                break;
            case "Astolfo":
                color2 = ColorUtil.blendRainbowColours(counter2 * 150L);
                break;
        }

        int x = 62;
        int y = 62;
        FontRenderer fr = mc.fontRenderer;

        if (TopHat.moduleManager.getByClass(PostProcessing.class).isEnabled() && TopHat.moduleManager.getByClass(PostProcessing.class).blurShader.get()) {
            GaussianBlur.startBlur();
            RoundedUtil.drawRound(60, 60, 120, 52, 4, new Color(13, 60, 123));
            GaussianBlur.endBlur(4, 2);
        }

        RoundedUtil.drawRoundOutline(60, 60, 120, 52, 4, 0.30f, new Color(255, 255, 255, 25), new Color(color));

        fr.drawStringWithShadow("Session Info", 62, 62, color2);
        fr.drawString("Play Time: " + ServerUtil.getSessionLengthString(), x, y + 30, -1);
        fr.drawString("Kills: " + kills, x, y + 10, -1);
        fr.drawString("Deaths: " + deaths, x, y + 20, -1);
        fr.drawString("KDR: " + Math.max(1, this.kills) / Math.max(1, this.deaths), x, y + 40, -1);
        counter++;
        counter2++;
    }
}
