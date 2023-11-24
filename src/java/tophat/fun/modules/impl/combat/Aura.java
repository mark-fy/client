package tophat.fun.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.EntityLivingBase;
import tophat.fun.Client;
import tophat.fun.events.Event;
import tophat.fun.events.impl.game.UpdateEvent;
import tophat.fun.events.impl.player.MotionEvent;
import tophat.fun.events.impl.render.Render3DEvent;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.modules.settings.impl.BooleanSetting;
import tophat.fun.modules.settings.impl.NumberSetting;
import tophat.fun.modules.settings.impl.StringSetting;
import tophat.fun.utilities.Methods;
import tophat.fun.utilities.math.MathUtil;
import tophat.fun.utilities.player.RotationUtil;
import tophat.fun.utilities.render.esp.EntityESPUtil;

import java.awt.*;
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
    private final BooleanSetting targetESP = new BooleanSetting(this, "TargetESP", false);

    public Aura() {
        Client.INSTANCE.settingManager.add(
                sorting,reach,aimRange,minCPS,maxCPS, targetESP
        );
    }

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
                    .filter(entityLivingBase -> entityLivingBase.getDistanceToEntity(mc.thePlayer) <= aimRange.get().floatValue()
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
            mc.thePlayer.rotationYaw = RotationUtil.getRotationsNeeded(target)[0];
            mc.thePlayer.rotationPitch = RotationUtil.getRotationsNeeded(target)[1];
            if(mc.thePlayer.getDistanceToEntity(target) <= reach.get().floatValue() && mc.pointedEntity == target && time <= System.currentTimeMillis() + cpsdelay){
                mc.playerController.attackEntity(mc.thePlayer, target);
                mc.thePlayer.swingItem();
                time = System.currentTimeMillis();
            }
        }
    }

    @Listen
    public void onRender(Render3DEvent event) {
        if(target != null && !target.isDead) {
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
