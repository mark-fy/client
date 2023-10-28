package wtf.tophat.utilities.math;

import java.util.Random;

public class MathUtil {

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

}
