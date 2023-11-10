package wtf.tophat.client.utilities.player.movement;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Vector2f;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.MotionEvent;
import wtf.tophat.client.modules.impl.player.ScaffoldWalk;
import wtf.tophat.client.utilities.Methods;

public class MoveUtil implements Methods {

    public static double getPredictedMotion(double motion, int ticks) {
        if (ticks == 0) return motion;
        double predicted = motion;

        for (int i = 0; i < ticks; i++) {
            predicted = (predicted - 0.08) * 0.98F;
        }

        return predicted;
    }

    public static void stop() {
        mc.player.motionX = 0;
        mc.player.motionZ = 0;
    }
    public static void spoof(double x, double y, double z, boolean ground) {
        mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z, ground));
    }

    public static void spoof(double y, boolean ground) {
        spoof(0, y, 0, ground);
    }


    public void strafeNoTargetStrafe(MotionEvent event, double speed) {
        float direction = (float) Math.toRadians(getPlayerDirection());

        if (isMoving()) {
            event.setX(mc.player.motionX = -Math.sin(direction) * speed);
            event.setZ(mc.player.motionZ = Math.cos(direction) * speed);
        } else {
            event.setX(mc.player.motionX = 0);
            event.setZ(mc.player.motionZ = 0);
        }
    }

    public static int getSpeedAmplifier() {
        if(mc.player.isPotionActive(Potion.moveSpeed)) {
            return 1 + mc.player.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
        }

        return 0;
    }

    public static void jump(MotionEvent event) {
        double jumpY = (double) mc.player.getJumpUpwardsMotion();

        if(mc.player.isPotionActive(Potion.jump)) {
            jumpY += (double)((float)(mc.player.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
        }

        event.setY(mc.player.motionY = jumpY);
    }

    public static float getPlayerDirection() {
        float direction = mc.player.rotationYaw;

        if (mc.player.moveForward > 0) {
            if (mc.player.moveStrafing > 0) {
                direction -= 45;
            } else if (mc.player.moveStrafing < 0) {
                direction += 45;
            }
        } else if (mc.player.moveForward < 0) {
            if (mc.player.moveStrafing > 0) {
                direction -= 135;
            } else if (mc.player.moveStrafing < 0) {
                direction += 135;
            } else {
                direction -= 180;
            }
        } else {
            if (mc.player.moveStrafing > 0) {
                direction -= 90;
            } else if (mc.player.moveStrafing < 0) {
                direction += 90;
            }
        }

        return direction;
    }

    public static float getPlayerDirection(float baseYaw) {
        float direction = baseYaw;

        if (mc.player.moveForward > 0) {
            if (mc.player.moveStrafing > 0) {
                direction -= 45;
            } else if (mc.player.moveStrafing < 0) {
                direction += 45;
            }
        } else if (mc.player.moveForward < 0) {
            if (mc.player.moveStrafing > 0) {
                direction -= 135;
            } else if (mc.player.moveStrafing < 0) {
                direction += 135;
            } else {
                direction -= 180;
            }
        } else {
            if (mc.player.moveStrafing > 0) {
                direction -= 90;
            } else if (mc.player.moveStrafing < 0) {
                direction += 90;
            }
        }

        return direction;
    }
    
    public static double[] getMotion(final double speed, final float strafe, final float forward, final float yaw) {
        final float friction = (float)speed;
        final float f1 = MathHelper.sin(yaw * (float)Math.PI / 180.0f);
        final float f2 = MathHelper.cos(yaw * (float)Math.PI / 180.0f);
        final double motionX = strafe * friction * f2 - forward * friction * f1;
        final double motionZ = forward * friction * f2 + strafe * friction * f1;
        return new double[] { motionX, motionZ };
    }

    public static void addFriction() {
        addFriction(0.91);
    }

    public static void addFriction(double friction) {
        mc.player.motionX *= friction;
        mc.player.motionZ *= friction;
    }

    public static float getMaxFallDist() {
        return mc.player.getMaxFallHeight() + (mc.player.isPotionActive(Potion.jump) ? mc.player.getActivePotionEffect(Potion.jump).getAmplifier() + 1 : 0);
    }


    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873D;
        if (mc.player.isPotionActive(Potion.moveSpeed)) {
            int amplifier = mc.player.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= (1.0D + 0.2D * (amplifier + 1));
        }
        return baseSpeed;
    }

    public static float getSpeedBoost(float times) {
        float boost = (float) ((getBaseSpeed() - 0.2875F) * times);
        if(0 > boost) {
            boost = 0;
        }

        return boost;
    }

    public static double getSpeed() {
        return mc.player == null ? 0 : Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
    }

    public static float getMoveYaw(float yaw) {
        Vector2f from = new Vector2f((float) mc.player.lastTickPosX, (float) mc.player.lastTickPosZ),
                to = new Vector2f((float) mc.player.posX, (float) mc.player.posZ),
                diff = new Vector2f(to.x - from.x, to.y - from.y);

        double x = diff.x, z = diff.y;
        if (x != 0 && z != 0) {
            yaw = (float) Math.toDegrees((Math.atan2(-x, z) + MathHelper.PI2) % MathHelper.PI2);
        }
        return yaw;
    }

    public static double getBaseSpeed() {
        double baseSpeed = 0.272;
        if (mc.player.isPotionActive(Potion.moveSpeed)) {
            final int amplifier = mc.player.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + (0.2 * amplifier);
        }
        return baseSpeed;
    }

    public static void setSpeed(double moveSpeed, float yaw, double strafe, double forward) {
        if (forward != 0.0D) {
            if (strafe > 0.0D) {
                yaw += ((forward > 0.0D) ? -45 : 45);
            } else if (strafe < 0.0D) {
                yaw += ((forward > 0.0D) ? 45 : -45);
            }
            strafe = 0.0D;
            if (forward > 0.0D) {
                forward = 1.0D;
            } else if (forward < 0.0D) {
                forward = -1.0D;
            }
        }
        if (strafe > 0.0D) {
            strafe = 1.0D;
        } else if (strafe < 0.0D) {
            strafe = -1.0D;
        }
        double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
        double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
        mc.player.motionX = forward * moveSpeed * mx + strafe * moveSpeed * mz;
        mc.player.motionZ = forward * moveSpeed * mz - strafe * moveSpeed * mx;
    }

    public static void setSpeed(double moveSpeed) {
        setSpeed(moveSpeed, mc.player.rotationYaw, mc.player.movementInput.moveStrafe, mc.player.movementInput.moveForward);
    }

    public static boolean isBlockUnder() {
        if (mc.player.posY < 0) {
            return false;
        }
        for (int offset = 0; offset < (int) mc.player.posY + 2; offset += 2) {
            AxisAlignedBB bb = mc.player.getEntityBoundingBox().offset(0, -offset, 0);
            if (!mc.world.getCollidingBoundingBoxes(mc.player, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static void strafe() {
        strafe(getSpeed());
    }

    public static void strafe(double movementSpeed) {
        if (mc.player.movementInput.moveForward > 0.0) {
            mc.player.movementInput.moveForward = (float) 1.0;
        } else if (mc.player.movementInput.moveForward < 0.0) {
            mc.player.movementInput.moveForward = (float) -1.0;
        }

        if (mc.player.movementInput.moveStrafe > 0.0) {
            mc.player.movementInput.moveStrafe = (float) 1.0;
        } else if (mc.player.movementInput.moveStrafe < 0.0) {
            mc.player.movementInput.moveStrafe = (float) -1.0;
        }

        if (mc.player.movementInput.moveForward == 0.0 && mc.player.movementInput.moveStrafe == 0.0) {
            mc.player.motionX = 0.0;
            mc.player.motionZ = 0.0;
        }

        if (mc.player.movementInput.moveForward != 0.0 && mc.player.movementInput.moveStrafe != 0.0) {
            mc.player.movementInput.moveForward *= Math.sin(0.6398355709958845);
            mc.player.movementInput.moveStrafe *= Math.cos(0.6398355709958845);
        }

        mc.player.motionX = mc.player.movementInput.moveForward * movementSpeed * -Math.sin(Math.toRadians(mc.player.rotationYaw)) + mc.player.movementInput.moveStrafe * movementSpeed * Math.cos(Math.toRadians(mc.player.rotationYaw));
        mc.player.motionZ = mc.player.movementInput.moveForward * movementSpeed * Math.cos(Math.toRadians(mc.player.rotationYaw)) - mc.player.movementInput.moveStrafe * movementSpeed * -Math.sin(Math.toRadians(mc.player.rotationYaw));
    }

    public static float getDirection(float forward, float strafing, float yaw) {
        if (forward == 0.0 && strafing == 0.0) return yaw;
        boolean reversed = (forward < 0.0);
        float strafingYaw = 90f * ((forward > 0) ? 0.5f : (reversed ? -0.5f : 1));
        if (reversed) yaw += 180;
        if (strafing > 0) {
            yaw -= strafingYaw;
        } else if (strafing < 0) {
            yaw += strafingYaw;
        }
        return yaw;
    }

    public static float getDirection() {
        return getDirection(mc.player.moveForward, mc.player.moveStrafing, mc.player.rotationYaw);
    }

    // Rise 6 MoveUtil :
    public static int depthStriderLevel() {
        return EnchantmentHelper.getDepthStriderModifier(mc.player);
    }
    public static double getAllowedHorizontalDistance() {
        double horizontalDistance;
        boolean useBaseModifiers = false;
        if (mc.player.isInWater() || mc.player.isInLava()) {
            horizontalDistance = MOD_SWIM * WALK_SPEED;

            final int depthStriderLevel = depthStriderLevel();
            if (depthStriderLevel > 0) {
                horizontalDistance *= MOD_DEPTH_STRIDER[depthStriderLevel];
                useBaseModifiers = true;
            }
        } else if (mc.player.isSneaking()) {
            horizontalDistance = MOD_SNEAK * WALK_SPEED;
        } else {
            horizontalDistance = WALK_SPEED;
            useBaseModifiers = true;
        }
        if (useBaseModifiers) {
            if (canSprint(false)) {
                horizontalDistance *= MOD_SPRINTING;
            }

            final ScaffoldWalk scaffold = TopHat.moduleManager.getByClass(ScaffoldWalk.class);

            if (mc.player.isPotionActive(Potion.moveSpeed) && mc.player.getActivePotionEffect(Potion.moveSpeed).getDuration() > 0) {
                horizontalDistance *= 1 + (0.2 * (mc.player.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1));
            }

            if (mc.player.isPotionActive(Potion.moveSlowdown)) {
                horizontalDistance = 0.29;
            }
        }
        return horizontalDistance;
    }
    public static boolean enoughMovementForSprinting() {
        return Math.abs(mc.player.moveForward) >= 0.8F || Math.abs(mc.player.moveStrafing) >= 0.8F;
    }
    public static boolean canSprint(final boolean legit) {
        return (legit ? mc.player.moveForward >= 0.8F
                && !mc.player.isCollidedHorizontally
                && (mc.player.getFoodStats().getFoodLevel() > 6 || mc.player.capabilities.allowFlying)
                && !mc.player.isPotionActive(Potion.blindness)
                && !mc.player.isUsingItem()
                && !mc.player.isSneaking()
                : enoughMovementForSprinting());
    }
    public static final double WALK_SPEED = 0.221;
    public static final double MOD_SPRINTING = 1.3F;
    public static final double MOD_SNEAK = 0.3F;
    public static final double MOD_SWIM = 0.115F / WALK_SPEED;
    public static final double[] MOD_DEPTH_STRIDER = {
            1.0F,
            0.1645F / MOD_SWIM / WALK_SPEED,
            0.1995F / MOD_SWIM / WALK_SPEED,
            1.0F / MOD_SWIM,
    };
}
