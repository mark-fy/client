package wtf.tophat.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;
import wtf.tophat.Client;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.utilities.time.Stopwatch;

@ModuleInfo(name = "Auto Armor", desc = "automatically equip the best armor",category = Module.Category.PLAYER)
public class AutoArmor extends Module {
    Stopwatch timer = new Stopwatch();

    public final NumberSetting delay;
    public final BooleanSetting inventoryonly;

    public AutoArmor() {
        Client.settingManager.add(
                delay = new NumberSetting(this, "Delay", 0, 1000, 150, 2),
                inventoryonly = new BooleanSetting(this, "Inv Only", true)
        );
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if (inventoryonly.get() && !(mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat) {
            if (timer.timeElapsed(delay.get().longValue())) {
                getBestArmor();
            }
        }
    }

    public void getBestArmor() {
        for (int type = 1; type < 5; type++) {
            if (mc.player.inventoryContainer.getSlot(4 + type).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(4 + type).getStack();
                if (isBestArmor(is, type)) {
                    continue;
                } else {
                    C16PacketClientStatus p = new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT);
                    mc.player.sendQueue.send(p);
                    drop(4 + type);
                }
            }
            for (int i = 9; i < 45; i++) {
                if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                    ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                    if (isBestArmor(is, type) && getProtection(is) > 0) {
                        shiftClick(i);
                        timer.resetTime();
                        if ((delay.get()).longValue() > 0)
                            return;
                    }
                }
            }
        }
    }

    public static boolean isBestArmor(ItemStack stack, int type) {
        float prot = getProtection(stack);
        String[] typeNames = {"", "helmet", "chestplate", "leggings", "boots"};
        String strType = (type >= 1 && type <= 4) ? typeNames[type] : "";

        if (strType.isEmpty() || !stack.getUnlocalizedName().contains(strType)) {
            return false;
        }

        for (int i = 5; i < 45; i++) {
            if (Minecraft.getMinecraft().player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = Minecraft.getMinecraft().player.inventoryContainer.getSlot(i).getStack();
                if (is.getUnlocalizedName().contains(strType) && getProtection(is) > prot) {
                    return false;
                }
            }
        }

        return true;
    }

    public void shiftClick(int slot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, 1, mc.player);
    }

    public void drop(int slot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 1, 4, mc.player);
    }

    public static float getProtection(ItemStack stack) {
        float prot = 0;
        if ((stack.getItem() instanceof ItemArmor)) {
            ItemArmor armor = (ItemArmor) stack.getItem();
            prot += armor.damageReduceAmount + (100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.0075D;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100d;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100d;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100d;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50d;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.baneOfArthropods.effectId, stack) / 100d;
        }
        return prot;
    }
}