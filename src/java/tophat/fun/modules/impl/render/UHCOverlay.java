package tophat.fun.modules.impl.render;

import tophat.fun.modules.base.Module;
import tophat.fun.modules.base.ModuleInfo;
import tophat.fun.modules.base.settings.impl.NumberSetting;

@ModuleInfo(name = "UHCOverlay", desc = "modifies your game for UHC's.", category = Module.Category.RENDER)
public class UHCOverlay extends Module {

    public final NumberSetting gAppleSize = new NumberSetting(this, "GAppleSize", 0.1, 4, 1.7, 1);

}
