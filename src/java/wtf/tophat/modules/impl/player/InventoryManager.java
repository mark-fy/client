package wtf.tophat.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.utilities.time.TimeUtil;

import java.util.ArrayList;
import java.util.function.Function;

@ModuleInfo(name = "Inventory Manager", desc = "sorts your inventory automatically", category = Module.Category.PLAYER)
public class InventoryManager extends Module {

    public final NumberSetting delay, blockcap;
    public final BooleanSetting food, sort, archery, sword, invcleaner, uhc, inventoryonly;

    private TimeUtil timer = new TimeUtil();

    private int lastSlot;

    private final int weaponSlot = 36, pickaxeSlot = 37, axeSlot = 38, shovelSlot = 39;
    final ArrayList<Integer> whitelistedItems = new ArrayList<>();

    public InventoryManager(){
        TopHat.settingManager.add(
                delay = new NumberSetting(this, "Delay", 0, 1000, 500, 2),
                blockcap = new NumberSetting(this, "Block Cap", 0, 512, 128, 2),
                food = new BooleanSetting(this, "Food", true),
                sort = new BooleanSetting(this, "Sort", true),
                archery = new BooleanSetting(this, "Archery", true),
                sword = new BooleanSetting(this, "Sword", true),
                invcleaner = new BooleanSetting(this, "Cleaner", true),
                uhc = new BooleanSetting(this, "UHC", false),
                inventoryonly = new BooleanSetting(this, "Inventory Only", true)
        );
    }

    @Override
    public void onEnable() {
        lastSlot = -1;
        super.onEnable();
    }

    @Listen
    public void onMotion(MotionEvent event) {
        if (mc.player.openContainer instanceof ContainerChest && mc.currentScreen instanceof GuiContainer)
            return;

        long delay2 = delay.get().longValue();

        mc.player.sendQueue.send(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));

        if (inventoryonly.get() && !(mc.currentScreen instanceof GuiInventory)) {
            return;
        }

        if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat) {
            if (timer.elapsed(delay2)) {

                if (!mc.player.inventoryContainer.getSlot(weaponSlot).getHasStack()) {
                    getBestWeapon(weaponSlot);
                } else {
                    if (!isBestWeapon(mc.player.inventoryContainer.getSlot(weaponSlot).getStack())) {
                        getBestWeapon(weaponSlot);
                    }
                }
            }
            if (sort.get()) {
                if (timer.elapsed(delay2)) {
                    getBestPickaxe(pickaxeSlot);
                    getBestShovel(shovelSlot);
                    getBestAxe(axeSlot);
                }
            }

            if (timer.elapsed(delay2) && invcleaner.get() && !mc.player.isUsingItem())
                for (int i = 9; i < 45; i++) {
                    if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                        ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                        if (shouldDrop(is, i)) {
                            drop(i);
                            timer.reset();
                            if (delay2 > 0)
                                break;
                        }
                    }
                }
        }
    }

    public void shiftClick(int slot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, 1, mc.player);
    }

    public void swap(int slot1, int hotbarSlot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot1, hotbarSlot, 2, mc.player);
    }

    public void drop(int slot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 1, 4, mc.player);
    }

    public boolean isBestWeapon(ItemStack stack) {
        float swordDamage = getDamage(stack, item -> {
            if (item instanceof ItemSword) {
                return ((ItemSword) item).getDamageVsEntity();
            }
            return 0f; // Default damage for non-swords
        });

        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                if (swordDamage > swordDamage && (is.getItem() instanceof ItemSword || !sword.get()))
                    return false;
            }
        }
        return stack.getItem() instanceof ItemSword || !sword.get();

    }

    public void getBestWeapon(int slot) {
        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();

                float swordDamage = getDamage(is, item -> {
                    if (item instanceof ItemSword) {
                        return ((ItemSword) item).getDamageVsEntity();
                    }
                    return 0f;
                });

                if (isBestWeapon(is) && swordDamage > 0 && (is.getItem() instanceof ItemSword || !sword.get())) {
                    swap(i, slot - 36);
                    timer.reset();
                    break;
                }
            }
        }
    }

    private float getDamage(ItemStack stack, Function<Item, Float> damageFunction) {
        Item item = stack.getItem();
        float damage = 0;

        if (item != null) {
            damage += damageFunction.apply(item);

            // Calculate damage from enchantments
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f +
                    EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.01f;
        }

        return damage;
    }


    public boolean shouldDrop(ItemStack stack, int slot) {
        if (stack.getDisplayName().contains("���") || stack.getDisplayName().contains("�Ҽ�") ||
                stack.getDisplayName().toLowerCase().contains("(right click)") ||
                stack.getDisplayName().toLowerCase().contains("tracking compass")) {
            return false;
        }

        if (uhc.get()) {
            String displayName = stack.getDisplayName().toLowerCase();
            String[] keywords = {
                    "ͷ", "apple", "head", "gold", "crafting table",
                    "stick", "and", "ril", "axe of perun", "barbarian",
                    "bloodlust", "dragonchest", "dragon sword", "dragon armor",
                    "excalibur", "exodus", "fusion armor", "hermes boots",
                    "hide of leviathan", "scythe", "seven-league boots",
                    "shoes of vidar", "apprentice", "master", "vorpal",
                    "enchanted", "spiked", "tarnhelm", "philosopher",
                    "anvil", "panacea", "fusion", "excalibur",
                    "ѧͽ", "��ʦ����", "ն��֮��", "��ħ", "����֮��",
                    "����֮��", "�м�", "�߹�սѥ", "������", "����",
                    "����", "ƻ��", "��", "����֮��", "�����֮��", "��¯",
                    "backpack", "�۱�֮��", "����", "����", "����", "��ϫ",
                    "�׸�", "����֮��", "�������", "��������", "����֮��",
                    "ά��սѥ", "���֮��", "����֮��", "����֮ѥ", "hermes",
                    "barbarian"
            };

            for (String keyword : keywords) {
                if (displayName.contains(keyword)) {
                    return false;
                }
            }
        }

        if ((slot == weaponSlot && isBestWeapon(mc.player.inventoryContainer.getSlot(weaponSlot).getStack())) ||
                (slot == pickaxeSlot && isBestPickaxe(mc.player.inventoryContainer.getSlot(pickaxeSlot).getStack()) && pickaxeSlot >= 0) ||
                (slot == axeSlot && isBestAxe(mc.player.inventoryContainer.getSlot(axeSlot).getStack()) && axeSlot >= 0) ||
                (slot == shovelSlot && isBestShovel(mc.player.inventoryContainer.getSlot(shovelSlot).getStack()) && shovelSlot >= 0)) {
            return false;
        }

        if (stack.getItem() instanceof ItemArmor) {
            for (int type = 1; type < 5; type++) {
                if (mc.player.inventoryContainer.getSlot(4 + type).getHasStack()) {
                    ItemStack is = mc.player.inventoryContainer.getSlot(4 + type).getStack();
                    if (isBestArmor(is, type)) {
                        continue;
                    }
                }
                if (isBestArmor(stack, type)) {
                    return false;
                }
            }
        }

        if (blockcap.get().intValue() != 0 && stack.getItem() instanceof ItemBlock &&
                (getBlockCount() > blockcap.get().intValue())) {
            return true;
        }

        if (stack.getItem() instanceof ItemPotion) {
            if (isBadPotion(stack)) {
                return true;
            }
        }

        if (stack.getItem() instanceof ItemFood && food.get() && !(stack.getItem() instanceof ItemAppleGold)) {
            return true;
        }

        if (stack.getItem() instanceof ItemHoe || stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemArmor) {
            return true;
        }

        if ((stack.getItem() instanceof ItemBow || stack.getItem().getUnlocalizedName().contains("arrow")) && archery.get()) {
            return true;
        }

        return (stack.getItem().getUnlocalizedName().contains("tnt")) ||
                (stack.getItem().getUnlocalizedName().contains("stick")) ||
                (stack.getItem().getUnlocalizedName().contains("egg")) ||
                (stack.getItem().getUnlocalizedName().contains("string")) ||
                (stack.getItem().getUnlocalizedName().contains("cake")) ||
                (stack.getItem().getUnlocalizedName().contains("mushroom")) ||
                (stack.getItem().getUnlocalizedName().contains("flint")) ||
                (stack.getItem().getUnlocalizedName().contains("compass")) ||
                (stack.getItem().getUnlocalizedName().contains("dyePowder")) ||
                (stack.getItem().getUnlocalizedName().contains("feather")) ||
                (stack.getItem().getUnlocalizedName().contains("bucket")) ||
                (stack.getItem().getUnlocalizedName().contains("chest") && !stack.getDisplayName().toLowerCase().contains("collect")) ||
                (stack.getItem().getUnlocalizedName().contains("snow")) ||
                (stack.getItem().getUnlocalizedName().contains("fish")) ||
                (stack.getItem().getUnlocalizedName().contains("enchant")) ||
                (stack.getItem().getUnlocalizedName().contains("exp")) ||
                (stack.getItem().getUnlocalizedName().contains("shears")) ||
                (stack.getItem().getUnlocalizedName().contains("anvil")) ||
                (stack.getItem().getUnlocalizedName().contains("torch")) ||
                (stack.getItem().getUnlocalizedName().contains("seeds")) ||
                (stack.getItem().getUnlocalizedName().contains("leather")) ||
                (stack.getItem().getUnlocalizedName().contains("reeds")) ||
                (stack.getItem().getUnlocalizedName().contains("skull")) ||
                (stack.getItem().getUnlocalizedName().contains("record")) ||
                (stack.getItem().getUnlocalizedName().contains("snowball")) ||
                (stack.getItem() instanceof ItemGlassBottle) ||
                (stack.getItem().getUnlocalizedName().contains("piston"));
    }

    public ArrayList<Integer> getWhitelistedItem() {
        return whitelistedItems;
    }

    private int getBlockCount() {
        int blockCount = 0;

        for (ItemStack stack : mc.player.inventory.mainInventory) {
            if (stack != null && stack.getItem() instanceof ItemBlock) {
                blockCount += stack.stackSize;
            }
        }

        return blockCount;
    }

    private void getBestPickaxe(int slot) {
        for (int i = 9; i < 45; i++) {
            Slot currentSlot = mc.player.inventoryContainer.getSlot(i);

            if (!currentSlot.getHasStack())
                continue;

            ItemStack is = currentSlot.getStack();

            if (isBestPickaxe(is) && pickaxeSlot != i) {
                if (!isBestWeapon(is) && shouldSwap(slot, pickaxeSlot - 36, this::isBestPickaxe)) {
                    swap(i, pickaxeSlot - 36);
                    timer.reset();

                    if (delay.get().longValue() > 0)
                        return;
                }
            }
        }
    }

    private void getBestShovel(int slot) {
        for (int i = 9; i < 45; i++) {
            Slot currentSlot = mc.player.inventoryContainer.getSlot(i);

            if (!currentSlot.getHasStack())
                continue;

            ItemStack is = currentSlot.getStack();

            if (isBestShovel(is) && shovelSlot != i) {
                if (!isBestWeapon(is) && shouldSwap(slot, shovelSlot - 36, this::isBestShovel)) {
                    swap(i, shovelSlot - 36);
                    timer.reset();

                    if (delay.get().longValue() > 0)
                        return;
                }
            }
        }
    }

    private void getBestAxe(int slot) {
        for (int i = 9; i < 45; i++) {
            Slot currentSlot = mc.player.inventoryContainer.getSlot(i);

            if (!currentSlot.getHasStack())
                continue;

            ItemStack is = currentSlot.getStack();

            if (isBestAxe(is) && axeSlot != i) {
                if (!isBestWeapon(is) && shouldSwap(slot, axeSlot - 36, stack -> isBestAxe(stack) && !isBestWeapon(stack))) {
                    swap(i, axeSlot - 36);
                    timer.reset();

                    if (delay.get().longValue() > 0)
                        return;
                }
            }
        }
    }

    private boolean isBestPickaxe(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemPickaxe))
            return false;

        float value = getToolEffect(stack);

        for (int i = 9; i < 45; i++) {
            Slot slot = mc.player.inventoryContainer.getSlot(i);

            if (slot.getHasStack()) {
                ItemStack is = slot.getStack();

                if (is.getItem() instanceof ItemPickaxe && getToolEffect(is) > value) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isBestShovel(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemSpade))
            return false;

        float value = getToolEffect(stack);

        for (int i = 9; i < 45; i++) {
            Slot slot = mc.player.inventoryContainer.getSlot(i);

            if (slot.getHasStack()) {
                ItemStack is = slot.getStack();

                if (is.getItem() instanceof ItemSpade && getToolEffect(is) > value) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isBestAxe(ItemStack stack) {
        Item item = stack.getItem();

        if (!(item instanceof ItemAxe) || isBestWeapon(stack)) {
            return false;
        }

        float value = getToolEffect(stack);

        for (int i = 9; i < 45; i++) {
            Slot slot = mc.player.inventoryContainer.getSlot(i);

            if (slot.getHasStack()) {
                ItemStack is = slot.getStack();

                if (is.getItem() instanceof ItemAxe && getToolEffect(is) > value) {
                    return false;
                }
            }
        }

        return true;
    }


    private float getToolEffect(ItemStack stack) {
        Item item = stack.getItem();

        if (!(item instanceof ItemTool)) {
            return 0;
        }

        ItemTool tool = (ItemTool) item;
        float value;

        if (item instanceof ItemPickaxe) {
            value = tool.getStrVsBlock(stack, Blocks.stone);
        } else if (item instanceof ItemSpade) {
            value = tool.getStrVsBlock(stack, Blocks.dirt);
        } else if (item instanceof ItemAxe) {
            value = tool.getStrVsBlock(stack, Blocks.log);
        } else {
            return 1f; // Unknown tool type
        }

        String name = item.getUnlocalizedName().toLowerCase();

        if (name.contains("gold")) {
            value -= 5;
        }

        value += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 0.0075F;
        value += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 100.0F;

        return value;
    }

    private boolean isBadPotion(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            final ItemPotion potion = (ItemPotion) stack.getItem();

            if (potion.getEffects(stack) != null) {
                for (final Object o : potion.getEffects(stack)) {
                    final PotionEffect effect = (PotionEffect) o;

                    int potionId = effect.getPotionID();
                    if (potionId == Potion.poison.getId() || potionId == Potion.harm.getId() || potionId == Potion.moveSlowdown.getId() || potionId == Potion.weakness.getId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    boolean invContainsType(int type) {
        for (int i = 9; i < 45; i++) {
            ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();
            if (stack != null) {
                Item item = stack.getItem();
                if (item instanceof ItemArmor) {
                    ItemArmor armor = (ItemArmor) item;
                    if (type == armor.armorType) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isBestArmor(ItemStack stack, int type) {
        float prot = getProtection(stack);
        String strType = "";

        switch (type) {
            case 1:
                strType = "helmet";
                break;
            case 2:
                strType = "chestplate";
                break;
            case 3:
                strType = "leggings";
                break;
            case 4:
                strType = "boots";
                break;
        }

        if (!stack.getUnlocalizedName().contains(strType)) {
            return false;
        }

        for (int i = 5; i < 45; i++) {
            Slot slot = Minecraft.getMinecraft().player.inventoryContainer.getSlot(i);

            if (slot.getHasStack()) {
                ItemStack is = slot.getStack();

                if (getProtection(is) > prot && is.getUnlocalizedName().contains(strType)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static float getProtection(ItemStack stack) {
        float prot = 0;

        if (stack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) stack.getItem();
            prot += calculateProtectionFromDamageReduce(armor.damageReduceAmount, stack);
            prot += calculateProtectionFromEnchantment(Enchantment.protection, stack, 0.0075D);
            prot += calculateProtectionFromEnchantment(Enchantment.blastProtection, stack, 0.01D);
            prot += calculateProtectionFromEnchantment(Enchantment.fireProtection, stack, 0.01D);
            prot += calculateProtectionFromEnchantment(Enchantment.thorns, stack, 0.01D);
            prot += calculateProtectionFromEnchantment(Enchantment.unbreaking, stack, 0.02D);
            prot += calculateProtectionFromEnchantment(Enchantment.featherFalling, stack, 0.01D);
        }

        return prot;
    }

    private static float calculateProtectionFromDamageReduce(int damageReduceAmount, ItemStack stack) {
        return (float) (damageReduceAmount + (100 - damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.0075D);
    }

    private static float calculateProtectionFromEnchantment(Enchantment enchantment, ItemStack stack, double multiplier) {
        int level = EnchantmentHelper.getEnchantmentLevel(enchantment.effectId, stack);
        return (float) (level * multiplier);
    }

    private boolean shouldSwap(int newSlot, int currentSlot, Function<ItemStack, Boolean> isBestToolOrArmor) {
        if (mc.player.inventoryContainer.getSlot(currentSlot).getHasStack()) {
            ItemStack currentStack = mc.player.inventoryContainer.getSlot(currentSlot).getStack();
            return !isBestToolOrArmor.apply(currentStack);
        }
        return true;
    }
}