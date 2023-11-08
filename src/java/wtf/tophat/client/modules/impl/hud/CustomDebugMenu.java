package wtf.tophat.client.modules.impl.hud;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.client.events.impl.DebugOverlayEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;

@ModuleInfo(name = "Custom Debug Menu",desc = "change the debug menu", category = Module.Category.HUD)
public class CustomDebugMenu extends Module {

    @Listen
    public void onDebug(DebugOverlayEvent event) {
        event.setName("TopHat");
        event.setVersion("0.0.5");
    }

}
