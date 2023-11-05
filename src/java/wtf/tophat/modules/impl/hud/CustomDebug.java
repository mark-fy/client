package wtf.tophat.modules.impl.hud;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.events.impl.DebugOverlayEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;

@ModuleInfo(name = "Custom Debug",desc = "change the debug menu", category = Module.Category.HUD)
public class CustomDebug extends Module {

    @Listen
    public void onDebug(DebugOverlayEvent event) {
        event.setName("TopHat");
        event.setVersion("0.0.5");
    }

}
