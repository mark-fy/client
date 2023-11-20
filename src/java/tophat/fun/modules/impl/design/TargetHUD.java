package tophat.fun.modules.impl.design;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;
import tophat.fun.Client;
import tophat.fun.events.impl.render.Render2DEvent;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.modules.impl.combat.Aura;
import tophat.fun.utilities.font.CFont;
import tophat.fun.utilities.font.renderer.TTFFontRenderer;
import tophat.fun.utilities.render.RenderUtil;
import tophat.fun.utilities.render.RoundUtil;

import java.awt.*;

@ModuleInfo(name = "TargetHUD", desc = "displays the aura's target info.", category = Module.Category.DESIGN)
public class TargetHUD extends Module {

    private final static TTFFontRenderer poppins = CFont.FONT_MANAGER.getFont("PoppinsMedium 18");

    @Listen
    public void on2D(Render2DEvent event) {
        float x = (float) event.getScaledResolution().getScaledWidth() / 2 + 10;
        float y = (float) event.getScaledResolution().getScaledHeight() / 2 + 10;
        float width = 120;
        float height = 35;

        String text = "";
        if(!(mc.currentScreen instanceof GuiChat)) {
            if(Client.INSTANCE.moduleManager.getByClass(Aura.class).isEnabled() && Aura.target != null && !Aura.target.isDead) {
                if(!(Aura.target instanceof EntityPlayer)) return;
                text = Aura.target.getName();

                EntityLivingBase et = (EntityLivingBase) Aura.target;
                if(et.getHealth() <= 0) return;

                RoundUtil.round( x - 1, y - 1, width + 2, height + 2, 6, new Color(24, 175, 162));
                RoundUtil.round( x, y, width, height, 6, new Color(25,25,25));
                RenderUtil.drawHead(et, x + width - 35, y + 3, 30);

                RoundUtil.round(x + 3 - 1, y + 22.5, width - 40 + 2, 8 + 2, 3, new Color(24, 175, 175));
                RoundUtil.round(x + 3, y + 23.5, width - 40, 8, 3, new Color(25,25,25));

                float healthPercentage = Math.min(et.getHealth() / et.getMaxHealth(), 1.0f);
                float healthBarWidth = (width - 40) * healthPercentage;
                RoundUtil.round(x + 3, y + 23.5, healthBarWidth, 8, 3, new Color(31, 206, 206));

                float textX = et.getHealth() <= 9 ? x + healthBarWidth - 5 : x + healthBarWidth - 7.5f;
                poppins.drawString(et.getHealth() <= 1 ? "" : String.valueOf((int) et.getHealth()), textX, y + 22.5f, -1);

                poppins.drawString(text, x + 4, y + 4, -1);
            }
        } else {
            text = "You";

            RoundUtil.round(x - 1, y - 1, width + 2, height + 2, 6, new Color(24, 175, 162));
            RoundUtil.round(x, y, width, height, 6, new Color(25,25,25));
            RenderUtil.drawHead(mc.thePlayer, x + width - 33, y + 3, 30);

            RoundUtil.round(x + 3 - 1, y + 22.5, width - 40 + 2, 8 + 2, 3, new Color(24, 175, 175));
            RoundUtil.round(x + 3, y + 23.5, width - 40, 8, 3, new Color(25,25,25));

            float healthPercentage = Math.min(mc.thePlayer.getHealth() / mc.thePlayer.getMaxHealth(), 1.0f);
            float healthBarWidth = (width - 40) * healthPercentage;
            RoundUtil.round(x + 3, y + 23.5, healthBarWidth, 8, 3, new Color(31, 206, 206));

            float textX = mc.thePlayer.getHealth() <= 9 ? x + healthBarWidth - 5 : x + healthBarWidth - 7.5f;
            poppins.drawString(mc.thePlayer.getHealth() <= 1 ? "" : String.valueOf((int) mc.thePlayer.getHealth()), textX, y + 22.5f, -1);

            poppins.drawString(text, x + 4, y + 4, -1);
        }
    }

}
