package wtf.tophat.utilities.math;

public class TimeUtil {

    private long lastMS = System.currentTimeMillis();

    public void reset() { lastMS = System.currentTimeMillis(); }

    public boolean elapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - lastMS > time) {
            if (reset) {
                reset();
            }

            return true;
        }

        return false;
    }

    public boolean elapsed(long time) { return System.currentTimeMillis() - lastMS > time; }

    public long getLastMS() { return lastMS; }
}
