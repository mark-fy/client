package wtf.tophat.module.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;
import wtf.tophat.Client;
import wtf.tophat.events.impl.DirectionSprintCheckEvent;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.movement.MoveUtil;

@ModuleInfo(name = "Sprint",desc = "auto sprint", category = Module.Category.MOVE)
public class Spider extends Module {

    private final StringSetting mode;

    public Spider() {
        Client.settingManager.add(
                mode = new StringSetting(this, "Mode", "Vanilla", "Vanilla", "Verus", "Vulcan")
        );
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if (mc.player.isCollidedHorizontally) {
            if (!mc.player.onGround && mc.player.isCollidedVertically) {
                return;
            }

            switch (mode.getValue()) {
                case "Vanilla":
                    mc.player.jump();
                    break;
                case "Verus":
                    if (mc.player.ticksExisted % 3 == 0) {
                        mc.player.motionY = 0.42f;
                    }
                    break;
                case "Vulcan":
                    if (mc.player.isCollidedHorizontally) {
                        if (mc.player.ticksExisted % 2 == 0) {
                            event.setOnGround(true);
                            mc.player.motionY = 0.42F;
                        }
                        final double yaw = MoveUtil.getDirection();
                        event.setX(event.getX() - -MathHelper.sin((float) yaw) * 0.1f);
                        event.setZ(event.getZ() - MathHelper.cos((float) yaw) * 0.1f);
                    }
                    break;
            }
        }
    }

}
