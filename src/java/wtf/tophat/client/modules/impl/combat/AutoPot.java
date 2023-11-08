package wtf.tophat.client.modules.impl.combat;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import wtf.tophat.client.events.base.Event;
import wtf.tophat.client.events.impl.MotionEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;

@ModuleInfo(name = "Auto Pot", desc = "automatically throw potions at your feet", category = Module.Category.COMBAT)
public class AutoPot extends Module {

    @Listen
    public void onMotion(MotionEvent event) {
        if(event.getState() == Event.State.PRE) {
            for (int i = 0; i < 9; ++i) {
                if (mc.player.inventory.getStackInSlot(i) == null || mc.player.inventory.getStackInSlot(i).getItem() == null || !(mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemPotion))
                    continue;
                ItemPotion potion = (ItemPotion)mc.player.inventory.getStackInSlot(i).getItem();
                ItemStack stack = mc.player.inventory.getStackInSlot(i);
                if (!ItemPotion.isSplash(stack.getMetadata()))
                    continue;
                boolean shouldSplash = false;
                for (PotionEffect potionEffect : potion.getEffects(stack.getMetadata())) {
                    if (!isValidEffect(potionEffect))
                        continue;
                    shouldSplash = true;
                }
                if (!shouldSplash)
                    continue;
                switchToSlot(i);
                mc.player.rotationPitchHead = 90.0f;
                mc.player.sendQueue.send(new C03PacketPlayer.C05PacketPlayerLook(getYaw(), 90.0f, getGround()));
                mc.player.sendQueue.send(new C08PacketPlayerBlockPlacement(stack, new BlockPos(-1, -1, -1)));
                switchToSlot(mc.player.inventory.currentItem);
            }
        }
    }

    private void switchToSlot(int slot) {
        mc.player.sendQueue.send(new C09PacketHeldItemChange(slot));
    }

    public boolean isValidEffect(PotionEffect potionEffect) {
        switch (potionEffect.getPotionID()) {
            case 1: {
                return !mc.player.isPotionActive(Potion.moveSpeed);
            }
            case 10: {
                return !mc.player.isPotionActive(Potion.regeneration) && (double)mc.player.getHealth() <= 10;
            }
            case 12: {
                return !mc.player.isPotionActive(Potion.fireResistance);
            }
            case 21: {
                return (double)mc.player.getHealth() <= 10;
            }

        }
        return false;
    }
}