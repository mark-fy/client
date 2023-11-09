package wtf.tophat.client.modules.impl.render;

import wtf.tophat.client.TopHat;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.NumberSetting;

@ModuleInfo(name = "View Model",desc = "changes your hand item size & position", category = Module.Category.RENDER)
public class ViewModel extends Module {

    public final NumberSetting xPos, yPos, scale;

    public ViewModel() {
        TopHat.settingManager.add(
                xPos = new NumberSetting(this, "Model X", 0.10f, 1f, 0.56f, 2),
                yPos = new NumberSetting(this, "Model Y", 0.10f, 1f, 0.52f, 2),
                scale = new NumberSetting(this, "Model Scale", 0.01f, 1f, 0.4f, 2)
        );
    }
}