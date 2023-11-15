package tophat.fun.modules.impl.combat;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import org.lwjgl.input.Keyboard;
import tophat.fun.events.Event;
import tophat.fun.events.impl.player.MotionEvent;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;
import tophat.fun.utilities.others.TimeUtil;
import tophat.fun.utilities.player.PlayerUtil;
import tophat.fun.utilities.player.RotationUtil;

@ModuleInfo(name = "Aura", desc = "kills entity", category = Module.Category.COMBAT, bind = Keyboard.KEY_K)
public class Aura extends Module {
    public static Entity target;
    public static float yaw;
    public static float pitch;
    public static float lastYaw;
    public static float lastPitch;
    private boolean blocking;
    private int targetIndex;
    public TimeUtil timeUtils = new TimeUtil();
    public Random random = new Random();
    // public StringSetting mode = new StringSetting(this,"Mode", "Single", "Single", "Switch", "Multi");
    // public StringSetting rotations = new StringSetting(this,"Rotations", "Default", "Default", "Custom", "Smooth", "Derp", "None");
    // public NumberSetting maxrot = new NumberSetting(this,"Max Rotation", 0.0, 180.0, 180.0, 1);
    // public NumberSetting minrot = new NumberSetting(this,"Min Rotation", 0.0, 180.0, 180.0, 1);
    // public StringSetting sort = new StringSetting(this,"Sort", "Distance", "Distance", "Health", "Hurttime", "FOV", "Armor");
    // public StringSetting blockMode = new StringSetting(this,"Block Mode", "Vanilla", "None", "Vanilla", "Hypixel", "Interact", "Legit", "Fake", "NCP");
    // public NumberSetting aimRange = new NumberSetting(this,"Aim range", 1.0, 6.0, 4.5, 1);

    // public NumberSetting range = new NumberSetting(this,"Range", 1.0, 6.0, 3.0, 1);
    // public NumberSetting aps = new NumberSetting(this,"Aps", 1.0, 20.0, 10.0, 1);
    // public BooleanSetting ondead = new BooleanSetting(this,"Disable on death", true);
    // public BooleanSetting thruwalls = new BooleanSetting(this,"Trought Walls", false);
    // public BooleanSetting keepsprint = new BooleanSetting(this,"Keep Sprint", true);
    // public BooleanSetting invisibles = new BooleanSetting(this,"Invisibles", false);
    // public BooleanSetting thePlayers = new BooleanSetting(this,"thePlayers", true);
    // public BooleanSetting nonthePlayers = new BooleanSetting(this,"Non thePlayers", false);
    // public BooleanSetting dead = new BooleanSetting(this,"Dead", false);
    // public BooleanSetting teams = new BooleanSetting(this,"Teams", false);

    @Listen
    public void onMotion(MotionEvent event) {
        if(event.getState() == Event.State.PRE) {
            for (Entity e : mc.theWorld.loadedEntityList) {
                if (!this.allowedToRotate(e)) continue;
                target = e;
                this.updateTarget();
            }
            if (!this.allowedToRotate(target)) {
                target = null;
                this.unblock();
            }
            if (target != null) {
                float[] rotation = RotationUtil.getRotationsNeeded(target);
                event.setYaw(rotation[0]);
                event.setPitch(rotation[1]);


                mc.thePlayer.rotationYawHead = rotation[0];
                mc.thePlayer.renderYawOffset = rotation[0];
                mc.thePlayer.rotationPitchHead = rotation[1];

                //lastYaw = mc.thePlayer.rotationYaw;
                //lastPitch = mc.thePlayer.rotationPitch;
                /*
                if (!this.thruwalls.get() && !mc.thePlayer.canEntityBeSeen(target)) {
                    return;
                }
                 */
                if (this.timeUtils.elapsed((long) (1000 / 14.7)/*CPS*/) && allowedToAttack(target)) {
                    this.Attack(target);
                    this.timeUtils.reset();
                }
            }
        }
    }

    @Override
    public void onEnable() {
        this.blocking = mc.gameSettings.keyBindUseItem.isKeyDown();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        if (PlayerUtil.isHoldingSword()) {
            this.unblock();
        }
        super.onDisable();
    }

    private void Attack(Entity e) {
        mc.thePlayer.swingItem();
        /*
        case "Single": {
            if (this.keepsprint.get()) {
                mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
            } else {
                mc.playerController.attackEntity(mc.thePlayer, target);
            }
            if (!(mc.thePlayer.fallDistance > 0.0f)) break;
            mc.thePlayer.onCriticalHit(target);
            break;
        }
         */
            List<EntityLivingBase> entities = this.getTargets();
            if (entities.size() >= this.targetIndex) {
                this.targetIndex = 0;
            }
            if (entities.isEmpty()) {
                this.targetIndex = 0;
                return;
            }
            EntityLivingBase entity = entities.get(this.targetIndex);
            //if (this.keepsprint.get()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
            //} else {
            //    mc.playerController.attackEntity(mc.thePlayer, entity);
            //}
            if (mc.thePlayer.fallDistance > 0.0f) {
                mc.thePlayer.onCriticalHit(target);
            }
            ++this.targetIndex;
            //break;
        /*
        case "Multi": {
            for (EntityLivingBase entity : this.getTargets()) {
                if (this.keepsprint.get()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                } else {
                    mc.playerController.attackEntity(mc.thePlayer, entity);
                }
                if (!(mc.thePlayer.fallDistance > 0.0f)) continue;
                mc.thePlayer.onCriticalHit(entity);
            }
            break;
        }
         */
    }

    public List<EntityLivingBase> getTargets() {
        List<EntityLivingBase> entities = mc.theWorld.loadedEntityList.stream().filter(entity -> entity instanceof EntityLivingBase).map(entity -> (EntityLivingBase)entity).filter(entity -> {
            if (entity.isInvisible()) {
                return false;
            }
            if (entity.isDead) {
                return false;
            }
            if (entity.deathTime != 0) {
                return false;
            }
            if (entity.ticksExisted < 2) {
                return false;
            }
            if (entity instanceof EntityPlayer) {
                EntityPlayer EntityPlayer = (EntityPlayer)entity;
            }
            return mc.thePlayer != entity;
        }).filter(entity -> {
            double girth = 0.5657;
            return mc.thePlayer.getDistanceToEntity(entity) - 0.5657 < 3.6 /*Aim Range*/;
        }).sorted(Comparator.comparingDouble(entity -> {
            return mc.thePlayer.getDistanceSqToEntity(entity);
            /*
            switch (this.sort.get()) {
                case "Distance": {
                    return mc.thePlayer.getDistanceSqToEntity(entity);
                }
                case "Health": {
                    return entity.getHealth() + entity.getAbsorptionAmount();
                }
                case "Hurttime": {
                    return entity.hurtTime;
                }
                case "FOV": {
                    return Math.abs(MathHelper.wrapAngleTo180_float(Math.abs(RotationUtil.getRotation(entity)[0] - getYaw())));
                }
                case "Armor": {
                    return entity.getTotalArmorValue();
                }
            }
            return -1.0;
             */
        })).sorted(Comparator.comparing(entity -> entity instanceof EntityPlayer)).collect(Collectors.toList());
        return entities;
    }

    private void updateTarget() {
        List<EntityLivingBase> entities = this.getTargets();
        target = entities.size() > 0 ? entities.get(0) : null;
    }

    private boolean allowedToAttack(Entity entity) {
        this.getTargets();
        return entity instanceof EntityLivingBase && entity != mc.thePlayer && mc.thePlayer.getDistanceToEntity(entity) <= 3.6 /*Range*/;
    }
    private boolean allowedToRotate(Entity entity) {
        this.getTargets();
        return entity instanceof EntityLivingBase && entity != mc.thePlayer && mc.thePlayer.getDistanceToEntity(entity) <= 3.6 /*Aim Range*/;
    }

    private void block() {
        this.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
        mc.gameSettings.keyBindUseItem.pressed = true;
        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0.0f, 0.0f, 0.0f));
        this.blocking = true;
    }

    private void unblock() {
        if (this.blocking) {
            mc.gameSettings.keyBindUseItem.pressed = false;
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            this.blocking = false;
        }
    }

    public void sendUseItem(EntityPlayer thePlayerIn, World worldIn, ItemStack itemStackIn) {
        if (mc.playerController.getCurrentGameType() != WorldSettings.GameType.SPECTATOR) {
            mc.playerController.syncCurrentPlayItem();
            int i = itemStackIn.stackSize;
            ItemStack itemstack = itemStackIn.useItemRightClick(worldIn, thePlayerIn);
            if (itemstack != itemStackIn || itemstack.stackSize != i) {
                thePlayerIn.inventory.mainInventory[thePlayerIn.inventory.currentItem] = itemstack;
                if (itemstack.stackSize == 0) {
                    thePlayerIn.inventory.mainInventory[thePlayerIn.inventory.currentItem] = null;
                }
            }
        }
    }
}