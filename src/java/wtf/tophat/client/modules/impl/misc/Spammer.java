package wtf.tophat.client.modules.impl.misc;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.move.MotionEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.utilities.math.MathUtil;
import wtf.tophat.client.utilities.math.time.TimeUtil;

@ModuleInfo(name = "Spammer", desc = "spam a text in the chat", category = Module.Category.MISC)
public final class Spammer extends Module {

    private final NumberSetting delay;
    private final BooleanSetting bypass, antispam;

    public Spammer() {
        TopHat.settingManager.add(
                delay = new NumberSetting(this, "Speed", 0, 1000, 0, 2),
                bypass = new BooleanSetting(this, "Bypass", true),
                antispam = new BooleanSetting(this, "Anti Spam", true)
        );
    }


    private TimeUtil timer = new TimeUtil();

    @Listen
    public void onMotion(MotionEvent event) {
        String spammerText = "buy TopHat @ tophat?fun";

        if (timer.elapsed(bypass.get() ? 2000 : delay.get().longValue())) {
            if (antispam.get()) {
                spammerText += " " + MathUtil.getRandInt(10, 100000);
            }

            mc.player.sendChatMessage(spammerText);
            timer.reset();
        }
    }
}