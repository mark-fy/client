package wtf.tophat.modules.impl.player;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import wtf.tophat.Client;
import wtf.tophat.events.impl.UpdateEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.utilities.item.ItemUtil;
import wtf.tophat.utilities.time.Stopwatch;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@ModuleInfo(name = "ChestStealer", desc = "steal chest automatically", category = Module.Category.PLAYER)
public class ChestStealer extends Module {

    public Stopwatch timer = new Stopwatch();

    private final NumberSetting delay;

    public ChestStealer() {
        Client.settingManager.add(
                delay = new NumberSetting(this, "Delay", 0, 1000, 500, 2)
        );
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        final int delay2 = delay.get().intValue();
        final EntityPlayerSP player = mc.player;
        int index;
        if (mc.currentScreen instanceof GuiChest) {
            final GuiChest chest = (GuiChest) mc.currentScreen;
            boolean titleCheck = (chest.getLowerChestInventory().getDisplayName().getUnformattedText().contains("Chest")) || chest.getLowerChestInventory().getDisplayName().getUnformattedText().equalsIgnoreCase("LOW");
            if (titleCheck) {
                if (isChestEmpty(chest) || isInventoryFull()) {
                    player.closeScreen();
                    return;
                }
                for (index = 0; index < chest.getLowerChestInventory().getSizeInventory(); ++index) {
                    final ItemStack stack = chest.getLowerChestInventory().getStackInSlot(index);
                    if (stack != null && timer.timeElapsed(delay2 - ThreadLocalRandom.current().nextInt(0, 250))) {
                        boolean trash = !ItemUtil.isTrash(stack);
                        if (trash) {
                            mc.playerController.windowClick(chest.inventorySlots.windowId, index, 0, 1, player);
                            timer.resetTime();
                            break;
                        }
                    }
                }
            }
        }
    }

    public void set(Set set, TileEntity chest) {
        if (set.size() > 128) {
            set.clear();
        }
        set.add(chest);
    }

    public boolean isChestEmpty(final GuiChest chest) {
        for (int index = 0; index < chest.getLowerChestInventory().getSizeInventory(); ++index) {
            ItemStack stack = chest.getLowerChestInventory().getStackInSlot(index);
            if (stack != null)

                if (!ItemUtil.isTrash(stack) )
                    return false;
        }
        return true;
    }

    public boolean isInventoryFull() {
        for (int index = 9; index <= 44; ++index) {
            ItemStack stack = mc.player.inventoryContainer.getSlot(index).getStack();
            if (stack == null) {
                return false;
            }
        }
        return true;
    }

    public void onEnable() {
        super.onEnable();
        timer.resetTime();
    }

    public void onDisable() {
        super.onDisable();
    }
}