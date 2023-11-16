package tophat.fun.modules.impl.movement;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.KeyBinding;
import tophat.fun.Client;
import tophat.fun.events.impl.player.MotionEvent;
import tophat.fun.events.impl.player.OmniSprintEvent;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.modules.settings.impl.BooleanSetting;
import tophat.fun.modules.settings.impl.NumberSetting;
import tophat.fun.modules.settings.impl.StringSetting;
import tophat.fun.utilities.player.MoveUtil;


@ModuleInfo(name = "Sprint", desc = "sprints for you.", category = Module.Category.MOVEMENT)
public class Sprint extends Module {

    private final BooleanSetting legit = new BooleanSetting(this, "Legit Sprint", true);
    private final BooleanSetting omni = new BooleanSetting(this, "All Directions", false);
    private final StringSetting mode = new StringSetting(this, "Mode", "Vanilla", "Vanilla", "Legit");
    private final NumberSetting speed = new NumberSetting(this, "Speed", 1, 10, 5, 0);

    public Sprint() {
        Client.INSTANCE.settingManager.add(
                legit, omni, mode, speed
        );
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if (legit.get()) {
            mc.gameSettings.keyBindSprint.pressed = true;
        } else {
            if (MoveUtil.getSpeed() != 0) {
                mc.thePlayer.setSprinting(true);
            }
        }
    }

    @Listen
    public void onOmniSprint(OmniSprintEvent event) {
        if(omni.get()) {
            if (MoveUtil.getSpeed() != 0) {
                event.setSprintCheck(false);
            }
        }
    }

    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
        super.onDisable();
    }

}
