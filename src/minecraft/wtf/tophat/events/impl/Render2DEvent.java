package wtf.tophat.events.impl;

import net.minecraft.client.gui.ScaledResolution;
import wtf.tophat.events.base.Event;

public class Render2DEvent extends Event {

    private final ScaledResolution scaledResolution;
    private final float partialTicks;

    public Render2DEvent(ScaledResolution scaledResolution, float partialTicks) {
        this.scaledResolution = scaledResolution;
        this.partialTicks = partialTicks;
    }

    public ScaledResolution getScaledResolution() { return scaledResolution; }

    public float getPartialTicks() { return partialTicks; }

}
