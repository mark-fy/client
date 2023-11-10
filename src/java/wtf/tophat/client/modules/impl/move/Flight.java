package wtf.tophat.client.modules.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.input.Keyboard;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.base.Event;
import wtf.tophat.client.events.impl.world.CollisionBoxesEvent;
import wtf.tophat.client.events.impl.move.MotionEvent;
import wtf.tophat.client.events.impl.network.PacketEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.StringSetting;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.utilities.player.DamageUtil;
import wtf.tophat.client.utilities.player.movement.MoveUtil;

@ModuleInfo(name = "Flight",desc = "fly like a bird", category = Module.Category.MOVE)
public class Flight extends Module {

    private final StringSetting mode;
    private final NumberSetting speed, aac3hopdelay, aac3hopheight;

    public Flight() {
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "Motion", "Motion", "Collision", "Verus", "Vulcan", "BWPractice", "Old NCP", "AAC3", "Redesky", "Karhu"),
                aac3hopdelay = new NumberSetting(this, "Hop Delay", 1, 10, 3, 1)
                        .setHidden(() -> !mode.is("AAC3")),
                aac3hopheight = new NumberSetting(this, "Hop Height", 0, 0.5, 0.4, 1)
                        .setHidden(() -> !mode.is("AAC3")),
                speed = new NumberSetting(this, "Speed", 0, 4, 1, 2)
                        .setHidden(() -> !mode.is("Motion"))
        );
    }

    private boolean hasDamaged;

    // Verus
    private boolean up;

    // Old NCP
    int ticks;
    double OldNCPSpeed = 0.2800F;

    // AAC3
    double startY;

    // Redesky
    public float val0 = 0, val1 = 0;

    @Listen
    public void onMotion(MotionEvent event) {
        switch ((mode.get())){
            case "Verus":
                if(event.getState() == Event.State.PRE) {
                    if (!mc.settings.keyBindJump.isKeyDown()) {
                        if (getGround()) {
                            mc.player.motionY = 0.42f;
                            up = true;
                        } else if (up) {
                            if (!mc.player.isCollidedHorizontally) {
                                mc.player.motionY = -0.0784000015258789;
                            }
                            up = false;
                        }
                    } else if (mc.player.ticksExisted % 3 == 0) {
                        mc.player.motionY = 0.42f;
                    }
                    MoveUtil.setSpeed(mc.settings.keyBindJump.isKeyDown() ? 0 : 0.33);
                }
                break;
            case "Vulcan":
                mc.player.motionY = 0.4641593749554431f;
                mc.player.prevChasingPosY = mc.player.lastReportedPosY;
                mc.player.motionY = mc.settings.keyBindJump.isKeyDown() ? 1F : mc.settings.keyBindSneak.isKeyDown() ? -1F : 0.0;
                mc.player.isAirBorne = true;
                MoveUtil.strafe(0.2753);
                mc.player.motionY = mc.settings.keyBindJump.isKeyDown() ? 1F : mc.settings.keyBindSneak.isKeyDown() ? -1F : 0.0;
                MoveUtil.strafe(1.40);
                break;
            case "Motion":
                mc.player.motionY = 0;

                if (Keyboard.isKeyDown(mc.settings.keyBindJump.getKeyCode())) {
                    mc.player.motionY = speed.get().floatValue();
                }

                if (Keyboard.isKeyDown(mc.settings.keyBindSneak.getKeyCode())) {
                    mc.player.motionY = -speed.get().floatValue();
                }

                MoveUtil.setSpeed(speed.get().floatValue());
                break;
            case "BWPractice":
                mc.player.motionY = 0.0D;
                MoveUtil.setSpeed(0.2f);
                break;
            case "Old NCP":
                if (hasDamaged) {
                    event.setOnGround(true);
                    double baseSpeed = MoveUtil.getBaseMoveSpeed();
                    if (!isMoving() || mc.player.isCollidedHorizontally) {
                        OldNCPSpeed = baseSpeed;
                    }
                    if (OldNCPSpeed > baseSpeed) {
                        OldNCPSpeed -= OldNCPSpeed / 159.0;
                    }

                    OldNCPSpeed = Math.max(baseSpeed, OldNCPSpeed);

                    if (event.getState().equals(Event.State.PRE)) {
                        //mc.timer.timerSpeed = 1;
                        if (isMoving()) {
                            MoveUtil.setSpeed(OldNCPSpeed);
                        }
                        mc.player.motionY = 0;
                        double y = 1.0E-10;
                        event.setY(event.getY() - y);
                    }
                } else if (mc.player.onGround) {
                    DamageUtil.damage(DamageUtil.DamageType.NCP);
                    mc.player.jump();
                    hasDamaged = true;
                }
                break;
            case "AAC3":
                if (event.getState() == Event.State.PRE) {
                    mc.player.motionY = -0.0784000015258789;
                    if (mc.player.ticksExisted % aac3hopdelay.get().intValue() == 0) {
                        mc.player.motionY = aac3hopheight.get().doubleValue();
                    }
                }
                break;
            case "Redesky":
                mc.player.setSprinting(true);

                if(mc.player.onGround) {
                    mc.player.speedInAir = 0.02F;
                    if(!mc.settings.keyBindJump.pressed) {
                        mc.player.jump();
                    }
                    mc.player.motionY = 1;
                    val1 = 0.135f;
                    mc.timer.timerSpeed = 1F;
                } else {
                    if(val0 != 1) {
                        if(mc.player.motionY < 0.9) {
                            mc.player.jumpMovementFactor = 0.2F;
                        }
                    } else {
                        mc.player.jumpMovementFactor = val1;
                    }
                }

                if(isMoving()) {
                    mc.timer.timerSpeed = 1F;
                } else {
                    mc.timer.timerSpeed = 0.25F;
                }
                if(mc.player.ticksExisted % 10 == 0) {
                    event.setOnGround(true);
                }
                break;
            case "Karhu":
                if(isMoving()) {
                    if (mc.player.posY < startY) {
                        mc.timer.timerSpeed = 0.2F;
                        MoveUtil.setSpeed(4);
                        mc.player.motionY = 0.99;
                    } else {
                        mc.timer.timerSpeed = 1.0F;
                    }
                }

                if (mc.player.posY < startY) {
                    event.setOnGround(true);
                }
                break;
        }
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if (getPlayer() == null || getWorld() == null)
            return;

        switch (mode.get()){
            case "BWPractice":
                if (event.getPacket() instanceof C0APacketAnimation) {
                    event.setCancelled(true);
                }

                if (event.getPacket() instanceof C19PacketResourcePackStatus) {
                    event.setCancelled(true);
                }

                if (event.getPacket() instanceof C14PacketTabComplete) {
                    event.setCancelled(true);
                }
                break;
            case "Redesky":
                if(event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();

                    if(packet.getEntityID() == mc.player.getEntityId()) {
                        event.setCancelled(true);
                    }
                } else if(event.getPacket() instanceof S27PacketExplosion) {
                    event.setCancelled(true);;
                } else if(event.getPacket() instanceof S08PacketPlayerPosLook) {
                    val0 = 1;
                }
                break;
        }
    }

    @Listen
    public void onCollision(CollisionBoxesEvent event) {
        if (getPlayer() == null || getWorld() == null)
            return;

        switch (mode.get()) {
            case "Verus":
                event.setBoundingBox(new AxisAlignedBB(-5, -1, -5, 5, 1, 5).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ()));
                break;
            case "Collision":
                if(!mc.settings.keyBindSneak.pressed)
                    event.setBoundingBox(new AxisAlignedBB(-2, -1, -2, 2, 1, 2).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ()));
                break;
        }
    }

    @Override
    public void onEnable(){
        switch (mode.get()) {
            case "Old NCP":
                OldNCPSpeed = 1.4;
                break;
            case "AAC3":
                startY = mc.player.posY;
                break;
            case "Karhu":
                MoveUtil.spoof(3.1, false);
                MoveUtil.spoof(0, false);
                startY = (int) mc.player.posY;
                if(mc.player.onGround) mc.player.jump();
                break;
        }
        hasDamaged = false;
    }

    @Override
    public void onDisable() {
        up = false;
        switch (mode.get()){
            case "Old NCP":
                mc.player.motionX = mc.player.motionZ = 0;
                break;
            case "Vulcan":
                mc.player.motionY = -0.09800000190735147;
                MoveUtil.stop();
                MoveUtil.strafe(0.1);
                break;
            case "Redesky":
                val0 = 0;
                val1 = 0;
                break;
            case "Karhu":
                mc.timer.timerSpeed = 1.0F;
                MoveUtil.setSpeed(0);
                break;
        }
        super.onDisable();
    }
}
