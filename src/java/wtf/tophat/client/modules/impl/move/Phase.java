package wtf.tophat.client.modules.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import wtf.tophat.client.events.impl.world.CollisionBoxesEvent;
import wtf.tophat.client.events.impl.world.UpdateEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;

@ModuleInfo(name = "Phase", desc = "pass trough blocks", category = Module.Category.MOVE)
public class Phase extends Module {


    //Skip phase

    @Listen
    public void onUpdate(UpdateEvent event) {
        if (isInsideBlock() && this.mc.player.isSneaking()) {
            final float yaw = this.mc.player.rotationYaw;
            final float distance = 1.0f;
            this.mc.player.getEntityBoundingBox().offsetAndUpdate(distance * Math.cos(Math.toRadians(yaw + 90.0f)), 0.0, distance * Math.sin(Math.toRadians(yaw + 90.0f)));
        }

    }

    @Listen
    public void onCollisionBoxes(CollisionBoxesEvent event) {
        if (event.getBoundingBox() != null && event.getBoundingBox().maxY > this.mc.player.getEntityBoundingBox().minY && this.mc.player.isSneaking()) {
            event.setBoundingBox(null);
        }
    }


    public static boolean isInsideBlock() {
        for (int x = MathHelper.floor_double(mc.player.getEntityBoundingBox().minX); x < MathHelper
                .floor_double(mc.player.getEntityBoundingBox().maxX) + 1; ++x) {
            for (int y = MathHelper.floor_double(mc.player.getEntityBoundingBox().minY); y < MathHelper
                    .floor_double(mc.player.getEntityBoundingBox().maxY) + 1; ++y) {
                for (int z = MathHelper
                        .floor_double(mc.player.getEntityBoundingBox().minZ); z < MathHelper
                        .floor_double(mc.player.getEntityBoundingBox().maxZ) + 1; ++z) {
                    final Block block = mc.world.getBlockState(new BlockPos(x, y, z))
                            .getBlock();
                    final AxisAlignedBB boundingBox;
                    if (block != null && !(block instanceof BlockAir)
                            && (boundingBox = block.getCollisionBoundingBox(mc.world,
                            new BlockPos(x, y, z),
                            mc.world.getBlockState(new BlockPos(x, y, z)))) != null
                            && mc.player.getEntityBoundingBox().intersectsWith(boundingBox)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
