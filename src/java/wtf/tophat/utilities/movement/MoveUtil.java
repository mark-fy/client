package wtf.tophat.utilities.movement;

import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import wtf.tophat.utilities.Methods;

public class MoveUtil implements Methods {

    public static double getPredictedMotion(double motion, int ticks) {
        if (ticks == 0) return motion;
        double predicted = motion;

        for (int i = 0; i < ticks; i++) {
            predicted = (predicted - 0.08) * 0.98F;
        }

        return predicted;
    }

    public static double[] getMotion(final double speed, final float strafe, final float forward, final float yaw) {
        final float friction = (float)speed;
        final float f1 = MathHelper.sin(yaw * (float)Math.PI / 180.0f);
        final float f2 = MathHelper.cos(yaw * (float)Math.PI / 180.0f);
        final double motionX = strafe * friction * f2 - forward * friction * f1;
        final double motionZ = forward * friction * f2 + strafe * friction * f1;
        return new double[] { motionX, motionZ };
    }

    public static float getSpeedBoost(float times) {
        float boost = (float) ((getBaseSpeed() - 0.2875F) * times);
        if(0 > boost) {
            boost = 0;
        }

        return boost;
    }

    public static double getSpeed() {
        return mc.player == null ? 0 : Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
    }

    public static double getBaseSpeed() {
        double baseSpeed = 0.272;
        if (mc.player.isPotionActive(Potion.moveSpeed)) {
            final int amplifier = mc.player.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + (0.2 * amplifier);
        }
        return baseSpeed;
    }

    public static void setSpeed(double moveSpeed, float yaw, double strafe, double forward) {
        if (forward != 0.0D) {
            if (strafe > 0.0D) {
                yaw += ((forward > 0.0D) ? -45 : 45);
            } else if (strafe < 0.0D) {
                yaw += ((forward > 0.0D) ? 45 : -45);
            }
            strafe = 0.0D;
            if (forward > 0.0D) {
                forward = 1.0D;
            } else if (forward < 0.0D) {
                forward = -1.0D;
            }
        }
        if (strafe > 0.0D) {
            strafe = 1.0D;
        } else if (strafe < 0.0D) {
            strafe = -1.0D;
        }
        double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
        double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
        mc.player.motionX = forward * moveSpeed * mx + strafe * moveSpeed * mz;
        mc.player.motionZ = forward * moveSpeed * mz - strafe * moveSpeed * mx;
    }

    public static void setSpeed(double moveSpeed) {
        setSpeed(moveSpeed, mc.player.rotationYaw, mc.player.movementInput.moveStrafe, mc.player.movementInput.moveForward);
    }


    public static boolean isBlockUnder() {
        if (mc.player.posY < 0) {
            return false;
        }
        for (int offset = 0; offset < (int) mc.player.posY + 2; offset += 2) {
            AxisAlignedBB bb = mc.player.getEntityBoundingBox().offset(0, -offset, 0);
            if (!mc.world.getCollidingBoundingBoxes(mc.player, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static void strafe() {
        strafe(getSpeed());
    }

    public static void strafe(double movementSpeed) {
        if (mc.player.movementInput.moveForward > 0.0) {
            mc.player.movementInput.moveForward = (float) 1.0;
        } else if (mc.player.movementInput.moveForward < 0.0) {
            mc.player.movementInput.moveForward = (float) -1.0;
        }

        if (mc.player.movementInput.moveStrafe > 0.0) {
            mc.player.movementInput.moveStrafe = (float) 1.0;
        } else if (mc.player.movementInput.moveStrafe < 0.0) {
            mc.player.movementInput.moveStrafe = (float) -1.0;
        }

        if (mc.player.movementInput.moveForward == 0.0 && mc.player.movementInput.moveStrafe == 0.0) {
            mc.player.motionX = 0.0;
            mc.player.motionZ = 0.0;
        }

        if (mc.player.movementInput.moveForward != 0.0 && mc.player.movementInput.moveStrafe != 0.0) {
            mc.player.movementInput.moveForward *= Math.sin(0.6398355709958845);
            mc.player.movementInput.moveStrafe *= Math.cos(0.6398355709958845);
        }

        mc.player.motionX = mc.player.movementInput.moveForward * movementSpeed * -Math.sin(Math.toRadians(mc.player.rotationYaw)) + mc.player.movementInput.moveStrafe * movementSpeed * Math.cos(Math.toRadians(mc.player.rotationYaw));
        mc.player.motionZ = mc.player.movementInput.moveForward * movementSpeed * Math.cos(Math.toRadians(mc.player.rotationYaw)) - mc.player.movementInput.moveStrafe * movementSpeed * -Math.sin(Math.toRadians(mc.player.rotationYaw));
    }

    public static float getDirection() {
        return getDirection(mc.player.moveForward, mc.player.moveStrafing, mc.player.rotationYaw);
    }

    public static float getDirection(float forward, float strafing, float yaw) {
        if (forward == 0.0 && strafing == 0.0) return yaw;
        boolean reversed = (forward < 0.0);
        float strafingYaw = 90f * ((forward > 0) ? 0.5f : (reversed ? -0.5f : 1));
        if (reversed) yaw += 180;
        if (strafing > 0) {
            yaw -= strafingYaw;
        } else if (strafing < 0) {
            yaw += strafingYaw;
        }
        return yaw;
    }

}