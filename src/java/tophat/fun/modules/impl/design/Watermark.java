package tophat.fun.modules.impl.design;

import net.minecraft.client.Minecraft;
import tophat.fun.Client;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.utilities.font.CFont;
import tophat.fun.utilities.font.renderer.TTFFontRenderer;
import tophat.fun.utilities.render.shader.DrawHelper;

import java.awt.*;

@ModuleInfo(name = "Watermark", desc = "client watermark.", category = Module.Category.DESIGN)
public class Watermark extends Module {

    public Watermark() {
        setEnabled(true);
    }

    private final static TTFFontRenderer poppins = CFont.FONT_MANAGER.getFont("PoppinsMedium 18");

    public void renderIngame() {
        String text = Client.CNAME + " | " + Client.CVERSION + " | " + mc.getSession().getUsername() + " | " + Minecraft.getDebugFPS();
        DrawHelper.drawRoundedRect(6,6, poppins.getWidth(text) + 7, 16, 6, new Color(25,25,25));
        DrawHelper.drawRoundedRectOutline(5,5, poppins.getWidth(text) + 9, 18, 6,2, new Color(24,175,162));
        poppins.drawString(text, 10, 8, -1);
    }

    @Override
    public void renderDummy() {
        renderIngame();
    }

}
