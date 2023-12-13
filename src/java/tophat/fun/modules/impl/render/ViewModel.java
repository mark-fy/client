package tophat.fun.modules.impl.render;

import tophat.fun.modules.base.Module;
import tophat.fun.modules.base.ModuleInfo;
import tophat.fun.modules.base.settings.impl.BooleanSetting;
import tophat.fun.modules.base.settings.impl.NumberSetting;

@ModuleInfo(name = "ViewModel", desc = "modify your hand item properties.", category = Module.Category.RENDER)
public class ViewModel extends Module {

    public final NumberSetting xPos = new NumberSetting(this, "ModelX", 0.10, 1, 0.56, 2);
    public final NumberSetting yPos = new NumberSetting(this, "ModelY", 0.10, 1, 0.52, 2);
    public final NumberSetting zPos = new NumberSetting(this, "ModelZ", 0.10, 2, 0.72, 2);
    public final NumberSetting scale = new NumberSetting(this, "ModelScale", 0.01, 1, 0.4, 2);
    public final NumberSetting rotation = new NumberSetting(this, "ModelRotation", 0, 360, 45, 0);
    public final BooleanSetting usingItem = new BooleanSetting(this, "UsingItem", true);

}
