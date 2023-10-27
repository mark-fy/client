package wtf.tophat.modules.impl.hud;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import wtf.tophat.Client;
import wtf.tophat.events.impl.Render2DEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.modules.impl.render.PostProcessing;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.render.shaders.RenderUtil;
import wtf.tophat.utilities.render.shaders.RoundedUtil;
import wtf.tophat.utilities.render.shaders.bloom.KawaseBloom;
import wtf.tophat.utilities.render.shaders.blur.GaussianBlur;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;

import static wtf.tophat.utilities.render.Colors.DEFAULT_COLOR;
import static wtf.tophat.utilities.render.Colors.WHITE_COLOR;

@SuppressWarnings({"ConstantValue", "UnusedAssignment"})
@ModuleInfo(name = "Target HUD", desc = "shows your enemy info", category = Module.Category.HUD)
public class TargetHUD extends Module {

    private Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);

    private final StringSetting mode, color;
    private final BooleanSetting fontShadow;

    public TargetHUD() {
        Client.settingManager.add(
                mode = new StringSetting(this, "Mode", "GameSense", "GameSense", "Modern"),
                color = new StringSetting(this, "Color", "Gradient", "Gradient", "Astolfo", "Rainbow"),
                fontShadow = new BooleanSetting(this, "Font Shadow", true)
        );
    }

    @Listen
    public void on2D(Render2DEvent event) {
        ScaledResolution sr = event.getScaledResolution();
        FontRenderer fr = mc.fontRenderer;

        int centerX = sr.getScaledWidth() / 2, centerY = sr.getScaledHeight() / 2;

        int x = centerX + 10, y = centerY + 5, height = 32, width = 100;

        int counter = 0;
        int color = 0;

        if (!(mc.currentScreen instanceof GuiChat)) {
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
            }

            Entity targetEntity = mc.pointedEntity;
            if (targetEntity instanceof EntityLivingBase) {
                EntityLivingBase livingEntity = (EntityLivingBase) targetEntity;
                float health = livingEntity.getHealth();

                if (health > 0.1) {
                    switch (mode.get()) {
                        case "GameSense":
                            float healthPercentage = Math.min(1.0f, health / livingEntity.getMaxHealth());
                            int sliderWidth = (int) (95 * healthPercentage);

                            DrawingUtil.rectangle(x - 6, y, width + 11, height + 20, true, new Color(5, 5, 5));
                            DrawingUtil.rectangle(x - 5, y + 1, width + 9, height + 18, true, new Color(60, 60, 60));
                            DrawingUtil.rectangle(x - 4, y + 2, width + 7, height + 16, true, new Color(40, 40, 40));
                            DrawingUtil.rectangle(x - 2, y + 4, width + 3, height + 12, true, new Color(60, 60, 60));
                            DrawingUtil.rectangle(x - 1, y + 5, width + 1, height + 10, true, new Color(22, 22, 22));
                            DrawingUtil.rectangle(x - 1, y + 5, width + 1, 1, true, new Color(color));

                            fr.drawString(livingEntity.getName(), x + 2, y + 10, -1);
                            fr.drawString("Health: " + (int) Math.ceil(health), x + 2, y + 29, -1);
                            GuiInventory.drawEntityOnScreen(x + (100 - 15), y + 30, 12, livingEntity.rotationYaw, livingEntity.rotationPitch, livingEntity);
                            break;
                        case "Modern":
                            width = 185;
                            height = 65;

                            float healthPercentage1 = Math.min(1.0f, health / livingEntity.getMaxHealth());
                            int sliderWidth1 = (int) (173 * healthPercentage1);

                            if (Client.moduleManager.getByClass(PostProcessing.class).isEnabled() && Client.moduleManager.getByClass(PostProcessing.class).blurShader.get()) {
                                GaussianBlur.startBlur();
                                RoundedUtil.drawRound(x, y, width, height, 8, new Color(13, 60, 123));
                                GaussianBlur.endBlur(8, 2);
                            }

                            RoundedUtil.drawRoundOutline(x, y, width, height, 8, 0.30f, new Color(255, 255, 255, 25), new Color(color));
                            RoundedUtil.drawRound(x + 6, y + 51, sliderWidth1, 8, 4, new Color(0, 255, 0, 125));
                            RoundedUtil.drawRoundOutline(x + 5, y + 50, 175, 10, 4, 0.30f, new Color(255, 255, 255, 125), new Color(color));

                            fr.drawString(livingEntity.getName() + " - " + (int) Math.ceil(health) + "hp", x + 5, y + 5, -1);
                            break;
                    }
                }
            }
        } else {
            drawDefaultInfo(event);
        }
        counter++;
    }

    private void drawDefaultInfo(Render2DEvent event) {
        ScaledResolution sr = event.getScaledResolution();
        FontRenderer fr = mc.fontRenderer;

        int centerX = sr.getScaledWidth() / 2, centerY = sr.getScaledHeight() / 2;

        int x = centerX + 10, y = centerY + 5, height = 32, width = 100;

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
        }

        switch (mode.get()) {
            case "GameSense":
                float healthPercentage = Math.min(1.0f, 20 / 20);
                int sliderWidth = (int) (95 * healthPercentage);

                DrawingUtil.rectangle(x - 6, y, width + 11, height + 20, true, new Color(5, 5, 5));
                DrawingUtil.rectangle(x - 5, y + 1, width + 9, height + 18, true, new Color(60, 60, 60));
                DrawingUtil.rectangle(x - 4, y + 2, width + 7, height + 16, true, new Color(40, 40, 40));
                DrawingUtil.rectangle(x - 2, y + 4, width + 3, height + 12, true, new Color(60, 60, 60));
                DrawingUtil.rectangle(x - 1, y + 5, width + 1, height + 10, true, new Color(22, 22, 22));
                DrawingUtil.rectangle(x - 1, y + 5, width + 1, 1, true, new Color(color));

                fr.drawString("???", x + 2, y + 10, -1);
                fr.drawString("Health: ???", x + 2, y + 29, -1);
                break;
            case "Modern":
                width = 185;
                height = 65;

                float healthPercentage1 = Math.min(1.0f, 20 / 20);
                int sliderWidth1 = (int) (173 * healthPercentage1);

                if (Client.moduleManager.getByClass(PostProcessing.class).isEnabled() && Client.moduleManager.getByClass(PostProcessing.class).blurShader.get()) {
                    GaussianBlur.startBlur();
                    RoundedUtil.drawRound(x, y, width, height, 8, new Color(13, 60, 123));
                    GaussianBlur.endBlur(8, 2);
                }

                if (Client.moduleManager.getByClass(PostProcessing.class).isEnabled()) {
                    stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer);
                    stencilFramebuffer.framebufferClear();
                    stencilFramebuffer.bindFramebuffer(false);
                    stencilFramebuffer.unbindFramebuffer();
                    KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, 8, 10);
                }

                RoundedUtil.drawRoundOutline(x, y, width, height, 8, 0.30f, new Color(255, 255, 255, 25), new Color(color));
                RoundedUtil.drawRound(x + 6, y + 51, sliderWidth1, 8, 4, new Color(0, 255, 0, 125));
                RoundedUtil.drawRoundOutline(x + 5, y + 50, 175, 10, 4, 0.30f, new Color(255, 255, 255, 125), new Color(color));

                fr.drawString("???" + " - " + "???" + "hp", x + 5, y + 5, -1);
                break;
            case "Exhibition":
                Gui.drawRect2(x, y, x+width, y+height, 0xFF0a0a0a);
                double increment = .5;
                Gui.drawRect2(x+increment, y+increment, x+width-increment, y+height-increment, 0xFF3c3c3c);
                increment = 1;
                Gui.drawRect2(x+increment, y+increment, x+width-increment, y+height-increment, 0xFF222222);
                increment = 2.5;
                Gui.drawRect2(x+increment, y+increment, x+width-increment, y+height-increment, 0xFF3c3c3c);
                increment = 3;
                Gui.drawRect2(x+increment, y+increment, x+width-increment, y+height-increment, 0xFF161616);

                Gui.drawRect2(x+increment+1.5, y+increment+1.5, x-increment+43.5, y-increment+43.5, 0xFF0a0a0a);
                increment = 3.5;
                Gui.drawRect2(x+increment+1.5, y+increment+1.5, x-increment+43.5, y-increment+43.5, 0xFF303030);
                increment = 4;
                Gui.drawRect2(x+increment+1.5, y+increment+1.5, x-increment+43.5, y-increment+43.5, 0xFF111111);

                GlStateManager.color(1, 1, 1, 1);

                float hue = 1000 / 360;
                hue *= 20 * 8;
                hue *= 0.001f;

                Gui.drawRect2(x+42, y+14.5, x+104, y+18.5, 0xFF000000);
                Gui.drawRect2(x+42.5, y+15, x+103.5, y+18, color);
                Gui.drawRect2(x+42.5, y+15, x+103.5, y+18, 0xC0000000);
                Gui.drawRect2(x+42.5, y+15, x+42.5+(61/20)*20, y+18, color);

                double permove = 6.1;

                for(int index = 0; index+1 < 10; index++) {
                    Gui.drawRect2(x+42.5 + permove + index * permove, y+14.5, x+43 + permove+ index * permove, y+18.5, 0xFF000000);
                }

                int realdex = 0;

                mc.fontRenderer.drawStringWithShadow("???", x+42, (float) (y+5.5), -1);

                GlStateManager.scale(0.5,0.5,1);
                mc.fontRenderer.drawString("HP: " + "???" + " | Dist: " + "???", 2*(x+42.9F), 2*(y+20), -1, false);
                GlStateManager.scale(2,2,1);
        }
    }
}
