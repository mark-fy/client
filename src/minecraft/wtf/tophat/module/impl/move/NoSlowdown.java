package wtf.tophat.module.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import wtf.tophat.Client;
import wtf.tophat.events.base.Event;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.RunTickEvent;
import wtf.tophat.events.impl.SlowDownEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.module.impl.player.Timer;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.Methods;
import wtf.tophat.utilities.math.TimeUtil;

import java.util.Set;

@ModuleInfo(name = "No Slowdown",desc = "disable slow down effects", category = Module.Category.MOVE)
public class NoSlowdown extends Module {

    private final StringSetting mode;
    private final BooleanSetting sword, food, bows;
    private final NumberSetting swordForward, swordStrafe, foodForward, foodStrafe, bowForward, bowStrafe;

    public NoSlowdown() {
        Client.settingManager.add(
                mode = new StringSetting(this, "Mode", "Vanilla", "Vanilla", "Switch", "Grim", "Old Intave", "Fyre test"),

                sword = new BooleanSetting(this, "Sword", true),
                food = new BooleanSetting(this, "Food", true),
                bows = new BooleanSetting(this, "Bows", true),

                swordForward = new NumberSetting(this, "Sword Forward", 0, 1, 1, 2).setHidden(() -> !sword.getValue()),
                swordStrafe = new NumberSetting(this, "Sword Strafe", 0, 1, 1, 2).setHidden(() -> !sword.getValue()),
                foodForward = new NumberSetting(this, "Food Forward", 0, 1, 1, 2).setHidden(() -> !food.getValue()),
                foodStrafe = new NumberSetting(this, "Food Strafe", 0, 1, 1, 2).setHidden(() -> !food.getValue()),
                bowForward = new NumberSetting(this, "Bow Forward", 0, 1, 1, 2).setHidden(() -> !bows.getValue()),
                bowStrafe = new NumberSetting(this, "Bow Strafe", 0, 1, 1, 2).setHidden(() -> !bows.getValue())

        );
    }

    private final TimeUtil intaveTimer = new TimeUtil();
    private final TimeUtil fyretimer = new TimeUtil();

    @Listen
    public void onMotion(MotionEvent event) {
        ItemStack currentItem = mc.player.getCurrentEquippedItem();
        if (currentItem == null || !mc.player.isUsingItem() || !isMoving()) {
            return;
        }

        switch (mode.getValue()) {
            case "Switch":
            case "Fyre test":
                if(mc.player.isUsingItem() && currentItem.getItem() instanceof ItemSword && fyretimer.elapsed(75L)) {
                    if (event.getState() == Event.State.PRE) {
                        sendPacketUnlogged(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.UP));
                    }

                    if (event.getState() == Event.State.POST) {
                        sendPacketUnlogged(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.UP));
                        fyretimer.reset();
                    }
                }
                break;


            case "Grim":
                if(event.getState() == Event.State.PRE) {
                    sendPacket(new C09PacketHeldItemChange((mc.player.inventory.currentItem + 1) % 9));
                    sendPacket(new C09PacketHeldItemChange(mc.player.inventory.currentItem));
                }
                break;
            case "Old Intave":
                if(mc.player.isUsingItem() && currentItem.getItem() instanceof ItemSword && intaveTimer.elapsed(150L)) {
                    if(event.getState() == Event.State.PRE) {
                        sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    }

                    if(event.getState() == Event.State.POST) {
                        sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        intaveTimer.reset();
                    }
                }
                break;
        }
    }

    @Listen
    public void onSlow(SlowDownEvent event) {
        ItemStack currentItem = mc.player.getCurrentEquippedItem();
        if (currentItem == null || !mc.player.isUsingItem() || !isMoving()) {
            return;
        }

        if(sword.getValue() && currentItem.getItem() instanceof ItemSword) {
            event.setSprint(true);
            event.setForward(swordForward.getValue().floatValue());
            event.setStrafe(swordStrafe.getValue().floatValue());
        }

        if(food.getValue() && currentItem.getItem() instanceof ItemFood) {
            event.setSprint(true);
            event.setForward(foodForward.getValue().floatValue());
            event.setStrafe(foodStrafe.getValue().floatValue());
        }

        if(bows.getValue() && currentItem.getItem() instanceof ItemBow) {
            event.setSprint(true);
            event.setForward(bowForward.getValue().floatValue());
            event.setStrafe(bowStrafe.getValue().floatValue());
        }
    }

    @Listen
    public void onTick(RunTickEvent event) {
        if(Methods.mc.player == null || Methods.mc.world == null)
            return;

        switch (mode.getValue()) {
            case "Grim":
                if (mc.player.isBlocking()) {
                    sendPacket(new C08PacketPlayerBlockPlacement(BlockPos.ORIGIN, 255, mc.player.inventory.getCurrentItem(), 0.0f, 0.0f, 0.0f));
                } else if (mc.player.isUsingItem()) {
                    sendPacket(new C09PacketHeldItemChange((mc.player.inventory.currentItem + 1) % 9));
                    sendPacket(new C09PacketHeldItemChange(mc.player.inventory.currentItem));
                }
                break;
        }
    }

}
