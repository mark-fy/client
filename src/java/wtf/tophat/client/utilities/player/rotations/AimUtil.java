package wtf.tophat.client.utilities.player.rotations;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import wtf.tophat.client.utilities.Methods;

import java.util.concurrent.ThreadLocalRandom;

public final class AimUtil implements Methods {

    private static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Attempts to look at an {@code EntityLivingBase}'s head
     *
     * @param destinationRotations - The rotations to use.
     */
    public static void turnToEntityClient(Rotation destinationRotations) {
        mc.player.rotationYaw = destinationRotations.getRotationYaw();
        mc.player.rotationPitch = destinationRotations.getRotationPitch();
    }

    /**
     * Tries to get a {@code Rotation} for an {@code EntityLivingBase}
     *
     * @param entity - The entity to {@code EntityLivingBase} at.
     * @return The {@code Rotation} for this {@code Entity}
     */
    public static Rotation getRotationsRandom(EntityLivingBase entity) {

        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        double randomXZ = threadLocalRandom.nextDouble(-0.05, 0.1);
        double randomY = threadLocalRandom.nextDouble(-0.05, 0.1);
        double x = entity.posX + randomXZ;
        double y = entity.posY + (entity.getEyeHeight() / 2.05) + randomY;
        double z = entity.posZ + randomXZ;
        return attemptFacePosition(x, y, z);
    }

    /**
     * Wraps the specified angle between -180 and 180
     *
     * @param angle - Input angle
     * @return The wrapped angle
     * @author Mojang
     */
    public static float wrapAngle(float angle) {

        angle %= 360.0F;

        if (angle >= 180.0F) {
            angle -= 360.0F;
        }

        if (angle < -180.0F) {
            angle += 360.0F;
        }

        return angle;
    }


    /**
     * Attempts to get rotations to aim a perfect bow shot for this {@code Entity}
     *
     * @param entity - The {@code Entity} to get rotations
     * @return The predicted rotations for this entity
     */
    public static Rotation getBowAngles(Entity entity) {
        double xDelta = entity.posX - entity.lastTickPosX;
        double zDelta = entity.posZ - entity.lastTickPosZ;
        double distance = mc.player.getDistanceToEntity(entity) % .8;
        boolean sprint = entity.isSprinting();
        double xMulti = distance / .8 * xDelta * (sprint ? 1.45 : 1.3);
        double zMulti = distance / .8 * zDelta * (sprint ? 1.45 : 1.3);
        double x = entity.posX + xMulti - mc.player.posX;
        double y = mc.player.posY + mc.player.getEyeHeight()
                - (entity.posY + entity.getEyeHeight());
        double z = entity.posZ + zMulti - mc.player.posZ;
        double distanceToEntity = mc.player.getDistanceToEntity(entity);
        float yaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90;
        float pitch = (float) Math.toDegrees(Math.atan2(y, distanceToEntity));
        return new Rotation(yaw, pitch);
    }

    /**
     * Tries to get a {@code Rotation} for the specified coordinates
     *
     * @param x - The X coordinate
     * @param y - The Y coordinate
     * @param z - The Z coordinate
     * @return The rotations for the specified coordinates
     */
    public static Rotation attemptFacePosition(double x, double y, double z) {
        double xDiff = x - mc.player.posX;
        double yDiff = y - mc.player.posY - 1.2;
        double zDiff = z - mc.player.posZ;

        double dist = Math.hypot(xDiff, zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180 / Math.PI) - 90;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180 / Math.PI);
        return new Rotation(yaw, pitch);
    }

    public static Rotation getScaffoldRotations(final BlockPos position) { // Credits: Hideri
        double direction = direction();
        double posX = -Math.sin(direction) * 0.5F;
        double posZ = Math.cos(direction) * 0.5F;

        double x = position.getX() - mc.player.posX - posX;
        double y = position.getY() - mc.player.prevPosY - mc.player.getEyeHeight();
        double z = position.getZ() - mc.player.posZ - posZ;

        double distance = Math.hypot(x, z);

        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI - 90.0F);
        float pitch = (float) -(Math.atan2(y, distance) * 180.0D / Math.PI);

        return new Rotation(mc.player.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.player.rotationPitch));
    }

    private static double direction() { // Credits: Hideri
        float rotationYaw = mc.player.rotationYaw;
        if (mc.player.movementInput.moveForward < 0.0F)
            rotationYaw += 180.0F;
        float forward = 1.0F;
        if (mc.player.movementInput.moveForward < 0.0F)
            forward = -0.5F;
        else if (mc.player.movementInput.moveForward > 0.0F)
            forward = 0.5F;
        if (mc.player.movementInput.moveStrafe > 0.0F)
            rotationYaw -= 90.0F * forward;
        if (mc.player.movementInput.moveStrafe < 0.0F)
            rotationYaw += 90.0F * forward;
        return Math.toRadians(rotationYaw);
    }
}