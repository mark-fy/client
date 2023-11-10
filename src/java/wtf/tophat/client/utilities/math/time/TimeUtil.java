package wtf.tophat.client.utilities.math.time;

import wtf.tophat.client.utilities.Methods;

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

    // MISC:
    public static float DEFAULT_TIMER = 1.0F;

    public static void setTimer(float timer) {
        setDoTimer(timer, 0);
    }

    private static void setDoTimer(float timer, int ticks) {
        if (ticks == 0) {
            mc.timer.timerSpeed = timer;
        } else {
            mc.timer.timerSpeed = mc.player.ticksExisted % ticks == 0 ? timer : DEFAULT_TIMER;
        }
    }

    public long getTime() {
        return System.currentTimeMillis() - lastMS;
    }

    public void setTime(long time) {
        lastMS = time;
    }
}
