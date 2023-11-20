package tophat.fun.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import tophat.fun.events.impl.game.UpdateEvent;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;

@ModuleInfo(name = "Fullbright", desc = "turns up the brightness.", category = Module.Category.RENDER)
public class Fullbright extends Module {

    private float oldGamma;

    @Listen
    public void onUpdate(UpdateEvent updateEvent) {
        mc.gameSettings.gammaSetting = 100F;
    }

    @Override
    public void onEnable() {
        this.oldGamma = mc.gameSettings.gammaSetting;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = this.oldGamma;
        super.onDisable();
    }

}
