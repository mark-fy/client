package wtf.tophat.client.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.move.MotionEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.utilities.math.MathUtil;
import wtf.tophat.client.utilities.math.time.TimeUtil;

@ModuleInfo(name = "Auto Clicker", desc = "clicks the mouse for you", category = Module.Category.COMBAT)
public class AutoClicker extends Module {

    private final NumberSetting minCPS, maxCPS;
    private final TimeUtil timer = new TimeUtil();

    public AutoClicker() {
        TopHat.settingManager.add(
                minCPS = new NumberSetting(this, "Min CPS", 1, 24, 11, 0),
                maxCPS = new NumberSetting(this, "Max CPS", 2, 24, 12, 0)
        );
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if(mc.settings.keyBindAttack.isKeyDown() && !mc.player.isUsingItem() && mc.currentScreen == null) {
            if(timer.elapsed((long) (1000.0D / MathUtil.randomNumber(minCPS.get().doubleValue(), maxCPS.get().doubleValue())), true) && mc.player.ticksExisted % 5 != 0 && mc.player.ticksExisted % 17 != 0) {
                mc.leftClickCounter = 0;
                mc.click();
            }
        } else {
            timer.reset();
        }
    }
}