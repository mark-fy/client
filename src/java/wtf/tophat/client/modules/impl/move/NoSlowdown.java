package wtf.tophat.client.modules.impl.move;

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
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.base.Event;
import wtf.tophat.client.events.impl.move.MotionEvent;
import wtf.tophat.client.events.impl.move.SlowDownEvent;
import wtf.tophat.client.events.impl.world.TickEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.DividerSetting;
import wtf.tophat.client.settings.impl.NumberSetting;
import wtf.tophat.client.settings.impl.StringSetting;
import wtf.tophat.client.utilities.math.time.TimeUtil;

@ModuleInfo(name = "No Slowdown",desc = "disable slow down effects", category = Module.Category.MOVE)
public class NoSlowdown extends Module {

    private final DividerSetting modes, booleans, values;
    private final StringSetting mode;
    private final BooleanSetting sword, food, bows;
    private final NumberSetting swordForward, swordStrafe, foodForward, foodStrafe, bowForward, bowStrafe;

    public NoSlowdown() {
        TopHat.settingManager.add(
                modes = new DividerSetting(this, "Mode Settings"),
                mode = new StringSetting(this, "Mode", "Vanilla", "Vanilla", "Switch", "Grim", "Old Intave"),

                booleans = new DividerSetting(this, "Available Items"),
                sword = new BooleanSetting(this, "Sword", true),
                food = new BooleanSetting(this, "Food", true),
                bows = new BooleanSetting(this, "Bows", true),

                values = new DividerSetting(this, "Item Multipliers"),
                swordForward = new NumberSetting(this, "Sword Forward", 0, 1, 1, 2).setHidden(() -> !sword.get()),
                swordStrafe = new NumberSetting(this, "Sword Strafe", 0, 1, 1, 2).setHidden(() -> !sword.get()),
                foodForward = new NumberSetting(this, "Food Forward", 0, 1, 1, 2).setHidden(() -> !food.get()),
                foodStrafe = new NumberSetting(this, "Food Strafe", 0, 1, 1, 2).setHidden(() -> !food.get()),
                bowForward = new NumberSetting(this, "Bow Forward", 0, 1, 1, 2).setHidden(() -> !bows.get()),
                bowStrafe = new NumberSetting(this, "Bow Strafe", 0, 1, 1, 2).setHidden(() -> !bows.get())
        );
    }

    private final TimeUtil intaveTimer = new TimeUtil();

    @Listen
    public void onMotion(MotionEvent event) {
        ItemStack currentItem = mc.player.getCurrentEquippedItem();
        if (currentItem == null || !mc.player.isUsingItem() || !isMoving()) {
            return;
        }

        switch (mode.get()) {
            case "Grim":
            case "Switch":
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
        ItemStack currentItem = getPlayer().getCurrentEquippedItem();
        if (currentItem == null || !getPlayer().isUsingItem() || !isMoving()) {
            return;
        }

        if(sword.get() && currentItem.getItem() instanceof ItemSword) {
            event.setSprint(true);
            event.setForward(swordForward.get().floatValue());
            event.setStrafe(swordStrafe.get().floatValue());
        }

        if(food.get() && currentItem.getItem() instanceof ItemFood) {
            event.setSprint(true);
            event.setForward(foodForward.get().floatValue());
            event.setStrafe(foodStrafe.get().floatValue());
        }

        if(bows.get() && currentItem.getItem() instanceof ItemBow) {
            event.setSprint(true);
            event.setForward(bowForward.get().floatValue());
            event.setStrafe(bowStrafe.get().floatValue());
        }
    }

    @Listen
    public void onTick(TickEvent event) {
        if (getPlayer() == null || getWorld() == null)
            return;

        if(event.getState() == Event.State.PRE) {
            switch (mode.get()) {
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
}
