package wtf.tophat.client.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.world.TimeEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.utilities.math.time.TimeUtil;

@ModuleInfo(name = "Timer Range",desc = "shift time", category = Module.Category.COMBAT)
public class TimerRange extends Module {

    private final NumberSetting autoChargeDelay, maxDischarge;
    private final TimeUtil timer = new TimeUtil();
    private long shifted, previousTime;

    public TimerRange() {
        TopHat.settingManager.add(
                autoChargeDelay = new NumberSetting(this, "Auto Charge Delay", 10, 1500, 100, 0),
                maxDischarge = new NumberSetting(this, "Max Discharge Amount", 10, 1000, 300, 0)
        );
    }

    @Listen
    public void onTime(TimeEvent event) {
        if(timer.elapsed(autoChargeDelay.get().longValue())) {
            charge(event);
        }

        if(shifted >= maxDischarge.get().longValue()) {
            discharge();
        }

        previousTime = event.getBalance();
        event.setBalance(event.getBalance() - shifted);
    }

    public void charge(TimeEvent eventTime) { shifted += eventTime.getBalance() - previousTime; }

    public void discharge() { shifted = 0; }
}
