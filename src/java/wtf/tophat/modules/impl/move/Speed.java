package wtf.tophat.modules.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import wtf.tophat.TopHat;
import wtf.tophat.events.base.Event;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.PacketEvent;
import wtf.tophat.events.impl.RunTickEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.Methods;
import wtf.tophat.utilities.player.movement.MoveUtil;

import java.util.Random;

@ModuleInfo(name = "Speed", desc = "move faster", category = Module.Category.MOVE)
public class Speed extends Module {

    private final StringSetting mode;
    private final NumberSetting speed, aac3speed;

    public Speed() {
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "Vanilla", "Vanilla", "Intave", "Hypixel", "Verus", "Vulcan", "New NCP", "Matrix", "AAC3", "AAC4", "AAC5"),
                aac3speed = new NumberSetting(this, "AAC3 Speed", 1, 1.5, 1.2, 1)
                        .setHidden(() -> !mode.is("AAC3")),
                speed = new NumberSetting(this, "Speed", 0, 3, 0.29, 2)
                        .setHidden(() -> !mode.is("Vanilla"))
        );
    }

    // Hypixel
    private int hypixelTicks = 0;

    // Intave
    private int onTicks, offTicks;

    // Vulcan
    double moveSpeed = 0;
    int count = 0;

    // AAC3
    int aac3ticks;

    // AAC4
    int aac4ticks;

    @Listen
    public void onTick(RunTickEvent event) {
        if (getPlayer() == null || getWorld() == null)
            return;

        onTicks = getGround() ? ++onTicks : 0;
        offTicks = getGround() ? 0 : ++offTicks;
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if(event.getState() == Event.State.PRE) {
            switch (mode.get()) {
                case "Verus":
                    if (event.getState() == Event.State.PRE) {
                        if (Methods.isMoving()) {
                            if (getGround()) {
                                mc.player.jump();
                                MoveUtil.setSpeed(0.48);
                            } else {
                                MoveUtil.setSpeed(MoveUtil.getSpeed());
                            }
                        } else {
                            MoveUtil.setSpeed(0);
                        }
                    }
                    break;
                case "Vulcan":
                    if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                        if (mc.player.onGround && mc.world.isAirBlock(new BlockPos(mc.player.posX,mc.player.posY + 2,mc.player.posZ)) && mc.world.isAirBlock(new BlockPos(mc.player.posX,mc.player.posY + 1,mc.player.posZ))) {
                            double speed = Math.hypot(mc.player.motionX, mc.player.motionZ);
                            boolean boost = mc.player.isPotionActive(1);
                            switch (count) {
                                case 1:
                                    moveSpeed = 0.42f;
                                    speed = boost ? speed + 0.2 : 0.48;
                                    event.setOnGround(true);
                                    break;
                                case 2:
                                    speed = boost ? speed * 0.71 : 0.19;
                                    moveSpeed -= 0.0784f;
                                    event.setOnGround(false);
                                    break;
                                default:
                                    count = 0;
                                    speed /= boost ? 0.64 : 0.66;
                                    event.setOnGround(true);
                                    break;
                            }
                            MoveUtil.setSpeed(speed);
                            count++;
                            event.setY(event.getY() + moveSpeed);
                        }
                    } else {
                        count = 0;
                    }
                    break;
                case "Hypixel":
                    if (MoveUtil.getSpeed() == 0) {
                        mc.timer.timerSpeed = 1;
                    } else {
                        mc.timer.timerSpeed = (float) (1 + Math.random() / 30);
                        if (getGround()) {
                            hypixelTicks = 0;
                            mc.player.jump();
                            MoveUtil.strafe(0.418f);
                        } else {
                            hypixelTicks++;
                            mc.player.motionY -= 0.0008;
                            if (hypixelTicks == 1) {
                                mc.player.motionY -= 0.002;
                            }

                            if (hypixelTicks == 8) {
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
                    MoveUtil.setSpeed(speed.get().floatValue());
                    if (Methods.isMoving() && getGround()) {
                        mc.player.jump();
                    } else if (!Methods.isMoving()) {
                        mc.player.motionX = 0.0;
                        mc.player.motionZ = 0.0;
                    }
                    break;
                case "New NCP":
                    if (event.getState().equals(Event.State.POST) || !Methods.isMoving() || (mc.player.isInLava() && mc.player.isInWater())) {
                        return;
                    }

                    if(mc.player.hurtTime >1) {
                        MoveUtil.strafe(
                                MoveUtil.getSpeed() * 1.2F
                        );
                    }

                    if (mc.player.onGround) {
                        MoveUtil.strafe(
                                MoveUtil.getBaseMoveSpeed()
                        );
                        mc.player.jump();

                        if (mc.player.isPotionActive(Potion.moveSpeed)) {
                            MoveUtil.strafe(
                                    MoveUtil.getSpeed() * 1.2F
                            );
                        }
                    }

                    mc.timer.timerSpeed = (float) (1.075F - (Math.random() - 0.5) / 100.0F);


                    MoveUtil.strafe(
                            MoveUtil.getSpeed() - (float) (Math.random() - 0.5F) / 100.0F
                    );
                    break;
                case "Matrix":
                    if (!Methods.isMoving()) {
                        mc.settings.keyBindJump.pressed = false;
                        return;
                    }

                    mc.settings.keyBindJump.pressed = true;

                    mc.player.speedInAir = 0.0203F;

                    if(mc.player.motionY > 0.4) {
                        mc.player.motionX *= 1.003F;
                        mc.player.motionZ *= 1.003F;
                    }

                    if(mc.player.onGround) {
                        mc.timer.timerSpeed = (float) (1.1 + Math.random() / 50 - Math.random() / 50);
                        mc.player.motionX *= 1.0045F;
                        mc.player.motionZ *= 1.0045F;
                    } else {
                        mc.timer.timerSpeed = (float) (1 - Math.random() / 500);
                    }
                    break;
                case "AAC3":
                    if (mc.player.onGround) {
                        aac3ticks = 0;
                        mc.player.jump();
                    } else if (++aac3ticks == 1) {
                        mc.player.motionX *= aac3speed.get().doubleValue();
                        mc.player.motionZ *= aac3speed.get().doubleValue();
                    }
                    break;
                case "AAC4":
                    if (mc.player.onGround) {
                        aac4ticks = 0;
                        mc.player.jump();
                    } else if (++aac4ticks == 1) {
                        double multi = mc.player.movementInput.moveStrafe == 0 ? 1.075 : 1.05;
                        mc.player.motionX *= multi;
                        mc.player.motionZ *= multi;
                    }
                    break;
                case "AAC5":
                    if (Methods.isMoving()) {
                        if (mc.player.onGround) {
                            mc.player.jump();
                        }
                    }
                    if (mc.player.movementInput.moveStrafe == 0) {
                        mc.player.speedInAir = (float) (mc.player.ticksExisted % 2 == 0 ? 0.027 : 0.02);
                    } else {
                        mc.player.speedInAir = 0.02f;
                    }
                    break;
            }
        }
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if (getPlayer() == null || getWorld() == null)
            return;

        switch (mode.get()) {
            case "New NCP":
                if (event.getPacket() instanceof C0BPacketEntityAction) {
                    event.setCancelled(true);
                }
                break;
        }
    }

    @Override
    public void onEnable() {
        switch (mode.get()){
            case "Vulcan":
                double moveSpeed = 0;
                int count = 0;
                break;
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if(getPlayer() == null || getWorld() == null) {
            return;
        }

        onTicks = 0;
        offTicks = 0;
        hypixelTicks = 0;
        mc.settings.keyBindJump.pressed = false;
        getPlayer().speedInAir = 0.02F;
        getMCTimer().timerSpeed = 1.0f;
        KeyBinding.setKeyBindState(mc.settings.keyBindSprint.getKeyCode(), false);
        switch (mode.get()){
            case "New NCP":
                mc.player.motionX = 0;
                mc.player.motionZ = 0;
                break;
        }
        super.onDisable();
    }

    public static float getDirection(float rotationYaw) {
        float left = Minecraft.getMinecraft().settings.keyBindLeft.pressed ? Minecraft.getMinecraft().settings.keyBindBack.pressed ? 45 : Minecraft.getMinecraft().settings.keyBindForward.pressed ? -45 : -90 : 0;
        float right = Minecraft.getMinecraft().settings.keyBindRight.pressed ? Minecraft.getMinecraft().settings.keyBindBack.pressed ? -45 : Minecraft.getMinecraft().settings.keyBindForward.pressed ? 45 : 90 : 0;
        float back = Minecraft.getMinecraft().settings.keyBindBack.pressed ? +180 : 0;
        float yaw = left + right + back;
        return rotationYaw + yaw;
    }

    public static float getDirection() {
        Minecraft mc = Minecraft.getMinecraft();
        float var1 = mc.player.rotationYaw;

        if (mc.player.moveForward < 0.0F) {
            var1 += 180.0F;
        }

        float forward = 1.0F;

        if (mc.player.moveForward < 0.0F) {
            final float strafe = (float) MathHelper.getRandomDoubleInRange(new Random(), -0.50, -0.55);
            forward = -strafe;
        } else if (mc.player.moveForward > 0.0F) {
            final float strafe2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.50, 0.55);
            forward = strafe2;
        }

        if (mc.player.moveStrafing > 0.0F) {
            var1 -= 90.0F * forward;
        }

        if (mc.player.moveStrafing < 0.0F) {
            var1 += 90.0F * forward;
        }

        var1 *= 0.017453292F;
        return var1;
    }
}
