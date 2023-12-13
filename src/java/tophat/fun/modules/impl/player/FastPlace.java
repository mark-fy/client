package tophat.fun.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import tophat.fun.events.impl.game.UpdateEvent;
import tophat.fun.modules.base.Module;
import tophat.fun.modules.base.ModuleInfo;

@ModuleInfo(name = "FastPlace", desc = "removes the delay between block placements.", category = Module.Category.PLAYER)
public class FastPlace extends Module {

    @Listen
    public void onUpdate(UpdateEvent event) {
        mc.rightClickDelayTimer = 0;
    }

    @Override
    public void onDisable() {
        mc.rightClickDelayTimer = 6;
        super.onDisable();
    }

}
