package tophat.fun.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import tophat.fun.events.impl.game.KeyPressedEvent;
import tophat.fun.modules.base.Module;
import tophat.fun.modules.base.ModuleInfo;
import tophat.fun.modules.base.settings.impl.NumberSetting;
import tophat.fun.utilities.others.TimeUtil;

import java.util.Random;

@ModuleInfo(name = "AutoClicker", desc = "clicks for you.", category = Module.Category.COMBAT)
public class AutoClicker extends Module {

    private final NumberSetting minCPS = new NumberSetting(this, "MinCPS", 0, 20, 10, 0);
    private final NumberSetting maxCPS = new NumberSetting(this, "MaxCPS", 0, 20, 15, 0);
    private final NumberSetting randomization = new NumberSetting(this, "Randomization", 0, 10, 5, 0);

    private final TimeUtil timer = new TimeUtil();
    private final Random random = new Random();

    private int clicks = 0;
    private int lastTick = 0;

    @Listen
    public void onPressed(KeyPressedEvent event) {
        if (mc.thePlayer != null && mc.theWorld != null && mc.currentScreen == null && mc.thePlayer.ticksExisted > 20) {
            if (event.getKeyBinding() == mc.gameSettings.keyBindAttack) {
                if (timer.elapsed(1000)) {
                    clicks = 0;
                    timer.reset();
                }

                int targetCPS = (int) Math.round(minCPS.get().intValue() + (randomization.get().intValue() > 0 ? random.nextDouble() * randomization.get().intValue() : 0));
                int actualCPS = Math.max(minCPS.get().intValue(), Math.min(maxCPS.get().intValue(), targetCPS));

                if (randomization.get().doubleValue() > 0 && random.nextDouble() < 0.2) {
                    actualCPS += random.nextInt(5) - 2;
                }

                if (clicks <= actualCPS && lastTick != mc.thePlayer.ticksExisted) {
                    event.setPressed(true);
                    lastTick = mc.thePlayer.ticksExisted;
                    clicks++;
                }
            }
        }
    }
}
