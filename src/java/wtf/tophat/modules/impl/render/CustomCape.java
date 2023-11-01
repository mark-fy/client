package wtf.tophat.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.util.ResourceLocation;
import wtf.tophat.events.impl.UpdateEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;

@ModuleInfo(name = "Custom Cape",desc = "change the cape", category = Module.Category.RENDER)
public class CustomCape extends Module {

    @Listen
    public void onUpdate(UpdateEvent event) {
        ResourceLocation resourceLocation = new ResourceLocation("tophat/cape.png");;
        getPlayer().setLocationCape(resourceLocation);
    }

    @Override
    public void onDisable() {
        if(getPlayer() == null) {
            return;
        }
        getPlayer().setLocationCape(null);
    }

}
