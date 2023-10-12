package wtf.tophat.module.impl.hud;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.ScaledResolution;;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import wtf.tophat.Client;
import wtf.tophat.events.impl.Render2DEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.module.impl.render.PostProcessing;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.shader.RoundedUtil;
import wtf.tophat.shader.blur.GaussianBlur;
import wtf.tophat.utilities.font.CFontUtil;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;

import static wtf.tophat.utilities.Colors.DEFAULT_COLOR;
import static wtf.tophat.utilities.Colors.WHITE_COLOR;

@ModuleInfo(name = "Target HUD",desc = "shows your enemy info", category = Module.Category.HUD)
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

        int centerX = sr.getScaledWidth() / 2, centerY = sr.getScaledHeight() / 2;

        int x = centerX + 10, y = centerY + 5, height = 32, width = 100;

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

        Entity targetEntity = mc.pointedEntity;
        if (targetEntity != null && targetEntity instanceof EntityLivingBase) {
            EntityLivingBase livingEntity = (EntityLivingBase) targetEntity;
            float health = livingEntity.getHealth();

            if (health > 0.1) {
                switch (mode.getValue()) {
                    case "GameSense":
                        float healthPercentage = Math.min(1.0f, health / livingEntity.getMaxHealth());
                        int sliderWidth = (int) (95 * healthPercentage);

                        DrawingUtil.rectangle(x - 6, y, width + 11, height + 20, true, new Color(5, 5, 5));
                        DrawingUtil.rectangle(x - 5, y + 1, width + 9, height + 18, true, new Color(60, 60, 60));
                        DrawingUtil.rectangle(x - 4, y + 2, width + 7, height + 16, true, new Color(40, 40, 40));
                        DrawingUtil.rectangle(x - 2, y + 4, width + 3, height + 12, true, new Color(60, 60, 60));
                        DrawingUtil.rectangle(x - 1, y + 5, width + 1, height + 10, true, new Color(22, 22, 22));
                        DrawingUtil.rectangle(x - 1, y + 5, width + 1, 1, true, new Color(color));

                        DrawingUtil.rectangle(x + 2, y + 38, 95, 6, true, new Color(0, 0, 0));
                        DrawingUtil.rectangle(x + 2, y + 38, sliderWidth, 6, true, new Color(60, 60, 60));
                        DrawingUtil.rectangle(x + 2, y + 38, 95, 6, false, new Color(color));

                        CFontUtil.SF_Regular_20.getRenderer().drawStringChoose(fontShadow.getValue(), livingEntity.getName(), x + 2, y + 10, Color.WHITE);
                        CFontUtil.SF_Regular_20.getRenderer().drawStringChoose(fontShadow.getValue(),"Health: " + (int) Math.ceil(health), x + 2, y + 29, Color.WHITE);
                        GuiInventory.drawEntityOnScreen(x + (100 - 15), y + 30, 12, livingEntity.rotationYaw, livingEntity.rotationPitch, livingEntity);
                        break;
                    case "Modern":
                        width = 185;
                        height = 65;

                        float healthPercentage1 = Math.min(1.0f, health / livingEntity.getMaxHealth());
                        int sliderWidth1 = (int) (173 * healthPercentage1);

                        if(Client.moduleManager.getByClass(PostProcessing.class).isEnabled() && Client.moduleManager.getByClass(PostProcessing.class).blurShader.getValue()) {
                            GaussianBlur.startBlur();
                            RoundedUtil.drawRound(x, y, width, height, 8, new Color(13, 60, 123));
                            GaussianBlur.endBlur(8, 2);
                        }

                        RoundedUtil.drawRoundOutline(x, y, width, height, 8, 0.30f, new Color(255,255,255,25), new Color(color));
                        RoundedUtil.drawRound(x + 6, y + 51, sliderWidth1, 8, 4, new Color(0,255,0,125));
                        RoundedUtil.drawRoundOutline(x + 5, y + 50, 175, 10, 4, 0.30f, new Color(255,255,255,125), new Color(color));

                        CFontUtil.SF_Regular_20.getRenderer().drawStringChoose(fontShadow.getValue(), livingEntity.getName() + " - " + (int) Math.ceil(health) + "hp", x + 5, y + 5, Color.WHITE);
                        break;
                }
            }
        }
        counter++;
    }
}
