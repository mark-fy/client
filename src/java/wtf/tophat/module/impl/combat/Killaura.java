package wtf.tophat.module.impl.combat;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import wtf.tophat.Client;
import wtf.tophat.events.base.Event;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.RotationEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.DividerSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.Methods;
import wtf.tophat.utilities.combat.rotations.RotationUtil;
import wtf.tophat.utilities.math.TimeUtil;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ModuleInfo(name = "Killaura", desc = "kills entities", category = Module.Category.COMBAT)
public class Killaura extends Module {

    private final DividerSetting modes, values, toggleables;
    private final StringSetting sortingMode, autoBlockMode;
    private final NumberSetting distance, cps;
    private final BooleanSetting clientSwing, lockView, onlyPlayers;

    public Killaura() {
        Client.settingManager.add(
                modes = new DividerSetting(this, "Mode Settings"),
                sortingMode = new StringSetting(this, "Sorting Mode", "Distance", "Distance", "Health"),
                autoBlockMode = new StringSetting(this, "Auto-Block Mode","None", "Fake", "Vanilla"),

                values = new DividerSetting(this, "General Settings"),
                distance = new NumberSetting(this, "Distance", 0f ,10f, 4f, 1),
                cps = new NumberSetting(this,"CPS", 0, 20, 12, 0),

                toggleables = new DividerSetting(this, "Other Settings"),
                clientSwing = new BooleanSetting(this, "Client-side Swing", true),
                onlyPlayers = new BooleanSetting(this, "Attack only Players", true),
                lockView = new BooleanSetting(this, "Lock-view", false)
        );
    }

    // Targets
    public List<EntityLivingBase> targets = null;
    private final TimeUtil attackTimer = new TimeUtil();

    @Listen
    public void onMotion(MotionEvent event) {
        if (event.getState() == Event.State.PRE) {
            targets = mc.world.loadedEntityList.stream()
                    .filter(entity -> entity instanceof EntityLivingBase)
                    .map(entity -> (EntityLivingBase) entity)
                    .collect(Collectors.toList());

            targets = targets.stream().filter(
                    ent -> ent.getDistanceToEntity(mc.player) < distance.getValue().floatValue()
                            && ent != mc.player
                            && !ent.isDead
                            && !ent.isInvisible()
                            && ent.getHealth() > 0
                            && !ent.getName().isEmpty()
                            && !ent.getName().equalsIgnoreCase(""))
                    .collect(Collectors.toList());

            switch (sortingMode.getValue()) {
                case "Distance":
                    targets.sort(Comparator.comparingDouble(entity -> entity.getDistanceToEntity(mc.player)));
                    break;
                case "Health":
                    targets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
                    break;
            }

            if(onlyPlayers.getValue())
                targets = targets.stream().filter(EntityPlayer.class::isInstance).collect(Collectors.toList());

            if(!targets.isEmpty()) {
                EntityLivingBase target = targets.get(0);

                block(autoBlockMode.getValue());

                if(attackTimer.elapsed(1000 / cps.getValue().intValue(), true) && mc.currentScreen == null) {
                    swing(clientSwing.getValue(), target);
                }
            }
        }
    }

    private void swing(boolean animation, Entity target) {
        if(animation) {
            mc.player.swingItem();
            mc.player.sendQueue.send(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
        } else {
            mc.player.sendQueue.send(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
        }
    }

    private void block(String mode) {
        ItemStack currentItem = Methods.mc.player.getHeldItem();
        if (currentItem != null && currentItem.getItem() instanceof ItemSword && !mode.equalsIgnoreCase("None")) {
            switch (mode) {
                case "Vanilla":
                    Methods.mc.playerController.sendUseItem(Methods.mc.player, Methods.mc.world, currentItem);
                    break;
            }
        }
    }

    public boolean shouldBlock() {
        if(mc.player.getHeldItem() == null)
            return false;

        if(!autoBlockMode.compare("Fake"))
            return false;

        return mc.player.swingProgress > 0;
    }

    @Listen
    public void onRotations(RotationEvent event) {
        if(!targets.isEmpty()) {
            EntityLivingBase target = targets.get(0);

            event.setYaw(RotationUtil.getRotation(target)[0]);
            event.setPitch(RotationUtil.getRotation(target)[1]);

            if(lockView.getValue()) {
                mc.player.rotationYaw = RotationUtil.getRotation(target)[0];
                mc.player.rotationPitch = RotationUtil.getRotation(target)[1];
            }
        }
    }

    @Override
    public void onEnable() {
        targets = null;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        targets = null;
        super.onDisable();
    }
}
