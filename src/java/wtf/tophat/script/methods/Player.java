package wtf.tophat.script.methods;

import wtf.tophat.utilities.Methods;
import wtf.tophat.utilities.player.movement.MoveUtil;

public class Player implements Methods {

    public void setSpeed(double speed) {
        MoveUtil.setSpeed(speed);
    }

    public void setX(int x) {
        getPlayer().setPositionAndUpdate(getX() + x, getY(), getZ());
    }

    public void setY(int y) {
        getPlayer().setPositionAndUpdate(getX(), getY() + y, getZ());
    }

    public void setZ(int z) {
        getPlayer().setPositionAndUpdate(getX(), getY(), getZ() + z);
    }

    public void setPitch(float pitch) {
        getPlayer().rotationPitch += pitch;
    }

    public void setYaw(float yaw) {
        getPlayer().rotationYaw += yaw;
    }

    public void setMotionX(float x) {
        getPlayer().motionX = x;
    }

    public void setMotionY(float y) {
        getPlayer().motionY = y;
    }

    public void setMotionZ(float z) {
        getPlayer().motionZ = z;
    }

    public void setGround(boolean ground) {
        getPlayer().onGround = ground;
    }

    public boolean getGround() {
        return getPlayer().onGround;
    }

    public void setSprint(boolean sprint) {
        getPlayer().setSprinting(sprint);
    }

    public void jump() {
        getPlayer().jump();
    }

    public void swing() {
        getPlayer().swingItem();
    }

}
