package tophat.fun.events.impl.player;

import tophat.fun.events.Event;

public class RotationEvent extends Event {

    private float yaw, pitch;

    public RotationEvent(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

}
