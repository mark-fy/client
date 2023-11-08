package wtf.tophat.client.modules.impl.render;

import wtf.tophat.client.TopHat;
import wtf.tophat.client.modules.base.Module;
import wtf.tophat.client.modules.base.ModuleInfo;
import wtf.tophat.client.settings.impl.BooleanSetting;
import wtf.tophat.client.settings.impl.StringSetting;

@ModuleInfo(name = "Block Animations",desc = "changes your item blocking animation", category = Module.Category.RENDER)
public class BlockAnimations extends Module {

    public final StringSetting mode;
    public final BooleanSetting blockHit;

    public BlockAnimations() {
        TopHat.settingManager.add(
                mode = new StringSetting(this, "Style", "1.7", "1.7", "Exhibition", "Flux", "Swang", "Swong"),
                blockHit = new BooleanSetting(this, "Block Hit", false)
        );
    }
}
