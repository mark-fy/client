package tophat.fun.utilities.others;

import tophat.fun.utilities.Methods;

public class TimeUtil implements Methods {

    private long lastMS = System.currentTimeMillis();

    public long getCurrentMS() {
        return System.currentTimeMillis();
    }

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

    public long getTime() {
        return System.currentTimeMillis() - lastMS;
    }

    public void setTime(long time) {
        lastMS = time;
    }
}