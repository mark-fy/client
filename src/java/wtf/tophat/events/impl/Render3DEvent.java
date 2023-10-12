package wtf.tophat.events.impl;

import wtf.tophat.events.base.Event;

public class Render3DEvent extends Event {

    private final float partialTicks;

    public Render3DEvent(float partialTicks) { this.partialTicks = partialTicks; }

    public float getPartialTicks() { return partialTicks; }

}
