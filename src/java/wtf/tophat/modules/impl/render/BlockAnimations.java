package wtf.tophat.modules.impl.render;

import wtf.tophat.Client;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.BooleanSetting;
import wtf.tophat.settings.impl.StringSetting;

@ModuleInfo(name = "Block Animations",desc = "changes your item blocking animation", category = Module.Category.RENDER)
public class BlockAnimations extends Module {

    public final StringSetting mode;
    public final BooleanSetting blockHit;

    public BlockAnimations() {
        Client.settingManager.add(
                mode = new StringSetting(this, "Style", "1.7", "1.7", "Exhibition", "Flux", "Swang", "Swong"),
                blockHit = new BooleanSetting(this, "Block Hit", false)
        );
    }
}
