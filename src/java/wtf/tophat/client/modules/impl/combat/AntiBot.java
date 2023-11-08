package wtf.tophat.client.modules.impl.combat;

import wtf.tophat.client.TopHat;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.StringSetting;

@ModuleInfo(name = "Anti Bot", desc = "doesn't attack bots", category = Module.Category.COMBAT)
public class AntiBot extends Module {

    public final StringSetting mode;
    public final BooleanSetting flying;

    public AntiBot() {
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "Custom", "Custom"),
                flying = new BooleanSetting(this, "Flying", true).setHidden(() -> !mode.is("Custom"))
        );
    }
}
