package wtf.tophat.module.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.KeyBinding;
import wtf.tophat.Client;
import wtf.tophat.events.base.Event;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.RunTickEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.Methods;
import wtf.tophat.utilities.movement.MoveUtil;

@ModuleInfo(name = "Speed", desc = "move faster", category = Module.Category.MOVE)
public class Speed extends Module {

    private final StringSetting mode;
    private final NumberSetting speed;

    public Speed() {
        Client.settingManager.add(
                mode = new StringSetting(this, "Mode", "Vanilla", "Vanilla", "Intave", "Hypixel"),
                speed = new NumberSetting(this, "Speed", 0, 3, 1, 2)
                        .setHidden(() -> !mode.compare("Vanilla"))
        );
    }

    // Hypixel
    private int hypixelTicks = 0;

    // Intave
    private int onTicks, offTicks;

    @Listen
    public void onTick(RunTickEvent event) {
        if(Methods.mc.player == null || Methods.mc.world == null)
            return;

        onTicks = mc.player.onGround ? ++onTicks : 0;
        offTicks = mc.player.onGround ? 0 : ++offTicks;
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if(event.state == Event.State.PRE) {
            switch (mode.getValue()) {
                case "Hypixel":
                    if(MoveUtil.getSpeed() == 0) {
                        mc.timer.timerSpeed = 1;
                    } else {
                        mc.timer.timerSpeed = (float) (1 + Math.random() / 30);
                        if(mc.player.onGround) {
                            hypixelTicks = 0;
                            mc.player.jump();
                            MoveUtil.strafe(0.418f);
                        } else {
                            hypixelTicks++;
                            mc.player.motionY -= 0.0008;
                            if(hypixelTicks == 1) {
                                mc.player.motionY -= 0.002;
                            }

                            if(hypixelTicks == 8) {
                                mc.player.motionY -= 0.003;
                            }
                        }
                    }
                    break;
                case "Intave":
                    mc.settings.keyBindJump.pressed = true;

                    if (offTicks >= 10 && offTicks % 5 == 0) {
                        MoveUtil.setSpeed(MoveUtil.getSpeed());
                    }

                    break;
                case "Vanilla":
                    MoveUtil.setSpeed(speed.getValue().floatValue());
                    if(isMoving() && mc.player.onGround) {
                        mc.player.jump();
                    } else if(!isMoving()) {
                        mc.player.motionX = 0.0;
                        mc.player.motionZ = 0.0;
                    }
                    break;
            }
        }
    }

    @Override
    public void onDisable() {
        hypixelTicks = 0;
        onTicks = 0;
        offTicks = 0;
        mc.timer.timerSpeed = 1.0f;
        mc.player.speedInAir = 0.02f;
        mc.settings.keyBindJump.pressed = false;
        KeyBinding.setKeyBindState(mc.settings.keyBindSprint.getKeyCode(), false);
        super.onDisable();
    }

}
