package wtf.tophat.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.RandomUtils;
import wtf.tophat.Client;
import wtf.tophat.events.base.Event;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.Render2DEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.Methods;
import wtf.tophat.utilities.math.MathUtil;
import wtf.tophat.utilities.network.ServerUtil;
import wtf.tophat.utilities.player.movement.MoveUtil;
import wtf.tophat.utilities.player.rotations.RotationUtil;
import wtf.tophat.utilities.player.scaffold.ScaffoldUtil;
import wtf.tophat.utilities.render.ColorUtil;
import wtf.tophat.utilities.time.Stopwatch;
import wtf.tophat.utilities.vector.Vec3d;

import java.util.Arrays;
import java.util.List;

import static wtf.tophat.utilities.player.scaffold.ScaffoldUtil.getYLevel;
import static wtf.tophat.utilities.render.Colors.DEFAULT_COLOR;
import static wtf.tophat.utilities.render.Colors.WHITE_COLOR;

@ModuleInfo(name = "Scaffold",desc = "place blocks under feet", category = Module.Category.PLAYER)
public class Scaffold extends Module {

    private final StringSetting rotationMode, towerMode;
    private final BooleanSetting tower, slow;

    private static final List<Block> invalidBlocks = Arrays.asList(Blocks.air, Blocks.water, Blocks.tnt, Blocks.chest,
            Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.tnt, Blocks.enchanting_table, Blocks.carpet,
            Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice,
            Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.torch,
            Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore,
            Blocks.iron_ore, Blocks.lapis_ore, Blocks.sand, Blocks.lit_redstone_ore, Blocks.quartz_ore,
            Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate,
            Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button,
            Blocks.wooden_button, Blocks.lever, Blocks.enchanting_table, Blocks.red_flower, Blocks.double_plant,
            Blocks.yellow_flower, Blocks.bed, Blocks.ladder, Blocks.waterlily, Blocks.double_stone_slab, Blocks.stone_slab,
            Blocks.double_wooden_slab, Blocks.wooden_slab, Blocks.heavy_weighted_pressure_plate,
            Blocks.light_weighted_pressure_plate, Blocks.stone_pressure_plate, Blocks.wooden_pressure_plate, Blocks.stone_slab2,
            Blocks.double_stone_slab2, Blocks.tripwire, Blocks.tripwire_hook, Blocks.tallgrass, Blocks.dispenser,
            Blocks.command_block, Blocks.web);

    private final Stopwatch timer = new Stopwatch();
    private BlockData blockInfo;
    private int slot, newSlot, oldSlot;
    private float yaw;
    private float pitch;
    private float y;

    public Scaffold(){
        Client.settingManager.add(
                rotationMode = new StringSetting(this, "Rotations Mode", "Forward", "Forward", "45", "Watchdog"),
                tower = new BooleanSetting(this, "Tower", false),
                slow = new BooleanSetting(this, "Slow", true),
                towerMode = new StringSetting(this, "Tower Mode", "NCP", "NCP", "Vulcan", "Verus")
                        .setHidden(() -> !tower.get())
        );
    }

    public static Vec3d getVec3d(BlockPos pos, EnumFacing face) {
        double rand = RandomUtils.nextDouble(.48D, .49D);
        double x = pos.getX() + rand;
        double y = pos.getY() + rand;
        double z = pos.getZ() + rand;

        x += face.getFrontOffsetX() / 2.0D;
        y += face.getFrontOffsetY() / 2.0D;
        z += face.getFrontOffsetZ() / 2.0D;

        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += rand;
            z += rand;
        } else {
            y += rand;
            if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
                x += rand;
            } else if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
                z += rand;
            }
        }
        return new Vec3d(x, y, z);
    }

    @Listen
    public void onRender2D(Render2DEvent event) {
        int counter = 0;
        ScaledResolution sr = new ScaledResolution(mc);
        mc.fontRenderer.drawStringWithShadow(getBlockCount() == 1 ? getBlockCount() + " \247fBlock" : getBlockCount() + " \247fBlocks", (sr.getScaledWidth() >> 1) - 12 - mc.fontRenderer.getStringWidth(Integer.toString(getBlockCount())) / 2, (sr.getScaledHeight() >> 1) + 12, ColorUtil.fadeBetween(DEFAULT_COLOR, WHITE_COLOR, counter * 150L));
        counter++;
    }

    public static boolean isValid(ItemStack item) {
        if (isEmpty(item) || !(item.getItem() instanceof ItemBlock) || item.getUnlocalizedName().equalsIgnoreCase("tile.chest")) {
            return false;
        }

        return !invalidBlocks.contains(((ItemBlock) item.getItem()).getBlock());
    }

    public static boolean isEmpty(ItemStack stack) {
        return stack == null;
    }

    @Listen
    public void onMotion(MotionEvent motionEvent) {
        mc.player.setSprinting(false);

        if(slow.get()) {
            if (Methods.isMoving() && mc.player.onGround) {
                MoveUtil.setSpeed(0.10f);
            }
        }

        if (motionEvent.getState() == Event.State.PRE) {
            int tempSlot = getBlockSlot();

            if (invCheck()) {
                for (int i = 9; i < 36; ++i) {
                    Item item;
                    if (!mc.player.inventoryContainer.getSlot(i).getHasStack()
                            || !((item = mc.player.inventoryContainer.getSlot(i).getStack()
                            .getItem()) instanceof ItemBlock)
                            || invalidBlocks.contains(((ItemBlock) item).getBlock())
                            || ((ItemBlock) item).getBlock().getLocalizedName().toLowerCase().contains("chest"))
                        continue;
                    swap(i);
                    break;
                }
            }

            blockInfo = null;

            float[] rotations = new float[]{0, 0};

            switch (rotationMode.get()) {
                case "Forward":
                    float rotationYaw = mc.player.rotationYaw;
                    if (mc.player.moveForward < 0.0f && mc.player.moveStrafing == 0.0f) {
                        rotationYaw += 180.0f;
                    }
                    if (mc.player.moveStrafing > 0.0f) {
                        rotationYaw -= 90.0f;
                    }
                    if (mc.player.moveStrafing < 0.0f) {
                        rotationYaw += 90.0f;
                    }

                    this.yaw = (float) (Math.toRadians(rotationYaw) * 57.29577951308232 - 180.0 + Math.random());
                    this.pitch = (float) (76.0 + Math.random());
                    motionEvent.setYaw(yaw);
                    motionEvent.setPitch(pitch);
                    break;
                case "45":
                    float val;
                    if (Methods.isMoving()) {
                        float f = MoveUtil.getMoveYaw(motionEvent.getYaw()) - 180;
                        float[] numbers = new float[]{-135, -90, -45, 0, 45, 90, 135, 180};
                        float lastDiff = 999;
                        val = f;
                        for (float v : numbers) {
                            float diff = Math.abs(v - f);
                            if (diff < lastDiff) {
                                lastDiff = diff;
                                val = v;
                            }
                        }
                    } else {
                        val = rotations[0];
                    }
                    rotations = new float[]{
                            (val + MathHelper.wrapAngleTo180_float(mc.player.prevRotationYawHead)) / 2.0F,
                            (77 + MathHelper.wrapAngleTo180_float(mc.player.prevRotationPitchHead)) / 2.0F};
                    motionEvent.setYaw(rotations[0]);
                    motionEvent.setPitch(rotations[1]);
                    break;
                case "Watchdog":
                    rotations = new float[]{MoveUtil.getMoveYaw(motionEvent.getYaw()) - 180, y};
                    motionEvent.setYaw(rotations[0]);
                    motionEvent.setPitch(rotations[1]);
            }

            if(tower.get()) {
                switch (towerMode.get()) {
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

            slot = -1;
            if (tempSlot != -1) {
                newSlot = getBlockSlot();
                if (!ServerUtil.onHypixel()) {
                    oldSlot = mc.player.inventory.currentItem;
                }
                mc.player.inventory.currentItem = newSlot;
                if (!ServerUtil.onHypixel()) {
                    mc.player.inventory.currentItem = oldSlot;
                }


                if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ)).getBlock() == Blocks.air) {
                    blockInfo = getBlockData(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ));
                    slot = tempSlot;
                }
            }
        } else {
            if (blockInfo != null && timer.timeElapsed(0) && slot != -1) {
                final Vec3d hitVec = getVec3d(blockInfo.position, blockInfo.face);
                final EntitySnowball snowball = new EntitySnowball(mc.world, hitVec.xCoord, hitVec.yCoord, hitVec.zCoord);
                if (!mc.player.canEntityBeSeen(snowball))
                    return;

                mc.player.inventory.currentItem = newSlot;
                if (mc.playerController.onPlayerRightClick3d(mc.player, mc.world,
                        mc.player.inventoryContainer.getSlot(36 + slot).getStack(), blockInfo.position, blockInfo.face,
                        new Vec3d(blockInfo.position.getX() + Math.random(), blockInfo.position.getY() + Math.random(), blockInfo.position.getZ() + Math.random()))) {

                    mc.player.swingItem();
                }

                mc.player.inventory.currentItem = oldSlot;
            }
        }
    }

    private int getBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; ++i) {
            if (!mc.player.inventoryContainer.getSlot(i).getHasStack()) continue;
            ItemStack itemStack = mc.player.inventoryContainer.getSlot(i).getStack();
            Item item = itemStack.getItem();
            if (!(itemStack.getItem() instanceof ItemBlock) || invalidBlocks.contains(((ItemBlock) item).getBlock()))
                continue;
            blockCount += itemStack.stackSize;
        }
        return blockCount;
    }

    private BlockData getBlockData(BlockPos var1) {
        if (!invalidBlocks.contains(mc.world.getBlockState(var1.add(0, -1, 0)).getBlock()))
            return new BlockData(var1.add(0, -1, 0), EnumFacing.UP);
        if (!invalidBlocks.contains(mc.world.getBlockState(var1.add(-1, 0, 0)).getBlock()))
            return new BlockData(var1.add(-1, 0, 0), EnumFacing.EAST);
        if (!invalidBlocks.contains(mc.world.getBlockState(var1.add(1, 0, 0)).getBlock()))
            return new BlockData(var1.add(1, 0, 0), EnumFacing.WEST);
        if (!invalidBlocks.contains(mc.world.getBlockState(var1.add(0, 0, -1)).getBlock()))
            return new BlockData(var1.add(0, 0, -1), EnumFacing.SOUTH);
        if (!invalidBlocks.contains(mc.world.getBlockState(var1.add(0, 0, 1)).getBlock()))
            return new BlockData(var1.add(0, 0, 1), EnumFacing.NORTH);
        BlockPos add = var1.add(-1, 0, 0);
        if (!invalidBlocks.contains(mc.world.getBlockState(add.add(-1, 0, 0)).getBlock()))
            return new BlockData(add.add(-1, 0, 0), EnumFacing.EAST);
        if (!invalidBlocks.contains(mc.world.getBlockState(add.add(1, 0, 0)).getBlock()))
            return new BlockData(add.add(1, 0, 0), EnumFacing.WEST);
        if (!invalidBlocks.contains(mc.world.getBlockState(add.add(0, 0, -1)).getBlock()))
            return new BlockData(add.add(0, 0, -1), EnumFacing.SOUTH);
        if (!invalidBlocks.contains(mc.world.getBlockState(add.add(0, 0, 1)).getBlock()))
            return new BlockData(add.add(0, 0, 1), EnumFacing.NORTH);
        BlockPos add2 = var1.add(1, 0, 0);
        if (!invalidBlocks.contains(mc.world.getBlockState(add2.add(-1, 0, 0)).getBlock()))
            return new BlockData(add2.add(-1, 0, 0), EnumFacing.EAST);
        if (!invalidBlocks.contains(mc.world.getBlockState(add2.add(1, 0, 0)).getBlock()))
            return new BlockData(add2.add(1, 0, 0), EnumFacing.WEST);
        if (!invalidBlocks.contains(mc.world.getBlockState(add2.add(0, 0, -1)).getBlock()))
            return new BlockData(add2.add(0, 0, -1), EnumFacing.SOUTH);
        if (!invalidBlocks.contains(mc.world.getBlockState(add2.add(0, 0, 1)).getBlock()))
            return new BlockData(add2.add(0, 0, 1), EnumFacing.NORTH);
        BlockPos add3 = var1.add(0, 0, -1);
        if (!invalidBlocks.contains(mc.world.getBlockState(add3.add(-1, 0, 0)).getBlock()))
            return new BlockData(add3.add(-1, 0, 0), EnumFacing.EAST);
        if (!invalidBlocks.contains(mc.world.getBlockState(add3.add(1, 0, 0)).getBlock()))
            return new BlockData(add3.add(1, 0, 0), EnumFacing.WEST);
        if (!invalidBlocks.contains(mc.world.getBlockState(add3.add(0, 0, -1)).getBlock()))
            return new BlockData(add3.add(0, 0, -1), EnumFacing.SOUTH);
        if (!invalidBlocks.contains(mc.world.getBlockState(add3.add(0, 0, 1)).getBlock()))
            return new BlockData(add3.add(0, 0, 1), EnumFacing.NORTH);
        BlockPos add4 = var1.add(0, 0, 1);
        if (!invalidBlocks.contains(mc.world.getBlockState(add4.add(-1, 0, 0)).getBlock()))
            return new BlockData(add4.add(-1, 0, 0), EnumFacing.EAST);
        if (!invalidBlocks.contains(mc.world.getBlockState(add4.add(1, 0, 0)).getBlock()))
            return new BlockData(add4.add(1, 0, 0), EnumFacing.WEST);
        if (!invalidBlocks.contains(mc.world.getBlockState(add4.add(0, 0, -1)).getBlock()))
            return new BlockData(add4.add(0, 0, -1), EnumFacing.SOUTH);
        if (!invalidBlocks.contains(mc.world.getBlockState(add4.add(0, 0, 1)).getBlock()))
            return new BlockData(add4.add(0, 0, 1), EnumFacing.NORTH);
        return null;
    }

    private int getBlockSlot() {
        for (int i = 36; i < 45; ++i) {
            ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemBlock)
                if (!contains(((ItemBlock) stack.getItem()).getBlock()))
                    return i - 36;
        }
        return -1;
    }

    private boolean invCheck() {
        for (int i = 36; i < 45; ++i) {
            if (!mc.player.inventoryContainer.getSlot(i).getHasStack()
                    || !isValid(mc.player.inventoryContainer.getSlot(i).getStack()))
                continue;
            return false;
        }
        return true;
    }

    private void swap(int slot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 7, 2, mc.player);
    }

    private boolean contains(Block block) {
        return invalidBlocks.contains(block);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        timer.resetTime();
        y = 80;
    }

    @Override
    public void onDisable() {
        super.onEnable();
        if (ServerUtil.onHypixel()) {
            mc.player.inventory.currentItem = 0;
        }
    }

    public static class BlockData {
        public BlockPos position;
        public EnumFacing face;

        public BlockData(BlockPos position, EnumFacing face) {
            this.position = position;
            this.face = face;
        }
    }
}