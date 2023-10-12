package wtf.tophat.module.impl.render;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.events.impl.UpdateEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;

@ModuleInfo(name = "Fullbright",desc = "turns up your brightness", category = Module.Category.RENDER)
public class Fullbright extends Module {

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
