package tophat.fun.events.impl.player;

import tophat.fun.events.Event;

public class SafeWalkEvent extends Event {

    private boolean safe;

    public SafeWalkEvent(boolean safe) {
        this.safe = safe;
    }

    public boolean isSafe() {
        return safe;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }

}
