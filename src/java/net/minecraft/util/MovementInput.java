package net.minecraft.util;

public class MovementInput
{
    /**
     * The speed at which the player is strafing. Postive numbers to the left and negative to the right.
     */
    public float moveStrafe;

    /**
     * The speed at which the player is moving forward. Negative numbers will move backwards.
     */
    public float moveForward;
    public boolean jump;
    public boolean sneak;

    public void updatePlayerMoveState()
    {
    }

    public float getMoveForward() {
        return moveForward;
    }

    public void setMoveForward(float moveForward) {
        this.moveForward = moveForward;
    }

    public float getMoveStrafe() {
        return moveStrafe;
    }

    public void setMoveStrafe(float moveStrafe) {
        this.moveStrafe = moveStrafe;
    }

    public boolean jump() {
        return jump;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public boolean sneak() {
        return sneak;
    }

}
