package wtf.tophat.module.impl.combat;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.Client;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.utilities.math.TimeUtil;

@ModuleInfo(name = "TriggerBot",desc = "attacks on sight", category = Module.Category.COMBAT)
public class TriggerBot extends Module {

    private final NumberSetting cps;
    private final TimeUtil timer = new TimeUtil();

    public TriggerBot() {
        Client.settingManager.add(
                cps = new NumberSetting(this, "CPS", 1, 24, 12, 0)
        );
    }

    @Listen
    public void onMotion(MotionEvent event) {
        int randomizedCps = (int) ((cps.getValue().intValue() + Math.round(Math.random() / 6)) - Math.round(Math.random() / 8));
        boolean doubleClick;

        doubleClick = Math.random() * 100 < 33;

        if(mc.pointedEntity != null) {
            if(timer.elapsed(1000 / randomizedCps, true)) {
                click(doubleClick);
            }
        }
    }

}