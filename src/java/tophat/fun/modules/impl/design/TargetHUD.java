package tophat.fun.modules.impl.design;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import tophat.fun.Client;
import tophat.fun.events.impl.render.Render2DEvent;
import tophat.fun.modules.base.Module;
import tophat.fun.modules.base.ModuleInfo;
import tophat.fun.modules.impl.combat.Aura;
import tophat.fun.modules.base.settings.impl.BooleanSetting;
import tophat.fun.modules.base.settings.impl.StringSetting;
import tophat.fun.utilities.font.CFont;
import tophat.fun.utilities.font.renderer.TTFFontRenderer;
import tophat.fun.utilities.render.RenderUtil;
import tophat.fun.utilities.render.shader.DrawHelper;

import java.awt.*;

@ModuleInfo(name = "TargetHUD", desc = "displays the aura's target info.", category = Module.Category.DESIGN)
public class TargetHUD extends Module {

    private final StringSetting design = new StringSetting(this, "Design", "TopHat", "TopHat", "Heart");
    private final BooleanSetting gradientOutline = new BooleanSetting(this, "GradientOutline", false).setHidden(() -> !design.is("TopHat"));

    private final static TTFFontRenderer poppins = CFont.FONT_MANAGER.getFont("PoppinsMedium 18");
    private final static TTFFontRenderer roboto = CFont.FONT_MANAGER.getFont("Roboto 18");

    @Listen
    public void on2D(Render2DEvent event) {
        float x = (float) event.getScaledResolution().getScaledWidth() / 2 + 10;
        float y = (float) event.getScaledResolution().getScaledHeight() / 2 + 10;
        float width;
        float height;

        String text = "";
        if(!(mc.currentScreen instanceof GuiChat)) {
            if(Client.INSTANCE.moduleManager.getByClass(Aura.class).isEnabled() && Aura.target != null && !Aura.target.isDead) {
                if(!(Aura.target instanceof EntityPlayer)) return;
                text = Aura.target.getName();

                AbstractClientPlayer et = (AbstractClientPlayer) Aura.target;
                if(et.getHealth() <= 0) return;

                switch (design.get()) {
                    case "TopHat":

                        width = 120;
                        height = 35;
                        if(gradientOutline.get()) {
                            DrawHelper.drawRoundedGradientRect(x - 1, y - 1, width + 2, height + 2, 6, new Color(24, 175, 162), new Color(0, 101, 197), new Color(24, 175, 162).brighter().brighter(), new Color(0, 101, 197).brighter().brighter());
                            DrawHelper.drawRoundedRect(x, y, width, height, 6, new Color(25,25,25));

                            DrawHelper.drawRoundedGradientRect(x + 3 - 1, y + 22.5, width - 40 + 2, 8 + 2, 3, new Color(24, 175, 162), new Color(0, 101, 197), new Color(24, 175, 162).brighter().brighter(), new Color(0, 101, 197).brighter().brighter());
                            DrawHelper.drawRoundedRect(x + 3, y + 23.5, width - 40, 8, 3, new Color(25,25,25));
                        } else {
                            DrawHelper.drawRoundedRect(x, y, width, height, 6, new Color(25,25,25));
                            DrawHelper.drawRoundedRectOutline( x - 1, y - 1, width + 2, height + 2, 6, 2, new Color(24, 175, 162));

                            DrawHelper.drawRoundedRect(x + 3, y + 23.5, width - 40, 8, 3, new Color(25,25,25));
                            DrawHelper.drawRoundedRectOutline(x + 3 - 1, y + 22.5, width - 40 + 2, 8 + 2, 3, 2, new Color(24, 175, 175));
                        }

                        DrawHelper.drawRoundedTexture(new ResourceLocation(et.getLocationSkin().getResourcePath()), x + width - 33, y + 32.5, 30, 30, 8, 8, 8, 8, 12);

                        float healthPercentage = Math.min(et.getHealth() / et.getMaxHealth(), 1.0f);
                        float healthBarWidth = (width - 40) * healthPercentage;

                        DrawHelper.drawRoundedRect(x + 3, y + 23.5, healthBarWidth, 8, 3, new Color(31, 206, 206));

                        float textX = et.getHealth() <= 9 ? x + healthBarWidth - 5 : x + healthBarWidth - 7.5f;
                        poppins.drawString(et.getHealth() <= 1 ? "" : String.valueOf((int) et.getHealth()), textX - 1, y + 22.5f, -1);

                        poppins.drawString(text, x + 4, y + 4, -1);
                        break;
                    case "Heart":
                        width = 140;
                        height = 34;
                        DrawHelper.drawRoundedRect(x, y, width, height, 4, new Color(0,0,0, 150));
                        DrawHelper.drawRoundedRectOutline( x - 1, y - 1, width + 2, height + 2, 4, 2, new Color(130, 130, 130));
                        RenderUtil.drawSkinHead(et, x + 5, y + 5, 25);

                        // Health Bar
                        float healthPercentage1 = Math.min(et.getHealth() / et.getMaxHealth(), 1.0f);
                        float healthBarWidth1 = (width - 60) * healthPercentage1;
                        DrawHelper.drawRoundedRect(x + 33, y + 19, width - 60, 8, 3, new Color(255, 255, 255, 50));
                        DrawHelper.drawRoundedRect(x + 33, y + 19, healthBarWidth1, 8, 3, new Color(255, 255, 255));

                        roboto.drawString(text, x + 33, y + 5, -1);
                        roboto.drawString((int) et.getHealth() + "HP", x + width - 25, y + 18.5f, -1);
                        break;
                }
            }
        } else {
            text = mc.getSession().getUsername();

            switch (design.get()) {
                case "TopHat":
                    width = 120;
                    height = 35;
                    if(gradientOutline.get()) {
                        DrawHelper.drawRoundedGradientRect(x - 1, y - 1, width + 2, height + 2, 6, new Color(24, 175, 162), new Color(0, 101, 197), new Color(24, 175, 162).brighter().brighter(), new Color(0, 101, 197).brighter().brighter());
                        DrawHelper.drawRoundedRect(x, y, width, height, 6, new Color(25,25,25));

                        DrawHelper.drawRoundedGradientRect(x + 3 - 1, y + 22.5, width - 40 + 2, 8 + 2, 3, new Color(24, 175, 162), new Color(0, 101, 197), new Color(24, 175, 162).brighter().brighter(), new Color(0, 101, 197).brighter().brighter());
                        DrawHelper.drawRoundedRect(x + 3, y + 23.5, width - 40, 8, 3, new Color(25,25,25));
                    } else {
                        DrawHelper.drawRoundedRect(x, y, width, height, 6, new Color(25,25,25));
                        DrawHelper.drawRoundedRectOutline( x - 1, y - 1, width + 2, height + 2, 6, 2, new Color(24, 175, 162));

                        DrawHelper.drawRoundedRect(x + 3, y + 23.5, width - 40, 8, 3, new Color(25,25,25));
                        DrawHelper.drawRoundedRectOutline(x + 3 - 1, y + 22.5, width - 40 + 2, 8 + 2, 3, 2, new Color(24, 175, 175));
                    }

                    DrawHelper.drawRoundedTexture(new ResourceLocation(mc.thePlayer.getLocationSkin().getResourcePath()), x + width - 33, y + 32.5, 30, 30, 8, 8, 8, 8, 12);

                    float healthPercentage = Math.min(mc.thePlayer.getHealth() / mc.thePlayer.getMaxHealth(), 1.0f);
                    float healthBarWidth = (width - 40) * healthPercentage;
                    DrawHelper.drawRoundedRect(x + 3, y + 23.5, healthBarWidth, 8, 3, new Color(31, 206, 206));

                    float textX = mc.thePlayer.getHealth() <= 9 ? x + healthBarWidth - 5 : x + healthBarWidth - 7.5f;
                    poppins.drawString(mc.thePlayer.getHealth() <= 1 ? "" : String.valueOf((int) mc.thePlayer.getHealth()), textX - 1, y + 22.5f, -1);

                    poppins.drawString(text, x + 4, y + 4, -1);
                    break;
                case "Heart":
                    width = 140;
                    height = 34;
                    DrawHelper.drawRoundedRect(x, y, width, height, 4, new Color(0,0,0, 150));
                    DrawHelper.drawRoundedRectOutline( x - 1, y - 1, width + 2, height + 2, 4, 2, new Color(130, 130, 130));
                    RenderUtil.drawSkinHead(mc.thePlayer, x + 5, y + 5, 25);

                    // Health Bar
                    float healthPercentage1 = Math.min(mc.thePlayer.getHealth() / mc.thePlayer.getMaxHealth(), 1.0f);
                    float healthBarWidth1 = (width - 60) * healthPercentage1;
                    DrawHelper.drawRoundedRect(x + 33, y + 19, width - 60, 8, 3, new Color(255, 255, 255, 50));
                    DrawHelper.drawRoundedRect(x + 33, y + 19, healthBarWidth1, 8, 3, new Color(255, 255, 255));

                    roboto.drawString(text, x + 33, y + 5, -1);
                    roboto.drawString((int) mc.thePlayer.getHealth() + "HP", x + width - 25, y + 18.5f, -1);
                    break;
            }
        }
    }

}
