package tophat.fun.modules.impl.others;

import io.github.nevalackin.radbus.Listen;
import tophat.fun.events.impl.game.UpdateEvent;
import tophat.fun.modules.base.Module;
import tophat.fun.modules.base.ModuleInfo;

@ModuleInfo(name = "Optimisations", desc = "optimizes your performance.", category = Module.Category.OTHERS)
public class Optimisations extends Module {

    @Listen
    public void onUpdate(UpdateEvent event){
        // Sky
        mc.gameSettings.ofSky = false;
        mc.gameSettings.ofCustomSky = false;

        // Particles
        mc.gameSettings.ofVoidParticles = false;

        // Shaders
        mc.gameSettings.ofFastRender = false;
    }

}
