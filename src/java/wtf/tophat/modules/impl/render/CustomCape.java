package wtf.tophat.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.util.ResourceLocation;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.UpdateEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.StringSetting;

@ModuleInfo(name = "Custom Cape",desc = "change the cape", category = Module.Category.RENDER)
public class CustomCape extends Module {

    public final StringSetting mode;

    public CustomCape(){
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "TopHat", "TopHat")
        );
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        ResourceLocation resourceLocation = new ResourceLocation("tophat/capes/" + mode.get() + ".png");;
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
