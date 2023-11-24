package tophat.fun.utilities.math;

import tophat.fun.utilities.Methods;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil implements Methods {

    public static double square(double squareX) {
        squareX *= squareX;
        return squareX;
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal decimal = new BigDecimal(value);
        decimal = decimal.setScale(places, RoundingMode.HALF_UP);

        return decimal.doubleValue();
    }

    public static double interpolate(final double newPos, final double oldPos) {
        return oldPos + (newPos - oldPos) * mc.timer.renderPartialTicks;
    }
}
