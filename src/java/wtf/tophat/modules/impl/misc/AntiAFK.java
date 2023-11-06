package wtf.tophat.modules.impl.misc;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.UpdateEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.utilities.math.MathUtil;
import wtf.tophat.utilities.player.rotations.GCDFix;
import wtf.tophat.utilities.time.TimeUtil;

@ModuleInfo(name = "AntiAFK", desc = "don't be kicked when you are AFK", category = Module.Category.MISC)
public class AntiAFK extends Module {
    public TimeUtil timerHelper = new TimeUtil();
    public float rot = 0;

    @Listen
    public void onUpdate(UpdateEvent e) {
        if(mc.settings.keyBindJump.pressed) {
            return;
        }
        if (mc.player.onGround) {
            mc.player.jump();
        }
    }

    @Listen
    public void onMotion(MotionEvent event) {
        float yaw = GCDFix.getFixedRotation((float) (Math.floor(this.spinAim(25)) + MathUtil.randomNumber(-4.0F, 1.0F)));
        event.setYaw(yaw);
        mc.player.renderYawOffset = yaw;
        mc.player.rotationPitchHead = 0;
        mc.player.rotationYawHead = yaw;
        if (timerHelper.elapsed(10 * 20)) {
            timerHelper.reset();
        }
    }
    public float spinAim(float rots) {
        rot += rots;
        return rot;
    }
}
