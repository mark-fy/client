package wtf.tophat.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.Client;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.DividerSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.utilities.math.TimeUtil;

@ModuleInfo(name = "Trigger Bot",desc = "attacks on sight", category = Module.Category.COMBAT)
public class TriggerBot extends Module {

    private final DividerSetting general;
    private final NumberSetting cps;
    private final TimeUtil timer = new TimeUtil();

    public TriggerBot() {
        Client.settingManager.add(
                general = new DividerSetting(this, "General Settings"),
                cps = new NumberSetting(this, "CPS", 1, 24, 12, 0)
        );
    }

    @Listen
    public void onMotion(MotionEvent event) {
        int randomizedCps = (int) ((cps.get().intValue() + Math.round(Math.random() / 6)) - Math.round(Math.random() / 8));
        boolean doubleClick;

        doubleClick = Math.random() * 100 < 33;

        if(mc.pointedEntity != null) {
            if(timer.elapsed(1000 / randomizedCps, true)) {
                click(doubleClick);
            }
        }
    }

}
