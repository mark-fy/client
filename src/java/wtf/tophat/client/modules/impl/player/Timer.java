package wtf.tophat.client.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.UpdateEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.NumberSetting;

@ModuleInfo(name = "Timer",desc = "modify game speed", category = Module.Category.PLAYER)
public class Timer extends Module {

    private final NumberSetting speed;

    public Timer() {
        TopHat.settingManager.add(
                speed = new NumberSetting(this, "Timer Speed", 0.1, 5, 1, 1)
        );
    }

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
