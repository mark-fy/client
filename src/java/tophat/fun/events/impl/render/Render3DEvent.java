package tophat.fun.events.impl.render;

import tophat.fun.events.Event;

public class Render3DEvent extends Event {

    public float partialTicks;

    public Render3DEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

}
