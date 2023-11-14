package wtf.tophat.client.modules.impl.player;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.move.MotionEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.StringSetting;

@ModuleInfo(name = "Anti Fire", desc = "removes fire effects", category = Module.Category.PLAYER)
public class AntiFire extends Module {

    private final StringSetting mode;
    public final BooleanSetting visual;

    public AntiFire(){
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "Normal", "Normal"),
                visual = new BooleanSetting(this, "Remove Visual Fire", true)
        );
    }

    public void onMotion(MotionEvent event){
        switch (mode.get()){
            case "Normal":
                if (mc.player.isBurning()) {
                    if (mc.player.getHeldItem() == null) {
                        return;
                    }

                    for (int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
                        ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
                        if (itemStack == null)
                            continue;
                        if (Item.getIdFromItem(itemStack.getItem()) != 326)
                            continue;
                        mc.player.inventory.currentItem = i;
                    }

                    if (Item.getIdFromItem(mc.player.getHeldItem().getItem()) == 326) {
                        float oldPitch = mc.player.rotationPitch;
                        mc.player.rotationPitch = 90.0f;
                        mc.playerController.sendUseItem(mc.player, mc.world, mc.player.getHeldItem());
                        mc.player.rotationPitch = oldPitch;
                    }
                }
                break;
        }
    }
}
