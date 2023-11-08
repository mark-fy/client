package wtf.tophat.client.modules.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.MotionEvent;
import wtf.tophat.client.events.impl.RotationEvent;
import wtf.tophat.client.events.impl.UpdateEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.settings.impl.StringSetting;
import wtf.tophat.client.utilities.player.DamageUtil;
import wtf.tophat.client.utilities.player.movement.MoveUtil;
import wtf.tophat.client.utilities.math.time.TimeUtil;
import wtf.tophat.client.utilities.Methods;

@ModuleInfo(name = "Long Jump", desc = "perform longer jumps", category = Module.Category.MOVE)
public class LongJump extends Module {

    private final StringSetting mode;
    private final NumberSetting vertical, boost;

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
                if(Methods.mc.player.isRiding() && Methods.mc.player.ridingEntity != null) {
                    if(Methods.mc.player.ridingEntity instanceof EntityBoat) {
                        EntityBoat boat = (EntityBoat) Methods.mc.player.ridingEntity;
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
                if(Methods.mc.player.isRiding() && Methods.mc.player.ridingEntity instanceof EntityBoat) {
                    launch = true;
                }
                if(launch && !Methods.mc.player.isRiding()) {
                    Methods.mc.player.motionY = 1.5;
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
                if (Methods.mc.player.onGround) {
                    Methods.mc.player.jump();
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
                if(!BMChurt && Methods.mc.player.hurtTime > 0) {
                    BMChurt = true;
                    Methods.mc.player.motionY = 1.0;
                }
                if(BMChurt) {
                    Methods.mc.timer.timerSpeed = 1;
                    MoveUtil.setSpeed(1.89);
                    if(Methods.mc.player.onGround) {
                        toggle();
                    }
                }
                break;
            case "Old NCP":
                if (Methods.mc.player.onGround) {
                    if (!hasJumped) {
                        Methods.mc.player.jump();
                        hasJumped = true;
                        timer.reset();
                    }
                } else {
                    float dir = Methods.mc.player.rotationYaw + (float)(Methods.mc.player.moveForward < 0.0f ? 180 : 0) + (Methods.mc.player.moveStrafing > 0.0f ? -90.0f * (Methods.mc.player.moveForward < 0.0f ? -0.5f : (Methods.mc.player.moveForward > 0.0f ? 0.4f : 1.0f)) : 0.0f);
                    float xDir = (float)Math.cos((double)(dir + 90.0f) * Math.PI / 180.0);
                    float zDir = (float)Math.sin((double)(dir + 90.0f) * Math.PI / 180.0);
                    if (Methods.mc.player.motionY == 0.33319999363422365 && (Methods.mc.settings.keyBindForward.isKeyDown() || Methods.mc.settings.keyBindLeft.isKeyDown() || Methods.mc.settings.keyBindRight.isKeyDown() || Methods.mc.settings.keyBindBack.isKeyDown())) {
                        Methods.mc.player.motionX = (double)xDir * 1.6561;
                        if (stage != 2) {
                            Methods.mc.player.motionY += 0.05f;
                        }
                        Methods.mc.player.motionZ = (double)zDir * 1.6561;
                    }
                    if (Methods.mc.player.motionY < 0.0) {
                        stage = 2;
                    }
                }
                if (!timer.elapsed(700L, false) || !Methods.mc.player.onGround) break;
                toggle();
                break;
            case "Verus":
                if (Methods.mc.player.hurtTime > 1 && !launched) {
                    launched = true;
                }
                if (launched) {
                    if (!jumped) {
                        Methods.mc.player.motionY = vertical.get().doubleValue();
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
                Methods.mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(Methods.mc.player.posX, Methods.mc.player.posY + 3.1005, Methods.mc.player.posZ, false));
                Methods.mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(Methods.mc.player.posX, Methods.mc.player.posY, Methods.mc.player.posZ, false));
                Methods.mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(Methods.mc.player.posX, Methods.mc.player.posY, Methods.mc.player.posZ, true));
                break;
            case "Verus":
                if (!Methods.mc.player.onGround) {
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