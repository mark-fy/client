package wtf.tophat.modules.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.input.Keyboard;
import wtf.tophat.Client;
import wtf.tophat.events.base.Event;
import wtf.tophat.events.impl.CollisionBoxesEvent;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.PacketEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.utilities.Methods;
import wtf.tophat.utilities.player.DamageUtil;
import wtf.tophat.utilities.player.movement.MoveUtil;

@ModuleInfo(name = "Flight",desc = "fly like a bird", category = Module.Category.MOVE)
public class Flight extends Module {

    private final StringSetting mode;
    private final NumberSetting speed;

    public Flight() {
        Client.settingManager.add(
                mode = new StringSetting(this, "Mode", "Motion", "Motion", "Collision", "Verus", "BWPractice", "Old NCP"),
                speed = new NumberSetting(this, "Speed", 0, 4, 1, 2)
                        .setHidden(() -> !mode.is("Vanilla"))
        );
    }

    private boolean hasDamaged;

    // Verus
    private boolean up;

    // Old NCP
    int ticks;
    double OldNCPSpeed = 0.2873F;

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
                    if (!Methods.isMoving() || mc.player.isCollidedHorizontally) {
                        OldNCPSpeed = baseSpeed;
                    }
                    if (OldNCPSpeed > baseSpeed) {
                        OldNCPSpeed -= OldNCPSpeed / 159.0;
                    }

                    OldNCPSpeed = Math.max(baseSpeed, OldNCPSpeed);

                    if (event.getState().equals(Event.State.PRE)) {
                        mc.timer.timerSpeed = 1;
                        if (Methods.isMoving()) {
                            MoveUtil.setSpeed(OldNCPSpeed);
                        }
                        mc.player.motionY = 0;
                        double y = 1.0E-10;
                        event.setY(event.getY() - y);
                    }
                } else if (mc.player.onGround) {
                    DamageUtil.damage(DamageUtil.DamageType.OLDNCP);
                    mc.player.jump();
                    hasDamaged = true;
                }
        }
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if (getPlayer() == null || getWorld() == null)
            return;

        if (mode.get().equals("BWPractice")) {
            if (event.getPacket() instanceof C0APacketAnimation) {
                event.setCancelled(true);
            }

            if (event.getPacket() instanceof C19PacketResourcePackStatus) {
                event.setCancelled(true);
            }

            if (event.getPacket() instanceof C14PacketTabComplete) {
                event.setCancelled(true);
            }
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
                OldNCPSpeed = 1.6;
        }
        hasDamaged = false;
    }

    @Override
    public void onDisable() {
        up = false;
        super.onDisable();
        switch (mode.get()){
            case "Old NCP":
                mc.player.motionX = mc.player.motionZ = 0;
        }
    }
}
