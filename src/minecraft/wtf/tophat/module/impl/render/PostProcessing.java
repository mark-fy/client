package wtf.tophat.module.impl.render;

import wtf.tophat.Client;
import wtf.tophat.module.base.Module;
import wtf.tophat.module.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;

@ModuleInfo(name = "PostProcessing",desc = "modify shaders", category = Module.Category.RENDER)
public class PostProcessing extends Module {

    public final BooleanSetting blurShader;

    public PostProcessing() {
        Client.settingManager.add(
                blurShader = new BooleanSetting(this, "Blur Shader", true)
        );
    }

    @Override
    public void onEnable() {
        setEnabled(false);
        super.onEnable();
    }
}
