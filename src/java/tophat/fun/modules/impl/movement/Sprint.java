package tophat.fun.modules.impl.movement;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.KeyBinding;
import tophat.fun.events.impl.player.MotionEvent;
import tophat.fun.events.impl.player.OmniSprintEvent;
import tophat.fun.modules.base.Module;
import tophat.fun.modules.base.ModuleInfo;
import tophat.fun.modules.base.settings.impl.BooleanSetting;
import tophat.fun.utilities.player.MoveUtil;

@ModuleInfo(name = "Sprint", desc = "sprints for you.", category = Module.Category.MOVEMENT)
public class Sprint extends Module {

    private final BooleanSetting legit = new BooleanSetting(this, "LegitSprint", true);
    private final BooleanSetting omni = new BooleanSetting(this, "AllDirections", false);

    @Listen
    public void onMotion(MotionEvent event) {
        if (legit.get()) {
            mc.gameSettings.keyBindSprint.pressed = true;
        } else if (MoveUtil.getSpeed() != 0) {
            mc.thePlayer.setSprinting(true);
        }
    }

    @Listen
    public void onOmniSprint(OmniSprintEvent event) {
        if(omni.get() && MoveUtil.getSpeed() != 0) {
            event.setSprintCheck(false);
        }
    }

    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
        super.onDisable();
    }
}
