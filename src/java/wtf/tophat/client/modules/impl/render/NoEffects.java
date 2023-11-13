package wtf.tophat.client.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;
import wtf.tophat.client.events.impl.world.UpdateEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;

@ModuleInfo(name = "No Effects", desc = "removes effects", category = Module.Category.RENDER)
public final class NoEffects extends Module {

    @Listen
    public void onUpdate(UpdateEvent event) {
        EntityPlayerSP player = mc.player;
        Potion blind = Potion.blindness;
        Potion confusion = Potion.confusion;
        if (player.isPotionActive(blind)) {
            player.removePotionEffect(blind.id);
        }

        if (player.isPotionActive(confusion)) {
            player.removePotionEffect(confusion.id);
        }

    }
}