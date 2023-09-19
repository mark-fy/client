package wtf.tophat.module.impl.render;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.ScaledResolution;;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import wtf.tophat.Client;
import wtf.tophat.events.impl.Render2DEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.ModeSetting;
import wtf.tophat.utilities.font.CFontUtil;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.render.DrawingUtil;
import wtf.tophat.utilities.render.RoundUtil;

import java.awt.*;

import static wtf.tophat.utilities.ColorPallete.DEFAULT_COLOR;
import static wtf.tophat.utilities.ColorPallete.WHITE_COLOR;

@ModuleInfo(name = "TargetHUD",desc = "shows your enemy info", category = Module.Category.RENDER)
public class TargetHUD extends Module {

    private final ModeSetting mode, color;
    private final BooleanSetting fontShadow;

    public TargetHUD() {
        Client.settingManager.add(
                mode = new ModeSetting(this, "Mode", "GameSense", "GameSense", "Fyre"),
                color = new ModeSetting(this, "Color", "Gradient", "Gradient", "Astolfo", "Rainbow"),
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
        if (targetEntity != null && targetEntity instanceof EntityLivingBase) {
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

                        DrawingUtil.rectangle(x + 2, y + 38, 95, 6, true, new Color(0, 0, 0));
                        DrawingUtil.rectangle(x + 2, y + 38, sliderWidth, 6, true, new Color(60, 60, 60));
                        DrawingUtil.rectangle(x + 2, y + 38, 95, 6, false, new Color(color));

                        mc.fontRenderer.drawStringChoose(fontShadow.get(), livingEntity.getName(), x + 2, y + 10, -1);
                        mc.fontRenderer.drawStringChoose(fontShadow.get(),"Health: " + (int) Math.ceil(health), x + 2, y + 29, -1);
                        GuiInventory.drawEntityOnScreen(x + (100 - 15), y + 30, 12, livingEntity.rotationYaw, livingEntity.rotationPitch, livingEntity);
                        break;
                    case "Fyre":
                        width = 185;
                        height = 65;

                        float x1 = x, y1 = y, x2 = x1 + width, y2 = y1 + height;

                        RoundUtil.drawSmoothRoundedRect(x1, y1, x2, y2, 8, DEFAULT_COLOR);
                        CFontUtil.SF_Regular_20.getRenderer().drawStringChoose(fontShadow.get(), livingEntity.getName(), x, y + 10, Color.WHITE);
                        break;
                }
            }
        }
        counter++;
    }
}
