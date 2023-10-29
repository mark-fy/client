package wtf.tophat.modules.impl.hud;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import wtf.tophat.Client;
import wtf.tophat.events.impl.Render2DEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.modules.impl.combat.Killaura;
import wtf.tophat.modules.impl.render.PostProcessing;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.render.shaders.RoundedUtil;
import wtf.tophat.utilities.render.shaders.blur.GaussianBlur;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;

import static wtf.tophat.utilities.render.Colors.DEFAULT_COLOR;
import static wtf.tophat.utilities.render.Colors.WHITE_COLOR;

@SuppressWarnings({"ConstantValue", "UnusedAssignment"})
@ModuleInfo(name = "Target HUD", desc = "shows your enemy info", category = Module.Category.HUD)
public class TargetHUD extends Module {

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

            Entity targetEntity = Killaura.target;
            if (targetEntity instanceof EntityLivingBase) {
                EntityLivingBase livingEntity = (EntityLivingBase) targetEntity;
                float health = livingEntity.getHealth();

                if (health > 0.1) {
                    switch (mode.get()) {
                        case "GameSense":
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

                RoundedUtil.drawRoundOutline(x, y, width, height, 8, 0.30f, new Color(255, 255, 255, 25), new Color(color));
                RoundedUtil.drawRound(x + 6, y + 51, sliderWidth1, 8, 4, new Color(0, 255, 0, 125));
                RoundedUtil.drawRoundOutline(x + 5, y + 50, 175, 10, 4, 0.30f, new Color(255, 255, 255, 125), new Color(color));

                fr.drawString("???" + " - " + "???" + "hp", x + 5, y + 5, -1);
                break;
        }
    }
}
