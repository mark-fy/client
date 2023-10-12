package wtf.tophat.module.impl.combat;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;

@ModuleInfo(name = "No Click Delay", desc = "removes left click delay", category = Module.Category.COMBAT)
public class NoClickDelay extends Module {

    @Listen
    public void onMotion(MotionEvent event) {
        mc.leftClickCounter = 0;
    }

}
