package wtf.tophat.client.modules.impl.combat;

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
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.base.Event;
import wtf.tophat.client.events.impl.MotionEvent;
import wtf.tophat.client.events.impl.RotationEvent;
import wtf.tophat.client.events.impl.UpdateEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.settings.impl.StringSetting;
import wtf.tophat.client.utilities.math.MathUtil;
import wtf.tophat.client.utilities.math.time.TimeUtil;
import wtf.tophat.client.utilities.player.PlayerUtil;
import wtf.tophat.client.utilities.player.rotations.FixedRotations;
import wtf.tophat.client.utilities.player.rotations.RotationUtil;

@ModuleInfo(name = "Killaura", desc = "kills entity", category = Module.Category.COMBAT)
public class Killaura extends Module {
    public static Entity target;
    public static float yaw;
    public static float pitch;
    public static float lastYaw;
    public static float lastPitch;
    private boolean blocking;
    private int targetIndex;
    public TimeUtil timeUtils = new TimeUtil();
    public Random random = new Random();
    public StringSetting mode = new StringSetting(this,"Mode", "Single", "Single", "Switch", "Multi");
    public StringSetting rotations = new StringSetting(this,"Rotations", "Default", "Default", "Custom", "Smooth", "Derp", "None");
    public NumberSetting maxrot = new NumberSetting(this,"Max Rotation", 0.0, 180.0, 180.0, 1);
    public NumberSetting minrot = new NumberSetting(this,"Min Rotation", 0.0, 180.0, 180.0, 1);
    public StringSetting sort = new StringSetting(this,"Sort", "Distance", "Distance", "Health", "Hurttime");
    public StringSetting blockMode = new StringSetting(this,"Block Mode", "Vanilla", "None", "Vanilla", "Hypixel", "Interact", "Legit", "Fake", "NCP");
    public NumberSetting range = new NumberSetting(this,"Range", 1.0, 6.0, 3.0, 1);
    public NumberSetting aps = new NumberSetting(this,"Aps", 1.0, 20.0, 10.0, 1);
    public BooleanSetting ondead = new BooleanSetting(this,"Disable on death", true);
    public BooleanSetting thruwalls = new BooleanSetting(this,"Trought Walls", false);
    public BooleanSetting keepsprint = new BooleanSetting(this,"Keep Sprint", true);
    public BooleanSetting invisibles = new BooleanSetting(this,"Invisibles", false);
    public BooleanSetting players = new BooleanSetting(this,"Players", true);
    public BooleanSetting nonPlayers = new BooleanSetting(this,"Non Players", false);
    public BooleanSetting dead = new BooleanSetting(this,"Dead", false);
    public BooleanSetting teams = new BooleanSetting(this,"Teams", false);

    public Killaura() {
        TopHat.settingManager.add(
            mode,
            rotations,
            maxrot,
            minrot,
            blockMode,
            sort,
            range,
            aps,
            ondead,
            thruwalls,
            keepsprint,
            invisibles,
            players,
            nonPlayers,
            dead,
            teams    
        );
    }

    @Listen
    public void onRotation(RotationEvent event){
        float[] rotation = RotationUtil.getRotation(target);
        event.setYaw(rotation[0]);
        event.setPitch(rotation[1]);
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if(event.getState().equals(Event.State.PRE)) {
            for (Entity e : Killaura.mc.world.loadedEntityList) {
                if (!this.allowedToAttack(e)) continue;
                target = e;
                this.updateTarget();
            }
            if (!this.allowedToAttack(target)) {
                target = null;
                this.unblock();
            }
            if (target != null) {
                lastYaw = Killaura.mc.player.rotationYaw;
                lastPitch = Killaura.mc.player.rotationPitch;
                if (!this.thruwalls.get() && !Killaura.mc.player.canEntityBeSeen(target)) {
                    return;
                }
                if (this.timeUtils.elapsed(1000 / this.aps.get().intValue())) {
                    this.Attack(target);
                    this.timeUtils.reset();
                }
            }
        }
    }

    @Override
    public void onEnable() {
        this.blocking = Killaura.mc.settings.keyBindUseItem.isKeyDown();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (PlayerUtil.isHoldingSword()) {
            this.unblock();
        }
        super.onDisable();
    }

    private void Attack(Entity e) {
        Killaura.mc.player.swingItem();
        switch (blockMode.get()) {
            case "None": {
                break;
            }
            case "Legit": {
                if (PlayerUtil.isHoldingSword()) {
                    if (Killaura.mc.player.ticksExisted % 15 == 0) {
                        this.block();
                    } else {
                        this.unblock();
                    }
                }
                this.timeUtils.reset();
                break;
            }
            case "Vanilla": {
                if (PlayerUtil.isHoldingSword()) {
                    this.block();
                } else {
                    this.unblock();
                    break;
                }
            }
            case "Hypixel": {
                if (!(target instanceof EntityPlayer)) break;
                EntityPlayer player = (EntityPlayer)target;
                if (PlayerUtil.isHoldingSword()) {
                    if (player.hurtTime >= 5 || Killaura.mc.player.ticksExisted % 3 != 1) break;
                    sendPacket(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, Killaura.mc.player.getHeldItem(), 0.0f, 0.0f, 0.0f));
                    break;
                }
                sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                break;
            }
            case "Interact": {
                if (!PlayerUtil.isHoldingSword()) break;
                Killaura.mc.playerController.interactWithEntitySendPacket(Killaura.mc.player, target);
                this.sendPacket(new C08PacketPlayerBlockPlacement(Killaura.mc.player.getHeldItem()));
                break;
            }
            case "NCP": {
                if (!PlayerUtil.isHoldingSword()) break;
                this.sendPacket(new C08PacketPlayerBlockPlacement(Killaura.mc.player.getHeldItem()));
            }
        }
        switch (this.mode.get()) {
            case "Single": {
                if (this.keepsprint.get()) {
                    this.sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                } else {
                    Killaura.mc.playerController.attackEntity(Killaura.mc.player, target);
                }
                if (!(Killaura.mc.player.fallDistance > 0.0f)) break;
                Killaura.mc.player.onCriticalHit(target);
                break;
            }
            case "Switch": {
                List<EntityLivingBase> entities = this.getTargets();
                if (entities.size() >= this.targetIndex) {
                    this.targetIndex = 0;
                }
                if (entities.isEmpty()) {
                    this.targetIndex = 0;
                    return;
                }
                EntityLivingBase entity = entities.get(this.targetIndex);
                if (this.keepsprint.get()) {
                    this.sendPacket(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                } else {
                    Killaura.mc.playerController.attackEntity(Killaura.mc.player, entity);
                }
                if (Killaura.mc.player.fallDistance > 0.0f) {
                    Killaura.mc.player.onCriticalHit(target);
                }
                ++this.targetIndex;
                break;
            }
            case "Multi": {
                for (EntityLivingBase entity : this.getTargets()) {
                    if (this.keepsprint.get()) {
                        this.sendPacket(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                    } else {
                        Killaura.mc.playerController.attackEntity(Killaura.mc.player, entity);
                    }
                    if (!(Killaura.mc.player.fallDistance > 0.0f)) continue;
                    Killaura.mc.player.onCriticalHit(entity);
                }
                break;
            }
        }
    }

    public List<EntityLivingBase> getTargets() {
        List<EntityLivingBase> entities = Killaura.mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityLivingBase).map(entity -> (EntityLivingBase)entity).filter(entity -> {
            if (entity instanceof EntityPlayer && !this.players.get()) {
                return false;
            }
            if (!(entity instanceof EntityPlayer) && !this.nonPlayers.get()) {
                return false;
            }
            if (entity.isInvisible() && !this.invisibles.get()) {
                return false;
            }
            if (entity.isDead && !this.dead.get()) {
                return false;
            }
            if (entity.deathTime != 0 && !this.dead.get()) {
                return false;
            }
            if (entity.ticksExisted < 2) {
                return false;
            }
            if (entity instanceof EntityPlayer) {
                EntityPlayer entityPlayer = (EntityPlayer)entity;
            }
            return Killaura.mc.player != entity;
        }).filter(entity -> {
            double girth = 0.5657;
            return Killaura.mc.player.getDistanceToEntity(entity) - 0.5657 < range.get().doubleValue();
        }).sorted(Comparator.comparingDouble(entity -> {
            switch (this.sort.get()) {
                case "Distance": {
                    return Killaura.mc.player.getDistanceSqToEntity(entity);
                }
                case "Health": {
                    return entity.getHealth();
                }
                case "Hurttime": {
                    return entity.hurtTime;
                }
            }
            return -1.0;
        })).sorted(Comparator.comparing(entity -> entity instanceof EntityPlayer)).collect(Collectors.toList());
        return entities;
    }

    private void updateTarget() {
        List<EntityLivingBase> entities = this.getTargets();
        target = entities.size() > 0 ? entities.get(0) : null;
    }

    private boolean allowedToAttack(Entity entity) {
        this.getTargets();
        return entity instanceof EntityLivingBase && entity != Killaura.mc.player && Killaura.mc.player.getDistanceToEntity(entity) <= range.get().intValue();
    }

    private void block() {
        this.sendUseItem(Killaura.mc.player, Killaura.mc.world, Killaura.mc.player.getCurrentEquippedItem());
        Killaura.mc.settings.keyBindUseItem.pressed = true;
        Killaura.mc.player.sendQueue.send(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, Killaura.mc.player.getHeldItem(), 0.0f, 0.0f, 0.0f));
        this.blocking = true;
    }

    private void unblock() {
        if (this.blocking) {
            Killaura.mc.settings.keyBindUseItem.pressed = false;
            this.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            this.blocking = false;
        }
    }

    public void sendUseItem(EntityPlayer playerIn, World worldIn, ItemStack itemStackIn) {
        if (Killaura.mc.playerController.getCurrentGameType() != WorldSettings.GameType.SPECTATOR) {
            Killaura.mc.playerController.syncCurrentPlayItem();
            int i = itemStackIn.stackSize;
            ItemStack itemstack = itemStackIn.useItemRightClick(worldIn, playerIn);
            if (itemstack != itemStackIn || itemstack.stackSize != i) {
                playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = itemstack;
                if (itemstack.stackSize == 0) {
                    playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
                }
            }
        }
    }
}