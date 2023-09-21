package wtf.tophat.module.impl.move;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import wtf.tophat.Client;
import wtf.tophat.events.base.Event;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.events.impl.RunTickEvent;
import wtf.tophat.events.impl.SlowDownEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.StringSetting;

@ModuleInfo(name = "No Slowdown",desc = "disable slow down effects", category = Module.Category.MOVE)
public class NoSlowdown extends Module {

    private final StringSetting mode;
    private final BooleanSetting sword, food, bows;

    public NoSlowdown() {
        Client.settingManager.add(
                mode = new StringSetting(this, "Mode", "Vanilla", "Vanilla", "Switch", "Grim"),
                sword = new BooleanSetting(this, "Sword", true),
                food = new BooleanSetting(this, "Food", true),
                bows = new BooleanSetting(this, "Bows", true)
        );
    }

    @Listen
    public void onMotion(MotionEvent event) {
        ItemStack currentItem = mc.player.getCurrentEquippedItem();
        if (currentItem == null || !mc.player.isUsingItem() || !isMoving()) {
            return;
        }

        switch (mode.getValue()) {
            case "Switch":
            case "Grim":
                if(event.state == Event.State.PRE) {
                    sendPacket(new C09PacketHeldItemChange((mc.player.inventory.currentItem + 1) % 9));
                    sendPacket(new C09PacketHeldItemChange(mc.player.inventory.currentItem));
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
            event.setForward(1f);
            event.setStrafe(1f);
        }

        if(food.getValue() && currentItem.getItem() instanceof ItemFood) {
            event.setSprint(true);
            event.setForward(1f);
            event.setStrafe(1f);
        }

        if(bows.getValue() && currentItem.getItem() instanceof ItemBow) {
            event.setSprint(true);
            event.setForward(1f);
            event.setStrafe(1f);
        }
    }

    @Listen
    public void onTick(RunTickEvent event) {
        if(mc.player == null || mc.world == null) {
            return;
        }

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
