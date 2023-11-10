package wtf.tophat.client.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.client.events.impl.world.UpdateEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;

@ModuleInfo(name = "No Click Delay", desc = "removes left click delay", category = Module.Category.COMBAT)
public class NoClickDelay extends Module {

    @Listen
    public void onUpdate(UpdateEvent event) {
        mc.leftClickCounter = 0;
    }
}
