package wtf.tophat.client.modules.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.world.UpdateEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.settings.impl.StringSetting;
import wtf.tophat.client.utilities.player.movement.MoveUtil;

@ModuleInfo(name = "Jesus", desc = "walk on water like jesus", category = Module.Category.MOVE)
public class Jesus extends Module {

    public final StringSetting mode;
    public final NumberSetting speed;

    public Jesus(){
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "NCP", "NCP", "Old Matrix", "Old Intave", "Verus"),
                speed = new NumberSetting(this, "Speed", 2, 10, 6, 1).setHidden(() -> !mode.is("Matrix"))
        );
    }

    @Listen
    public void onUpdate(UpdateEvent event){
        BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY - 0.1D, mc.player.posZ);
        Block block = mc.world.getBlockState(blockPos).getBlock();
        float yaw = (float) Math.toRadians(mc.player.rotationYaw);
        switch (mode.get()){
            case "Old Matrix":
                if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + 9.999999747378752E-2D, mc.player.posZ)).getBlock() instanceof BlockLiquid) {
                    mc.player.motionY = 0.07000000074505806D;
                }
                if (block instanceof BlockLiquid && !mc.player.onGround) {
                    if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).getBlock() == Blocks.water) {
                        mc.player.motionX = 0.0F;
                        mc.player.motionY = 0.036F;
                        mc.player.motionZ = 0.0F;
                    } else {
                        MoveUtil.setSpeed(speed.get().floatValue());
                    }
                    if (mc.player.isCollided) {
                        mc.player.motionY = 0.2;
                    }
                }
                break;
            case "NCP":
                BlockPos pos1 = new BlockPos(mc.player.posX, mc.player.posY - 0.03D, mc.player.posZ);
                Block block1 = mc.world.getBlockState(pos1).getBlock();
                if (block1.getMaterial() == Material.water) {
                    mc.player.motionY += 0.02;
                    mc.player.onGround = true;
                }
                break;
            case "Old Intave":
                float xZ = (float) (-Math.sin(yaw) * 0.5);
                float zZ = (float) (Math.cos(yaw) * 0.5);

                if (block.getMaterial() == Material.water) {
                    mc.player.motionY = 0;
                    mc.player.onGround = true;

                    if (mc.player.moveForward > 0) {
                        mc.player.motionX = -Math.sin(yaw) * 0.04;
                        mc.player.motionZ = Math.cos(yaw) * 0.04;
                    }
                }
                break;
            case "Verus":
                if (block.getMaterial() == Material.water) {
                    mc.player.cameraYaw = 0.1f;
                    mc.player.motionY = 0;
                    mc.player.onGround = true;

                    if (mc.player.moveForward > 0) {
                        MoveUtil.setSpeed(0.5);
                    }
                }
                break;
        }
    }

}
