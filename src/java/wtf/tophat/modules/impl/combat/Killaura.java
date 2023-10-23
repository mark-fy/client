package wtf.tophat.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import wtf.tophat.Client;
import wtf.tophat.events.base.Event;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.RotationEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.DividerSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.Methods;
import wtf.tophat.utilities.player.rotations.RotationUtil;
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
                autoBlockMode = new StringSetting(this, "Auto-Block Mode","None", "None", "Fake", "Vanilla"),

                values = new DividerSetting(this, "General Settings"),
                distance = new NumberSetting(this, "Distance", 0f ,10f, 4f, 1),
                cps = new NumberSetting(this,"CPS", 1, 20, 12, 0),

                toggleables = new DividerSetting(this, "Other Settings"),
                clientSwing = new BooleanSetting(this, "Client-side Swing", true),
                onlyPlayers = new BooleanSetting(this, "Attack only Players", true),
                lockView = new BooleanSetting(this, "Lock-view", false)
        );
    }

    // Targets
    public List<EntityLivingBase> targets = null;
    private final TimeUtil attackTimer = new TimeUtil();

    // Bots
    private AntiBot antiBot;

    @Listen
    public void onMotion(MotionEvent event) {
        if (event.getState() == Event.State.PRE) {
            targets = mc.world.loadedEntityList.stream()
                    .filter(entity -> entity instanceof EntityLivingBase)
                    .map(entity -> (EntityLivingBase) entity)
                    .collect(Collectors.toList());

            targets = targets.stream().filter(
                    ent -> ent.getDistanceToEntity(mc.player) < distance.get().floatValue()
                            && ent != mc.player
                            && !ent.isDead
                            && !ent.isInvisible()
                            && ent.getHealth() > 0
                            && !ent.getName().isEmpty()
                            && !ent.getName().equalsIgnoreCase(""))
                    .collect(Collectors.toList());

            switch (sortingMode.get()) {
                case "Distance":
                    targets.sort(Comparator.comparingDouble(entity -> entity.getDistanceToEntity(mc.player)));
                    break;
                case "Health":
                    targets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
                    break;
            }

            if (antiBot.isEnabled()) {
                switch (antiBot.mode.get()) {
                    case "Custom":
                        if (antiBot.flying.get() && !targets.isEmpty() && targets.get(0).ticksExisted > 20) {
                            EntityLivingBase target = targets.get(0);

                            if (target.isAirBorne) {
                                targets.remove(0);
                            }

                            if(target.posY % 1.0 > 0.9) {
                                targets.remove(0);
                            }
                        }
                        break;
                }
            }

            if(onlyPlayers.get())
                targets = targets.stream().filter(EntityPlayer.class::isInstance).collect(Collectors.toList());

            if(!targets.isEmpty()) {
                EntityLivingBase target = targets.get(0);

                block(autoBlockMode.get());

                if(attackTimer.elapsed(1000 / cps.get().intValue(), true) && mc.currentScreen == null) {
                    swing(clientSwing.get(), target);
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

        if(!autoBlockMode.is("Fake"))
            return false;

        return mc.player.swingProgress > 0;
    }

    @Listen
    public void onRotations(RotationEvent event) {
        if (getPlayer() == null || getWorld() == null)
            return;

        if(!targets.isEmpty()) {
            EntityLivingBase target = targets.get(0);

            event.setYaw(RotationUtil.getRotation(target)[0]);
            event.setPitch(RotationUtil.getRotation(target)[1]);

            if(lockView.get()) {
                mc.player.rotationYaw = RotationUtil.getRotation(target)[0];
                mc.player.rotationPitch = RotationUtil.getRotation(target)[1];
            }
        }
    }

    @Override
    public void onEnable() {
        targets = null;
        antiBot = Client.moduleManager.getByClass(AntiBot.class);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        targets = null;
        antiBot = null;
        super.onDisable();
    }
}
