package tophat.fun.modules.impl.movement;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.input.Keyboard;
import tophat.fun.events.Event;
import tophat.fun.events.impl.game.CollisionBoxesEvent;
import tophat.fun.events.impl.player.MotionEvent;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.modules.settings.impl.NumberSetting;
import tophat.fun.modules.settings.impl.StringSetting;
import tophat.fun.utilities.others.TimeUtil;
import tophat.fun.utilities.player.MoveUtil;

@ModuleInfo(name = "Flight", desc = "makes you float midair.", category = Module.Category.MOVEMENT)
public class Flight extends Module {

    private final StringSetting mode = new StringSetting(this, "Mode", "Vanilla", "Vanilla", "Jump", "Ground", "Collision");
    private final NumberSetting vSpeed = new NumberSetting(this, "VanillaSpeed", 0, 2, 0.5, 1).setHidden(() -> !mode.is("Vanilla"));

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
    public void onCollision(CollisionBoxesEvent event) {
        if(mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        switch (mode.get()) {
            case "Collision":
                if(!mc.gameSettings.keyBindSneak.pressed) {
                    event.setBoundingBox(new AxisAlignedBB(-2, -1, -2, 2, 1, 2).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ()));
                }
                break;
        }
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if(event.getState() == Event.State.PRE) {
            switch (mode.get()) {
                case "Jump":
                    if (timer.elapsed(545, true)) {
                        mc.thePlayer.jump();
                        mc.thePlayer.onGround = true;
                    }
                    break;
                case "Ground":
                    mc.thePlayer.motionY = 0.0;
                    mc.thePlayer.onGround = true;
                    break;
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

}
