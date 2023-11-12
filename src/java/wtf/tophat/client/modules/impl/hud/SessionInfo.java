package wtf.tophat.client.modules.impl.hud;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.network.play.server.S02PacketChat;
import org.lwjgl.opengl.GL11;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.player.OnDeathEvent;
import wtf.tophat.client.events.impl.network.PacketEvent;
import wtf.tophat.client.events.impl.render.Render2DEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.modules.impl.render.PostProcessing;
import wtf.tophat.client.settings.impl.DividerSetting;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.settings.impl.StringSetting;
import wtf.tophat.client.utilities.network.ServerUtil;
import wtf.tophat.client.utilities.render.ColorUtil;
import wtf.tophat.client.utilities.render.shaders.RenderUtil;
import wtf.tophat.client.utilities.render.shaders.RoundedUtil;
import wtf.tophat.client.utilities.render.shaders.bloom.KawaseBloom;
import wtf.tophat.client.utilities.render.shaders.blur.GaussianBlur;

import java.awt.*;

@SuppressWarnings({"ConstantValue", "UnusedAssignment"})
@ModuleInfo(name = "Session Info", desc = "displays info about the current session", category = Module.Category.HUD)
public class SessionInfo extends Module {

    public int deaths = 0;
    public int kills = 0;

    private final StringSetting mode, color, color2;
    private final NumberSetting red, green, blue, red1, green1, blue1, darkFactor, red2, green2, blue2, red3, green3, blue3, darkFactor2;
    private final DividerSetting outlineColor, textColor;

    public SessionInfo() {
        TopHat.settingManager.add(
                outlineColor = new DividerSetting(this, "Outline"),
                mode = new StringSetting(this, "Mode", "Modern", "Modern"),
                color = new StringSetting(this, "Color", "Gradient", "Gradient", "Fade", "Astolfo", "Rainbow"),
                red = new NumberSetting(this, "Red", 0, 255, 95, 0).setHidden(() -> (!color.is("Gradient") && !color.is("Fade"))),
                green = new NumberSetting(this, "Green", 0, 255, 61, 0).setHidden(() -> (!color.is("Gradient") && !color.is("Fade"))),
                blue = new NumberSetting(this, "Blue", 0, 255, 248, 0).setHidden(() -> (!color.is("Gradient") && !color.is("Fade"))),
                red1 = new NumberSetting(this, "Second Red", 0, 255, 255, 0).setHidden(() -> !color.is("Gradient")),
                green1 = new NumberSetting(this, "Second Green", 0, 255, 255, 0).setHidden(() -> !color.is("Gradient")),
                blue1 = new NumberSetting(this, "Second Blue", 0, 255, 255, 0).setHidden(() -> !color.is("Gradient")),
                darkFactor = new NumberSetting(this, "Dark Factor", 0, 1, 0.49, 2).setHidden(() -> !color.is("Fade")),
                textColor = new DividerSetting(this, "Text"),
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
        if (event.getPacket() instanceof S02PacketChat) {
            String cp21 = ((S02PacketChat)event.getPacket()).getChatComponent().getUnformattedText();
            String username = mc.session.getUsername();
            if (cp21.matches(".*was (killed|slain|shot and killed|snowballed to death|killed with (magic|an explosion|a potion)) by " + username + ".*")) {
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

        int x = 62;
        int y = 62;
        FontRenderer fr = mc.fontRenderer;


        Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);

        if (TopHat.moduleManager.getByClass(PostProcessing.class).isEnabled() && TopHat.moduleManager.getByClass(PostProcessing.class).blurShader.get()) {
            GaussianBlur.startBlur();
            RoundedUtil.drawRound(60, 60, 120, 52, 4, new Color(13, 60, 123));
            GaussianBlur.endBlur(4, 2);
        }
        if (TopHat.moduleManager.getByClass(PostProcessing.class).isEnabled() && TopHat.moduleManager.getByClass(PostProcessing.class).bloomShader.get()) {
            GL11.glPushMatrix();
            stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer);
            stencilFramebuffer.framebufferClear();
            stencilFramebuffer.bindFramebuffer(false);
            RoundedUtil.drawRound(60, 60, 120, 52, 4, new Color(color));
            stencilFramebuffer.unbindFramebuffer();
            KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, TopHat.moduleManager.getByClass(PostProcessing.class).iterations.get().intValue(), TopHat.moduleManager.getByClass(PostProcessing.class).offset.get().intValue());
            GL11.glPopMatrix();
        }

        RoundedUtil.drawRoundOutline(60, 60, 120, 52, 4, 0.30f, new Color(255, 255, 255, 25), new Color(color));

        fr.drawStringWithShadow("Session Info", 62, 62, color);
        fr.drawString("Play Time: " + ServerUtil.getSessionLengthString(), x, y + 30, -1);
        fr.drawString("Kills: " + kills, x, y + 10, -1);
        fr.drawString("Deaths: " + deaths, x, y + 20, -1);
        fr.drawString("KDR: " + Math.max(1, this.kills) / Math.max(1, this.deaths), x, y + 40, -1);
        counter++;
    }
}
