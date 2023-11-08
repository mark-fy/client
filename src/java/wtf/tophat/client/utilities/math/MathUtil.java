package wtf.tophat.client.utilities.math;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import wtf.tophat.client.utilities.Methods;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class MathUtil implements Methods {

    public static final float PI = (float) Math.PI;
    private static final float[] ASIN_TABLE = new float[65536];

    public static float asin(float value) {
        return ASIN_TABLE[(int) ((double) (value + 1.0F) * 32767.5D) & 65535];
    }

    public static float acos(float value) {
        return ((float) Math.PI / 2F) - ASIN_TABLE[(int) ((double) (value + 1.0F) * 32767.5D) & 65535];
    }

    public static float roundToFloat(double d) {
        return (float) ((double) Math.round(d * 1.0E8D) / 1.0E8D);
    }

    public static double randomNumber(final double max, final double min) {
        return Math.random() * (max - min) + min;
    }

    public static int getRandInt(final int min, final int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    static {
        for (int i = 0; i < 65536; ++i) {
            ASIN_TABLE[i] = (float) Math.asin((double) i / 32767.5D - 1.0D);
        }

        for (int j = -1; j < 2; ++j) {
            ASIN_TABLE[(int) (((double) j + 1.0D) * 32767.5D) & 65535] = (float) Math.asin(j);
        }
    }

    public static Vec3 getCenter(AxisAlignedBB alignedBB) {
        double centerX = alignedBB.minX + (alignedBB.maxX - alignedBB.minX) / 2.0;
        double centerZ = alignedBB.minZ + (alignedBB.maxZ - alignedBB.minZ) / 2.0;
        double centerY = alignedBB.minY + (alignedBB.maxY - alignedBB.minY) / 2.0;
        return new Vec3(centerX, centerY, centerZ);
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal decimal = new BigDecimal(value);
        decimal = decimal.setScale(places, RoundingMode.HALF_UP);

        return decimal.doubleValue();
    }

    public static double square(double squareX) {
        squareX *= squareX;
        return squareX;
    }

    public static double clamp(double value, double minimum, double maximum) {
        return value < minimum ? minimum : (Math.min(value, maximum));
    }

    public static float clamp(float value, float minimum, float maximum) {
        return value < minimum ? minimum : (Math.min(value, maximum));
    }

    public static int clamp(int value, int minimum, int maximum) {
        return value < minimum ? minimum : (Math.min(value, maximum));
    }

    public static double interpolate(final double newPos, final double oldPos) {
        return oldPos + (newPos - oldPos) * mc.timer.renderPartialTicks;
    }

}
