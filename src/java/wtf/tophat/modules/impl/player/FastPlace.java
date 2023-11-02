package wtf.tophat.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.events.impl.UpdateEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;

@ModuleInfo(name = "Fast Place", desc = "removes placing delay", category = Module.Category.PLAYER)
public class FastPlace extends Module {

    @Listen
    public void onUpdate(UpdateEvent event) {
        mc.rightClickDelayTimer = 0;
    }

    @Override
    public void onDisable() {
        mc.rightClickDelayTimer = 6;
        super.onDisable();
    }
}
