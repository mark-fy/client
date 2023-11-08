package wtf.tophat.client.modules.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.KeyBinding;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.DirectionSprintCheckEvent;
import wtf.tophat.client.events.impl.MotionEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.modules.impl.player.ScaffoldWalk;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.utilities.player.movement.MoveUtil;

@ModuleInfo(name = "Sprint",desc = "auto sprint", category = Module.Category.MOVE)
public class Sprint extends Module {

    private final BooleanSetting legit, omni;

    public Sprint() {
        TopHat.settingManager.add(
                legit = new BooleanSetting(this, "Legit", true),
                omni = new BooleanSetting(this, "All directions", false)
        );
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if(!TopHat.moduleManager.getByClass(ScaffoldWalk.class).isEnabled()) {
            if (legit.get()) {
                mc.settings.keyBindSprint.pressed = true;
            } else {
                if (MoveUtil.getSpeed() != 0) {
                    mc.player.setSprinting(true);
                }
            }
        }
    }

    @Listen
    public void onOmniSprint(DirectionSprintCheckEvent event) {
        if(omni.get()) {
            if(!TopHat.moduleManager.getByClass(ScaffoldWalk.class).isEnabled()) {
                if (MoveUtil.getSpeed() != 0) {
                    event.setSprintCheck(false);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(mc.settings.keyBindSprint.getKeyCode(), false);
        super.onDisable();
    }
}
