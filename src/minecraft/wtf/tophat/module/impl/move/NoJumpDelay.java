package wtf.tophat.module.impl.move;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.events.impl.DelayJumpEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.utilities.Methods;

@ModuleInfo(name = "NoJumpDelay",desc = "removes jumping delay", category = Module.Category.MOVE)
public class NoJumpDelay extends Module {

    @Listen
    public void onJumpDelay(DelayJumpEvent event) {
        if(Methods.mc.player == null || Methods.mc.world == null)
            return;

        event.setCancelled(true);
    }

}
