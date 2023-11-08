package wtf.tophat.client.modules.impl.render;

import wtf.tophat.client.TopHat;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.NumberSetting;

@ModuleInfo(name = "Hit Animations",desc = "changes your item swinging animation", category = Module.Category.RENDER)
public class HitAnimations extends Module {

    public final NumberSetting swingSpeed;
    public final BooleanSetting smoothSwing;

    public HitAnimations() {
        TopHat.settingManager.add(
                swingSpeed = new NumberSetting(this, "Swing Speed", 0.1f, 3.5f, 1.2f, 1),
                smoothSwing = new BooleanSetting(this, "Smooth Swing", false)
        );
    }
}
