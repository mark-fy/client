package wtf.tophat.utilities.player.rotations;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import wtf.tophat.utilities.Methods;

public class RotationUtil implements Methods {

    public static float[] getRotation(final Entity entity) {
        if (entity == null) {
            return null;
        }
        Minecraft mc = Minecraft.getMinecraft();
        final double xSize = entity.posX - mc.player.posX;
        final double ySize = entity.posY + entity.getEyeHeight() / 2 - (mc.player.posY + mc.player.getEyeHeight());
        final double zSize = entity.posZ - mc.player.posZ;
        final double theta = MathHelper.sqrt_double(xSize * xSize + zSize * zSize);
        final float yaw = (float) (Math.atan2(zSize, xSize) * 180 / Math.PI) - 90;
        final float pitch = (float) (-(Math.atan2(ySize, theta) * 180 / Math.PI));
        return new float[]{(mc.player.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.player.rotationYaw)) % 360, (mc.player.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.player.rotationPitch)) % 360.0f};
    }

    public static float[] getNeededRotations(Entity entity) {
        double d0 = entity.posX - Minecraft.getMinecraft().player.posX;
        double d1 = entity.posZ - Minecraft.getMinecraft().player.posZ;
        double d2 = entity.posY + entity.getEyeHeight()
                - (Minecraft.getMinecraft().player.getEntityBoundingBox().minY
                + (Minecraft.getMinecraft().player.getEntityBoundingBox().maxY
                - Minecraft.getMinecraft().player.getEntityBoundingBox().minY));
        double d3 = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
        float f = (float) (MathHelper.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        float f1 = (float) (-(MathHelper.atan2(d2, d3) * 180.0D / Math.PI));
        return new float[] { f, f1 };
    }
}
