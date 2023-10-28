package wtf.tophat.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import wtf.tophat.Client;
import wtf.tophat.events.base.Event;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.modules.impl.player.Scaffold;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.entity.EntityUtil;
import wtf.tophat.utilities.math.MathUtil;
import wtf.tophat.utilities.player.rotations.RotationUtil;
import wtf.tophat.utilities.time.Stopwatch;

import java.util.ArrayList;

@ModuleInfo(name = "Killaura", desc = "kills entities", category = Module.Category.COMBAT)
public class Killaura extends Module {

    private final Stopwatch timer = new Stopwatch();
    private final StringSetting rotate;
    private final NumberSetting minCps, maxCps, minDistance, maxDistance;

    private EntityLivingBase lastTarget;
    private double lastHealth;

    public static boolean blocking;

    public static ArrayList<EntityLivingBase> totalTargets = new ArrayList<EntityLivingBase>();

    public Killaura(){
        Client.settingManager.add(
                rotate = new StringSetting(this, "Rotate", "Pre", "Pre", "Post", "Dynamic"),
                minCps = new NumberSetting(this, "Min CPS", 1, 20, 12, 1),
                maxCps = new NumberSetting(this, "Max CPS", 1, 20, 17, 1),
                minDistance = new NumberSetting(this, "Min Range", 2, 6, 3.4, 1),
                maxDistance = new NumberSetting(this, "Max Range", 2, 6, 4.5, 1)
        );
    }

    @Override
    public void onDisable() {
        blocking = false;
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    public static EntityLivingBase target = null;

    @Listen
    public void onMotion(MotionEvent e) {
        if (Client.moduleManager.getByClass(Scaffold.class).isEnabled())
            return;
        double range = MathUtil.randomNumber(minDistance.get().doubleValue(), maxDistance.get().doubleValue());
        target = EntityUtil.getClosestEntity(range);

        if (target != null) {

            if(mc.getNetHandler().doneLoadingTerrain) {
                if (!totalTargets.contains(target)) {
                    totalTargets.add(target);
                }
            } else {
                totalTargets.clear();
            }

            float[] rotations = RotationUtil.getRotation(target);
            float rot0 = (float) (rotations[0] + MathUtil.randomNumber(-5, 5));
            float rot1 = (float) (rotations[1] + MathUtil.randomNumber(-8, 4));

            switch (rotate.get()){
                case "Pre":
                    if(e.getState() == Event.State.PRE) {
                        e.setYaw(rot0);
                        e.setPitch(rot1);
                    }
                    break;
                case "Post":
                    if(e.getState() == Event.State.POST) {
                        e.setYaw(rot0);
                        e.setPitch(rot1);
                    }
                    break;
                case "Dynamic":
                    if(e.getState() == Event.State.PRE && e.getState() == Event.State.POST){
                        e.setYaw(rot0);
                        e.setPitch(rot1);
                    }
                    break;
            }

            if (e.getState() == Event.State.PRE) {
                if (timer.timeElapsed(1000 / MathUtil.getRandInt(minCps.get().intValue(), maxCps.get().intValue()))) {
                    attack(target);
                    timer.resetTime();
                }
            }
        }
    }

    public void attack(Entity entity) {
        mc.player.swingItem();
        mc.playerController.attackEntity(mc.player, entity);
    }
}