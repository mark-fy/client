package wtf.tophat.utilities.time;

import wtf.tophat.utilities.Methods;

public final class Stopwatch implements Methods {

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
}