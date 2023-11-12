package wtf.tophat.client.modules.impl.render;

import wtf.tophat.client.TopHat;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.NumberSetting;

@ModuleInfo(name = "Post Processing",desc = "modify shaders", category = Module.Category.RENDER)
public class PostProcessing extends Module {

    public final BooleanSetting blurShader, bloomShader;
    public final NumberSetting iterations, offset;

    public PostProcessing() {
        TopHat.settingManager.add(
                blurShader = new BooleanSetting(this, "Blur Shader", true),
                bloomShader = new BooleanSetting(this, "Bloom Shader", true),
                iterations = new NumberSetting(this, "Bloom Iterations", 1, 8, 3, 0).setHidden(() -> !bloomShader.get()),
                offset = new NumberSetting(this, "Bloom Offset", 1, 10, 1, 0).setHidden(() -> !bloomShader.get())
        );
        setEnabled(true);
    }
}
