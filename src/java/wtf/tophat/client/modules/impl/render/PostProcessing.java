package wtf.tophat.client.modules.impl.render;

import wtf.tophat.client.TopHat;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.NumberSetting;

@ModuleInfo(name = "Post Processing",desc = "modify shaders", category = Module.Category.RENDER)
public class PostProcessing extends Module {

    public final BooleanSetting blurShader;

    public PostProcessing() {
        TopHat.settingManager.add(
                blurShader = new BooleanSetting(this, "Blur Shader", true)
        );
        setEnabled(true);
    }
}
