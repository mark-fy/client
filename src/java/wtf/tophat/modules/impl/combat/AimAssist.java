package wtf.tophat.modules.impl.combat;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

import net.minecraft.item.ItemSword;
import wtf.tophat.Client;
import wtf.tophat.events.base.Event;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;

@ModuleInfo(name = "Aim Assist", desc = "assists aiming at players", category = Module.Category.COMBAT)
public class AimAssist extends Module {

    private final NumberSetting horizontalSpeed, verticalSpeed, cameraShake, minRange, maxRange;
    private final BooleanSetting swordCheck, clickAim;

    public AimAssist() {
        Client.settingManager.add(
                horizontalSpeed = new NumberSetting(this, "Horizontal Aim Speed", 0, 7, 0.5, 2),
                verticalSpeed = new NumberSetting(this, "Vertical Aim Speed", 0, 7, 0.5, 2),
                cameraShake = new NumberSetting(this, "Camera Shake Amount", 0, 5, 0.2, 1),
                minRange = new NumberSetting(this, "Min Range", 0, 10, 3, 1),
                maxRange = new NumberSetting(this, "Max Range", 1, 10, 5, 1),
                swordCheck = new BooleanSetting(this,"Sword Only", false),
                clickAim = new BooleanSetting(this, "Click Aim", true)
        );
    }

    @Listen
    public void onMotionUpdate(MotionEvent event) {
        if (event.getState() == Event.State.PRE) {
            List<EntityLivingBase> targets = mc.world.loadedEntityList.stream()
                    .filter(entity -> entity instanceof EntityLivingBase)
                    .map(entity -> (EntityLivingBase) entity)
                    .filter(entityLivingBase -> {
                        double distanceToPlayer = entityLivingBase.getDistanceToEntity(mc.player);
                        return distanceToPlayer >= minRange.get().doubleValue()
                                && distanceToPlayer <= maxRange.get().doubleValue()
                                && entityLivingBase != mc.player
                                && !entityLivingBase.isDead
                                && entityLivingBase.getHealth() > 0
                                && !entityLivingBase.isInvisible()
                                && !entityLivingBase.getName().isEmpty()
                                && !entityLivingBase.getName().contains(" ");
                    })
                    .sorted(Comparator.comparingDouble(entity -> entity.getDistanceToEntity(mc.player)))
                    .collect(Collectors.toList());

            if (!targets.isEmpty()) {
                EntityLivingBase target = targets.get(0);
                aim(target);
            }
        }
    }

    public void aim(EntityLivingBase entityLivingBase) {
        ItemStack heldItem = getPlayer().getHeldItem();
        if (mc.currentScreen == null && heldItem != null) {

            if (swordCheck.get() && heldItem.getItem() instanceof ItemSword) {
                setRotations(entityLivingBase);
            } else {
                setRotations(entityLivingBase);
            }
        }
    }

    public void setRotations(EntityLivingBase e) {
        float[] rotations = getRotations(e);

        if (clickAim.get()) {
            if (Mouse.isButtonDown(0)) {
                getPlayer().rotationYaw = rotations[0];
               getPlayer().rotationPitch = rotations[1];
            }
        } else {
            getPlayer().rotationYaw = rotations[0];
            getPlayer().rotationPitch = rotations[1];
        }
    }

    private float[] getRotations(Entity entity) {
        float rotationSpeedX = horizontalSpeed.get().floatValue();
        float rotationSpeedY = verticalSpeed.get().floatValue();
        float cameraShakeSpeed = (float) (Math.random() * cameraShake.get().floatValue());

        double deltaX = entity.posX - getPlayer().posX;
        double deltaY = entity.posY - 3.5 + entity.getEyeHeight() - getPlayer().posY + getPlayer().getEyeHeight();
        double deltaZ = entity.posZ - getPlayer().posZ;

        float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)));

        float deltaYaw = MathHelper.wrapAngleTo180_float(yaw - getPlayer().rotationYaw);
        float deltaPitch = MathHelper.wrapAngleTo180_float(pitch - getPlayer().rotationPitch);

        deltaYaw = Math.min(rotationSpeedX, Math.max(-rotationSpeedX, deltaYaw));
        deltaPitch = Math.min(rotationSpeedY, Math.max(-rotationSpeedY, deltaPitch));
        yaw = getPlayer().rotationYaw + deltaYaw + cameraShakeSpeed;
        pitch = getPlayer().rotationPitch + deltaPitch + cameraShakeSpeed;

        return new float[]{yaw, pitch};
    }
}