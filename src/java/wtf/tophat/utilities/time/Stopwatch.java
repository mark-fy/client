package wtf.tophat.utilities.time;

import wtf.tophat.utilities.Methods;

public final class Stopwatch implements Methods {

    public long lastMS = System.currentTimeMillis();

    private long currentTime;

    public Stopwatch() {
        setCurrentTime(getCurrentTime());
    }

    private long getCurrentTime() {
        return System.nanoTime() / 1000000;
    }

    public void resetTime() {
        setCurrentTime(getCurrentTime());
    }

    public long getStartTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public boolean timeElapsed(long milliseconds) {
        return getCurrentTime() - getStartTime() >= milliseconds;
    }

    public boolean hasTimeElapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - lastMS > time) {
            if (reset) resetTime();
            return true;
        }

        return false;
    }
}