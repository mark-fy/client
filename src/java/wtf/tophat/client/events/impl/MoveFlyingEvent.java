package wtf.tophat.client.events.impl;

import wtf.tophat.client.events.base.Event;

public final class MoveFlyingEvent extends Event {
    private float strafe;
    private float forward;
    private float friction;

    public MoveFlyingEvent(float f, float f2, float f3) {
        this.strafe = f;
        this.forward = f2;
        this.friction = f3;
    }

    public float getStrafe() {
        return this.strafe;
    }

    public void setStrafe(float f) {
        this.strafe = f;
    }

    public float getForward() {
        return this.forward;
    }

    public void setForward(float f) {
        this.forward = f;
    }

    public float getFriction() {
        return this.friction;
    }

    public void setFriction(float f) {
        this.friction = f;
    }

}
