package wtf.tophat.client.utilities.player.rotations;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import wtf.tophat.client.utilities.Methods;

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

    public static float[] getRotationsToPosition(double x, double y, double z) {
        double deltaX = x - mc.player.posX;
        double deltaY = y - mc.player.posY - mc.player.getEyeHeight();
        double deltaZ = z - mc.player.posZ;

        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) Math.toDegrees(-Math.atan2(deltaX, deltaZ));
        float pitch = (float) Math.toDegrees(-Math.atan2(deltaY, horizontalDistance));

        return new float[] {yaw, pitch};
    }

    public static float[] getRotationsToPosition(double x, double y, double z, double targetX, double targetY, double targetZ) {
        double dx = targetX - x;
        double dy = targetY - y;
        double dz = targetZ - z;

        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) Math.toDegrees(-Math.atan2(dx, dz));
        float pitch = (float) Math.toDegrees(-Math.atan2(dy, horizontalDistance));

        return new float[] {yaw, pitch};
    }

    public static float getGCD() {
        return (float) (Math.pow(mc.settings.mouseSensitivity * 0.6 + 0.2, 3) * 1.2);
    }
}
