package wtf.tophat.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.util.ResourceLocation;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.UpdateEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;

@ModuleInfo(name = "Custom Cape",desc = "change the cape", category = Module.Category.RENDER)
public class CustomCape extends Module {

    public final StringSetting mode, waveStyle, windMode;
    public final NumberSetting heightMultiplier, gravity;

    public CustomCape(){
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Mode", "TopHat", "TopHat"),

                waveStyle = new StringSetting(this, "Cape Wave Style", "Smooth", "Smooth", "Blocky"),
                windMode = new StringSetting(this, "Cape Wind Mode", "None", "None", "Waves"),
                heightMultiplier = new NumberSetting(this, "Cape Height Multiplier", 1, 16, 6, 0),
                gravity = new NumberSetting(this, "Cape Gravity", 1, 32, 25, 0)
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
