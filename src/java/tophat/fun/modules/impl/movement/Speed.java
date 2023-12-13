package tophat.fun.modules.impl.movement;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.KeyBinding;
import tophat.fun.events.Event;
import tophat.fun.events.impl.game.TickEvent;
import tophat.fun.events.impl.game.UpdateEvent;
import tophat.fun.events.impl.player.MotionEvent;
import tophat.fun.modules.base.Module;
import tophat.fun.modules.base.ModuleInfo;
import tophat.fun.modules.base.settings.impl.NumberSetting;
import tophat.fun.modules.base.settings.impl.StringSetting;
import tophat.fun.utilities.player.MoveUtil;

@ModuleInfo(name = "Speed", desc = "increases your movement speed.", category = Module.Category.MOVEMENT)
public class Speed extends Module {

    private final StringSetting mode = new StringSetting(this, "Mode", "Vanilla", "Vanilla", "Legit", "MMC");
    private final NumberSetting vSpeed = new NumberSetting(this, "VanillaSpeed", 1, 1.5, 0.5, 1).setHidden(() -> !mode.is("Vanilla"));

    @Override
    public void onDisable() {
        if(mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        mc.timer.timerSpeed = 1.0F;
        mc.gameSettings.keyBindJump.pressed = false;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
        super.onDisable();
    }

    @Listen
    public void onTick(TickEvent event) {
        if(mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        if(event.getState() == Event.State.PRE) {
            switch (mode.get()) {
                case "MMC":
                    if(isMoving()) {
                        if(mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                            MoveUtil.setSpeed(MoveUtil.getSpeed() * 1.1);
                        }
                        MoveUtil.setSpeed(MoveUtil.getSpeed());
                    }
                    break;
            }
        }
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if(event.getState() == Event.State.PRE) {
            switch (mode.get()) {
                case "Vanilla":
                    MoveUtil.setSpeed(vSpeed.get().floatValue());

                    if (isMoving() && getGround()) {
                        mc.thePlayer.jump();
                    } else if (!isMoving()) {
                        mc.thePlayer.motionX = 0.0;
                        mc.thePlayer.motionZ = 0.0;
                    }
                    break;
            }
        }
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        switch (mode.get()) {
            case "Legit":
                mc.gameSettings.keyBindSprint.pressed = true;

                if (mc.thePlayer.onGround && isMoving()){
                    mc.thePlayer.jump();
                }
                break;
        }
    }

}
