package wtf.tophat.client.events.impl.render;

import wtf.tophat.client.events.base.Event;

public class PerspectiveEvent extends Event {

    private float aspect;
    private final boolean hand;

    public PerspectiveEvent(float aspect, boolean hand) {
        this.aspect = aspect;
        this.hand = hand;
    }

    public PerspectiveEvent(float aspect) {
        this.aspect = aspect;
        this.hand = false;
    }

    public float getAspect() {
        return aspect;
    }

    public boolean isHand() {
        return hand;
    }

    public void setAspect(float aspect) {
        this.aspect = aspect;
    }
}
