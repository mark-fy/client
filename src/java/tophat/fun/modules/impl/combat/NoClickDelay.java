package tophat.fun.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import tophat.fun.events.impl.game.UpdateEvent;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;

@ModuleInfo(name = "NoClickDelay", desc = "removes left click delay.", category = Module.Category.COMBAT)
public class NoClickDelay extends Module {

    @Listen
    public void onUpdate(UpdateEvent event) {
        mc.leftClickCounter = 0;
    }

}
