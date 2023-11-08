package wtf.tophat.client.modules.impl.move;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.client.events.impl.DelayJumpEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;

@ModuleInfo(name = "No Jump Delay",desc = "removes jumping delay", category = Module.Category.MOVE)
public class NoJumpDelay extends Module {

    @Listen
    public void onJumpDelay(DelayJumpEvent event) {
        if (getPlayer() == null || getWorld() == null)
            return;

        event.setCancelled(true);
    }
}
