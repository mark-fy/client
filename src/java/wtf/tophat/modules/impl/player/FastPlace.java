package wtf.tophat.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.events.base.Event;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;

@ModuleInfo(name = "FastPlace", desc = "enables you to place blocks faster", category = Module.Category.PLAYER)
public class FastPlace extends Module {

    @Listen
    public void onMotion(MotionEvent eventMotion) {
        if (eventMotion.getState() == Event.State.PRE) {
            mc.rightClickDelayTimer = 0;
        }
    }

    @Override
    public void onDisable() { mc.rightClickDelayTimer = 6; }

}
