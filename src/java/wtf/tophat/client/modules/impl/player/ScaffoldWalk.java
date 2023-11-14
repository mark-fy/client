package wtf.tophat.client.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.base.Event;
import wtf.tophat.client.events.impl.move.MotionEvent;
import wtf.tophat.client.events.impl.move.SafeWalkEvent;
import wtf.tophat.client.events.impl.render.Render2DEvent;
import wtf.tophat.client.events.impl.combat.RotationEvent;
import wtf.tophat.client.events.impl.world.UpdateEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.modules.impl.combat.KillAura;
import wtf.tophat.client.modules.impl.move.CorrectMovement;
import wtf.tophat.client.modules.impl.move.Speed;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.settings.impl.StringSetting;
import wtf.tophat.client.utilities.Methods;
import wtf.tophat.client.utilities.math.MathUtil;
import wtf.tophat.client.utilities.network.PacketUtil;
import wtf.tophat.client.utilities.player.KeyboardUtil;
import wtf.tophat.client.utilities.player.PlayerUtil;
import wtf.tophat.client.utilities.player.movement.MoveUtil;
import wtf.tophat.client.utilities.player.rotations.FixedRotations;
import wtf.tophat.client.utilities.player.rotations.RotationUtil;
import wtf.tophat.client.utilities.world.BlockInfo;
import wtf.tophat.client.utilities.world.WorldUtil;

import java.awt.*;
import java.lang.reflect.Field;

@ModuleInfo(name = "Scaffold Walk", desc = "better scaffold :3", category = Module.Category.PLAYER)
public class ScaffoldWalk extends Module {

    private final StringSetting rotationsTiming, rotationsMode, jump, tower, blockPicker;
    private final BooleanSetting rotChangeOverAir, noSprintMode, safeWalk, noSwing, randomised;
    private final NumberSetting yawOffset, pitchValue, timer, speed, negativeExpand, offGroundNegativeExpand, delayBetweenPlacements;

    private BlockInfo info;
    private Vec3 vec3;
    private double lastY;
    private FixedRotations rotations;
    private boolean isRotating;
    private int oldSlot;
    private int placeDelay;
    private boolean hadBlockInfo;

    public ScaffoldWalk() {
        TopHat.settingManager.add(
                rotationsTiming = new StringSetting(this, "Rotations timing", "Always", "Always", "Over air", "Never"),
                rotationsMode = new StringSetting(this, "Rotations mode", "Center", "Normal", "Center", "Movement").setHidden(() -> rotationsTiming.is("Never")),
                rotChangeOverAir = new BooleanSetting(this, "Rot change over air", false).setHidden(() -> !rotationsMode.is("Normal")),
                randomised = new BooleanSetting(this, "Randomised", false).setHidden(() -> !rotationsMode.is("Center")),
                yawOffset = new NumberSetting(this, "Yaw offset", 0, 180, 180, 1).setHidden(() -> !rotationsMode.is("Movement")),
                pitchValue = new NumberSetting(this, "Pitch",70, 110, 81.5, 1).setHidden(() -> !rotationsMode.is("Movement")),
                timer = new NumberSetting(this, "Timer",0.1, 10, 1, 1),
                speed = new NumberSetting(this, "Speed",0.01, 0.8, 0.1, 2),
                noSprintMode = new BooleanSetting(this, "No sprint", true),
                negativeExpand = new NumberSetting(this, "Negative expand", 0, 0.24, 0, 1),
                offGroundNegativeExpand = new NumberSetting(this, "OffGround negative expand", 0, 0.24, 0, 1),
                delayBetweenPlacements = new NumberSetting(this, "Delay", 0, 10, 0, 1),
                jump = new StringSetting(this, "Jump", "Disabled", "Disabled", "Enabled"),
                safeWalk = new BooleanSetting(this, "Safe walk", false),
                tower = new StringSetting(this, "Tower", "None", "None", "Vulcan", "Verus", "NCP"),
                blockPicker = new StringSetting(this, "Block picker", "Switch", "None", "Switch", "Spoof"),
                noSwing = new BooleanSetting(this, "No swing", false)
        );
    }

    @Override
    public void onEnable() {
        rotations = new FixedRotations(mc.player.rotationYaw, mc.player.rotationPitch);

        switch (rotationsMode.get()) {
            case "Normal":
                rotations.updateRotations(MathHelper.wrapAngleTo180_float(MoveUtil.getPlayerDirection() - 180), 81.5F);
                break;
            case "Movement":
                rotations.updateRotations(MoveUtil.getPlayerDirection() - yawOffset.get().floatValue(), pitchValue.get().floatValue());
                break;
        }

        info = null;
        vec3 = null;

        oldSlot = mc.player.inventory.currentItem;

        placeDelay = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if(getPlayer() == null || getWorld() == null) {
            return;
        }

        KeyboardUtil.resetKeybindings(mc.settings.keyBindSprint, mc.settings.keyBindSneak);
        switchToOriginalSlot();
        super.onDisable();
    }

    private void switchToOriginalSlot() {
        if(!blockPicker.is("None")) {
            mc.player.inventory.currentItem = oldSlot;
        }

        TopHat.bestnameuwu.stopSpoofing();
    }

    private void pickBlock() {
        if(!blockPicker.is("None")) {
            for(int i = 8; i >= 0; i--) {
                ItemStack stack = mc.player.inventory.getStackInSlot(i);

                if(stack != null && stack.getItem() instanceof ItemBlock && !PlayerUtil.isBlockBlacklisted(stack.getItem()) && stack.stackSize > 0) {
                    mc.player.inventory.currentItem = i;
                    break;
                }
            }
        }

        if(blockPicker.is("Spoof")) {
            TopHat.bestnameuwu.startSpoofing(oldSlot);
        }
    }

    @Listen
    public void onSafe(SafeWalkEvent event) {
        if (getPlayer() == null || getWorld() == null)
            return;

        if(safeWalk.get()) {
            event.setSafe(mc.player.onGround);
        }
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        if(mc.player.ticksExisted < 10) {
            this.setEnabled(false);
            return;
        }

        pickBlock();

        MoveUtil.setSpeed(speed.get().doubleValue());
        mc.timer.timerSpeed = timer.get().floatValue();

        if(!jump.is("Disabled") || TopHat.moduleManager.getByClass(Speed.class).isEnabled()) {
            if(mc.player.onGround || mc.settings.keyBindJump.isKeyDown()) {
                lastY = mc.player.posY;
            }
        } else {
            lastY = mc.player.posY;
        }

        info = WorldUtil.getBlockUnder(lastY, 1);

        float yaw = rotations.getYaw();
        float pitch = rotations.getPitch();

        switch (rotationsMode.get()) {
            case "Normal":
                float normalRots[] = getNormalRotations(rotChangeOverAir.get());

                yaw = normalRots[0];
                pitch = normalRots[1];
                break;
            case "Center":
                if(info != null && WorldUtil.negativeExpand(getNegativeExpand())) {
                    Vec3 vec = WorldUtil.getVec3(info.getPos(), info.getFacing(), randomised.get());

                    float[] rots = RotationUtil.getRotationsToPosition(vec.xCoord, vec.yCoord, vec.zCoord);

                    yaw = rots[0];
                    pitch = rots[1];
                }
                break;
            case "Movement":
                yaw = MoveUtil.getPlayerDirection() - yawOffset.get().floatValue();

                if(info != null) {
                    if(info.getFacing() == EnumFacing.UP) {
                        pitch = 90F;
                    } else {
                        pitch = (float) pitchValue.get().doubleValue();
                    }
                }
                break;
        }

        rotations.updateRotations(yaw, pitch);

        isRotating = rotationsTiming.is("Always") || (rotationsTiming.is("Over air") && info != null && WorldUtil.negativeExpand(getNegativeExpand()));

        if(noSprintMode.get()) {
            mc.settings.keyBindSprint.pressed = false;
            mc.player.setSprinting(false);
        }

        if(jump.is("Enabled")) {
            if(mc.player.onGround && isMoving() && !mc.settings.keyBindJump.isKeyDown()) {
                mc.player.jump();
            }
        }

        boolean placed = false;

        if(info != null) {
            placed = placeBlock();
        }

        if(!placed && mc.player.ticksExisted % 2 == 0) {

            if(!(KillAura.target == null)) {
                PacketUtil.sendBlocking(true, false);
            }
        }

        KeyboardUtil.resetKeybinding(mc.settings.keyBindSneak);

        hadBlockInfo = info != null;

        placeDelay++;
    }

    private float[] getNormalRotations(boolean firstTickOverAir) {
        float yaw = MathHelper.wrapAngleTo180_float(rotations.getYaw());
        float pitch = rotations.getPitch();

        boolean condition = firstTickOverAir ? info != null && !hadBlockInfo : info != null && WorldUtil.negativeExpand(getNegativeExpand());

        if(condition) {
            BlockPos pos = info.getPos();
            EnumFacing facing = info.getFacing();

            BlockPos playerPos = mc.player.getPosition();

            switch (facing) {
                case EAST:
                    if(yaw > 136 || yaw < 44) {
                        if(playerPos.getZ() > pos.getZ()) {
                            yaw = 135;
                        } else {
                            yaw = 45;
                        }
                    }
                    break;
                case WEST:
                    if(yaw < -136 || yaw > -44) {
                        if(playerPos.getZ() > pos.getZ()) {
                            yaw = -135;
                        } else {
                            yaw = -45;
                        }
                    }
                    break;
                case NORTH:
                    if(yaw < -46 || yaw > 46) {
                        if(playerPos.getX() > pos.getX()) {
                            yaw = 45;
                        } else {
                            yaw = -45;
                        }
                    }
                    break;
                case SOUTH:
                    if(yaw < 134 && yaw > -134) {
                        if(playerPos.getX() > pos.getX()) {
                            yaw = 135;
                        } else {
                            yaw = -135;
                        }
                    }
                    break;
            }

            if(facing == EnumFacing.UP) {
                pitch = 90;
            } else {
                boolean found = false;

                for(float i = 75; i <= 85; i += 0.5F) {
                    MovingObjectPosition movingObjectPosition = WorldUtil.raytrace(yaw, i);

                    if(movingObjectPosition != null && movingObjectPosition.sideHit == info.getFacing()) {
                        pitch = i;
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    pitch = 80F;
                }
            }
        }

        return new float[] {yaw, pitch};
    }

    public float getYawBackward() {
        float yaw = MathHelper.wrapAngleTo180_float(mc.player.rotationYaw);

        MovementInput input = mc.player.movementInput;
        float strafe = input.getMoveStrafe(), forward = input.getMoveForward();

        if (forward != 0) {
            if (strafe < 0) {
                yaw += forward < 0 ? 135 : 45;
            } else if (strafe > 0) {
                yaw -= forward < 0 ? 135 : 45;
            } else if (strafe == 0 && forward < 0) {
                yaw -= 180;
            }

        } else {
            if (strafe < 0) {
                yaw += 90;
            } else if (strafe > 0) {
                yaw -= 90;
            }
        }

        return MathHelper.wrapAngleTo180_float(yaw - 180);
    }

    @Listen
    public void onRender2D(Render2DEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);
        mc.fontRenderer.drawStringWithShadow(getBlockCount() == 1 ? getBlockCount() + " \247fBlock" : getBlockCount() + " \247fBlocks", (sr.getScaledWidth() >> 1) - 12 - mc.fontRenderer.getStringWidth(Integer.toString(getBlockCount())) / 2, (sr.getScaledHeight() >> 1) + 12, new Color(255, 255, 255).getRGB());
    }

    private int getBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; ++i) {
            if (!mc.player.inventoryContainer.getSlot(i).getHasStack()) continue;
            ItemStack itemStack = mc.player.inventoryContainer.getSlot(i).getStack();
            Item item = itemStack.getItem();
            if (!(itemStack.getItem() instanceof ItemBlock))
                continue;
            blockCount += itemStack.stackSize;
        }
        return blockCount;
    }

    @Listen
    public void onRots(RotationEvent event) {

        if(isRotating) {
            event.setYaw(rotations.getYaw());
            event.setPitch(rotations.getPitch());
        }

        switch (tower.get()) {
            case "NCP":
                if (mc.settings.keyBindJump.isKeyDown()) {
                    if (!isMoving() || MoveUtil.getSpeed() < 0.16) {
                        if (mc.player.onGround) {
                            mc.player.motionY = 0.42;
                        } else if (mc.player.motionY < 0.23) {
                            mc.player.setPosition(mc.player.posX, (int) mc.player.posY, mc.player.posZ);
                            mc.player.motionY = 0.42;
                        }
                    }
                }
                break;
            case "Vulcan":
                if (mc.settings.keyBindJump.isKeyDown() && mc.player.offGroundTicks > 3) {
                    mc.player.onGround = true;
                    mc.player.motionY = MathUtil.randomNumber(0.47F, 0.50F);
                }
                break;
            case "Verus":
                if (mc.settings.keyBindJump.isKeyDown() && mc.player.ticksExisted % 2 == 0) {
                    mc.player.motionY = 0.42f;
                }
                break;
        }
    }

    public boolean placeBlock() {
        ItemStack stack = mc.player.getHeldItem();

        if(info != null && stack != null && stack.getItem() instanceof ItemBlock) {
            if(placeDelay >= delayBetweenPlacements.get().doubleValue()) {
                if(WorldUtil.negativeExpand(getNegativeExpand())) {
                    return sendPlacing();
                }
            }
        }

        return false;
    }

    public boolean sendPlacing() {
        ItemStack stack = mc.player.getHeldItem();
        Vec3 vec = vec3 != null ? vec3 : WorldUtil.getVec3(info.getPos(), info.getFacing(), true);

        boolean success = mc.playerController.onPlayerRightClick(mc.player, mc.world, stack, info.getPos(), info.getFacing(), vec);

        if(success) {
            if(noSwing.get()) {
                mc.player.sendQueue.send(new C0APacketAnimation());
            } else {
                mc.player.swingItem();
            }

            placeDelay = 0;
        }

        vec3 = null;

        return success;
    }

    public double getNegativeExpand() {
        return mc.settings.keyBindJump.isKeyDown() || !mc.player.onGround ? offGroundNegativeExpand.get().doubleValue() : negativeExpand.get().doubleValue();
    }

}