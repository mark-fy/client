package wtf.tophat.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.Client;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.utilities.math.MathUtil;
import wtf.tophat.utilities.time.TimeUtil;

@ModuleInfo(name = "Auto Clicker", desc = "clicks the mouse for you", category = Module.Category.COMBAT)
public class AutoClicker extends Module {

    private final NumberSetting minCPS, maxCPS;
    private TimeUtil timer = new TimeUtil();

    public AutoClicker() {
        Client.settingManager.add(
                minCPS = new NumberSetting(this, "Min CPS", 1, 24, 11, 0),
                maxCPS = new NumberSetting(this, "Max CPS", 2, 24, 12, 0)
        );
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if(mc.settings.keyBindAttack.isKeyDown() && !mc.player.isUsingItem()) {
            if(timer.elapsed((long) (1000.0D / MathUtil.randomNumber(minCPS.get().doubleValue(), maxCPS.get().doubleValue())), true) && mc.player.ticksExisted % 5 != 0 && mc.player.ticksExisted % 17 != 0) {
                mc.leftClickCounter = 0;
                mc.click();
            }
        } else {
            timer.reset();
        }
    }
}