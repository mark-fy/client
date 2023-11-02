package net.minecraft.client.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.*;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import wtf.capes.sim.StickSimulation;
import wtf.tophat.events.base.Event;
import wtf.tophat.events.handler.PlayerHandler;
import wtf.tophat.events.impl.*;
import wtf.tophat.utilities.player.movement.MoveUtil;

import java.util.ArrayList;
import java.util.List;

public class EntityPlayerSP extends AbstractClientPlayer
{
    public final NetHandlerPlayClient sendQueue;
    private final StickSimulation sharedSimulation;
    private final StatFileWriter statWriter;
    public int offGroundTicks;
    public int onGroundTicks;



    /**
     * The last X position which was transmitted to the server, used to determine when the X position changes and needs
     * to be re-trasmitted
     */
    private double lastReportedPosX;

    /**
     * The last Y position which was transmitted to the server, used to determine when the Y position changes and needs
     * to be re-transmitted
     */
    private double lastReportedPosY;

    /**
     * The last Z position which was transmitted to the server, used to determine when the Z position changes and needs
     * to be re-transmitted
     */
    private double lastReportedPosZ;

    /**
     * The last yaw value which was transmitted to the server, used to determine when the yaw changes and needs to be
     * re-transmitted
     */
    private float lastReportedYaw;

    /**
     * The last pitch value which was transmitted to the server, used to determine when the pitch changes and needs to
     * be re-transmitted
     */
    private float lastReportedPitch;

    /** the last sneaking state sent to the server */
    private boolean serverSneakState;

    /** the last sprinting state sent to the server */
    private boolean serverSprintState;

    public double floatingTickCount;

    /**
     * Reset to 0 every time position is sent to the server, used to send periodic updates every 20 ticks even when the
     * player is not moving.
     */
    private int positionUpdateTicks;
    private boolean hasValidHealth;
    private String clientBrand;
    public MovementInput movementInput;
    protected Minecraft mc;

    /**
     * Used to tell if the player pressed forward twice. If this is at 0 and it's pressed (And they are allowed to
     * sprint, aka enough food on the ground etc) it sets this to 7. If it's pressed and it's greater than 0 enable
     * sprinting.
     */
    protected int sprintToggleTimer;

    /** Ticks left before sprinting is disabled. */
    public int sprintingTicksLeft;
    public float renderArmYaw;
    public float renderArmPitch;
    public float prevRenderArmYaw;
    public float prevRenderArmPitch;
    private int horseJumpPowerCounter;
    private float horseJumpPower;

    /** The amount of time an entity has been in a Portal */
    public float timeInPortal;

    /** The amount of time an entity has been in a Portal the previous tick */
    public float prevTimeInPortal;

    public EntityPlayerSP(Minecraft mcIn, World worldIn, NetHandlerPlayClient netHandler, StatFileWriter statFile)
    {
        super(worldIn, netHandler.getGameProfile());
        this.sendQueue = netHandler;
        this.statWriter = statFile;
        this.mc = mcIn;
        this.dimension = 0;
        sharedSimulation = new StickSimulation();
    }

    @Override
    public StickSimulation getSharedSimulation() {
        return sharedSimulation;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        return false;
    }

    /**
     * Heal living entity (param: amount of half-hearts)
     */
    public void heal(float healAmount)
    {
    }

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    public void mountEntity(Entity entityIn)
    {
        super.mountEntity(entityIn);

        if (entityIn instanceof EntityMinecart)
        {
            this.mc.getSoundHandler().playSound(new MovingSoundMinecartRiding(this, (EntityMinecart)entityIn));
        }
    }

    @Override
    public void moveFlying(float f, float f2, float f3) {
        MoveFlyingEvent moveFlyingEvent = new MoveFlyingEvent(f, f2, f3);
        moveFlyingEvent.call();

        f = moveFlyingEvent.getStrafe();
        f2 = moveFlyingEvent.getForward();
        if (moveFlyingEvent.isCancelled()) {
            return;
        }

        float f4 = PlayerHandler.moveFix ? PlayerHandler.yaw : this.rotationYaw;
        float f5 = f * f + f2 * f2;
        if (f5 >= 1.0E-4f) {
            if ((f5 = MathHelper.sqrt_float(f5)) < 1.0f) {
                f5 = 1.0f;
            }
            f5 = f3 / f5;
            float f6 = MathHelper.sin(f4 * (float)Math.PI / 180.0f);
            float f7 = MathHelper.cos(f4 * (float)Math.PI / 180.0f);
            this.motionX += (f *= f5) * f7 - (f2 *= f5) * f6;
            this.motionZ += f2 * f7 + f * f6;
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (this.worldObj.isBlockLoaded(new BlockPos(this.posX, 0.0D, this.posZ)))
        {
            UpdateEvent updateEvent = new UpdateEvent();
            updateEvent.call();
            super.onUpdate();

            if (this.isRiding())
            {
                this.sendQueue.send(new C03PacketPlayer.C05PacketPlayerLook(PlayerHandler.yaw, PlayerHandler.pitch, this.onGround));
                this.sendQueue.send(new C0CPacketInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
            }
            else
            {
                this.onUpdateWalkingPlayer(PlayerHandler.yaw, PlayerHandler.pitch);
            }
        }
    }

    /**
     * called every tick when the player is on foot. Performs all the things that normally happen during movement.
     */
    public void onUpdateWalkingPlayer(float yaw, float pitch)
    {
        if (this.onGround) {
            this.offGroundTicks = 0;
            ++this.onGroundTicks;
        } else {
            this.onGroundTicks = 0;
            ++this.offGroundTicks;
        }
        MotionEvent motionEvent = new MotionEvent(this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround);
        motionEvent.setState(Event.State.PRE);
        motionEvent.call();

        boolean flag = isSprinting();
        if (flag != this.serverSprintState) {
            if (flag) {
                this.sendQueue.send(new C0BPacketEntityAction((Entity)this, C0BPacketEntityAction.Action.START_SPRINTING));
            } else {
                this.sendQueue.send(new C0BPacketEntityAction((Entity)this, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }
            this.serverSprintState = flag;
        }
        boolean flag1 = isSneaking();
        if (flag1 != this.serverSneakState) {
            if (flag1) {
                this.sendQueue.send(new C0BPacketEntityAction((Entity)this, C0BPacketEntityAction.Action.START_SNEAKING));
            } else {
                this.sendQueue.send(new C0BPacketEntityAction((Entity)this, C0BPacketEntityAction.Action.STOP_SNEAKING));
            }
            this.serverSneakState = flag1;
        }
        if (isCurrentViewEntity()) {
            double d0 = motionEvent.getX() - this.lastReportedPosX;
            double d1 = motionEvent.getY() - this.lastReportedPosY;
            double d2 = motionEvent.getZ() - this.lastReportedPosZ;
            double d3 = (motionEvent.getYaw() - this.lastReportedYaw);
            double d4 = (motionEvent.getPitch() - this.lastReportedPitch);
            boolean flag2 = (d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4D || this.positionUpdateTicks >= 20);
            boolean flag3 = (d3 != 0.0D || d4 != 0.0D);
            if (this.ridingEntity == null) {
                if (flag2 && flag3) {
                    this.sendQueue.send(new C03PacketPlayer.C06PacketPlayerPosLook(motionEvent.getX(), motionEvent.getY(), motionEvent.getZ(), motionEvent.getYaw(), motionEvent.getPitch(), motionEvent.isOnGround()));
                } else if (flag2) {
                    this.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(motionEvent.getX(), motionEvent.getY(), motionEvent.getZ(), motionEvent.isOnGround()));
                } else if (flag3) {
                    this.sendQueue.send(new C03PacketPlayer.C05PacketPlayerLook(motionEvent.getYaw(), motionEvent.getPitch(), motionEvent.isOnGround()));
                } else {
                    this.sendQueue.send(new C03PacketPlayer(motionEvent.isOnGround()));
                }
            } else {
                this.sendQueue.send(new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0D, this.motionZ, motionEvent.getYaw(), motionEvent.getPitch(), motionEvent.isOnGround()));
                flag2 = false;
            }
            this.positionUpdateTicks++;
            if (flag2) {
                this.lastReportedPosX = motionEvent.getX();
                this.lastReportedPosY = motionEvent.getY();
                this.lastReportedPosZ = motionEvent.getZ();
                this.positionUpdateTicks = 0;
            }
            if (flag3) {
                this.lastReportedYaw = motionEvent.getYaw();
                this.lastReportedPitch = motionEvent.getPitch();
            }
        }
        motionEvent.setState(Event.State.POST);
        motionEvent.call();
    }

    /**
     * Called when player presses the drop item key
     */
    public EntityItem dropOneItem(boolean dropAll)
    {
        C07PacketPlayerDigging.Action c07packetplayerdigging$action = dropAll ? C07PacketPlayerDigging.Action.DROP_ALL_ITEMS : C07PacketPlayerDigging.Action.DROP_ITEM;
        this.sendQueue.send(new C07PacketPlayerDigging(c07packetplayerdigging$action, BlockPos.ORIGIN, EnumFacing.DOWN));
        return null;
    }

    /**
     * Joins the passed in entity item with the world. Args: entityItem
     */
    protected void joinEntityItemWithWorld(EntityItem itemIn)
    {
    }

    /**
     * Sends a chat message from the player. Args: chatMessage
     */
    public void sendChatMessage(String message)
    {
        ChatEvent chatEvent = new ChatEvent(message);
        chatEvent.call();

        if(chatEvent.isCancelled()) {
            return;
        }

        this.sendQueue.send(new C01PacketChatMessage(message));
    }

    /**
     * Swings the item the player is holding.
     */
    public void swingItem()
    {
        super.swingItem();
        this.sendQueue.send(new C0APacketAnimation());
    }

    public void respawnPlayer()
    {
        this.sendQueue.send(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
    }

    /**
     * Deals damage to the entity. If its a EntityPlayer then will take damage from the armor first and then health
     * second with the reduced value. Args: damageAmount
     */
    protected void damageEntity(DamageSource damageSrc, float damageAmount)
    {
        if (!this.isEntityInvulnerable(damageSrc))
        {
            this.setHealth(this.getHealth() - damageAmount);
        }
    }

    /**
     * set current crafting inventory back to the 2x2 square
     */
    public void closeScreen()
    {
        this.sendQueue.send(new C0DPacketCloseWindow(this.openContainer.windowId));
        this.closeScreenAndDropStack();
    }

    public void closeScreenAndDropStack()
    {
        this.inventory.setItemStack((ItemStack)null);
        super.closeScreen();
        this.mc.displayGuiScreen((GuiScreen)null);
    }

    /**
     * Updates health locally.
     */
    public void setPlayerSPHealth(float health)
    {
        if (this.hasValidHealth)
        {
            float f = this.getHealth() - health;

            if (f <= 0.0F)
            {
                this.setHealth(health);

                if (f < 0.0F)
                {
                    this.hurtResistantTime = this.maxHurtResistantTime / 2;
                }
            }
            else
            {
                this.lastDamage = f;
                this.setHealth(this.getHealth());
                this.hurtResistantTime = this.maxHurtResistantTime;
                this.damageEntity(DamageSource.generic, f);
                this.hurtTime = this.maxHurtTime = 10;
            }
        }
        else
        {
            this.setHealth(health);
            this.hasValidHealth = true;
        }
    }

    /**
     * Adds a value to a statistic field.
     */
    public void addStat(StatBase stat, int amount)
    {
        if (stat != null)
        {
            if (stat.isIndependent)
            {
                super.addStat(stat, amount);
            }
        }
    }

    /**
     * Sends the player's abilities to the server (if there is one).
     */
    public void sendPlayerAbilities()
    {
        this.sendQueue.send(new C13PacketPlayerAbilities(this.capabilities));
    }

    /**
     * returns true if this is an EntityPlayerSP, or the logged in player.
     */
    public boolean isUser()
    {
        return true;
    }

    protected void sendHorseJump()
    {
        this.sendQueue.send(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.RIDING_JUMP, (int)(this.getHorseJumpPower() * 100.0F)));
    }

    public void sendHorseInventory()
    {
        this.sendQueue.send(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.OPEN_INVENTORY));
    }

    public void setClientBrand(String brand)
    {
        this.clientBrand = brand;
    }

    public String getClientBrand()
    {
        return this.clientBrand;
    }

    public StatFileWriter getStatFileWriter()
    {
        return this.statWriter;
    }

    public void addChatComponentMessage(IChatComponent chatComponent)
    {
        this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponent);
    }

    protected boolean pushOutOfBlocks(double x, double y, double z)
    {
        if (this.noClip)
        {
            return false;
        }
        else
        {
            BlockPos blockpos = new BlockPos(x, y, z);
            double d0 = x - (double)blockpos.getX();
            double d1 = z - (double)blockpos.getZ();

            if (!this.isOpenBlockSpace(blockpos))
            {
                int i = -1;
                double d2 = 9999.0D;

                if (this.isOpenBlockSpace(blockpos.west()) && d0 < d2)
                {
                    d2 = d0;
                    i = 0;
                }

                if (this.isOpenBlockSpace(blockpos.east()) && 1.0D - d0 < d2)
                {
                    d2 = 1.0D - d0;
                    i = 1;
                }

                if (this.isOpenBlockSpace(blockpos.north()) && d1 < d2)
                {
                    d2 = d1;
                    i = 4;
                }

                if (this.isOpenBlockSpace(blockpos.south()) && 1.0D - d1 < d2)
                {
                    d2 = 1.0D - d1;
                    i = 5;
                }

                float f = 0.1F;

                if (i == 0)
                {
                    this.motionX = (double)(-f);
                }

                if (i == 1)
                {
                    this.motionX = (double)f;
                }

                if (i == 4)
                {
                    this.motionZ = (double)(-f);
                }

                if (i == 5)
                {
                    this.motionZ = (double)f;
                }
            }

            return false;
        }
    }

    /**
     * Returns true if the block at the given BlockPos and the block above it are NOT full cubes.
     */
    private boolean isOpenBlockSpace(BlockPos pos)
    {
        return !this.worldObj.getBlockState(pos).getBlock().isNormalCube() && !this.worldObj.getBlockState(pos.up()).getBlock().isNormalCube();
    }

    /**
     * Set sprinting switch for Entity.
     */
    public void setSprinting(boolean sprinting)
    {
        super.setSprinting(sprinting);
        this.sprintingTicksLeft = sprinting ? 600 : 0;
    }

    /**
     * Sets the current XP, total XP, and level number.
     */
    public void setXPStats(float currentXP, int maxXP, int level)
    {
        this.experience = currentXP;
        this.experienceTotal = maxXP;
        this.experienceLevel = level;
    }

    /**
     * Send a chat message to the CommandSender
     */
    public void addChatMessage(IChatComponent component)
    {
        this.mc.ingameGUI.getChatGUI().printChatMessage(component);
    }

    /**
     * Returns {@code true} if the CommandSender is allowed to execute the command, {@code false} if not
     */
    public boolean canCommandSenderUseCommand(int permLevel, String commandName)
    {
        return permLevel <= 0;
    }

    /**
     * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
     * the coordinates 0, 0, 0
     */
    public BlockPos getPosition()
    {
        return new BlockPos(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D);
    }

    public void playSound(String name, float volume, float pitch)
    {
        this.worldObj.playSound(this.posX, this.posY, this.posZ, name, volume, pitch, false);
    }

    /**
     * Returns whether the entity is in a server world
     */
    public boolean isServerWorld()
    {
        return true;
    }

    public boolean isRidingHorse()
    {
        return this.ridingEntity != null && this.ridingEntity instanceof EntityHorse && ((EntityHorse)this.ridingEntity).isHorseSaddled();
    }

    public float getHorseJumpPower()
    {
        return this.horseJumpPower;
    }

    public void openEditSign(TileEntitySign signTile)
    {
        this.mc.displayGuiScreen(new GuiEditSign(signTile));
    }

    public void openEditCommandBlock(CommandBlockLogic cmdBlockLogic)
    {
        this.mc.displayGuiScreen(new GuiCommandBlock(cmdBlockLogic));
    }

    /**
     * Displays the GUI for interacting with a book.
     */
    public void displayGUIBook(ItemStack bookStack)
    {
        Item item = bookStack.getItem();

        if (item == Items.writable_book)
        {
            this.mc.displayGuiScreen(new GuiScreenBook(this, bookStack, true));
        }
    }

    /**
     * Displays the GUI for interacting with a chest inventory. Args: chestInventory
     */
    public void displayGUIChest(IInventory chestInventory)
    {
        String s = chestInventory instanceof IInteractionObject ? ((IInteractionObject)chestInventory).getGuiID() : "minecraft:container";

        if ("minecraft:chest".equals(s))
        {
            this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
        }
        else if ("minecraft:hopper".equals(s))
        {
            this.mc.displayGuiScreen(new GuiHopper(this.inventory, chestInventory));
        }
        else if ("minecraft:furnace".equals(s))
        {
            this.mc.displayGuiScreen(new GuiFurnace(this.inventory, chestInventory));
        }
        else if ("minecraft:brewing_stand".equals(s))
        {
            this.mc.displayGuiScreen(new GuiBrewingStand(this.inventory, chestInventory));
        }
        else if ("minecraft:beacon".equals(s))
        {
            this.mc.displayGuiScreen(new GuiBeacon(this.inventory, chestInventory));
        }
        else if (!"minecraft:dispenser".equals(s) && !"minecraft:dropper".equals(s))
        {
            this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
        }
        else
        {
            this.mc.displayGuiScreen(new GuiDispenser(this.inventory, chestInventory));
        }
    }

    public void displayGUIHorse(EntityHorse horse, IInventory horseInventory)
    {
        this.mc.displayGuiScreen(new GuiScreenHorseInventory(this.inventory, horseInventory, horse));
    }

    public void displayGui(IInteractionObject guiOwner)
    {
        String s = guiOwner.getGuiID();

        if ("minecraft:crafting_table".equals(s))
        {
            this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.worldObj));
        }
        else if ("minecraft:enchanting_table".equals(s))
        {
            this.mc.displayGuiScreen(new GuiEnchantment(this.inventory, this.worldObj, guiOwner));
        }
        else if ("minecraft:anvil".equals(s))
        {
            this.mc.displayGuiScreen(new GuiRepair(this.inventory, this.worldObj));
        }
    }

    public void displayVillagerTradeGui(IMerchant villager)
    {
        this.mc.displayGuiScreen(new GuiMerchant(this.inventory, villager, this.worldObj));
    }

    /**
     * Called when the player performs a critical hit on the Entity. Args: entity that was hit critically
     */
    public void onCriticalHit(Entity entityHit)
    {
        this.mc.effectRenderer.emitParticleAtEntity(entityHit, EnumParticleTypes.CRIT);
    }

    public void onEnchantmentCritical(Entity entityHit)
    {
        this.mc.effectRenderer.emitParticleAtEntity(entityHit, EnumParticleTypes.CRIT_MAGIC);
    }

    /**
     * Returns if this entity is sneaking.
     */
    public boolean isSneaking()
    {
        boolean flag = this.movementInput != null ? this.movementInput.sneak : false;
        return flag && !this.sleeping;
    }

    public void updateEntityActionState()
    {
        super.updateEntityActionState();

        if (this.isCurrentViewEntity())
        {
            this.moveStrafing = this.movementInput.moveStrafe;
            this.moveForward = this.movementInput.moveForward;
            this.isJumping = this.movementInput.jump;
            this.prevRenderArmYaw = this.renderArmYaw;
            this.prevRenderArmPitch = this.renderArmPitch;
            this.renderArmPitch = (float)((double)this.renderArmPitch + (double)(this.rotationPitch - this.renderArmPitch) * 0.5D);
            this.renderArmYaw = (float)((double)this.renderArmYaw + (double)(this.rotationYaw - this.renderArmYaw) * 0.5D);
        }
    }

    protected boolean isCurrentViewEntity()
    {
        return this.mc.getRenderViewEntity() == this;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (this.sprintingTicksLeft > 0)
        {
            --this.sprintingTicksLeft;

            if (this.sprintingTicksLeft == 0)
            {
                this.setSprinting(false);
            }
        }

        if (this.sprintToggleTimer > 0)
        {
            --this.sprintToggleTimer;
        }

        this.prevTimeInPortal = this.timeInPortal;

        if (this.inPortal)
        {
            if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame())
            {
                this.mc.displayGuiScreen((GuiScreen)null);
            }

            if (this.timeInPortal == 0.0F)
            {
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"), this.rand.nextFloat() * 0.4F + 0.8F));
            }

            this.timeInPortal += 0.0125F;

            if (this.timeInPortal >= 1.0F)
            {
                this.timeInPortal = 1.0F;
            }

            this.inPortal = false;
        }
        else if (this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getDuration() > 60)
        {
            this.timeInPortal += 0.006666667F;

            if (this.timeInPortal > 1.0F)
            {
                this.timeInPortal = 1.0F;
            }
        }
        else
        {
            if (this.timeInPortal > 0.0F)
            {
                this.timeInPortal -= 0.05F;
            }

            if (this.timeInPortal < 0.0F)
            {
                this.timeInPortal = 0.0F;
            }
        }

        if (this.timeUntilPortal > 0)
        {
            --this.timeUntilPortal;
        }

        boolean flag = this.movementInput.jump;
        boolean flag1 = this.movementInput.sneak;
        float f = 0.8F;
        boolean flag2 = this.movementInput.moveForward >= f;
        final float forward = this.movementInput.moveForward;
        final float strafe = this.movementInput.moveStrafe;
        this.movementInput.updatePlayerMoveState();

        SilentMoveEvent silentMoveEvent = new SilentMoveEvent();
        silentMoveEvent.call();

        if (PlayerHandler.moveFix && PlayerHandler.currentMode == PlayerHandler.MoveFixMode.SILENT) {
            final float[] floats = this.mySilentStrafe(this.movementInput.moveStrafe, this.movementInput.moveForward, this.rotationYaw, true);
            final float diffForward = forward - floats[1];
            final float diffStrafe = strafe - floats[0];
            if (this.movementInput.sneak) {
                this.movementInput.moveStrafe = MathHelper.clamp_float(floats[0], -0.3f, 0.3f);
                this.movementInput.moveForward = MathHelper.clamp_float(floats[1], -0.3f, 0.3f);
            }
            else {
                if (diffForward >= 2.0f) {
                    floats[1] = 0.0f;
                }
                if (diffForward <= -2.0f) {
                    floats[1] = 0.0f;
                }
                if (diffStrafe >= 2.0f) {
                    floats[0] = 0.0f;
                }
                if (diffStrafe <= -2.0f) {
                    floats[0] = 0.0f;
                }
                this.movementInput.moveStrafe = MathHelper.clamp_float(floats[0], -1.0f, 1.0f);
                this.movementInput.moveForward = MathHelper.clamp_float(floats[1], -1.0f, 1.0f);
            }
        }

        SlowDownEvent slowDownEvent = new SlowDownEvent(0.2f, 0.2f);
        slowDownEvent.call();

        if (this.isUsingItem() && !this.isRiding())
        {
            MovementInput moveInput1 = this.movementInput;
            moveInput1.moveStrafe *= slowDownEvent.getStrafe();
            MovementInput moveInput2 = this.movementInput;
            moveInput2.moveForward *= slowDownEvent.getForward();
            this.sprintToggleTimer = 0;
        }

        this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ + (double)this.width * 0.35D);
        this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ - (double)this.width * 0.35D);
        this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ - (double)this.width * 0.35D);
        this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ + (double)this.width * 0.35D);
        boolean flag69 = (float)this.getFoodStats().getFoodLevel() > 6.0F || this.capabilities.allowFlying;

        float movef = this.movementInput.moveForward;
        if (PlayerHandler.shouldSprintReset) {
            movef = this.movementInput.moveForward;
            this.movementInput.moveForward = 0;
        }

        if (this.onGround && !flag1 && !flag2 && this.movementInput.moveForward >= f && !this.isSprinting() && flag69 && (!this.isUsingItem() || slowDownEvent.isSprint()) && !this.isPotionActive(Potion.blindness)) {
            if (this.sprintToggleTimer <= 0 && !this.mc.settings.keyBindSprint.isKeyDown()) {
                this.sprintToggleTimer = 7;
            } else {
                this.setSprinting(true);
            }
        }

        if (!this.isSprinting() && this.movementInput.moveForward >= f && flag69 && (!this.isUsingItem() || slowDownEvent.isSprint()) && !this.isPotionActive(Potion.blindness) && this.mc.settings.keyBindSprint.isKeyDown()) {
            this.setSprinting(true);
        }

        DirectionSprintCheckEvent dirEvent = new DirectionSprintCheckEvent(this.movementInput.moveForward < f);
        dirEvent.call();
        if (this.isSprinting() && (dirEvent.isSprintCheck() || this.isCollidedHorizontally || !flag69)) {
            this.setSprinting(false);
        }

        if (!slowDownEvent.isSprint() && this.isUsingItem() && !this.isRiding()) {
            this.setSprinting(false);
        }

        if (PlayerHandler.shouldSprintReset) {
            this.movementInput.moveForward = movef;
            PlayerHandler.shouldSprintReset = false;
        }

        if (this.capabilities.allowFlying)
        {
            if (this.mc.playerController.isSpectatorMode())
            {
                if (!this.capabilities.isFlying)
                {
                    this.capabilities.isFlying = true;
                    this.sendPlayerAbilities();
                }
            }
            else if (!flag && this.movementInput.jump)
            {
                if (this.flyToggleTimer == 0)
                {
                    this.flyToggleTimer = 7;
                }
                else
                {
                    this.capabilities.isFlying = !this.capabilities.isFlying;
                    this.sendPlayerAbilities();
                    this.flyToggleTimer = 0;
                }
            }
        }

        if (this.capabilities.isFlying && this.isCurrentViewEntity())
        {
            if (this.movementInput.sneak)
            {
                this.motionY -= (double)(this.capabilities.getFlySpeed() * 3.0F);
            }

            if (this.movementInput.jump)
            {
                this.motionY += (double)(this.capabilities.getFlySpeed() * 3.0F);
            }
        }

        if (this.isRidingHorse())
        {
            if (this.horseJumpPowerCounter < 0)
            {
                ++this.horseJumpPowerCounter;

                if (this.horseJumpPowerCounter == 0)
                {
                    this.horseJumpPower = 0.0F;
                }
            }

            if (flag && !this.movementInput.jump)
            {
                this.horseJumpPowerCounter = -10;
                this.sendHorseJump();
            }
            else if (!flag && this.movementInput.jump)
            {
                this.horseJumpPowerCounter = 0;
                this.horseJumpPower = 0.0F;
            }
            else if (flag)
            {
                ++this.horseJumpPowerCounter;

                if (this.horseJumpPowerCounter < 10)
                {
                    this.horseJumpPower = (float)this.horseJumpPowerCounter * 0.1F;
                }
                else
                {
                    this.horseJumpPower = 0.8F + 2.0F / (float)(this.horseJumpPowerCounter - 9) * 0.1F;
                }
            }
        }
        else
        {
            this.horseJumpPower = 0.0F;
        }

        super.onLivingUpdate();

        if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.isSpectatorMode())
        {
            this.capabilities.isFlying = false;
            this.sendPlayerAbilities();
        }
    }

    public float[] mySilentStrafe(final float strafe, final float forward, final float yaw, final boolean advanced) {
        final Minecraft mc = Minecraft.getMinecraft();
        final float diff = MathHelper.wrapAngleTo180_float(yaw - PlayerHandler.yaw);
        float newForward = 0.0f;
        float newStrafe = 0.0f;
        if (!advanced) {
            if (diff >= 22.5 && diff < 67.5) {
                newStrafe += strafe;
                newForward += forward;
                newStrafe -= forward;
                newForward += strafe;
            }
            else if (diff >= 67.5 && diff < 112.5) {
                newStrafe -= forward;
                newForward += strafe;
            }
            else if (diff >= 112.5 && diff < 157.5) {
                newStrafe -= strafe;
                newForward -= forward;
                newStrafe -= forward;
                newForward += strafe;
            }
            else if (diff >= 157.5 || diff <= -157.5) {
                newStrafe -= strafe;
                newForward -= forward;
            }
            else if (diff > -157.5 && diff <= -112.5) {
                newStrafe -= strafe;
                newForward -= forward;
                newStrafe += forward;
                newForward -= strafe;
            }
            else if (diff > -112.5 && diff <= -67.5) {
                newStrafe += forward;
                newForward -= strafe;
            }
            else if (diff > -67.5 && diff <= -22.5) {
                newStrafe += strafe;
                newForward += forward;
                newStrafe += forward;
                newForward -= strafe;
            }
            else {
                newStrafe += strafe;
                newForward += forward;
            }
            return new float[] { newStrafe, newForward };
        }
        final double[] realMotion = MoveUtil.getMotion(0.22, strafe, forward, mc.player.rotationYaw);
        final double[] array;
        final double[] realPos = array = new double[] { mc.player.posX, mc.player.posZ };
        final int n = 0;
        array[n] += realMotion[0];
        final double[] array2 = realPos;
        final int n2 = 1;
        array2[n2] += realMotion[1];
        final ArrayList<float[]> possibleForwardStrafe = new ArrayList<float[]>();
        int i = 0;
        boolean b = false;
        while (!b) {
            newForward = 0.0f;
            newStrafe = 0.0f;
            if (i == 0) {
                newStrafe += strafe;
                newForward += forward;
                newStrafe -= forward;
                newForward += strafe;
                possibleForwardStrafe.add(new float[] { newForward, newStrafe });
            }
            else if (i == 1) {
                newStrafe -= forward;
                newForward += strafe;
                possibleForwardStrafe.add(new float[] { newForward, newStrafe });
            }
            else if (i == 2) {
                newStrafe -= strafe;
                newForward -= forward;
                newStrafe -= forward;
                newForward += strafe;
                possibleForwardStrafe.add(new float[] { newForward, newStrafe });
            }
            else if (i == 3) {
                newStrafe -= strafe;
                newForward -= forward;
                possibleForwardStrafe.add(new float[] { newForward, newStrafe });
            }
            else if (i == 4) {
                newStrafe -= strafe;
                newForward -= forward;
                newStrafe += forward;
                newForward -= strafe;
                possibleForwardStrafe.add(new float[] { newForward, newStrafe });
            }
            else if (i == 5) {
                newStrafe += forward;
                newForward -= strafe;
                possibleForwardStrafe.add(new float[] { newForward, newStrafe });
            }
            else if (i == 6) {
                newStrafe += strafe;
                newForward += forward;
                newStrafe += forward;
                newForward -= strafe;
                possibleForwardStrafe.add(new float[] { newForward, newStrafe });
            }
            else {
                newStrafe += strafe;
                newForward += forward;
                possibleForwardStrafe.add(new float[] { newForward, newStrafe });
                b = true;
            }
            ++i;
        }
        double distance = 5000.0;
        float[] floats = new float[2];
        for (final float[] flo : possibleForwardStrafe) {
            if (flo[0] > 1.0f) {
                flo[0] = 1.0f;
            }
            else if (flo[0] < -1.0f) {
                flo[0] = -1.0f;
            }
            if (flo[1] > 1.0f) {
                flo[1] = 1.0f;
            }
            else if (flo[1] < -1.0f) {
                flo[1] = -1.0f;
            }
            final double[] motion2;
            final double[] motion = motion2 = MoveUtil.getMotion(0.22, flo[1], flo[0], PlayerHandler.yaw);
            final int n3 = 0;
            motion2[n3] += mc.player.posX;
            final double[] array3 = motion;
            final int n4 = 1;
            array3[n4] += mc.player.posZ;
            final double diffX = Math.abs(realPos[0] - motion[0]);
            final double diffZ = Math.abs(realPos[1] - motion[1]);
            final double d0 = diffX * diffX + diffZ * diffZ;
            if (d0 < distance) {
                distance = d0;
                floats = flo;
            }
        }
        return new float[] { floats[1], floats[0] };
    }

    public boolean sendTeleportPackets(List<Vec3> path, boolean safe, boolean onGround) {
        if (this.mc.player.floatingTickCount <= (double)(79 - path.size()) || !safe) {
            for (Vec3 vec3 : path) {
                this.mc.player.sendQueue.send(new C03PacketPlayer.C04PacketPlayerPosition(vec3.xCoord, vec3.yCoord, vec3.zCoord, onGround));
            }
            return true;
        }
        return false;
    }
}
