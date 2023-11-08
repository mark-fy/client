package wtf.tophat.client.events.impl;

import wtf.tophat.client.events.base.Event;

public class Render3DEvent extends Event {

    private final float partialTicks;

    public Render3DEvent(float partialTicks) { this.partialTicks = partialTicks; }

    public float getPartialTicks() { return partialTicks; }

}
