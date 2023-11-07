package wtf.tophat.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import wtf.tophat.events.impl.OnDeathEvent;
import wtf.tophat.events.impl.Render2DEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.utilities.render.DrawingUtil;

import java.awt.*;

@ModuleInfo(name = "Session Info", desc = "displays info about the current session", category = Module.Category.RENDER)
public class SessionInfo extends Module {

    private int deaths = 0;

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
    public void onRender(Render2DEvent event) {
        ScaledResolution sr = event.getScaledResolution();
        FontRenderer fr = mc.fontRenderer;

        // WIP UI
        DrawingUtil.rectangle(60, 60, 120, 40, true, new Color(45,45, 45));
        fr.drawStringWithShadow("Session Info", 62, 62, Color.BLUE);
        fr.drawStringWithShadow("Deaths: " + deaths, 62, 72, Color.RED);
    }

}
