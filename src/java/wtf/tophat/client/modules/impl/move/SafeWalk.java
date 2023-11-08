package wtf.tophat.client.modules.impl.move;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.client.events.impl.SafeWalkEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;

@ModuleInfo(name = "Safe Walk", desc = "doesn't let you go off edges", category = Module.Category.MOVE)
public class SafeWalk extends Module {

    @Listen
    public void onSafe(SafeWalkEvent event) {
        if (getPlayer() == null || getWorld() == null)
            return;

        event.setSafe(mc.player.onGround);
    }
}
