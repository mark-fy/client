package wtf.tophat.modules.impl.combat;

import java.util.concurrent.ThreadLocalRandom;

import io.github.nevalackin.radbus.Listen;
import org.lwjgl.input.Mouse;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import wtf.tophat.Client;
import wtf.tophat.events.base.Event;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.utilities.player.PlayerUtil;
import wtf.tophat.utilities.player.rotations.RotationUtil;
import wtf.tophat.utilities.time.TimeUtil;

@ModuleInfo(name = "AimAssist", desc = "assist your aim automatically (goofy)", category = Module.Category.COMBAT)
public class AimAssist extends Module {
    public TimeUtil timer;

    private final BooleanSetting teams;
    private final BooleanSetting swordCheck;
    private final BooleanSetting clickAim;
    private final NumberSetting aimDist;
    private final NumberSetting aimSpeed;

    public AimAssist() {
        Client.settingManager.add(
                teams = new BooleanSetting(this,"Teams Check", true),
                swordCheck = new BooleanSetting(this,"Sword Only", false),
                clickAim = new BooleanSetting(this, "Click Aim", true),
                aimDist = new NumberSetting(this, "Aim Distance", 1.0, 10.0, 3.6, 1),
                aimSpeed = new NumberSetting(this, "Aim Distance", 1.0, 10.0, 3.6, 1)
        );
    }

    @Listen
    public void onMotionUpdate(MotionEvent event) {
        if (event.getState().equals(Event.State.PRE)) {
            final float var = (float) ThreadLocalRandom.current().nextDouble(0.7f, 0.8f);
            for (final Object theObject : mc.world.loadedEntityList) {
                if (theObject instanceof EntityPlayer && theObject != mc.player) {
                    final EntityPlayer entityplayer = (EntityPlayer)theObject;
                    if (swordCheck.get()) {
                        if (mc.player.getHeldItem() == null) {
                            continue;
                        }
                        if (!(mc.player.getHeldItem().getItem() instanceof ItemSword)) {
                            continue;
                        }
                    }
                    if (entityplayer == mc.player) {
                        continue;
                    }
                    if (clickAim.get() && !Mouse.isButtonDown(0)) {
                        continue;
                    }
                    if (PlayerUtil.isOnSameTeam(entityplayer) && teams.get()) {
                        continue;
                    }
                    if (mc.player.getDistanceToEntity(entityplayer) > aimDist.get().doubleValue()) {
                        continue;
                    }
                    final float[] rot = RotationUtil.getNeededRotations(entityplayer);
                    if (mc.player.rotationYaw <= rot[0] - 12.0f || mc.player.rotationYaw >= rot[0] + 12.0f) {
                        if (mc.player.rotationYaw > rot[0]) {
                            mc.player.rotationYaw -= aimSpeed.get().doubleValue() / var;
                        }
                        else {
                            mc.player.rotationYaw += aimSpeed.get().doubleValue() / var;
                        }
                    }
                    mc.player.rotationYaw *= ThreadLocalRandom.current().nextDouble(0.9999f, 1.0001f);
                    mc.player.rotationPitch *= ThreadLocalRandom.current().nextDouble(0.99f, 1.01f);
                }
            }
        }
    }
}