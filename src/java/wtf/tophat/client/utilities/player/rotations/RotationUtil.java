package wtf.tophat.client.utilities.player.rotations;

import de.florianmichael.rclasses.math.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.tophat.client.events.handler.PlayerHandler;
import wtf.tophat.client.utilities.Methods;
import wtf.tophat.client.utilities.math.MathUtil;
import wtf.tophat.client.utilities.math.RandomUtil;
import wtf.tophat.client.utilities.misc.RaytraceUtil;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

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

    public static Vec3 getBestVector(Vec3 look, AxisAlignedBB axisAlignedBB) {
        return new Vec3(MathUtil.clamp(look.xCoord, axisAlignedBB.minX, axisAlignedBB.maxX), MathUtil.clamp(look.yCoord, axisAlignedBB.minY, axisAlignedBB.maxY), MathUtil.clamp(look.zCoord, axisAlignedBB.minZ, axisAlignedBB.maxZ));
    }

    public static float[] getRotation(Vec3 aimVector) {
        double x = aimVector.xCoord - mc.player.posX;
        double y = aimVector.yCoord - (mc.player.posY + (double) mc.player.getEyeHeight());
        double z = aimVector.zCoord - mc.player.posZ;

        double d3 = MathUtil.sqrt(x * x + z * z);
        float f = (float) (MathHelper.atan2(z, x) * (180 / Math.PI)) - 90.0F;
        float f1 = (float) (-(MathHelper.atan2(y, d3) * (180 / Math.PI)));
        f1 = MathUtil.clamp(f1, -90, 90);
        return new float[]{f, f1};
    }

    public static float[] getRotation(Entity entity, String vectorMode, float heightDivisor, boolean mouseFix, boolean heuristics, double minRandomYaw, double maxRandomYaw, double minRandomPitch, double maxRandomPitch, boolean prediction, float minYaw, float maxYaw, float minPitch, float maxPitch, boolean snapYaw, boolean snapPitch) {
        Vec3 aimVector = getBestVector(mc.player.getPositionEyes(1F), entity.getEntityBoundingBox());
        switch (vectorMode) {
            case "Bruteforce":
                for (double yPercent = 1; yPercent >= 0; yPercent -= 0.25) {
                    for (double xPercent = 1; xPercent >= -0.5; xPercent -= 0.5) {
                        for (double zPercent = 1; zPercent >= -0.5; zPercent -= 0.5) {
                            Vec3 tempVec = new Vec3(xPercent, yPercent, zPercent);
                            if (RaytraceUtil.rayCast(1F, getRotation(tempVec)).typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                                aimVector = tempVec;
                            }
                        }
                    }
                }
                break;
            case "Head":
                aimVector = new Vec3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
                break;
            case "Torso":
                aimVector = new Vec3(entity.posX, entity.posY + entity.getEyeHeight() / 2d, entity.posZ);
                break;
            case "Feet":
                aimVector = new Vec3(entity.posX, entity.posY, entity.posZ);
                break;
            case "Custom":
                aimVector = new Vec3(entity.posX, entity.posY + entity.getEyeHeight() / heightDivisor, entity.posZ);
                break;
            case "Random":
                double x = RandomUtil.randomBetween(0, 0.4) - 0.2;
                double y = RandomUtil.randomBetween(0, 1) + 1;
                double z = RandomUtil.randomBetween(0, 0.4) - 0.2;
                aimVector = new Vec3(entity.posX + x, entity.posY + y, entity.posZ + z);
                break;
        }
        aimVector.xCoord += RandomUtil.randomBetween(minRandomYaw, maxRandomYaw);
        aimVector.yCoord += RandomUtil.randomBetween(minRandomPitch, maxRandomPitch);
        aimVector.zCoord += RandomUtil.randomBetween(minRandomYaw, maxRandomYaw);
        double x = aimVector.xCoord - mc.player.posX;
        double y = aimVector.yCoord - (mc.player.posY + (double) mc.player.getEyeHeight());
        double z = aimVector.zCoord - mc.player.posZ;

        if (prediction) {
            final boolean targetIsSprinting = entity.isSprinting();
            final boolean playerIsSprinting = mc.player.isSprinting();

            final float walkingSpeed = 0.10000000149011612f;
            final float targetSpeed = targetIsSprinting ? 1.25f : walkingSpeed;
            final float playerSpeed = playerIsSprinting ? 1.25f : walkingSpeed;

            final float targetPredictedX = (float) ((entity.posX - entity.prevPosX) * targetSpeed);
            final float targetPredictedZ = (float) ((entity.posZ - entity.prevPosZ) * targetSpeed);
            final float playerPredictedX = (float) ((mc.player.posX - mc.player.prevPosX) * playerSpeed);
            final float playerPredictedZ = (float) ((mc.player.posZ - mc.player.prevPosZ) * playerSpeed);

            if (targetPredictedX != 0.0f && targetPredictedZ != 0.0f || playerPredictedX != 0.0f && playerPredictedZ != 0.0f) {
                x += targetPredictedX + playerPredictedX;
                z += targetPredictedZ + playerPredictedZ;
            }
        }

        if (heuristics) {
            try {
                x += SecureRandom.getInstanceStrong().nextDouble() * 0.1;
                y += SecureRandom.getInstanceStrong().nextDouble() * 0.1;
                z += SecureRandom.getInstanceStrong().nextDouble() * 0.1;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        double d3 = MathUtil.sqrt(x * x + z * z);
        float yawSpeed = (float) RandomUtil.randomBetween(minYaw, maxYaw);
        float pitchSpeed = (float) RandomUtil.randomBetween(minPitch, maxPitch);
        float f = (float) (MathHelper.atan2(z, x) * (180 / Math.PI)) - 90.0F;
        float f1 = (float) (-(MathHelper.atan2(y, d3) * (180 / Math.PI)));
        final float deltaYaw = (((f - PlayerHandler.yaw) + 540) % 360) - 180;
        final float deltaPitch = f1 - PlayerHandler.pitch;
        final float yawDistance = MathHelper.clamp_float(deltaYaw, -yawSpeed, yawSpeed);
        final float pitchDistance = MathHelper.clamp_float(deltaPitch, -pitchSpeed, pitchSpeed);
        float calcYaw = snapYaw ? f : PlayerHandler.yaw + yawDistance;
        float calcPitch = snapPitch ? f1 : PlayerHandler.pitch + pitchDistance;
        calcPitch = MathUtil.clamp(calcPitch, -90, 90);
        if (!mouseFix)
            return new float[]{calcYaw, calcPitch};
        return applyMouseFix(calcYaw, calcPitch);
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

    public static float[] applyMouseFix(float newYaw, float newPitch) {
        final float sensitivity = Math.max(0.001F, mc.settings.mouseSensitivity);
        final int deltaYaw = (int) ((newYaw - PlayerHandler.yaw) / ((sensitivity * (sensitivity >= 0.5 ? sensitivity : 1) / 2)));
        final int deltaPitch = (int) ((newPitch - PlayerHandler.pitch) / ((sensitivity * (sensitivity >= 0.5 ? sensitivity : 1) / 2))) * -1;
        final float f = sensitivity * 0.6F + 0.2F;
        final float f1 = f * f * f * 8.0F;
        final float f2 = (float) deltaYaw * f1;
        final float f3 = (float) deltaPitch * f1;

        final float endYaw = (float) ((double) PlayerHandler.yaw + (double) f2 * 0.15);
        float endPitch = (float) ((double) PlayerHandler.pitch - (double) f3 * 0.15);
        endPitch = MathUtil.clamp(endPitch, -90, 90);
        return new float[]{endYaw, endPitch};
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
