package wtf.tophat.client.modules.impl.misc;

import io.github.nevalackin.radbus.Listen;
import wtf.tophat.client.events.impl.combat.RotationEvent;
import wtf.tophat.client.events.impl.world.UpdateEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.utilities.math.MathUtil;
import wtf.tophat.client.utilities.player.rotations.GCDFix;
import wtf.tophat.client.utilities.math.time.TimeUtil;

@ModuleInfo(name = "Anti AFK", desc = "prevent anti afk plugins from kicking you", category = Module.Category.MISC)
public class AntiAFK extends Module {
    private final TimeUtil timerHelper = new TimeUtil();
    private float rot = 0;

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
    public void onRots(RotationEvent event) {
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
