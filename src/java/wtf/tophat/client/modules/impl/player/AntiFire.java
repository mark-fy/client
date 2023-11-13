package wtf.tophat.client.modules.impl.player;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import wtf.tophat.client.TopHat;
import wtf.tophat.client.events.impl.move.MotionEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.StringSetting;

@ModuleInfo(name = "Anti Fire", desc = "removes you fire", category = Module.Category.PLAYER)
public class AntiFire extends Module {

    public final StringSetting mode;

    public AntiFire(){
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "Normal", "Normal")
        );
    }

    public void onMotion(MotionEvent event){
        switch (mode.get()){
            case "Normal":
                if (this.mc.player.isBurning()) {
                    for (int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
                        ItemStack itemStack = this.mc.player.inventory.getStackInSlot(i);
                        if (itemStack == null) continue;
                        itemStack.getItem();
                        if (Item.getIdFromItem(itemStack.getItem()) != 326) continue;
                        this.mc.player.inventory.currentItem = i;
                    }
                    if (this.mc.player.getHeldItem() == null) {
                        return;
                    }
                    this.mc.player.getHeldItem().getItem();
                    if (Item.getIdFromItem(this.mc.player.getHeldItem().getItem()) == 326) {
                        float oldpitch = this.mc.player.rotationPitch;
                        this.mc.player.rotationPitch = 90.0f;
                        this.mc.playerController.sendUseItem(this.mc.player, this.mc.world, this.mc.player.getHeldItem());
                        this.mc.player.rotationPitch = oldpitch;
                    }
                }
                break;
        }
    }
}
