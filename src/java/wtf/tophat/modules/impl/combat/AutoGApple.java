package wtf.tophat.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import wtf.tophat.Client;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;
import wtf.tophat.utilities.player.inventory.InvUtil;

@ModuleInfo(name = "AutoGApple", desc = "eat gapple automatically for you", category = Module.Category.COMBAT)
public class AutoGApple extends Module {

    public final NumberSetting slotValue;
    public final NumberSetting health;
    public final StringSetting healMode;

    int timer, eatTicks, slot, oldSlot;

    public AutoGApple() {
        Client.settingManager.add(
                slotValue = new NumberSetting(this, "Slot", 1, 9, 6, 10),
                health = new NumberSetting(this, "Health", 1, 20, 14, 5),
                healMode = new StringSetting(this, "Heal Mode", "Absorption", "Absorption", "Regen")
        );
    }

    @Listen
    public void onMotion(MotionEvent e) {
        if (timer < 20)
            timer++;

        if(timer >= 20) {
            if (mc.player.getHealth() < health.get().floatValue()
                    && ((!mc.player.isPotionActive(Potion.regeneration) && healMode.is("Regen"))
                    || (mc.player.getAbsorptionAmount() == 0 && healMode.is("Absorption")))) {
                slot = getAppleFromInventory();

                if(slot != -1) {
                    slot = slot - 36;
                    eatTicks = 0;
                    timer = 0;
                }
            }
        }

        if(eatTicks >= 0 && slot >= 0) {
            eatTicks++;

            if (eatTicks == 1) {
                oldSlot = mc.player.inventory.currentItem;
                mc.player.inventory.currentItem = slot;
            } else if(eatTicks >= 2) {
                mc.player.sendQueue.send(new C08PacketPlayerBlockPlacement(mc.player.inventory.getCurrentItem()));

                mc.player.sendQueue.send(new C09PacketHeldItemChange(slot + 1 >= 9 ? 0 : slot + 1));
                mc.player.sendQueue.send(new C09PacketHeldItemChange(slot));

                mc.player.inventory.currentItem = oldSlot;
                eatTicks = -1;
            }
        }
    }

    private int getAppleFromInventory() {
        for (int i = 36; i < 45; i++) {
            ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();

            if (InvUtil.isItemEmpty(stack.getItem()))
                continue;

            if (stack.getItem() != Items.golden_apple)
                continue;

            return i;
        }

        for (int i = 9; i < 36; i++) {
            ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();

            if (InvUtil.isItemEmpty(stack.getItem()))
                continue;

            if (stack.getItem() != Items.golden_apple)
                continue;

            mc.playerController.windowClick(mc.player.openContainer.windowId, i, slotValue.get().intValue(), 2, mc.player);
        }

        return -1;
    }
}