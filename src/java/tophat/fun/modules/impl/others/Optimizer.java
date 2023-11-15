package tophat.fun.modules.impl.others;

import tophat.fun.events.impl.game.UpdateEvent;
import tophat.fun.modules.Module;
import tophat.fun.modules.ModuleInfo;

@ModuleInfo(name = "Optimizer", desc = "better fps", category = Module.Category.OTHERS)
public class Optimizer extends Module {

    public void onUpdate(UpdateEvent event){
        // Memory Clean
        System.gc();

        // Sky
        mc.gameSettings.ofSky = false;
        mc.gameSettings.ofCustomSky = false;

        // Particles
        mc.gameSettings.ofVoidParticles = false;
    }

}
