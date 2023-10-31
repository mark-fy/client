package wtf.tophat.modules.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Timer;
import wtf.tophat.Client;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.player.DamageUtil;
import wtf.tophat.utilities.player.movement.MoveUtil;
import wtf.tophat.utilities.time.TimeUtil;

@ModuleInfo(name = "LongJump", desc = "jump better", category = Module.Category.MOVE)
public class LongJump extends Module {

    public final StringSetting mode;
    public final NumberSetting vertical, boost;

    public LongJump() {
        Client.settingManager.add(
                mode = new StringSetting(this, "Mode", "NCP", "NCP", "Old NCP", "BlocksMC", "Verus"),
                vertical = new NumberSetting(this, "Vertical", 0.05, 10.0,0.8, 1),
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

    @Listen
    public void onMotion(MotionEvent event){
        switch (mode.get()){
            case "NCP":
                if (mc.player.onGround) {
                    mc.player.jump();
                    this.setSpeed = false;
                } else {
                    if (!this.setSpeed) {
                        MoveUtil.setSpeed(MoveUtil.getBaseMoveSpeed() + 1.0);
                        this.setSpeed = true;
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
                if (this.mc.player.onGround) {
                    if (!this.hasJumped) {
                        this.mc.player.jump();
                        this.hasJumped = true;
                        this.timer.reset();
                    }
                } else {
                    float dir = this.mc.player.rotationYaw + (float)(this.mc.player.moveForward < 0.0f ? 180 : 0) + (this.mc.player.moveStrafing > 0.0f ? -90.0f * (this.mc.player.moveForward < 0.0f ? -0.5f : (this.mc.player.moveForward > 0.0f ? 0.4f : 1.0f)) : 0.0f);
                    float xDir = (float)Math.cos((double)(dir + 90.0f) * Math.PI / 180.0);
                    float zDir = (float)Math.sin((double)(dir + 90.0f) * Math.PI / 180.0);
                    if (this.mc.player.motionY == 0.33319999363422365 && (this.mc.settings.keyBindForward.isKeyDown() || this.mc.settings.keyBindLeft.isKeyDown() || this.mc.settings.keyBindRight.isKeyDown() || this.mc.settings.keyBindBack.isKeyDown())) {
                        this.mc.player.motionX = (double)xDir * 1.6561;
                        if (this.stage != 2) {
                            this.mc.player.motionY += (double)0.05f;
                        }
                        this.mc.player.motionZ = (double)zDir * 1.6561;
                    }
                    if (this.mc.player.motionY < 0.0) {
                        this.stage = 2;
                    }
                }
                if (!this.timer.elapsed(700L, false) || !this.mc.player.onGround) break;
                this.toggle();
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
        }
        launched = false;
        wasLaunched = false;
        jumped = false;
        timer.reset();

        switch (mode.get()) {
            case "Verus":
                if (!mc.player.onGround) {
                    toggle();
                    return;
                }
                timer.setTimer(0.3f);
                DamageUtil.damage(DamageUtil.DamageType.VERUS);
                break;
        }
        hasJumped = false;
        stage = 0;
        super.onEnable();
    }

    @Override
    public void onDisable(){
        BMChurt = false;
        timer.setTimer(1.0f);
        super.onDisable();
    }
}
