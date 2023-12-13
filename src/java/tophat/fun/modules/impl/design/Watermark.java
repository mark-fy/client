package tophat.fun.modules.impl.design;

import net.minecraft.client.Minecraft;
import tophat.fun.Client;
import tophat.fun.modules.base.Module;
import tophat.fun.modules.base.ModuleInfo;
import tophat.fun.modules.base.settings.impl.BooleanSetting;
import tophat.fun.utilities.font.CFont;
import tophat.fun.utilities.font.renderer.TTFFontRenderer;
import tophat.fun.utilities.render.shader.DrawHelper;

import java.awt.*;

@ModuleInfo(name = "Watermark", desc = "client watermark.", category = Module.Category.DESIGN)
public class Watermark extends Module {

    private final BooleanSetting gradientOutline = new BooleanSetting(this, "GradientOutline", false);

    public Watermark() {
        setEnabled(true);
        setHidden(true);
    }

    private final static TTFFontRenderer poppins = CFont.FONT_MANAGER.getFont("PoppinsMedium 18");

    public void renderIngame() {
        String text = Client.CNAME + " | " + Client.CVERSION + " | " + mc.getSession().getUsername() + " | " + Minecraft.getDebugFPS();
        if(gradientOutline.get()) {
            DrawHelper.drawRoundedGradientRect(5, 5, poppins.getWidth(text) + 9, 18, 6, new Color(24, 175, 162), new Color(0, 101, 197), new Color(24, 175, 162).brighter().brighter(), new Color(0, 101, 197).brighter().brighter());
            DrawHelper.drawRoundedRect(6, 6, poppins.getWidth(text) + 7, 16, 6, new Color(25, 25, 25));
            poppins.drawString(text, 10, 8.5f, -1);
        } else {
            DrawHelper.drawRoundedRect(6, 6, poppins.getWidth(text) + 7, 16, 6, new Color(25, 25, 25));
            DrawHelper.drawRoundedRectOutline(5, 5, poppins.getWidth(text) + 9, 18, 6, 2, new Color(24, 175, 162));
            poppins.drawString(text, 10, 8, -1);
        }
    }

    @Override
    public void renderDummy() {
        renderIngame();
    }

}
