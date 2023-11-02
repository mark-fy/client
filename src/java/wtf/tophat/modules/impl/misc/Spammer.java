package wtf.tophat.modules.impl.misc;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.Client;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.utilities.math.MathUtil;
import wtf.tophat.utilities.time.TimeUtil;

@ModuleInfo(name = "Spammer", desc = "spam a text in the chat", category = Module.Category.EXPLOIT)
public final class Spammer extends Module {

    private final NumberSetting delay;
    private final BooleanSetting bypass, antispam;

    public Spammer() {
        Client.settingManager.add(
                delay = new NumberSetting(this, "Speed", 0, 1000, 0, 2),
                bypass = new BooleanSetting(this, "Bypass", true),
                antispam = new BooleanSetting(this, "Anti Spam", true)
        );
    }


    private TimeUtil timer = new TimeUtil();

    @Listen
    public void onMotion(MotionEvent event) {
        String spammerText = "buy TopHat @ tophat?wtf";

        if (timer.elapsed(bypass.get() ? 2000 : delay.get().longValue())) {
            if (antispam.get()) {
                spammerText += " " + MathUtil.getRandInt(10, 100000);
            }

            mc.player.sendChatMessage(spammerText);
            timer.reset();
        }
    }
}