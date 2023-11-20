package tophat.fun.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import tophat.fun.events.Event;
import tophat.fun.events.impl.game.UpdateEvent;
import tophat.fun.events.impl.player.MotionEvent;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.modules.settings.impl.NumberSetting;
import tophat.fun.modules.settings.impl.StringSetting;
import tophat.fun.utilities.Methods;
import tophat.fun.utilities.player.RotationUtil;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ModuleInfo(name = "Aura", desc = "attacks other players around you", category = Module.Category.COMBAT)
public class Aura extends Module {

    private final NumberSetting reach = new NumberSetting(this, "Reach", 0.0, 6.0, 3.0, 1);
    private final NumberSetting aimRange = new NumberSetting(this, "Aim range", 0.0, 6.0, 4.5, 1);
    private final NumberSetting minCPS = new NumberSetting(this, "MinCPS", 0, 20, 8, 0);
    private final NumberSetting maxCPS = new NumberSetting(this, "MaxCPS", 0, 20, 13, 0);

    Entity target;
    int cpsdelay = 0;
    long time = System.currentTimeMillis();
    @Listen
    public void onMotion(MotionEvent event) {
        if (event.getState() == Event.State.PRE) {
            List<Entity> targets = Methods.mc.theWorld.loadedEntityList.stream()
                    .filter(entity -> entity instanceof EntityLivingBase)
                    .map(entity -> (EntityLivingBase) entity)
                    .filter(entityLivingBase -> entityLivingBase.getDistanceToEntity(mc.thePlayer) <= aimRange.get().floatValue()
                            && entityLivingBase != Methods.mc.thePlayer
                            && !entityLivingBase.isDead
                            && entityLivingBase.getHealth() > 0
                            && !entityLivingBase.getName().isEmpty()
                            && !entityLivingBase.getName().contains(" "))
                    .sorted(Comparator.comparingDouble(entity -> entity.getDistanceToEntity(Methods.mc.thePlayer)))
                    .collect(Collectors.toList());
            if(!targets.isEmpty()){
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
}
