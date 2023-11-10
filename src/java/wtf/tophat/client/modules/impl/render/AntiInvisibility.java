package wtf.tophat.client.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.potion.Potion;
import wtf.tophat.client.events.impl.move.MotionEvent;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;

@ModuleInfo(name = "Anti Invisibility",desc = "removes invisibility potion effects from players", category = Module.Category.RENDER)
public class AntiInvisibility extends Module {

    @Listen
    public void onMotion(MotionEvent event) {
        getWorld().playerEntities.stream()
                .filter(player -> player != mc.player && player.isPotionActive(Potion.invisibility))
                .forEach(player -> {
                    player.removePotionEffect(Potion.invisibility.getId());
                    player.setInvisible(false);
                });
    }
}
