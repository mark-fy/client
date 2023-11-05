package wtf.tophat.modules.impl.hud;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.Render2DEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.modules.impl.combat.Killaura;
import wtf.tophat.modules.impl.render.PostProcessing;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.render.shaders.RenderUtil;
import wtf.tophat.utilities.render.shaders.RoundedUtil;
import wtf.tophat.utilities.render.shaders.blur.GaussianBlur;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;
import java.text.DecimalFormat;

import static wtf.tophat.utilities.render.Colors.DEFAULT_COLOR;
import static wtf.tophat.utilities.render.Colors.WHITE_COLOR;

@SuppressWarnings({"ConstantValue", "UnusedAssignment"})
@ModuleInfo(name = "Target HUD", desc = "shows your enemy info", category = Module.Category.HUD)
public class TargetHUD extends Module {

    private final StringSetting mode, color;

    // I took these from summer
    private final DecimalFormat DF_1 = new DecimalFormat("0.0");
    private float aFloat;

    public TargetHUD() {
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "GameSense", "GameSense", "Modern", "Exhibition"),
                color = new StringSetting(this, "Color", "Gradient", "Gradient", "Astolfo", "Rainbow")
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

            EntityLivingBase targetEntity = Killaura.target;
            if (targetEntity != null) {
                float health = targetEntity.getHealth();

                if (health > 0.1) {
                    switch (mode.get()) {
                        case "Exhibition":
                            width = 130;
                            height = 50;

                            RenderUtil.scaleStart(x + width / 2, y + height / 2, 1);
                            RoundedUtil.drawRoundOutline(x, y,
                                    width + 2, height, 2, 1f, new Color(0xFF080809), new Color(0xFF3C3C3D));
                            RoundedUtil.drawRoundOutline(x + 3, y + 3, 32, 44, 2,
                                    0.5f, new Color(0xFF151515), new Color(0xFF3C3C3D));

                            GL11.glPushMatrix();
                            RendererLivingEntity.NAME_TAG_RANGE = 0;
                            RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 0;
                            GuiInventory.drawEntityOnScreen((int) (x + 18), (int) (y + 44), 20, targetEntity.rotationYaw, -targetEntity.rotationPitch, targetEntity);
                            RendererLivingEntity.NAME_TAG_RANGE = 64f;
                            RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 32f;
                            GL11.glPopMatrix();

                            health = (float) RenderUtil.animate((width - 41) * (targetEntity.getHealth() / targetEntity.getMaxHealth()), health, 0.025f);
                            aFloat = (float) RenderUtil.animate(width - 41, aFloat, 0.025f);
                            fr.drawStringWithShadow(targetEntity.getName(), x + 37, y + 6, -1);

                            RoundedUtil.drawRound(x + 38, y + 8 + fr.FONT_HEIGHT,
                                    aFloat, 5, 0, new Color(ColorUtil.getHealthColor(targetEntity)).darker().darker());

                            RoundedUtil.drawRound(x + 38, y + 8 + fr.FONT_HEIGHT, health,
                                    5, 0, new Color(ColorUtil.getHealthColor(targetEntity)));

                            RenderUtil.scissorStart(x + 38, y + 7 + fr.FONT_HEIGHT, aFloat, 9);

                            float amount = aFloat / 10;
                            float length = aFloat / amount;
                            for(int i = 1; i < amount; i++){
                                RoundedUtil.drawRound(x + 38 + i * length - 0.5f, y + 6.9f + fr.FONT_HEIGHT, 0.5f, 6.9f, 0, Color.black);
                            }
                            RenderUtil.scissorEnd();

                            fr.drawStringWithShadow(String.format("HP: %s | DIST: %s", DF_1.format(targetEntity.getHealth()), DF_1.format(mc.player.getDistanceToEntity(targetEntity))),
                                   x + 38, y + 27, -1);

                            boolean winning = targetEntity.getHealth() < mc.player.getHealth();
                            fr.drawStringWithShadow(mc.player.getHealth() == targetEntity.getHealth() ? "Indecisive" : winning ? "Winning" : "Losing", x + 38, y + 29 + fr.FONT_HEIGHT, -1);
                            RenderUtil.scaleEnd();
                            break;
                        case "GameSense":
                            DrawingUtil.rectangle(x - 6, y, width + 11, height + 20, true, new Color(5, 5, 5));
                            DrawingUtil.rectangle(x - 5, y + 1, width + 9, height + 18, true, new Color(60, 60, 60));
                            DrawingUtil.rectangle(x - 4, y + 2, width + 7, height + 16, true, new Color(40, 40, 40));
                            DrawingUtil.rectangle(x - 2, y + 4, width + 3, height + 12, true, new Color(60, 60, 60));
                            DrawingUtil.rectangle(x - 1, y + 5, width + 1, height + 10, true, new Color(22, 22, 22));
                            DrawingUtil.rectangle(x - 1, y + 5, width + 1, 1, true, new Color(color));

                            RendererLivingEntity.NAME_TAG_RANGE = 0;
                            RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 0;
                            GuiInventory.drawEntityOnScreen(x + (100 - 15), y + 30, 12, targetEntity.rotationYaw, targetEntity.rotationPitch, targetEntity);
                            RendererLivingEntity.NAME_TAG_RANGE = 64f;
                            RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 32f;

                            fr.drawString(targetEntity.getName(), x + 2, y + 10, -1);
                            fr.drawString("Health: " + (int) Math.ceil(health), x + 2, y + 29, -1);
                            break;
                        case "Modern":
                            width = 185;
                            height = 65;

                            float healthPercentage1 = Math.min(1.0f, health / targetEntity.getMaxHealth());
                            int sliderWidth1 = (int) (173 * healthPercentage1);

                            if (TopHat.moduleManager.getByClass(PostProcessing.class).isEnabled() && TopHat.moduleManager.getByClass(PostProcessing.class).blurShader.get()) {
                                GaussianBlur.startBlur();
                                RoundedUtil.drawRound(x, y, width, height, 8, new Color(13, 60, 123));
                                GaussianBlur.endBlur(8, 2);
                            }

                            RoundedUtil.drawRoundOutline(x, y, width, height, 8, 0.30f, new Color(255, 255, 255, 25), new Color(color));
                            RoundedUtil.drawRound(x + 6, y + 51, sliderWidth1, 8, 4, new Color(0, 255, 0, 125));
                            RoundedUtil.drawRoundOutline(x + 5, y + 50, 175, 10, 4, 0.30f, new Color(255, 255, 255, 125), new Color(color));

                            fr.drawString(targetEntity.getName() + " - " + (int) Math.ceil(health) + "hp", x + 5, y + 5, -1);
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

        float health = mc.player.getHealth();
        switch (mode.get()) {
            case "Exhibition":
                width = 130;
                height = 50;

                RenderUtil.scaleStart(x + (float) width / 2, y + (float) height / 2, 1);
                RoundedUtil.drawRoundOutline(x, y,
                        width + 2, height, 2, 1f, new Color(0xFF080809), new Color(0xFF3C3C3D));
                RoundedUtil.drawRoundOutline(x + 3, y + 3, 32, 44, 2,
                        0.5f, new Color(0xFF151515), new Color(0xFF3C3C3D));

                GL11.glPushMatrix();
                RendererLivingEntity.NAME_TAG_RANGE = 0;
                RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 0;
                GuiInventory.drawEntityOnScreen(x + 18, y + 44, 20, mc.player.rotationYaw, -mc.player.rotationPitch, mc.player);
                RendererLivingEntity.NAME_TAG_RANGE = 64f;
                RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 32f;
                GL11.glPopMatrix();

                health = (float) RenderUtil.animate((width - 41) * (mc.player.getHealth() / mc.player.getMaxHealth()), health, 0.025f);
                aFloat = (float) RenderUtil.animate(width - 41, aFloat, 0.025f);
                fr.drawStringWithShadow(mc.player.getName(), x + 37, y + 6, -1);

                RoundedUtil.drawRound(x + 38, y + 8 + fr.FONT_HEIGHT,
                        aFloat, 5, 0, new Color(ColorUtil.getHealthColor(mc.player)).darker().darker());

                RoundedUtil.drawRound(x + 38, y + 8 + fr.FONT_HEIGHT, health,
                        5, 0, new Color(ColorUtil.getHealthColor(mc.player)));

                RenderUtil.scissorStart(x + 38, y + 7 + fr.FONT_HEIGHT, aFloat, 9);

                float amount = aFloat / 10;
                float length = aFloat / amount;
                for(int i = 1; i < amount; i++){
                    RoundedUtil.drawRound(x + 38 + i * length - 0.5f, y + 6.9f + fr.FONT_HEIGHT, 0.5f, 6.9f, 0, Color.black);
                }
                RenderUtil.scissorEnd();

                fr.drawStringWithShadow(String.format("HP: %s | DIST: %s", DF_1.format(mc.player.getHealth()), DF_1.format(mc.player.getDistanceToEntity(mc.player))),
                        x + 38, y + 27, -1);

                boolean winning = mc.player.getHealth() < mc.player.getHealth();
                fr.drawStringWithShadow(mc.player.getHealth() == mc.player.getHealth() ? "Indecisive" : winning ? "Winning" : "Losing", x + 38, y + 29 + fr.FONT_HEIGHT, -1);
                RenderUtil.scaleEnd();
                break;
            case "GameSense":
                DrawingUtil.rectangle(x - 6, y, width + 11, height + 20, true, new Color(5, 5, 5));
                DrawingUtil.rectangle(x - 5, y + 1, width + 9, height + 18, true, new Color(60, 60, 60));
                DrawingUtil.rectangle(x - 4, y + 2, width + 7, height + 16, true, new Color(40, 40, 40));
                DrawingUtil.rectangle(x - 2, y + 4, width + 3, height + 12, true, new Color(60, 60, 60));
                DrawingUtil.rectangle(x - 1, y + 5, width + 1, height + 10, true, new Color(22, 22, 22));
                DrawingUtil.rectangle(x - 1, y + 5, width + 1, 1, true, new Color(color));

                RendererLivingEntity.NAME_TAG_RANGE = 0;
                RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 0;
                GuiInventory.drawEntityOnScreen(x + (100 - 15), y + 30, 12, mc.player.rotationYaw, mc.player.rotationPitch, mc.player);
                RendererLivingEntity.NAME_TAG_RANGE = 64f;
                RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 32f;

                fr.drawString(mc.player.getName(), x + 2, y + 10, -1);
                fr.drawString("Health: " + (int) Math.ceil(health), x + 2, y + 29, -1);

                break;
            case "Modern":
                width = 185;
                height = 65;

                float healthPercentage1 = Math.min(1.0f, (float) 20 / 20);
                int sliderWidth1 = (int) (173 * healthPercentage1);

                if (TopHat.moduleManager.getByClass(PostProcessing.class).isEnabled() && TopHat.moduleManager.getByClass(PostProcessing.class).blurShader.get()) {
                    GaussianBlur.startBlur();
                    RoundedUtil.drawRound(x, y, width, height, 8, new Color(13, 60, 123));
                    GaussianBlur.endBlur(8, 2);
                }

                RoundedUtil.drawRoundOutline(x, y, width, height, 8, 0.30f, new Color(255, 255, 255, 25), new Color(color));
                RoundedUtil.drawRound(x + 6, y + 51, sliderWidth1, 8, 4, new Color(0, 255, 0, 125));
                RoundedUtil.drawRoundOutline(x + 5, y + 50, 175, 10, 4, 0.30f, new Color(255, 255, 255, 125), new Color(color));

                fr.drawString(mc.player.getName() + " - " + (int) Math.ceil(health) + "hp", x + 5, y + 5, -1);
                break;
        }
    }
}
