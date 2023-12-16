package tophat.fun.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import tophat.fun.events.Event;
import tophat.fun.events.impl.game.UpdateEvent;
import tophat.fun.events.impl.player.MotionEvent;
import tophat.fun.events.impl.player.RotationEvent;
import tophat.fun.events.impl.render.Render3DEvent;
import tophat.fun.modules.base.Module;
import tophat.fun.modules.base.ModuleInfo;
import tophat.fun.modules.base.settings.impl.BooleanSetting;
import tophat.fun.modules.base.settings.impl.NumberSetting;
import tophat.fun.modules.base.settings.impl.StringSetting;
import tophat.fun.utilities.Methods;
import tophat.fun.utilities.math.MathUtil;
import tophat.fun.utilities.player.PlayerUtil;
import tophat.fun.utilities.player.RotationUtil;
import tophat.fun.utilities.render.esp.EntityESPUtil;

import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ModuleInfo(name = "Aura", desc = "attacks other players around you", category = Module.Category.COMBAT)
public class Aura extends Module {

    private final StringSetting sorting = new StringSetting(this, "Sorting", "Distance", "Distance", "Health", "FOV");
    private final NumberSetting reach = new NumberSetting(this, "Reach", 0.0, 6.0, 3.0, 1);
    private final NumberSetting aimRange = new NumberSetting(this, "AimRange", 0.0, 6.0, 4.5, 1);
    private final NumberSetting minCPS = new NumberSetting(this, "MinCPS", 0, 20, 8, 0);
    private final NumberSetting maxCPS = new NumberSetting(this, "MaxCPS", 0, 20, 13, 0);
    private final BooleanSetting attackWhenLooking = new BooleanSetting(this, "AttackWhenLooking", false);
    private final BooleanSetting targetESP = new BooleanSetting(this, "TargetESP", false);
    private final BooleanSetting lockAim = new BooleanSetting(this, "LockAim", false);
    private final BooleanSetting randomizeRotations = new BooleanSetting(this, "RandomizeRotations", false);
    private final BooleanSetting mouseFix = new BooleanSetting(this, "GCDFix", true);

    public static EntityLivingBase target;
    int cpsdelay = 0;
    long time = System.currentTimeMillis();

    @Override
    public void onDisable() {
        target = null;
        cpsdelay = 0;
        super.onDisable();
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if (event.getState() == Event.State.PRE) {
            List<EntityLivingBase> targets = Methods.mc.theWorld.loadedEntityList.stream()
                    .filter(entity -> entity instanceof EntityLivingBase)
                    .map(entity -> (EntityLivingBase) entity)
                    .filter(entityLivingBase -> PlayerUtil.getRange(entityLivingBase) <= aimRange.get().floatValue()
                            && entityLivingBase != Methods.mc.thePlayer
                            && !entityLivingBase.isDead
                            && entityLivingBase.getHealth() > 0
                            && !entityLivingBase.getName().isEmpty()
                            && !entityLivingBase.getName().contains(" "))
                    .sorted(getComparator(sorting.get()))
                    .collect(Collectors.toList());

            if (!targets.isEmpty()) {
                target = targets.get(0);
            }
        }
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        cpsdelay = (int) ((Math.random() * (maxCPS.value.intValue() - minCPS.value.intValue())) + minCPS.value.intValue());

        if(target != null && !target.isDead){
            if(PlayerUtil.getRange(target) <= reach.get().floatValue() && time <= System.currentTimeMillis() + cpsdelay){
                if(attackWhenLooking.get() && mc.pointedEntity != target) {
                    return;
                }

                mc.playerController.attackEntity(mc.thePlayer, target);
                mc.thePlayer.swingItem();

                time = System.currentTimeMillis();
            }
        }
    }

    @Listen
    public void onRotation(RotationEvent event) {
        if(event.getState() == Event.State.PRE) {
            if(target != null && !target.isDead){
                float[] rotations = getRotations();

                if(lockAim.get()) {
                    mc.thePlayer.rotationYaw = rotations[0];
                    mc.thePlayer.rotationPitch = rotations[1];
                }

                event.setYaw(rotations[0]);
                event.setPitch(rotations[1]);
            }
        }
    }

    private float[] getRotations() {
        // Initial values
        final double eyeX = mc.thePlayer.posX;
        final double eyeY = mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
        final double eyeZ = mc.thePlayer.posZ;

        // Finding ideal aim vector
        Vec3 aimVector = RotationUtil.getBestVector(mc.thePlayer.getPositionEyes(1f), target.getEntityBoundingBox());

        double entityX = aimVector.xCoord;
        double entityY = aimVector.yCoord;
        double entityZ = aimVector.zCoord;

        // Randomizing rotations, bypasses Intave, I don't know about Polar
        if(randomizeRotations.get()) {
            try {
                entityX += SecureRandom.getInstanceStrong().nextDouble() * 0.1;
                entityY += SecureRandom.getInstanceStrong().nextDouble() * 0.1;
                entityZ += SecureRandom.getInstanceStrong().nextDouble() * 0.1;
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        // Rotation calculation
        double x = entityX - eyeX;
        double y = entityY - eyeY;
        double z = entityZ - eyeZ;

        double angle = MathHelper.sqrt_double(x * x + z * z);

        float yawAngle = (float) (MathHelper.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitchAngle = (float) (-(MathHelper.atan2(y, angle) * 180.0D / Math.PI));

        // Adding sensitivity to the rotations
        if(mouseFix.get()) {
            float[] fixed = RotationUtil.applyMouseFix(yawAngle, pitchAngle);
            yawAngle = fixed[0];
            pitchAngle = fixed[1];
        }

        return new float[] {yawAngle, pitchAngle};
    }

    @Listen
    public void onRender(Render3DEvent event) {
        if(target != null && !target.isDead && targetESP.get()) {
            double x = MathUtil.interpolate(target.posX, target.lastTickPosX) - mc.getRenderManager().renderPosX;
            double y = MathUtil.interpolate(target.posY, target.lastTickPosY) - mc.getRenderManager().renderPosY;
            double z = MathUtil.interpolate(target.posZ, target.lastTickPosZ) - mc.getRenderManager().renderPosZ;

            EntityESPUtil.renderBoxESP(target, x, y, z, target.hurtTime > 0 ? new Color(255, 0, 0, 100) : new Color(0, 255, 0, 100));
        }
    }

    private Comparator<EntityLivingBase> getComparator(String sortingOption) {
        switch (sortingOption.toLowerCase()) {
            case "health":
                return Comparator.comparingDouble(EntityLivingBase::getHealth);
            case "fov":
                return Comparator.comparingDouble(entity -> {
                    double deltaX = entity.posX - Methods.mc.thePlayer.posX;
                    double deltaZ = entity.posZ - Methods.mc.thePlayer.posZ;
                    double angle = Math.toDegrees(Math.atan2(deltaZ, deltaX));
                    double playerYaw = Math.toDegrees(Methods.mc.thePlayer.rotationYaw) % 360;
                    if (playerYaw < 0) {
                        playerYaw += 360;
                    }
                    return Math.abs(playerYaw - angle);
                });
            case "distance":
            default:
                return Comparator.comparingDouble(entity -> entity.getDistanceToEntity(Methods.mc.thePlayer));
        }
    }
}
