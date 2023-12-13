package tophat.fun.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import tophat.fun.events.impl.game.UpdateEvent;
import tophat.fun.modules.base.Module;
import tophat.fun.modules.base.ModuleInfo;
import tophat.fun.modules.base.settings.impl.NumberSetting;

@ModuleInfo(name = "Timer", desc = "modifies the game speed.", category = Module.Category.PLAYER)
public class Timer extends Module {

    private final NumberSetting speed = new NumberSetting(this, "Timer Speed", 0.1, 5, 1, 1);

    @Listen
    public void onUpdate(UpdateEvent updateEvent) {
        mc.timer.timerSpeed = speed.get().floatValue();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1f;
        super.onDisable();
    }

}
