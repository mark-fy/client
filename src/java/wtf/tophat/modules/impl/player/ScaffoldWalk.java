package wtf.tophat.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.Render2DEvent;
import wtf.tophat.events.impl.UpdateEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.modules.impl.combat.Killaura;
import wtf.tophat.modules.impl.move.Speed;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.Methods;
import wtf.tophat.utilities.math.MathUtil;
import wtf.tophat.utilities.network.PacketUtil;
import wtf.tophat.utilities.player.KeyboardUtil;
import wtf.tophat.utilities.player.PlayerUtil;
import wtf.tophat.utilities.player.movement.MoveUtil;
import wtf.tophat.utilities.player.rotations.FixedRotations;
import wtf.tophat.utilities.player.rotations.RotationUtil;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.world.BlockInfo;
import wtf.tophat.utilities.world.WorldUtil;

import java.awt.*;

@ModuleInfo(name = "ScaffoldWalk", desc = "better scaffold :3", category = Module.Category.PLAYER)
public class ScaffoldWalk extends Module {

    // Change the settings and some names for ppl dont see the skid (xDDD)

    private final StringSetting rotationsTiming = new StringSetting(this, "Rotations timing", "Always", "Always", "Over air", "Never");
    private final StringSetting rotationsMode = new StringSetting(this, "Rotations mode", "Center", "Normal", "Center", "Movement").setHidden(() -> rotationsTiming.is("Never"));
    private final BooleanSetting rotChangeOverAir = new BooleanSetting(this, "Rot change over air", false).setHidden(() -> !rotationsMode.is("Normal"));
    private final BooleanSetting randomised = new BooleanSetting(this, "Randomised", false).setHidden(() -> !rotationsMode.is("Center"));
    private final NumberSetting yawOffset = new NumberSetting(this, "Yaw offset", 0, 180, 180, 1).setHidden(() -> !rotationsMode.is("Movement"));
    private final NumberSetting pitchValue = new NumberSetting(this, "Pitch",70, 110, 81.5, 1).setHidden(() -> !rotationsMode.is("Movement"));
    private final StringSetting noSprintTiming = new StringSetting(this, "No sprint timing", "Always", "Always", "When rotating", "Never");
    private final StringSetting noSprintMode = new StringSetting(this, "No sprint", "Normal", "Normal", "Spoof").setHidden(() -> noSprintTiming.is("Never"));
    private final StringSetting raytrace = new StringSetting(this, "Raytrace", "Disabled", "Disabled", "Normal", "Legit");
    private final NumberSetting negativeExpand = new NumberSetting(this, "Negative expand", 0, 0.24, 0, 1);
    private final NumberSetting offGroundNegativeExpand = new NumberSetting(this, "Offground negative expand", 0, 0.24, 0, 1);
    private final NumberSetting delayBetweenPlacements = new NumberSetting(this, "Delay between placements", 0, 10, 0, 1);
    private final BooleanSetting moveFix = new BooleanSetting(this, "Move fix", false);
    private final StringSetting offGroundStrafe = new StringSetting(this, "Off ground strafe", "Disabled", "Disabled", "Enabled", "Keep movement").setHidden(() -> moveFix.get());
    private final NumberSetting strafeSpeed = new NumberSetting(this, "Speed", 0.1, 0.5, 0.2, 1).setHidden(() -> moveFix.get());
    private final NumberSetting overAirSpeed = new NumberSetting(this, "Over air speed", 0, 0.5, 0.1, 1).setHidden(() -> moveFix.get());
    private final NumberSetting offGroundSpeed = new NumberSetting(this, "Offground speed", 0.1, 0.5, 0.2, 1).setHidden(() -> offGroundStrafe.is("Disabled") && moveFix.get());
    private final NumberSetting strafeSpeedPotExtra = new NumberSetting(this, "Speed pot extra", 0, 0.2, 0.2, 1).setHidden(() -> moveFix.get());
    private final BooleanSetting randomisedSpeed = new BooleanSetting(this, "Randomised speed", false).setHidden(() -> moveFix.get());
    private final StringSetting jump = new StringSetting(this, "Jump", "Disabled", "Disabled", "Enabled");
    private final NumberSetting range = new NumberSetting(this, "Range", 1, 4, 2, 1);
    private final StringSetting tower = new StringSetting(this, "Tower", "None", "NCP", "NCP", "Vulcan", "Verus", "None");
    private final StringSetting blockPicker = new StringSetting(this, "Block picker", "Switch", "None", "Switch", "Spoof");
    private final BooleanSetting noSwing = new BooleanSetting(this, "No swing", false);

    private BlockInfo info, prevBlockInfo;
    private Vec3 vec3;
    private double lastY;
    private FixedRotations rotations;
    private boolean isRotating;
    private double lastMotionX, lastMotionZ;
    private boolean startedLowhop;
    private int oldSlot;
    private int towerTicks;
    private int placeDelay;
    private boolean hadBlockInfo;

    public ScaffoldWalk() {
        TopHat.settingManager.add(
                rotationsTiming,
                rotationsMode,
                rotChangeOverAir,
                randomised,
                yawOffset,
                pitchValue,
                noSprintTiming,
                noSprintMode,
                raytrace,
                negativeExpand,
                offGroundNegativeExpand,
                delayBetweenPlacements,
                moveFix,
                offGroundStrafe,
                strafeSpeed,
                overAirSpeed,
                offGroundSpeed,
                strafeSpeedPotExtra,
                randomisedSpeed,
                jump,
                range,
                tower,
                blockPicker,
                noSwing
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

        lastMotionX = lastMotionZ = 0;

        startedLowhop = false;

        towerTicks = 0;

        oldSlot = mc.player.inventory.currentItem;

        placeDelay = 0;
    }

    @Override
    public void onDisable() {
        KeyboardUtil.resetKeybindings(mc.settings.keyBindSprint, mc.settings.keyBindSneak);
        switchToOriginalSlot();
    }

    private void switchToOriginalSlot() {
        if(!blockPicker.is("None")) {
            mc.player.inventory.currentItem = oldSlot;
        }

        TopHat.slotSpoofHandler.stopSpoofing();
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
            TopHat.slotSpoofHandler.startSpoofing(oldSlot);
        }
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        if(mc.player.ticksExisted < 10) {
            this.setEnabled(false);
            return;
        }

        pickBlock();

        if(!jump.is("Disabled") || TopHat.moduleManager.getByClass(Speed.class).isEnabled()) {
            if(mc.player.onGround || mc.settings.keyBindJump.isKeyDown()) {
                lastY = mc.player.posY;
            }
        } else {
            lastY = mc.player.posY;
        }

        info = WorldUtil.getBlockUnder(lastY, range.get().intValue());

        float yaw = rotations.getYaw();
        float pitch = rotations.getPitch();

        boolean isAirUnder = WorldUtil.isAirOrLiquid(new BlockPos(mc.player.posX, lastY - 1, mc.player.posZ));

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

        if(noSprintMode.is("Normal")) {
            if(noSprintTiming.is("Always") || ((noSprintTiming.is("When rotating") && isRotating))) {
                mc.settings.keyBindSprint.pressed = false;
                mc.player.setSprinting(false);
            }
        }

        if(jump.is("Enabled")) {
            if(mc.player.onGround && Methods.isMoving() && !mc.settings.keyBindJump.isKeyDown()) {
                mc.player.jump();
            }
        }

        boolean placed = false;

        if(info != null) {
            placed = placeBlock();
        }

        if(!placed && mc.player.ticksExisted % 2 == 0) {
            Killaura killaura = TopHat.moduleManager.getByClass(Killaura.class);

            if(!(killaura.target == null)) {
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

    @Listen
    public void onRender2D(Render2DEvent event) {
        int counter = 0;
        ScaledResolution sr = new ScaledResolution(mc);
        mc.fontRenderer.drawStringWithShadow(getBlockCount() == 1 ? getBlockCount() + " \247fBlock" : getBlockCount() + " \247fBlocks", (sr.getScaledWidth() >> 1) - 12 - mc.fontRenderer.getStringWidth(Integer.toString(getBlockCount())) / 2, (sr.getScaledHeight() >> 1) + 12, ColorUtil.fadeBetween(new Color(1, 236, 183).getRGB(), new Color(246, 4, 234).getRGB(), counter * 150L));
        counter++;
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
    public void onMotion(MotionEvent event) {
        if(mc.settings.keyBindJump.isPressed()){
            if(moveFix.get() && isRotating) {
                event.setYaw(rotations.getYaw());
            }
        }

        if(noSprintMode.is("Spoof")) {
            if(noSprintTiming.is("Always") || (noSprintTiming.is("When rotating") && isRotating)) {
                mc.player.setSprinting(false);
            }
        }

        if(isRotating) {
            event.setYaw(rotations.getYaw());
            event.setPitch(rotations.getPitch());
        }

        switch (tower.get()) {
            case "NCP":
                if (mc.settings.keyBindJump.isKeyDown()) {
                    if (!Methods.isMoving() || MoveUtil.getSpeed() < 0.16) {
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

    private boolean raytrace() {
        MovingObjectPosition movingObjectPosition;

        if(raytrace.is("Legit")) {
            movingObjectPosition = WorldUtil.raytraceLegit(rotations.getYaw(), rotations.getPitch(), rotations.getLastYaw(), rotations.getLastPitch());
        } else {
            movingObjectPosition = WorldUtil.raytrace(rotations.getYaw(), rotations.getPitch());
        }

        if(movingObjectPosition != null && movingObjectPosition.sideHit == info.getFacing() && movingObjectPosition.getBlockPos().equals(info.getPos())) {
            vec3 = movingObjectPosition.hitVec;
            return true;
        }

        return false;
    }

    public boolean placeBlock() {
        ItemStack stack = mc.player.getHeldItem();

        if(info != null && stack != null && stack.getItem() instanceof ItemBlock) {
            if(placeDelay >= delayBetweenPlacements.get().doubleValue()) {
                if(raytrace.is("Disabled") || raytrace()) {
                    if(WorldUtil.negativeExpand(getNegativeExpand())) {
                        return sendPlacing();
                    }
                }
            }
        }

        return false;
    }

    public boolean sendPlacing() {
        ItemStack stack = mc.player.getHeldItem();
        Vec3 vec = !raytrace.is("None") && vec3 != null ? vec3 : WorldUtil.getVec3(info.getPos(), info.getFacing(), true);

        boolean success = mc.playerController.onPlayerRightClick(mc.player, mc.world, stack, info.getPos(), info.getFacing(), vec);

        if(success) {
            if(noSwing.get()) {
                mc.player.sendQueue.send(new C0APacketAnimation());
            } else {
                mc.player.swingItem();
            }

            prevBlockInfo = info;

            placeDelay = 0;
        }

        vec3 = null;

        return success;
    }

    public double getNegativeExpand() {
        return mc.settings.keyBindJump.isKeyDown() || !mc.player.onGround ? offGroundNegativeExpand.get().doubleValue() : negativeExpand.get().doubleValue();
    }

}