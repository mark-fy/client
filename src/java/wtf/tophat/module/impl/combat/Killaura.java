package wtf.tophat.module.impl.combat;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovingObjectPosition;
import wtf.tophat.Client;
import wtf.tophat.events.handler.PlayerHandler;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.RotationEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.Methods;
import wtf.tophat.utilities.combat.FightUtil;
import wtf.tophat.utilities.math.TimeUtil;
import wtf.tophat.utilities.combat.rotations.RotationUtil;

import java.util.Comparator;
import java.util.List;

@ModuleInfo(name = "Killaura", desc = "attacks entities for you", category = Module.Category.COMBAT)
public class Killaura extends Module {

    private final StringSetting targetMode, priorityMode, aimVector, autoBlockMode, unnecessaryRotations;
    private final NumberSetting attackRange,nearDistance, switchDelay, heightDivisor, minYaw, maxYaw, minPitch, maxPitch, minRandomYaw, maxRandomYaw, minRandomPitch, maxRandomPitch;
    private final BooleanSetting lockOn, mouseFix, heuristics, prediction, snapYaw, snapPitch, skipUnnecessaryRotations, skipIfNear;

    public Killaura() {
        Client.settingManager.add(
                targetMode = new StringSetting(this, "Target Mode", "Single", "Single", "Multi", "Switch"),
                priorityMode = new StringSetting(this, "Priority", "Health", "Health", "Distance"),
                aimVector = new StringSetting(this, "Aim Vector", "Perfect", "Perfect", "Bruteforce", "Head", "Torso", "Feet", "Custom", "Random"),
                autoBlockMode = new StringSetting(this, "Auto Block", "None", "None", "Fake", "Vanilla", "Grim", "Intave"),

                attackRange = new NumberSetting(this, "Attack Range", 0, 6f, 3f, 1),
                switchDelay = new NumberSetting(this, "Switch Delay", 0L, 1000, 300L, 0),

                heightDivisor = new NumberSetting(this, "Height Divisor", 1f ,10f, 2f, 1),

                snapYaw = new BooleanSetting(this, "Snap Yaw", false),
                snapPitch = new BooleanSetting(this, "Snap Pitch", false),

                minYaw = new NumberSetting(this, "Minimum Yaw", 0f ,180f, 40f, 0),
                maxYaw = new NumberSetting(this, "Maximum Yaw", 0f ,180f, 40f, 0),
                minPitch = new NumberSetting(this, "Minimum Pitch", 0f ,180f, 40f, 0),
                maxPitch = new NumberSetting(this, "Maximum Pitch", 0f ,180f, 40f, 0),

                minRandomYaw = new NumberSetting(this, "Minimum Yaw", -1f ,0f, 0f, 2),
                maxRandomYaw = new NumberSetting(this, "Maximum Yaw", 0f ,1f, 0f, 2),
                minRandomPitch = new NumberSetting(this, "Minimum Pitch", -1f ,0f, 0f, 2),
                maxRandomPitch = new NumberSetting(this, "Maximum Pitch", 0f ,1f, 0f, 2),

                lockOn = new BooleanSetting(this, "Lock-view", false),
                mouseFix = new BooleanSetting(this, "Mouse Fix", true),
                heuristics = new BooleanSetting(this, "Heuristics", true),
                prediction = new BooleanSetting(this, "Prediction", false),
                skipUnnecessaryRotations = new BooleanSetting(this, "Necessary Rotations", false),
                unnecessaryRotations = new StringSetting(this, "Necessary Mode", "Both", "Both", "Yaw", "Pitch")
                        .setHidden(skipUnnecessaryRotations::getValue),
                skipIfNear = new BooleanSetting(this, "Skip If Near", true)
                        .setHidden(skipUnnecessaryRotations::getValue),
                nearDistance = new NumberSetting(this, "Near Distance", 0f, 0.5f, 0.5f, 2)
                        .setHidden(() -> skipUnnecessaryRotations.getValue() && skipIfNear.getValue())
        );
    }

    private final class AttackRangeSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            int first = FightUtil.getRange(o1) <= attackRange.getValue().floatValue() ? 0 : 1;
            int second = FightUtil.getRange(o2) <= attackRange.getValue().floatValue() ? 0 : 1;
            return Double.compare(first, second);
        }
    }

    private final class HealthSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(FightUtil.getEffectiveHealth(o1), FightUtil.getEffectiveHealth(o2));
        }
    }

    private final class DistanceSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(Methods.mc.player.getDistanceToEntity(o1), Methods.mc.player.getDistanceToEntity(o2));
        }
    }

    // Targets
    public static Entity target;
    private final TimeUtil switchTimer = new TimeUtil();

    // Rotations
    private float yawRot, pitchRot;

    @Listen
    public final void onRotation(RotationEvent rotationEvent) {
        if (target != null) {
            final float[] rotations = RotationUtil.getRotation(target, aimVector.getValue(), 2f, mouseFix.getValue(), heuristics.getValue(), minRandomYaw.getValue().doubleValue(), maxRandomYaw.getValue().doubleValue(), minRandomPitch.getValue().doubleValue(), maxRandomPitch.getValue().doubleValue(), this.prediction.getValue(), this.minYaw.getValue().floatValue(), this.maxYaw.getValue().floatValue(), this.minPitch.getValue().floatValue(), this.maxPitch.getValue().floatValue(), snapYaw.getValue(), snapPitch.getValue());

            neccessaryRots: {
                if(skipUnnecessaryRotations.getValue()) {
                    MovingObjectPosition movingObjectPosition = mc.objectMouseOver;
                    if(movingObjectPosition == null) {
                        yawRot = rotations[0];
                        pitchRot = rotations[1];
                        break neccessaryRots;
                    }
                    boolean shouldSkip = (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY || movingObjectPosition.entityHit == target) || (skipIfNear.getValue() && FightUtil.getRange(target) <= nearDistance.getValue().doubleValue());
                    if(shouldSkip) {
                        if(unnecessaryRotations.getValue().equalsIgnoreCase("Yaw") || unnecessaryRotations.getValue().equalsIgnoreCase("Both"))
                            yawRot = PlayerHandler.yaw;
                        if(unnecessaryRotations.getValue().equalsIgnoreCase("Pitch") || unnecessaryRotations.getValue().equalsIgnoreCase("Both"))
                            pitchRot = PlayerHandler.pitch;
                    } else {
                        yawRot = rotations[0];
                        pitchRot = rotations[1];
                    }
                } else {
                    yawRot = rotations[0];
                    pitchRot = rotations[1];
                }
            }

            rotationEvent.setYaw(yawRot);
            rotationEvent.setPitch(pitchRot);

            if(this.lockOn.getValue()) {
                Methods.mc.player.rotationYaw = yawRot;
                Methods.mc.player.rotationPitch = pitchRot;
            }
        }
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if(target != null) {
            if(!autoBlockMode.compare("None")) {
                ItemStack currentItem = Methods.mc.player.getHeldItem();
                if (currentItem != null && currentItem.getItem() instanceof ItemSword) {
                    switch (autoBlockMode.getValue()) {
                        case "Vanilla":
                            Methods.mc.playerController.sendUseItem(Methods.mc.player, Methods.mc.world, currentItem);
                            break;
                        case "GrimAC":
                        case "Intave":
                            Methods.mc.playerController.interactWithEntitySendPacket(Methods.mc.player, target);
                            Methods.mc.player.sendQueue.send(new C08PacketPlayerBlockPlacement(currentItem));
                            break;
                    }
                }
            }
            Methods.mc.player.swingItem();
            Methods.mc.playerController.attackEntity(Methods.mc.player, target);
        }
    }

    public static long getAttackSpeed(final ItemStack itemStack, final boolean responsive) {
        double baseSpeed = 250;
        if (!responsive) {
            return Long.MAX_VALUE;
        } else if (itemStack != null) {
            if (itemStack.getItem() instanceof ItemSword) {
                baseSpeed = 625;
            }
            if (itemStack.getItem() instanceof ItemSpade) {
                baseSpeed = 1000;
            }
            if (itemStack.getItem() instanceof ItemPickaxe) {
                baseSpeed = 833.333333333333333;
            }
            if (itemStack.getItem() instanceof ItemAxe) {
                if (itemStack.getItem() == Items.wooden_axe) {
                    baseSpeed = 1250;
                }
                if (itemStack.getItem() == Items.stone_axe) {
                    baseSpeed = 1250;
                }
                if (itemStack.getItem() == Items.iron_axe) {
                    baseSpeed = 1111.111111111111111;
                }
                if (itemStack.getItem() == Items.diamond_axe) {
                    baseSpeed = 1000;
                }
                if (itemStack.getItem() == Items.golden_axe) {
                    baseSpeed = 1000;
                }
            }
            if (itemStack.getItem() instanceof ItemHoe) {
                if (itemStack.getItem() == Items.wooden_hoe) {
                    baseSpeed = 1000;
                }
                if (itemStack.getItem() == Items.stone_hoe) {
                    baseSpeed = 500;
                }
                if (itemStack.getItem() == Items.iron_hoe) {
                    baseSpeed = 333.333333333333333;
                }
                if (itemStack.getItem() == Items.diamond_hoe) {
                    baseSpeed = 250;
                }
                if (itemStack.getItem() == Items.golden_hoe) {
                    baseSpeed = 1000;
                }
            }
        }
        if (Methods.mc.player.isPotionActive(Potion.digSpeed)) {
            baseSpeed *= 1.0 + 0.1 * (Methods.mc.player.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1);
        }
        return Math.round(baseSpeed);
    }

    @Override
    public void onEnable() {
        target = null;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        Methods.mc.settings.keyBindUseItem.pressed = false;
        target = null;
        super.onDisable();
    }
}
