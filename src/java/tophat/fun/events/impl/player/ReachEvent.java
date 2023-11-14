package tophat.fun.events.impl.player;

import tophat.fun.events.Event;
import tophat.fun.utilities.Methods;

public class ReachEvent extends Event implements Methods {

    private float blockReachDistance, rayTraceRange, range;

    public ReachEvent(float range) {
        this.blockReachDistance = mc.playerController.getBlockReachDistance();
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
