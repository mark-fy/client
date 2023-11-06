package wtf.tophat.utilities.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class SlotSpoofHandler {

    private int spoofedSlot;

    private boolean spoofing;

    public void setSpoofedSlot(int spoofedSlot) {
        this.spoofedSlot = spoofedSlot;
    }

    public boolean isSpoofing() {
        return spoofing;
    }

    public void setSpoofing(boolean spoofing) {
        this.spoofing = spoofing;
    }

    public void startSpoofing(int slot) {
        this.spoofing = true;
        this.spoofedSlot = slot;
    }

    public void stopSpoofing() {
        this.spoofing = false;
    }

    public int getSpoofedSlot() {
        return spoofing ? spoofedSlot : Minecraft.getMinecraft().player.inventory.currentItem;
    }

    public ItemStack getSpoofedStack() {
        return spoofing ? Minecraft.getMinecraft().player.inventory.getStackInSlot(spoofedSlot) : Minecraft.getMinecraft().player.inventory.getCurrentItem();
    }

}