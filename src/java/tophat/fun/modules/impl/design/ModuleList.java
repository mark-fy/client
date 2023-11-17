package tophat.fun.modules.impl.design;

import net.minecraft.client.gui.ScaledResolution;
import tophat.fun.Client;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.utilities.font.CFont;
import tophat.fun.utilities.font.renderer.TTFFontRenderer;
import tophat.fun.utilities.render.RectUtil;
import tophat.fun.utilities.render.RoundUtil;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

@ModuleInfo(name = "ModuleList", desc = "renders a list of enabled modules.", category = Module.Category.DESIGN)
public class ModuleList extends Module {

    public ModuleList() {
        setEnabled(true);
    }

    private final static TTFFontRenderer poppins = CFont.FONT_MANAGER.getFont("PoppinsMedium 18");

    public void renderIngame() {
        ScaledResolution sr = new ScaledResolution(mc);
        List<Module> enabledModules = Client.INSTANCE.moduleManager.getEnabledModules()
                .stream()
                .sorted((module1, module2) -> {
                    int width1 = (int) poppins.getWidth(module1.getName()) + 1;
                    int width2 = (int) poppins.getWidth(module2.getName()) + 1;
                    return width2 - width1;
                })
                .collect(Collectors.toList());

        if (enabledModules.isEmpty()) {
            return;
        }

        int scWidth = sr.getScaledWidth(), pHeight = (int) ((poppins.getHeight() + 2 - poppins.getHeight()) / 2 - 3), y = 6;

        for (Module module : enabledModules) {
            RectUtil.rectangle(scWidth - 8, y + pHeight, 4, 15.5, true, new Color(25,25,25));
            RoundUtil.drawRoundedRect(scWidth - 5 - poppins.getWidth(module.getName()), y + pHeight, poppins.getWidth(module.getName()) + 2, poppins.getHeight() + 4, 6, new Color(25,25,25));
            RectUtil.rectangle(scWidth - 4, y + pHeight, 4, 15.5, true, new Color(25,25,25));
            poppins.drawString(module.getName(), scWidth - 2 - poppins.getWidth(module.getName()), y + (poppins.getHeight() + 4 - poppins.getHeight()) / 2 - 2, -1);
            y += 12;
        }
    }

    @Override
    public void renderDummy() {
        renderIngame();
    }

}
