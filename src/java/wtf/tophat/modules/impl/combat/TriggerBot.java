package wtf.tophat.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.DividerSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.utilities.math.MathUtil;
import wtf.tophat.utilities.math.time.TimeUtil;

@ModuleInfo(name = "Trigger Bot",desc = "attacks on sight", category = Module.Category.COMBAT)
public class TriggerBot extends Module {

    private final DividerSetting general;
    private final NumberSetting minCPS, maxCPS;
    private final TimeUtil timer = new TimeUtil();

    public TriggerBot() {
        TopHat.settingManager.add(
                general = new DividerSetting(this, "General Settings"),
                minCPS = new NumberSetting(this, "Min CPS", 1, 24, 11, 0),
                maxCPS = new NumberSetting(this, "Max CPS", 2, 24, 12, 0)
        );
    }

    @Listen
    public void onMotion(MotionEvent event) {
        boolean doubleClick;

        doubleClick = Math.random() * 100 < 33;

        if(mc.pointedEntity != null) {
            if(timer.elapsed((long) (1000.0D / MathUtil.randomNumber(minCPS.get().doubleValue(), maxCPS.get().doubleValue())))) {
                click(doubleClick);
            }
        }
    }
}
