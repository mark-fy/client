package wtf.tophat.client.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Mouse;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.combat.RotationEvent;
import wtf.tophat.client.events.impl.world.UpdateEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.utilities.player.rotations.FixedRotations;
import wtf.tophat.client.utilities.player.rotations.RotationUtil;

@ModuleInfo(name = "Breaker", desc = "break bed through blocks", category = Module.Category.PLAYER)
public class Breaker extends Module {

    private BlockPos bedPos;

    private FixedRotations rotations;

    private final NumberSetting range;
    private final BooleanSetting rotate, moveFix, hypixel;

    public Breaker() {
        TopHat.settingManager.add(
                range = new NumberSetting(this,"Range", 1, 6, 4, 1),
                rotate = new BooleanSetting(this, "Rotate", true),
                moveFix = new BooleanSetting(this, "Move fix", false).setHidden(() -> !rotate.get()),
                hypixel= new BooleanSetting(this, "Hypixel (not working)", false)
        );
    }

    @Override
    public void onEnable() {
        bedPos = null;

        rotations = new FixedRotations(mc.player.rotationYaw, mc.player.rotationPitch);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.settings.keyBindAttack.pressed = Mouse.isButtonDown(0);
        super.onDisable();
    }

    @Listen
    public void onUpdate(UpdateEvent event){
        bedPos = null;

        boolean found = false;

        float yaw = rotations.getYaw();
        float pitch = rotations.getPitch();

        for(double x = mc.player.posX - range.get().floatValue(); x <= mc.player.posX + range.get().floatValue(); x++) {
            for(double y = mc.player.posY + mc.player.getEyeHeight() - range.get().floatValue(); y <= mc.player.posY + mc.player.getEyeHeight() + range.get().floatValue(); y++) {
                for(double z = mc.player.posZ - range.get().floatValue(); z <= mc.player.posZ + range.get().floatValue(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);

                    if(mc.world.getBlockState(pos).getBlock() instanceof BlockBed && !found) {
                        bedPos = pos;

                        if(hypixel.get() && isBlockOver(bedPos)) {
                            BlockPos posOver = pos.add(0, 1, 0);

                            mc.objectMouseOver = new MovingObjectPosition(new Vec3(posOver.getX() + 0.5, posOver.getY() + 1, posOver.getZ() + 0.5), EnumFacing.UP, posOver);

                            mc.settings.keyBindAttack.pressed = true;

                            float[] rots = RotationUtil.getRotationsToPosition(posOver.getX() + 0.5, posOver.getY() + 1, posOver.getZ() + 0.5);

                            yaw = rots[0];
                            pitch = rots[1];
                        } else {
                            mc.objectMouseOver = new MovingObjectPosition(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), EnumFacing.UP, bedPos);

                            mc.settings.keyBindAttack.pressed = true;

                            float[] rots = RotationUtil.getRotationsToPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

                            yaw = rots[0];
                            pitch = rots[1];
                        }

                        found = true;
                    }
                }
            }
        }

        if(!found && mc.currentScreen == null) {
            mc.settings.keyBindAttack.pressed = Mouse.isButtonDown(0);
        }

        rotations.updateRotations(yaw, pitch);
    }

    public boolean isBlockOver(BlockPos pos) {
        BlockPos posOver = pos.add(0, 1, 0);

        Block block = mc.world.getBlockState(posOver).getBlock();

        return !(block instanceof BlockAir || block instanceof BlockLiquid);
    }

    public boolean isBreakingBed() {
        return bedPos != null;
    }

    @Listen
    public void onRots(RotationEvent event) {
        if(bedPos != null && rotate.get()) {
            event.setYaw(rotations.getYaw());
            event.setPitch(rotations.getPitch());
        }

        if(mc.settings.keyBindJump.isPressed()) {
            if (bedPos != null && rotate.get() && moveFix.get()) {
                event.setYaw(rotations.getYaw());
            }
        }
        
        if(mc.settings.keyBindRight.isPressed() || mc.settings.keyBindLeft.isPressed()){
            if(bedPos != null && rotate.get() && moveFix.get()) {
                event.setYaw(rotations.getYaw());
            }
        }
    }
}