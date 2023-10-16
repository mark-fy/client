package wtf.tophat.module.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.KeyBinding;
import wtf.tophat.Client;
import wtf.tophat.events.impl.DirectionSprintCheckEvent;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.utilities.movement.MoveUtil;

@ModuleInfo(name = "Sprint",desc = "auto sprint", category = Module.Category.MOVE)
public class Sprint extends Module {

    private final BooleanSetting legit, omni;

    public Sprint() {
        Client.settingManager.add(
                legit = new BooleanSetting(this, "Legit", true),
                omni = new BooleanSetting(this, "All directions", false)
        );
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if(legit.get()) {
            mc.settings.keyBindSprint.pressed = true;
        } else {
            if (MoveUtil.getSpeed() != 0) {
                mc.player.setSprinting(true);
            }
        }
    }

    @Listen
    public void onOmniSprint(DirectionSprintCheckEvent event) {
        if(omni.get()) {
            if(MoveUtil.getSpeed() != 0) {
                event.setSprintCheck(false);
            }
        }
    }

    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(mc.settings.keyBindSprint.getKeyCode(), false);
        super.onDisable();
    }
}
