package wtf.tophat.modules.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.RotationEvent;
import wtf.tophat.events.impl.UpdateEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.player.DamageUtil;
import wtf.tophat.utilities.player.movement.MoveUtil;
import wtf.tophat.utilities.time.TimeUtil;

@ModuleInfo(name = "Long Jump", desc = "perform longer jumps", category = Module.Category.MOVE)
public class LongJump extends Module {

    public final StringSetting mode;
    public final NumberSetting vertical, boost;

    public LongJump() {
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "NCP", "NCP", "Old NCP", "BlocksMC", "Verus", "Grim Boat"),
                vertical = new NumberSetting(this, "Vertical", 0.05, 10.0,0.8, 1)
                        .setHidden(() -> !mode.is("Verus")),
                boost = new NumberSetting(this, "Boost", 0.05, 10.0,1.45, 1)
                        .setHidden(() -> !mode.is("Verus"))
        );
    }

    public TimeUtil timer = new TimeUtil();

    // Verus
    public boolean launched = false;
    public boolean wasLaunched = false;
    public boolean jumped = false;

    //BlocksMC
    public boolean BMChurt = false;

    //NCP
    public boolean setSpeed;

    //Old NCP
    public boolean hasJumped;
    public int stage;

    // Grim
    private boolean launch;

    @Listen
    public void onRotation(RotationEvent event) {
        switch (this.mode.get()) {
            case "Grim Boat":
                if(mc.player.isRiding() && mc.player.ridingEntity != null) {
                    if(mc.player.ridingEntity instanceof EntityBoat) {
                        EntityBoat boat = (EntityBoat) mc.player.ridingEntity;
                        float yaw = boat.rotationYaw;
                        float pitch = 90;
                        event.setYaw(yaw);
                        event.setPitch(pitch);
                    }
                }
                break;
        }
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        switch (mode.get()) {
            case "Grim Boat":
                if(mc.player.isRiding() && mc.player.ridingEntity instanceof EntityBoat) {
                    launch = true;
                }
                if(launch && !mc.player.isRiding()) {
                    mc.player.motionY = 1.5;
                    MoveUtil.strafe(1.5);
                    launch = false;
                }
                break;
        }
    }

    @Listen
    public void onMotion(MotionEvent event){
        switch (mode.get()){
            case "NCP":
                if (mc.player.onGround) {
                    mc.player.jump();
                    setSpeed = false;
                } else {
                    if (!setSpeed) {
                        MoveUtil.setSpeed(MoveUtil.getBaseMoveSpeed() + 1.0);
                        setSpeed = true;
                    }

                    MoveUtil.addFriction(0.97);
                }
                break;
            case "BlocksMC":
                if(!BMChurt && mc.player.hurtTime > 0) {
                    BMChurt = true;
                    mc.player.motionY = 1.0;
                }
                if(BMChurt) {
                    mc.timer.timerSpeed = 1;
                    MoveUtil.setSpeed(1.89);
                    if(mc.player.onGround) {
                        toggle();
                    }
                }
                break;
            case "Old NCP":
                if (mc.player.onGround) {
                    if (!hasJumped) {
                        mc.player.jump();
                        hasJumped = true;
                        timer.reset();
                    }
                } else {
                    float dir = mc.player.rotationYaw + (float)(mc.player.moveForward < 0.0f ? 180 : 0) + (mc.player.moveStrafing > 0.0f ? -90.0f * (mc.player.moveForward < 0.0f ? -0.5f : (mc.player.moveForward > 0.0f ? 0.4f : 1.0f)) : 0.0f);
                    float xDir = (float)Math.cos((double)(dir + 90.0f) * Math.PI / 180.0);
                    float zDir = (float)Math.sin((double)(dir + 90.0f) * Math.PI / 180.0);
                    if (mc.player.motionY == 0.33319999363422365 && (mc.settings.keyBindForward.isKeyDown() || mc.settings.keyBindLeft.isKeyDown() || mc.settings.keyBindRight.isKeyDown() || mc.settings.keyBindBack.isKeyDown())) {
                        mc.player.motionX = (double)xDir * 1.6561;
                        if (stage != 2) {
                            mc.player.motionY += 0.05f;
                        }
                        mc.player.motionZ = (double)zDir * 1.6561;
                    }
                    if (mc.player.motionY < 0.0) {
                        stage = 2;
                    }
                }
                if (!timer.elapsed(700L, false) || !mc.player.onGround) break;
                toggle();
                break;
            case "Verus":
                if (mc.player.hurtTime > 1 && !launched) {
                    launched = true;
                }
                if (launched) {
                    if (!jumped) {
                        mc.player.motionY = vertical.get().doubleValue();
                        jumped = true;
                    }
                    MoveUtil.setSpeed(boost.get().floatValue());
                    launched = false;
                    wasLaunched = true;
                    toggle();
                }
                break;
        }
    }

    @Override
    public void onEnable(){
        switch (mode.get()) {
            case "BlocksMC":
                mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX, mc.player.posY + 3.1005, mc.player.posZ, false));
                mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX, mc.player.posY, mc.player.posZ, true));
                break;
            case "Verus":
                if (!mc.player.onGround) {
                    toggle();
                    return;
                }
                TimeUtil.setTimer(0.3f);
                DamageUtil.damage(DamageUtil.DamageType.VERUS);
                break;
        }
        wasLaunched = false;
        hasJumped = false;
        launched = false;
        launch = false;
        jumped = false;
        stage = 0;
        timer.reset();
        super.onEnable();
    }

    @Override
    public void onDisable(){
        BMChurt = false;
        launch = false;
        timer.setTimer(1.0f);
        super.onDisable();
    }
}