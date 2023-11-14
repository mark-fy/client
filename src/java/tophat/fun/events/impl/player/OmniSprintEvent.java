package tophat.fun.events.impl.player;

import tophat.fun.events.Event;

public class OmniSprintEvent extends Event {

    private boolean sprintCheck;

    public OmniSprintEvent(boolean sprintCheck) {
        this.sprintCheck = sprintCheck;
    }

    public boolean isSprintCheck() {
        return sprintCheck;
    }

    public void setSprintCheck(boolean sprintCheck) {
        this.sprintCheck = sprintCheck;
    }

}
