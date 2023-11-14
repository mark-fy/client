package tophat.fun.events.impl.render;

import net.minecraft.client.gui.ScaledResolution;
import tophat.fun.events.Event;

public class Render2DEvent extends Event {

    public ScaledResolution scaledResolution;
    public float partialTicks;

    public Render2DEvent(ScaledResolution scaledResolution, float partialTicks) {
        this.scaledResolution = scaledResolution;
        this.partialTicks = partialTicks;
    }

    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

}
