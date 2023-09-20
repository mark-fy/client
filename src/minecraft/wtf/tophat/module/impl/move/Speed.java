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
                mode = new StringSetting(this, "Mode", "Vanilla", "Vanilla", "Intave"),
                speed = new NumberSetting(this, "Speed", 0, 3, 1, 2)
                        .setHidden(() -> !mode.compare("Vanilla"))
        );
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if(event.state == Event.State.PRE) {
            switch (mode.getValue()) {
                case "Intave":
                    mc.settings.keyBindJump.pressed = MoveUtil.getSpeed() != 0;

                    if(mc.player.onGround) {
                        mc.timer.timerSpeed = 1.07F;
                    } else {
                        mc.timer.timerSpeed = (float) (1 + Math.random() / 1200);
                    }

                    if(mc.player.motionY > 0) {
                        mc.timer.timerSpeed += 0.01;
                        mc.player.motionX *= 1.0004;
                        mc.player.motionZ *= 1.0004;
                    }

                    if(mc.player.hurtTime != 0) {
                        mc.timer.timerSpeed = 1.21f;
                    }
                    break;
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
