package wtf.tophat.module.impl.render;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.potion.Potion;
import wtf.tophat.events.impl.MotionEvent;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;

@ModuleInfo(name = "Anti Invisibility",desc = "removes invisibility potion effects from players", category = Module.Category.RENDER)
public class AntiInvisibility extends Module {

    @Listen
    public void onMotion(MotionEvent event) {
        mc.world.playerEntities.stream()
                .filter(player -> player != mc.player && player.isPotionActive(Potion.invisibility))
                .forEach(player -> {
                    player.removePotionEffect(Potion.invisibility.getId());
                    player.setInvisible(false);
                });
    }
}
