package wtf.tophat.client.events.impl;

import wtf.tophat.client.events.base.Event;
import wtf.tophat.client.utilities.Methods;

public class RayTraceRangeEvent extends Event implements Methods {

    private float blockReachDistance, rayTraceRange, range;

    public RayTraceRangeEvent(final float range) {
        this.blockReachDistance = RayTraceRangeEvent.mc.playerController.getBlockReachDistance();
        this.range = range;
    }

    public float getBlockReachDistance() {
        return blockReachDistance;
    }

    public float getRange() {
        return range;
    }

    public float getRayTraceRange() {
        return rayTraceRange;
    }

    public void setBlockReachDistance(float blockReachDistance) {
        this.blockReachDistance = blockReachDistance;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public void setRayTraceRange(float rayTraceRange) {
        this.rayTraceRange = rayTraceRange;
    }

}
