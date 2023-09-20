package wtf.tophat.module.impl.move;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.Client;
import wtf.tophat.events.Event;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.movement.MoveUtil;

@ModuleInfo(name = "Speed", desc = "move faster", category = Module.Category.MOVE)
public class Speed extends Module {

    private final StringSetting mode;
    private final NumberSetting speed;

    public Speed() {
        Client.settingManager.add(
                mode = new StringSetting(this, "Mode", "Vanilla", "Vanilla"),
                speed = new NumberSetting(this, "Speed", 0, 3, 1, 2)
                        .setHidden(() -> !mode.compare("Vanilla"))
        );
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if(event.state == Event.State.PRE) {
            switch (mode.getValue()) {
                case "Vanilla":
                    MoveUtil.setSpeed(speed.getValue().floatValue());
                    if(isMoving() && mc.player.onGround) {
                        mc.player.jump();
                    } else if(!isMoving()) {
                        mc.player.motionX = 0.0;
                        mc.player.motionZ = 0.0;
                    }
                    break;
            }
        }
    }

}
