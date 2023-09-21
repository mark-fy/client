package wtf.tophat.events.impl;

import wtf.tophat.events.base.Event;

public class SafeWalkEvent extends Event {

    private boolean safe;

    public SafeWalkEvent(boolean safe) { this.safe = safe; }

    public boolean isSafe() { return safe; }

    public void setSafe(boolean safe) { this.safe = safe; }

}
