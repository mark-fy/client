package tophat.fun.modules.impl.movement;

import io.github.nevalackin.radbus.Listen;
import tophat.fun.events.impl.player.SafeWalkEvent;
import tophat.fun.modules.base.Module;
import tophat.fun.modules.base.ModuleInfo;

@ModuleInfo(name = "SafeWalk", desc = "prevents you from falling off edges.", category = Module.Category.MOVEMENT)
public class SafeWalk extends Module {

    @Listen
    public void onSafe(SafeWalkEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        event.setSafe(mc.thePlayer.onGround);
    }
}
