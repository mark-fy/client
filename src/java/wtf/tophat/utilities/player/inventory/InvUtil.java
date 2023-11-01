package wtf.tophat.utilities.player.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import wtf.tophat.utilities.Methods;

public class InvUtil implements Methods {

    public static int findEmptySlot() {
        for (int i = 0; i < 8; i++) {
            if (mc.player.inventory.mainInventory[i] == null)
                return i;
        }

        return mc.player.inventory.currentItem + (mc.player.inventory.getCurrentItem() == null ? 0 : ((mc.player.inventory.currentItem < 8) ? 4 : -1));
    }

    // TODO: AutoPot refill always put potions on slot 1, bugs here?
    public static int findEmptySlot(int priority) {
        if (mc.player.inventory.mainInventory[priority] == null)
            return priority;

        return findEmptySlot();
    }

    public static void swapShift(int slot) {
        mc.playerController.windowClick(
                mc.player.inventoryContainer.windowId, slot, 0, 1,
                mc.player);
    }

    public static void swap(int slot, int hotbarNum) {
        mc.playerController.windowClick(
                mc.player.inventoryContainer.windowId, slot, hotbarNum, 2,
                mc.player);
    }

    public static boolean isFull() {
        return !Arrays.asList(mc.player.inventory.mainInventory).contains(null);
    }

    public static int armorSlotToNormalSlot(int armorSlot) {
        return 8 - armorSlot;
    }

    public static void block() {
        mc.playerController.sendUseItem(mc.player, mc.world, mc.player.inventory.getCurrentItem());
    }

    public static ItemStack getCurrentItem() {
        return mc.player.getCurrentEquippedItem() == null ? new ItemStack(Blocks.air) : mc.player.getCurrentEquippedItem();
    }

    public static ItemStack getItemBySlot(int slot) {
        return mc.player.inventory.mainInventory[slot] == null ? new ItemStack(Blocks.air) : mc.player.inventory.mainInventory[slot];
    }

    public static List<ItemStack> getHotbarContent() {
        List<ItemStack> result = new ArrayList<>();
        result.addAll(Arrays.asList(mc.player.inventory.mainInventory).subList(0, 9));
        return result;
    }

    public static List<ItemStack> getAllInventoryContent() {
        List<ItemStack> result = new ArrayList<>();
        result.addAll(Arrays.asList(mc.player.inventory.mainInventory).subList(0, 35));
        for (int i = 0; i < 4; i++) {
            result.add(mc.player.inventory.armorItemInSlot(i));
        }
        return result;
    }

    public static List<ItemStack> getInventoryContent() {
        List<ItemStack> result = new ArrayList<>();
        result.addAll(Arrays.asList(mc.player.inventory.mainInventory).subList(9, 35));
        return result;
    }

    public static int getEmptySlotInHotbar() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.mainInventory[i] == null)
                return i;
        }
        return -1;
    }

    public static float getArmorScore(ItemStack itemStack) {
        if (itemStack == null || !(itemStack.getItem() instanceof ItemArmor))
            return -1;

        ItemArmor itemArmor = (ItemArmor) itemStack.getItem();
        float score = 0;

        //basic reduce amount
        score += itemArmor.damageReduceAmount;

        if (EnchantmentHelper.getEnchantments(itemStack).size() <= 0)
            score -= 0.1;

        int protection = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, itemStack);

        score += protection * 0.2;

        return score;
    }

    public static boolean hasWeapon() {
        if (mc.player.inventory.getCurrentItem() != null)
            return false;

        return (mc.player.inventory.getCurrentItem().getItem() instanceof ItemAxe) || (mc.player.inventory.getCurrentItem().getItem() instanceof ItemSword);
    }

    public static boolean isHeldingSword() {
        return mc.player.getHeldItem() != null && mc.player.getHeldItem().getItem() instanceof ItemSword;
    }
    public static int pickaxeSlot = 37, axeSlot = 38, shovelSlot = 39;

    public static void getBestPickaxe() {
        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();

                if (isBestPickaxe(is) && pickaxeSlot != i) {
                    if (!isBestWeapon(is))
                        if (!mc.player.inventoryContainer.getSlot(pickaxeSlot).getHasStack()) {
                            swap(i, pickaxeSlot - 36);
                        } else if (!isBestPickaxe(mc.player.inventoryContainer.getSlot(pickaxeSlot).getStack())) {
                            swap(i, pickaxeSlot - 36);
                        }

                }
            }
        }
    }

    public static void getBestShovel() {
        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();

                if (isBestShovel(is) && shovelSlot != i) {
                    if (!isBestWeapon(is))
                        if (!mc.player.inventoryContainer.getSlot(shovelSlot).getHasStack()) {
                            swap(i, shovelSlot - 36);
                        } else if (!isBestShovel(mc.player.inventoryContainer.getSlot(shovelSlot).getStack())) {
                            swap(i, shovelSlot - 36);
                        }

                }
            }
        }
    }

    public static void getBestAxe() {

        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();

                if (isBestAxe(is) && axeSlot != i) {
                    if (!isBestWeapon(is)) {
                        if (!mc.player.inventoryContainer.getSlot(axeSlot).getHasStack()) {
                            swap(i, axeSlot - 36);
                        } else if (!isBestAxe(mc.player.inventoryContainer.getSlot(axeSlot).getStack())) {
                            swap(i, axeSlot - 36);
                        }
                    }
                }
            }
        }
    }

    public static boolean isBestPickaxe(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemPickaxe))
            return false;
        float value = getToolEffect(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                if (getToolEffect(is) > value && is.getItem() instanceof ItemPickaxe) {
                    return false;
                }

            }
        }
        return true;
    }

    public static boolean isBestShovel(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemSpade))
            return false;
        float value = getToolEffect(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                if (getToolEffect(is) > value && is.getItem() instanceof ItemSpade) {
                    return false;
                }

            }
        }
        return true;
    }

    public static boolean isBestAxe(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemAxe))
            return false;
        float value = getToolEffect(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                if (getToolEffect(is) > value && is.getItem() instanceof ItemAxe && !isBestWeapon(stack)) {
                    return false;
                }

            }
        }
        return true;
    }

    public static float getToolEffect(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemTool))
            return 0;
        String name = item.getUnlocalizedName();
        ItemTool tool = (ItemTool) item;
        float value = 1;
        if (item instanceof ItemPickaxe) {
            value = tool.getStrVsBlock(stack, Blocks.stone);
            if (name.toLowerCase().contains("gold")) {
                value -= 5;
            }
        } else if (item instanceof ItemSpade) {
            value = tool.getStrVsBlock(stack, Blocks.dirt);
            if (name.toLowerCase().contains("gold")) {
                value -= 5;
            }
        } else if (item instanceof ItemAxe) {
            value = tool.getStrVsBlock(stack, Blocks.log);
            if (name.toLowerCase().contains("gold")) {
                value -= 5;
            }
        } else
            return 1f;
        value += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 0.0075D;
        value += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 100d;
        return value;
    }

    public static boolean isBestWeapon(ItemStack stack) {
        float damage = getDamage(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                if (getDamage(is) > damage && (is.getItem() instanceof ItemSword))
                    return false;
            }
        }
        if ((stack.getItem() instanceof ItemSword)) {
            return true;
        } else {
            return false;
        }

    }

    public static float getDamage(ItemStack stack) {
        float damage = 0;
        Item item = stack.getItem();
        if (item instanceof ItemTool) {
            ItemTool tool = (ItemTool) item;
            damage += tool.damageVsEntity;
        }
        if (item instanceof ItemSword) {
            ItemSword sword = (ItemSword) item;
            damage += sword.getDamageVsEntity();
        }
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f
                + EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.01f;
        return damage;
    }

    public static boolean isItemEmpty(Item item) {
        return item == null || Item.getIdFromItem(item) == 0;
    }
}