package wtf.tophat.utilities.movement;

import net.minecraft.util.AxisAlignedBB;
import wtf.tophat.utilities.Methods;

public class MoveUtil implements Methods {

    public static double getSpeed() {
        return mc.player == null ? 0 : Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
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

}
