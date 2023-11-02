package wtf.tophat.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.events.impl.UpdateEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;

@ModuleInfo(name = "No Click Delay", desc = "removes left click delay", category = Module.Category.COMBAT)
public class NoClickDelay extends Module {

    @Listen
    public void onUpdate(UpdateEvent event) {
        mc.leftClickCounter = 0;
    }
}
