package wtf.tophat.events.impl;

import wtf.tophat.events.Event;

public class DirectionSprintCheckEvent extends Event {

    private boolean sprintCheck;

    public DirectionSprintCheckEvent(boolean sprintCheck) {
        this.sprintCheck = sprintCheck;
    }

    public boolean isSprintCheck() {
        return sprintCheck;
    }

    public void setSprintCheck(boolean sprintCheck) {
        this.sprintCheck = sprintCheck;
    }
}
