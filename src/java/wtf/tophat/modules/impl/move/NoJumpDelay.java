package wtf.tophat.modules.impl.move;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.events.impl.DelayJumpEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;

@ModuleInfo(name = "No Jump Delay",desc = "removes jumping delay", category = Module.Category.MOVE)
public class NoJumpDelay extends Module {

    @Listen
    public void onJumpDelay(DelayJumpEvent event) {
        if (getPlayer() == null || getWorld() == null)
            return;

        event.setCancelled(true);
    }
}
