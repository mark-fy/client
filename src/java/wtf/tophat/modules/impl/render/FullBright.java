package wtf.tophat.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.events.impl.UpdateEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;

@ModuleInfo(name = "Full Bright",desc = "turns up your brightness", category = Module.Category.RENDER)
public class FullBright extends Module {

    private float oldGamma;

    @Listen
    public void onUpdate(UpdateEvent updateEvent) {
        mc.settings.gammaSetting = 100F;
    }

    @Override
    public void onEnable() {
        this.oldGamma = mc.settings.gammaSetting;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.settings.gammaSetting = this.oldGamma;
        super.onDisable();
    }
}
