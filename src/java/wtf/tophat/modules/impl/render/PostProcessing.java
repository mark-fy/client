package wtf.tophat.modules.impl.render;

import wtf.tophat.TopHat;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;

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
