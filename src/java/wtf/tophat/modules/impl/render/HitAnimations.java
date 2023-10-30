package wtf.tophat.modules.impl.render;


import wtf.tophat.Client;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;

@ModuleInfo(name = "Hit Animations",desc = "changes your item swinging animation", category = Module.Category.RENDER)
public class HitAnimations extends Module {

    public final NumberSetting swingSpeed;
    public final BooleanSetting smoothSwing;

    public HitAnimations() {
        Client.settingManager.add(
                swingSpeed = new NumberSetting(this, "Swing Speed", 0.1f, 3.5f, 1.2f, 1),
                smoothSwing = new BooleanSetting(this, "Smooth Swing", false)
        );
    }

}
