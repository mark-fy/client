package tophat.fun.modules.impl.movement;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.KeyBinding;
import tophat.fun.events.impl.player.MotionEvent;
import tophat.fun.events.impl.player.OmniSprintEvent;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.modules.settings.SettingInfo;
import tophat.fun.modules.settings.impl.BooleanSetting;
import tophat.fun.modules.settings.impl.NumberSetting;
import tophat.fun.modules.settings.impl.StringSetting;
import tophat.fun.utilities.player.MoveUtil;


@ModuleInfo(name = "Sprint", desc = "sprints for you", category = Module.Category.MOVEMENT)
public class Sprint extends Module {

    @SettingInfo(name = "Legit Sprint", booleanDefault = true)
    private final BooleanSetting legit = new BooleanSetting(this, "Legit Sprint", true);

    @SettingInfo(name = "All Directions", booleanDefault = false)
    private final BooleanSetting omni = new BooleanSetting(this, "All Directions", false);

    @SettingInfo(name = "Mode", stringDefault = "Vanilla")
    private final StringSetting mode = new StringSetting(this, "Mode", "Vanilla", "Vanilla", "Legit");

    @SettingInfo(name = "Speed", intDefault = 5)
    private final NumberSetting speed = new NumberSetting(this, "Speed", 1, 10, 5, 0);

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
