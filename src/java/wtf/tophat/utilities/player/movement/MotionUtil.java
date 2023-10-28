package wtf.tophat.utilities.player.movement;

import wtf.tophat.utilities.Methods;

public final class MotionUtil implements Methods {

    public static double[] teleportForward(final double speed) {
        final float forward = 1.0F;
        final float side = 0;
        final float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.timer.renderPartialTicks;
        final double sin = Math.sin(Math.toRadians(yaw + 90.0F));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0F));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[]{posX, posZ};
    }
}