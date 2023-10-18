package wtf.tophat.utilities.player.rotations;

import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import wtf.tophat.utilities.Methods;

public class RotationUtil implements Methods {

    public static Vec3 getBestVector(Vec3 look, AxisAlignedBB axisAlignedBB) {
        return new Vec3(MathHelper.clamp_double(look.xCoord, axisAlignedBB.minX, axisAlignedBB.maxX), MathHelper.clamp_double(look.yCoord, axisAlignedBB.minY, axisAlignedBB.maxY), MathHelper.clamp_double(look.zCoord, axisAlignedBB.minZ, axisAlignedBB.maxZ));
    }

    public static float[] getRotation(Entity entity) {
        double deltaX = entity.posX + (entity.posX - entity.lastTickPosX) - mc.player.posX,
               deltaY = entity.posY - 3.5 + entity.getEyeHeight() - mc.player.posY + mc.player.getEyeHeight(),
               deltaZ = entity.posZ + (entity.posZ - entity.lastTickPosZ) -mc.player.posZ,
               distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));

        float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ)),
              pitch = (float) -Math.toDegrees(Math.atan(deltaY / distance ));

        double degrees = Math.toDegrees(Math.atan(deltaZ / deltaX));
        if(deltaX < 0 && deltaZ < 0) {
            yaw = (float) (90 + degrees);
        } else if(deltaX > 0  && deltaZ < 0) {
            yaw = (float) (-90 + degrees);
        }

        return new float[] {yaw, pitch};
    }
}
