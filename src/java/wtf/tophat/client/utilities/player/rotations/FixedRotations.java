package wtf.tophat.client.utilities.player.rotations;

public class FixedRotations {

    private float yaw, pitch;
    private float lastYaw, lastPitch;

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

    public float getLastYaw() {
        return lastYaw;
    }

    public void setLastYaw(float lastYaw) {
        this.lastYaw = lastYaw;
    }

    public float getLastPitch() {
        return lastPitch;
    }

    public void setLastPitch(float lastPitch) {
        this.lastPitch = lastPitch;
    }

    public FixedRotations(float startingYaw, float startingPitch) {
        lastYaw = yaw = startingYaw;
        lastPitch = pitch = startingPitch;
    }

    public void updateRotations(float requestedYaw, float requestedPitch) {
        lastYaw = yaw;
        lastPitch = pitch;

        float gcd = RotationUtil.getGCD();

        float yawDiff = (requestedYaw - yaw);
        float pitchDiff = (requestedPitch - pitch);

        float fixedYawDiff = yawDiff - (yawDiff % gcd);
        float fixedPitchDiff = pitchDiff - (pitchDiff % gcd);

        yaw += fixedYawDiff;
        pitch += fixedPitchDiff;

        pitch = Math.max(-90, Math.min(90, pitch));
    }

}