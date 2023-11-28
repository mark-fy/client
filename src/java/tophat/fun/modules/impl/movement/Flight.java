package tophat.fun.modules.impl.movement;

import io.github.nevalackin.radbus.Listen;
import org.lwjgl.input.Keyboard;
import tophat.fun.Client;
import tophat.fun.events.Event;
import tophat.fun.events.impl.game.UpdateEvent;
import tophat.fun.events.impl.player.MotionEvent;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.modules.settings.impl.NumberSetting;
import tophat.fun.modules.settings.impl.StringSetting;
import tophat.fun.utilities.others.TimeUtil;
import tophat.fun.utilities.player.MoveUtil;

@ModuleInfo(name = "Flight", desc = "makes you float midair.", category = Module.Category.MOVEMENT)
public class Flight extends Module {

    private final StringSetting mode = new StringSetting(this, "Mode", "Vanilla", "Vanilla", "Jump");
    private final NumberSetting vSpeed = new NumberSetting(this, "VanillaSpeed", 1, 2, 0.5, 1).setHidden(() -> !mode.is("Vanilla"));

    public Flight() {
        Client.INSTANCE.settingManager.add(
                mode, vSpeed
        );
    }

    private final TimeUtil timer = new TimeUtil();

    @Override
    public void onDisable() {
        if(mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        mc.timer.timerSpeed = 1.0F;
        mc.gameSettings.keyBindJump.pressed = false;
        super.onDisable();
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if(event.getState() == Event.State.PRE) {
            switch (mode.get()) {
                case "Vanilla":
                    mc.thePlayer.motionY = 0;

                    if (Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                        mc.thePlayer.motionY = vSpeed.get().floatValue();
                    }

                    if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
                        mc.thePlayer.motionY = -vSpeed.get().floatValue();
                    }

                    MoveUtil.setSpeed(vSpeed.get().floatValue());
                    break;
            }
        }
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        switch (mode.get()) {
            case "Jump":
                if (timer.elapsed(545, true)) {
                    mc.thePlayer.jump();
                    mc.thePlayer.onGround = true;
                }
                break;
        }
    }

}
