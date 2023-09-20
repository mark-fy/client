package wtf.tophat.module.impl.move;

import io.github.nevalackin.radbus.Listen;
import org.lwjgl.input.Keyboard;
import wtf.tophat.Client;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.utilities.movement.MoveUtil;

@ModuleInfo(name = "Flight",desc = "fly like a bird", category = Module.Category.MOVE)
public class Flight extends Module {

    private final StringSetting mode;
    private final NumberSetting speed;

    public Flight() {
        Client.settingManager.add(
                mode = new StringSetting(this, "Mode", "Vanilla", "Vanilla"),
                speed = new NumberSetting(this, "Speed", 0, 4, 1, 2)
        );
    }

    @Listen
    public void onMotion(MotionEvent event) {
        switch ((mode.get())){
            case "Vanilla":
                mc.player.motionY = 0;

                if (Keyboard.isKeyDown(mc.settings.keyBindJump.getKeyCode())) {
                    mc.player.motionY = speed.get().floatValue();
                }

                if (Keyboard.isKeyDown(mc.settings.keyBindSneak.getKeyCode())) {
                    mc.player.motionY = -speed.get().floatValue();
                }

                MoveUtil.setSpeed(speed.get().floatValue());
                break;
        }
    }
}
