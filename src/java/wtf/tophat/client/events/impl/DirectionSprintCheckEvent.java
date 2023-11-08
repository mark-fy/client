package wtf.tophat.client.events.impl;

import wtf.tophat.client.events.base.Event;

public class DirectionSprintCheckEvent extends Event {

    private boolean sprintCheck;

    public DirectionSprintCheckEvent(boolean sprintCheck) { this.sprintCheck = sprintCheck; }

    public boolean isSprintCheck() { return sprintCheck; }

    public void setSprintCheck(boolean sprintCheck) { this.sprintCheck = sprintCheck; }
}
