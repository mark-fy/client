package wtf.script.methods;

import wtf.tophat.utilities.Methods;
import wtf.tophat.utilities.player.movement.MoveUtil;

public class Player implements Methods {

    // Methods

    public void jump() { getPlayer().jump(); }
    public void swing() { getPlayer().swingItem(); }

    // Setters

    public void setSpeed(double speed) { MoveUtil.setSpeed(speed); }
    public void setX(double x) { getPlayer().setPositionAndUpdate(getX() + x, getY(), getZ()); }
    public void setY(double y) { getPlayer().setPositionAndUpdate(getX(), getY() + y, getZ()); }
    public void setZ(double z) { getPlayer().setPositionAndUpdate(getX(), getY(), getZ() + z); }
    public void setPitch(float pitch) { getPlayer().rotationPitch += pitch; }
    public void setYaw(float yaw) { getPlayer().rotationYaw += yaw; }
    public void setMotionX(double x) { getPlayer().motionX = x; }
    public void setMotionY(double y) { getPlayer().motionY = y; }
    public void setMotionZ(double z) { getPlayer().motionZ = z; }
    public void setGround(boolean ground) { getPlayer().onGround = ground; }
    public void setSprint(boolean sprint) { getPlayer().setSprinting(sprint); }
    public void setFallDistance(float distance) { getPlayer().fallDistance = distance; }

    //Getters

    public boolean isMoving() { return isMoving(); }
    public boolean getGround() { return getPlayer().onGround; }
    public double getSpeed() { return MoveUtil.getSpeed(); }
    public float getBPS() { return getBPS(); }
    public double getBaseSpeed() { return MoveUtil.getBaseSpeed(); }
    public double getX() { return getPlayer().posX; }
    public double getY() { return getPlayer().posY; }
    public double getZ() { return getPlayer().posZ; }
    public double getMotionX() { return getPlayer().motionX; }
    public double getMotionY() { return getPlayer().motionY; }
    public double getMotionZ() { return getPlayer().motionZ; }
    public float getPitch() { return getPlayer().rotationPitch; }
    public float getYaw() { return getPlayer().rotationYaw; }
    public boolean getCollidedH() { return getPlayer().isCollidedHorizontally; }
    public boolean getCollidedV() { return getPlayer().isCollidedVertically; }
    public boolean getCollided() { return getPlayer().isCollided; }
    public boolean getDead() { return getPlayer().isDead; }
    public float getFallDistance() { return getPlayer().fallDistance; }
    public int getTicksExisted() { return getPlayer().ticksExisted; }
    public boolean canClimbWall() { return canClimbWall(); }
    public boolean isOnLadder() { return getPlayer().isOnLadder(); }
    public boolean isInWater() { return getPlayer().isInWater(); }
    public boolean isInLava() { return getPlayer().isInLava(); }
    public boolean isImmuneFromFire() { return getPlayer().isImmuneToFire(); }

}
