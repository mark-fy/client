package wtf.tophat.client.events.impl;

import wtf.tophat.client.events.base.Event;

public class SlowDownEvent extends Event {

    private float strafe;
    private float forward;
    private boolean sprint;

    public SlowDownEvent(float strafe, float forward) {
        this.strafe = strafe;
        this.forward = forward;
    }

    public float getStrafe() {
        return strafe;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public float getForward() {
        return forward;
    }

    public void setForward(float forward) {
        this.forward = forward;
    }

    public boolean isSprint() {
        return sprint;
    }

    public void setSprint(boolean sprint) {
        this.sprint = sprint;
    }
}
