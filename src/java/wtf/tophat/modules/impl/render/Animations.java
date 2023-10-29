package wtf.tophat.modules.impl.render;

import wtf.tophat.Client;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.StringSetting;

@ModuleInfo(name = "Animations", desc = "better hand render", category = Module.Category.RENDER)
public class Animations extends Module {

    //public final StringSetting mode;
    public final BooleanSetting smooth;

    // mode doesnt work

    public Animations(){
        Client.settingManager.add(
                //mode = new StringSetting(this, "Mode", "1.7", "1.7", "Exhibition", "Sigma"),
                smooth = new BooleanSetting(this, "Smooth", false)
        );
    }

}
