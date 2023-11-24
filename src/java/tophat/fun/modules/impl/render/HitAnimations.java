package tophat.fun.modules.impl.render;

import tophat.fun.Client;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.modules.settings.impl.BooleanSetting;
import tophat.fun.modules.settings.impl.NumberSetting;

@ModuleInfo(name = "HitAnimations", desc = "changes the item hit animation.", category = Module.Category.RENDER)
public class HitAnimations extends Module {

    public final NumberSetting swingSpeed = new NumberSetting(this, "SwingSpeed", 0.1, 3.5, 1.2, 1);
    public final BooleanSetting smoothSwing = new BooleanSetting(this, "SmoothSwing", false);

    public HitAnimations() {
        Client.INSTANCE.settingManager.add(
                swingSpeed, smoothSwing
        );
    }

}
