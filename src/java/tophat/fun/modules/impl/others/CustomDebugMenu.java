package tophat.fun.modules.impl.others;

import io.github.nevalackin.radbus.Listen;
import tophat.fun.Client;
import tophat.fun.events.impl.player.DebugOverlayEvent;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;

@ModuleInfo(name = "CustomDebugMenu", desc = "modifies the F3 debug menu.", category = Module.Category.OTHERS)
public class CustomDebugMenu extends Module {

    @Listen
    public void onDebug(DebugOverlayEvent event) {
        event.setName("TopHat");
        event.setVersion(Client.CVERSION);
    }

}
